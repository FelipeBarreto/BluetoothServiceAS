package br.ufc.great.somc.network.bluetoothservice;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.util.Log;
import android.widget.Toast;
import br.ufc.great.somc.network.base.BluetoothListener;
import br.ufc.great.somc.network.base.BluetoothServiceApi;
import br.ufc.great.somc.network.bluetoothservice.message.Message;
import br.ufc.great.somc.network.bluetoothservice.receivers.DeviceFoundReceiver;
import br.ufc.great.somc.network.bluetoothservice.receivers.DevicesConnectionReceiver;
import br.ufc.great.somc.network.bluetoothservice.receivers.ExceptionReceiver;
import br.ufc.great.somc.network.bluetoothservice.receivers.MessageReceiver;
import br.ufc.great.somc.network.bluetoothservice.receivers.NotNeighborDeviceReceiver;
import br.ufc.great.somc.network.bluetoothservice.receivers.StateChangeReceiver;
import br.ufc.great.somc.network.bluetoothservice.receivers.StateDiscoveryReceiver;
import br.ufc.great.somc.network.bluetoothservice.receivers.StateObservingReceiver;
import br.ufc.great.somc.network.bluetoothservice.receivers.StateScanModeReceiver;
import br.ufc.great.somc.network.bluetoothservice.threads.AcceptThread;
import br.ufc.great.somc.network.bluetoothservice.threads.ConnectThread;
import br.ufc.great.somc.network.bluetoothservice.threads.ConnectedThread;
import br.ufc.great.somc.network.bluetoothservice.threads.SlaveConnectedThread;
import br.ufc.great.somc.network.bluetoothservice.threads.Timer;

/**
 * This class implements a Bluetooth Service which is responsible to manage
 * bluetooth connections. It start a listing thread and allow to make connection
 * with nearby devices providing a bluetooth network. It implements all strategy
 * to maintain a scarttnet connected.
 * 
 * It service uses a aild interface, so it is possible use it in many
 * applications independently
 * 
 * @author bruno
 * @see Service
 * @see BluetoothServiceApi
 * @see BluetoothListener
 */

public class BluetoothNetworkManagerService extends Service implements
		BluetoothThreadListener {

	//private static final String BLUETOOTH_SERVICE_INTENT = "br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService";

	/**
	 * Tag for debug
	 */
	public static final String TAG = BluetoothNetworkManagerService.class
			.getSimpleName();

	/**
	 * The default discoverable time
	 */
	private static final int DISCOVERABLE_TIME = 300;
	/**
	 * Name for the SDP record when creating server socket
	 */
	private static final String NAME = "SEMC_BluetoothService";

	/**
	 * Unique UUID for this application
	 */
	protected static final UUID MY_UUID = UUID
			.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

	/**
	 * Indicate that the observing procedure has started.
	 */
	public static final int BLUETOOTH_SEARCH_WAIT_TIME = 0;
	/**
	 * Indicate that the observing time has changed.
	 */
	public static final int BLUETOOTH_UPDATE_SEARCH_WAIT_TIME = 1;
	/**
	 * Indicate that the observing procedure has stopped.
	 */
	public static final int BLUETOOTH_STOPPED_OBSERVING = 2;

	/**
	 * Extra used by {@link #ACTION_BLUETOOTH_SERVICE_DEVICE_CONNECTION}. This
	 * extra contains the device address involved of the connection.
	 */
	public static final String BLUETOOTH_SERVICE_DEVICE_ADDRESS = "br.ufc.great.semc.network.base.BluetoothNetworkManagerService.DEVICE_NAME";
	/**
	 * Extra used by {@link #ACTION_BLUETOOTH_SERVICE_DEVICE_CONNECTION}. This
	 * extra contains the device name involved of the connection.
	 */
	public static final String BLUETOOTH_SERVICE_DEVICE_NAME = "br.ufc.great.semc.network.base.BluetoothNetworkManagerService.DEVICE_ADDRESS";

	/**
	 * The default time to observe before start a new discovery
	 */
	public static int MIN_WAIT_TIME = 60000;

	/**
	 * The default timeout time of handshake protocol
	 */
	public static final int TIMEOUT = 5000;

	/**
	 * The amount of connection considered good to maintain a scatternet fully
	 * connected
	 */
	public static final int MIN_NEIGHBORS = 5;
	/**
	 * Max connection of a bluetooth device
	 */
	public static final int MAX_BLUETOOTH_CONNECTION = 7;

	private static int barrierCount = 0;
	/**
	 * String: Device's MAC ConnectedThread: The connection to this device
	 */
	private static ConnectionsPool connectionPool;
	private static Vector<BluetoothSocket> connections;

	private int currentState;
	private BluetoothAdapter bluetoothAdapter;

	private AcceptThread acceptThread;
	private ConnectThread connectThread;
	private ObserverThread observer;

	private Set<BluetoothDevice> nearbyDevices;
	private Set<BluetoothDevice> lastDevicesToConnect;
	private RemoteCallbackList<BluetoothListener> callbackListeners;

	private BroadcastReceiver stateChangeReceiver = new StateChangeReceiver(
			this);
	private BroadcastReceiver stateScanModeReceiver = new StateScanModeReceiver(
			this);
	private BroadcastReceiver stateObservingReceiver = new StateObservingReceiver(
			this);
	private BroadcastReceiver stateDiscoveryReceiver = new StateDiscoveryReceiver(
			this);
	private BroadcastReceiver deviceFoundReceiver = new DeviceFoundReceiver(
			this);
	private BroadcastReceiver deviceConnectionReceiver = new DevicesConnectionReceiver(
			this);
	private BroadcastReceiver messageReceiver = new MessageReceiver(this);
	private BroadcastReceiver exceptionReceiver = new ExceptionReceiver(this);
	private BroadcastReceiver notNeighborDeviceReceiver = new NotNeighborDeviceReceiver(
			this);

	private BluetoothServiceApi.Stub apiEndpoint = new ServiceBinder(this);

	@Override
	public IBinder onBind(Intent intent) {
		debugLog("Bound by intent " + intent);

		if (enableBlutooth()) {
			start();
		}
		return apiEndpoint;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		debugLog("Criando o service");

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_SHORT).show();
			stopSelf();
		}
		enableBlutooth();
		callbackListeners = new RemoteCallbackList<BluetoothListener>();
		registerReceivers();

		connectionPool = new ConnectionsPool();
		connections = new Vector<BluetoothSocket>();
	}

	/**
	 * Add a listener to callback list
	 * 
	 * @param listener
	 */
	public void addListener(BluetoothListener listener) {
		if (callbackListeners.register(listener)) {
			restoreSnapshot();
		}
	}

	/**
	 * Remove a specific listener from callback list
	 * 
	 * @param listener
	 */
	public void removeListener(BluetoothListener listener) {
		callbackListeners.unregister(listener);
	}

	/**
	 * Perform manual connection with another device
	 * 
	 * @param address
	 */
	public void manualConnect(String address) {
		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
		HashSet<BluetoothDevice> hash = new HashSet<BluetoothDevice>();
		hash.add(device);
		connect(hash);
	}

	/**
	 * Device's bluetooth address
	 * 
	 * @return address
	 */
	public String getMyAddress() {
		return bluetoothAdapter.getAddress();
	}

	/**
	 * This method is called on {@link #onStartCommand(Intent, int, int)} and is
	 * responsible to notifier the above layers about existents connections. To
	 * do this task it uses {@link #ACTION_BLUETOOTH_SERVICE_DEVICE_CONNECTION}.
	 */
	private void restoreSnapshot() {
		Set<String> addresses = connectionPool.keySet();
		Intent intent = new Intent(
				DevicesConnectionReceiver.ACTION_BLUETOOTH_SERVICE_DEVICE_CONNECTION);
		for (String macAddress : addresses) {
			intent.putExtra(
					DevicesConnectionReceiver.BLUETOOTH_SERVICE_DEVICE_CONNECTION_STATE,
					DevicesConnectionReceiver.BLUETOOTH_SERVICE_DEVICE_CONNECTED);
			intent.putExtra(BLUETOOTH_SERVICE_DEVICE_NAME,
					connectionPool.get(macAddress).getDestinationName());
			intent.putExtra(BLUETOOTH_SERVICE_DEVICE_ADDRESS, macAddress);
			sendBroadcast(intent);
		}
	}

	/**
	 * Return the current connection state.
	 */
	int getCurrentState() {
		return currentState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	private void start() {
		debugLog("start");

		// Request discover from BluetoothAdapter
		if (nearbyDevices == null) {
			nearbyDevices = new HashSet<BluetoothDevice>();
		} else {
			nearbyDevices.clear();
		}

		// Cancel any thread attempting to make a connection
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}

		// Start the thread to listen on a BluetoothServerSocket
		if (acceptThread == null) {
			acceptThread = new AcceptThread(NAME, MY_UUID, this);
			acceptThread.start();
		}
	}

	/**
	 * Add a new device to the nearby devices list
	 * 
	 * @param device
	 */
	public void addDevice(BluetoothDevice device) {
		if (nearbyDevices == null) {
			nearbyDevices = new HashSet<BluetoothDevice>();
		}
		nearbyDevices.add(device);
	}

	/**
	 * Calls connect with nearbyDevices as parameter
	 */
	public void connect() {
		connect(nearbyDevices);
	}

	/**
	 * Start the ConnectThread to initiate a connection to a list remote devices
	 * and control the timeout of the Handshake protocol. For each device, we
	 * try to connect up to three times. To do this, is necessary implement a
	 * concurrent mechanism called by barrier
	 * 
	 * @param devices
	 *            A list of BluetoothDevice to connect
	 */
	public void connect(Set<BluetoothDevice> devices) {

		Set<BluetoothDevice> devicesToConnect = removeConnectedDevices(devices);

		if ((lastDevicesToConnect != null)
				&& (lastDevicesToConnect.isEmpty() == false && devicesToConnect
						.isEmpty() == false)) {
			if (devicesToConnect.containsAll(lastDevicesToConnect)
					|| lastDevicesToConnect.containsAll(devicesToConnect)) {
				if (observer != null) {
					observer.incrementWaitTime();
				}

			} else {
				if (observer != null) {
					observer.resetWaitTime();
				}
			}
		}

		lastDevicesToConnect = new HashSet<BluetoothDevice>(
				devicesToConnect.size());
		for (BluetoothDevice device : devicesToConnect) {
			lastDevicesToConnect.add(device);
		}

		debugLog("connect to: " + devicesToConnect);

		HashMap<BluetoothDevice, Integer> retries = new HashMap<BluetoothDevice, Integer>(
				devicesToConnect.size());
		HashMap<BluetoothDevice, Timer> timers = new HashMap<BluetoothDevice, Timer>(
				devicesToConnect.size());

		while (devicesToConnect.size() > 0) {
			connectThread = new ConnectThread(devicesToConnect, MY_UUID, this,
					true);
			connectThread.start();

			try {
				connectThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (connections.size() == 0) {
				break;
			}

			barrierCount = connections.size();
			for (BluetoothSocket connection : connections) {
				BluetoothDevice device = connection.getRemoteDevice();
				timers.put(device, connected(connection));
				Integer numRetries = retries.get(device);
				if (numRetries == null) {
					retries.put(device, 1);
				} else {
					retries.put(device, ++numRetries);
				}
			}

			synchronized (this) {
				try {
					while (getBarrierCount() != 0) {
						wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			for (BluetoothSocket connection : connections) {
				BluetoothDevice device = connection.getRemoteDevice();
				int numRetries = retries.get(device);
				Timer timer = timers.get(device);

				if (timer == null || numRetries == 3
						|| timer.hasReceivedHandshake()) {
					devicesToConnect.remove(device);
				}
			}

			connections.clear();
			barrierCount = devicesToConnect.size();
		}
	}

	/**
	 * This method receives a set of devices nearby. It compares this set with
	 * hte set of connected devices and return a new set just with not connected
	 * devices, excluding connected devices of the initial set
	 * 
	 * @param devices
	 *            Set of devices nearby
	 * @return Set of devices to be connect
	 */
	private Set<BluetoothDevice> removeConnectedDevices(
			Set<BluetoothDevice> devices) {

		Set<BluetoothDevice> devicesToConnect = new HashSet<BluetoothDevice>();

		for (BluetoothDevice bluetoothDevice : devices) {
			if (connectionPool.get(bluetoothDevice.getAddress()) == null) {
				devicesToConnect.add(bluetoothDevice);
			}
		}

		connectionPool.removeRudantLinks(devicesToConnect.size());

		return devicesToConnect;
	}

	/**
	 * Create a SlaveConnect Thread and start the handshake protocol to
	 * establish the connection
	 * 
	 * @param socket
	 *            A opened socket to be used by SlaveConnectedThread
	 * @return timer A thread responsible to control the handshake protocol
	 *         TIMEOUT
	 */
	public synchronized Timer connected(BluetoothSocket socket) {
		SlaveConnectedThread connectedThread = new SlaveConnectedThread(socket,
				this);
		if (BluetoothNetworkManagerService.addConnectedThread(connectedThread)) {
			Timer timer = new Timer(TIMEOUT, connectedThread, this);
			timer.start();

			sendConnectedDeviceInfo(socket.getRemoteDevice().getName(), socket
					.getRemoteDevice().getAddress());
			return timer;
		} else {
			releaseBarrier();
			return null;
		}

	}

	/**
	 * Send the device information (name and address) to UI
	 * 
	 * @param deviceName
	 *            The device name
	 * @param deviceAddress
	 *            The device address
	 */
	private void sendConnectedDeviceInfo(String deviceName, String deviceAddress) {
		Intent intent = new Intent(
				DevicesConnectionReceiver.ACTION_BLUETOOTH_SERVICE_DEVICE_CONNECTION);
		intent.putExtra(
				DevicesConnectionReceiver.BLUETOOTH_SERVICE_DEVICE_CONNECTION_STATE,
				DevicesConnectionReceiver.BLUETOOTH_SERVICE_DEVICE_CONNECTED);
		intent.putExtra(BLUETOOTH_SERVICE_DEVICE_NAME, deviceName);
		intent.putExtra(BLUETOOTH_SERVICE_DEVICE_ADDRESS, deviceAddress);
		sendBroadcast(intent);
	}

	/**
	 * Stop all threads
	 * 
	 * @throws IOException
	 */
	public void stop() {
		debugLog("stop");

		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}

		connectionPool.cancelAllConnectedThreads();

		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}
	}

	@Override
	public void onMessageSent(String jsonMessage) {

	}

	@Override
	public void onMessageReceived(String jsonMessage) {
		Intent intent = new Intent(
				MessageReceiver.ACTION_BLUETOOTH_SERVICE_MESSAGE);
		intent.putExtra(MessageReceiver.BLUETOOTH_SERVICE_MESSAGE_STATE,
				MessageReceiver.BLUETOOTH_SERVICE_MESSAGE_RECEIVED);
		intent.putExtra(MessageReceiver.BLUETOOTH_SERVICE_MESSAGE_CONTENT,
				jsonMessage);
		sendBroadcast(intent);
	}

	@Override
	public void onDeviceConnect(String name, String address) {
		sendConnectedDeviceInfo(name, address);
	}

	@Override
	public void onDeviceDisconnect(String name, String address) {
		Intent intent = new Intent(
				DevicesConnectionReceiver.ACTION_BLUETOOTH_SERVICE_DEVICE_CONNECTION);
		intent.putExtra(
				DevicesConnectionReceiver.BLUETOOTH_SERVICE_DEVICE_CONNECTION_STATE,
				DevicesConnectionReceiver.BLUETOOTH_SERVICE_DEVICE_DISCONNECTED);
		intent.putExtra(BLUETOOTH_SERVICE_DEVICE_NAME, name);
		intent.putExtra(BLUETOOTH_SERVICE_DEVICE_ADDRESS, address);
		sendBroadcast(intent);
	}

	@Override
	public void exceptionHandler(int exception) {
		String exceptionMessage = null;
		switch (exception) {
		case UNABLE_TO_CONNECT:
			exceptionMessage = getResources().getString(
					R.string.unable_to_connect_message);
			break;

		case TIME_OUT:
			exceptionMessage = getResources().getString(
					R.string.timeout_message);
			break;

		case CONNECTION_LOST:
			exceptionMessage = getResources().getString(
					R.string.connection_lost_message);
			break;

		case UNKNOW_MESSAGE:
			exceptionMessage = getResources().getString(
					R.string.timeout_message);
			break;

		default:
			break;
		}
		Intent intent = new Intent(
				ExceptionReceiver.ACTION_BLUETOOTH_SERVICE_EXCEPTION_OCCURRED);
		intent.putExtra(ExceptionReceiver.BLUETOOTH_SERVICE_EXCEPTION_NAME,
				exception);
		intent.putExtra(ExceptionReceiver.BLUETOOTH_SERVICE_EXCEPTION_MESSAGE,
				exceptionMessage);
		sendBroadcast(intent);
	}

	@Override
	public void onNotNeighborDeviceFound(Neighborhood neighborhood) {
		Collection<Device> devices = neighborhood.getNeighborhoodDevices();
		Intent intent = new Intent(
				NotNeighborDeviceReceiver.ACTION_BLUETOOTH_SERVICE_NOT_NEIGHBOR_FOUND);
		for (Device device : devices) {
			intent.putExtra(BLUETOOTH_SERVICE_DEVICE_NAME, device.getName());
			intent.putExtra(BLUETOOTH_SERVICE_DEVICE_ADDRESS,
					device.getMacAddress());
			sendBroadcast(intent);
		}
	}

	/**
	 * @return All actives connection
	 * @see ConnectionsPool
	 */
	public static ConnectionsPool getConnectedThreads() {
		return connectionPool;
	}

	/**
	 * Add a connected thread to hashmap used to manage them
	 * 
	 * @param connectedThread
	 *            A connected thread
	 * @return
	 */
	public static boolean addConnectedThread(ConnectedThread connectedThread) {
		return connectionPool.newConnection(connectedThread);
	}

	/**
	 * Add a socket to hashmap used to manage them
	 * 
	 * @param connection
	 *            A Bluetooth socket
	 */
	public static void addConnectionSocket(BluetoothSocket connection) {
		connections.add(connection);
	}

	/**
	 * Remove a connected thread to hashmap used to manage them
	 * 
	 * @param connectedThread
	 */
	public static void removeConnectedThread(ConnectedThread connectedThread) {
		connectionPool.removeConnection(connectedThread);
	}

	/**
	 * 
	 * @return neighborhood a Hashmap that contain all neighbor of this device
	 */
	public static HashMap<String, Device> getNeighborhood() {
		HashMap<String, Device> neighborhood = new HashMap<String, Device>(
				connectionPool.size());

		synchronized (connectionPool) {
			Set<String> keys = connectionPool.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				Device neighbor = new Device(connectionPool.get(key));
				neighborhood.put(key, neighbor);
			}
		}

		return neighborhood;
	}

	/**
	 * Send a broadcast message. All neighbors will receive. If you the source
	 * message should not receive this, use the avoidAddress specifying its
	 * address.
	 * 
	 * @param jsonMessage
	 *            Routing message parsed to json
	 * @param avoidAddress
	 *            Address do not receive this message (the message source)
	 * @see RoutingMessage
	 */
	void sendBroadcastMessage(String jsonMessage, String avoidAddress) {
		Message msg = new Message();
		msg.setContent(jsonMessage);
		connectionPool.sendBrodcastMessage(msg, avoidAddress);
	}

	/**
	 * Send a unicast message to nextHop
	 * 
	 * @param route
	 *            Routing message
	 * @see RoutingMessage
	 */
	void sendUnicastMessage(String jsonMessage, String destinationAddress) {
		Message msg = new Message();
		msg.setDestinationMacAddress(destinationAddress);
		msg.setContent(jsonMessage);
		connectionPool.sendUnicastMessage(msg);
	}

	/**
	 * Get the current barrier variable value
	 * 
	 * @return barrierCount
	 */
	private static synchronized int getBarrierCount() {
		return barrierCount;
	}

	/**
	 * Decrement the barrier variable value
	 */
	private static synchronized void decrementBarrierCount() {
		barrierCount--;
	}

	/**
	 * Verify if the barrier can be releasead and notify the asleep thread
	 */
	public void releaseBarrier() {
		decrementBarrierCount();
		if (getBarrierCount() == 0) {
			synchronized (this) {
				notify();
			}
		}
	}

	/**
	 * Used to set if there are redundant connection
	 * 
	 * @param hasRedundacy
	 */
	public static synchronized void setHasRedundacy(boolean hasRedundacy) {
		connectionPool.setHasRedundacy(hasRedundacy);

	}

	/**
	 * Start bluetooth discovery.
	 */
	void startDiscovery() {
		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}
		bluetoothAdapter.startDiscovery();
	}

	/**
	 * Used to turn on the local bluetooth adapter
	 * 
	 * @return true if it is on. Otherwise return false.
	 */
	private boolean enableBlutooth() {
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			enableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(enableIntent);
			return false;
		}
		return true;
	}

	/**
	 * Used to make the bluetooth device discoverable
	 * 
	 * @return true if it become discoverable. Otherwise, return false.
	 */
	boolean ensureDiscoverable() {
		debugLog("ensure discoverable");

		if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
					DISCOVERABLE_TIME);
			discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(discoverableIntent);
			return false;
		}
		return true;
	}

	/**
	 * Used to get a {@link BluetoothDevice} according with the specified
	 * address
	 * 
	 * @param address
	 *            The device address
	 * @return The {@link BluetoothDevice}
	 */
	BluetoothDevice getRemoteDevice(String address) {
		return bluetoothAdapter.getRemoteDevice(address);
	}

	public RemoteCallbackList<BluetoothListener> getCallbackListeners() {
		return callbackListeners;
	}

	/**
	 * Start the observer thread.
	 * 
	 * @return true, if it is now observing. Otherwise, return false.
	 */
	boolean startObserver() {
		if (observer == null) {
			observer = new ObserverThread();
			observer.start();

			Intent intent = new Intent(
					StateObservingReceiver.ACTION_BLUETOOTH_SERVICE_OBSERVING_TIME);
			intent.putExtra(
					StateObservingReceiver.BLUETOOTH_SERVICE_OBSERVING_STATE,
					BLUETOOTH_SEARCH_WAIT_TIME);
			intent.putExtra(
					StateObservingReceiver.BLUETOOTH_SERVICE_OBSERVING_EXTRA,
					MIN_WAIT_TIME);
			sendBroadcast(intent);

			return true;
		}
		return false;
	}

	/**
	 * Stop the observer thread
	 */
	void stopObserver() {
		observer.interrupt();
		observer = null;

		Intent intent = new Intent(
				StateObservingReceiver.ACTION_BLUETOOTH_SERVICE_OBSERVING_TIME);
		intent.putExtra(
				StateObservingReceiver.BLUETOOTH_SERVICE_OBSERVING_STATE,
				BLUETOOTH_STOPPED_OBSERVING);
		intent.putExtra(
				StateObservingReceiver.BLUETOOTH_SERVICE_OBSERVING_EXTRA, 1);
		sendBroadcast(intent);
	}

	int getObservingTime() {
		return observer.getWaitTime();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stop();
		unregisterReceivers();
		callbackListeners.kill();

	}

	@Override
	public void onRebind(Intent intent) {
		debugLog("ReBound by intent " + intent);

		if (enableBlutooth()) {
			start();
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return true;
	}

	/**
	 * Register all broadcast receivers
	 */
	private void registerReceivers() {
		registerReceiver(stateChangeReceiver, new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED));

		registerReceiver(stateScanModeReceiver, new IntentFilter(
				BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));

		registerReceiver(stateObservingReceiver, new IntentFilter(
				StateObservingReceiver.ACTION_BLUETOOTH_SERVICE_OBSERVING_TIME));

		registerReceiver(stateDiscoveryReceiver, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_STARTED));

		registerReceiver(stateDiscoveryReceiver, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

		registerReceiver(deviceFoundReceiver, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));

		registerReceiver(
				deviceConnectionReceiver,
				new IntentFilter(
						DevicesConnectionReceiver.ACTION_BLUETOOTH_SERVICE_DEVICE_CONNECTION));

		registerReceiver(messageReceiver, new IntentFilter(
				MessageReceiver.ACTION_BLUETOOTH_SERVICE_MESSAGE));

		registerReceiver(exceptionReceiver, new IntentFilter(
				ExceptionReceiver.ACTION_BLUETOOTH_SERVICE_EXCEPTION_OCCURRED));

		registerReceiver(
				notNeighborDeviceReceiver,
				new IntentFilter(
						NotNeighborDeviceReceiver.ACTION_BLUETOOTH_SERVICE_NOT_NEIGHBOR_FOUND));

	}

	/**
	 * Unregister all broadcast receivers
	 */
	private void unregisterReceivers() {
		unregisterReceiver(stateChangeReceiver);
		unregisterReceiver(stateScanModeReceiver);
		unregisterReceiver(stateObservingReceiver);
		unregisterReceiver(stateDiscoveryReceiver);
		unregisterReceiver(deviceFoundReceiver);
		unregisterReceiver(deviceConnectionReceiver);
		unregisterReceiver(messageReceiver);
		unregisterReceiver(exceptionReceiver);
		unregisterReceiver(notNeighborDeviceReceiver);
	}

	/**
	 * Add a debug log for the service application.
	 * 
	 * @param message
	 */
	public static void debugLog(String message) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, message);
		}
	}

}