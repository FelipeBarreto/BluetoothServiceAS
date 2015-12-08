package br.ufc.great.somc.network.bluetoothservice.threads;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;
import br.ufc.great.somc.network.bluetoothservice.BluetoothThreadListener;
import br.ufc.great.somc.network.bluetoothservice.BuildConfig;
import br.ufc.great.somc.network.bluetoothservice.InsecureBluetooth;
import br.ufc.great.somc.network.bluetoothservice.Neighborhood;

/**
 * A thread responsible to try to establish connection with Master devices.
 * Important: All attempt to create a connection will use the insecure bluetooth
 * connection, withou pair devices
 * 
 * @author bruno
 */
public class ConnectThread extends ConectivityThread {

	protected final UUID uuid;
	protected final Set<BluetoothDevice> mmDevices;
	protected BluetoothSocket mmSocket;
	private boolean shouldSleep = false;
	private int sleepTime = BluetoothNetworkManagerService.TIMEOUT;

	private static final String TAG = ConnectThread.class.getSimpleName();

	/**
	 * @param bluetoothAdapter
	 * @param devices
	 *            A list of possible Masters devices
	 * @param uuid
	 *            Unique UUID for this application
	 * @param listener
	 *            Used to notify the bluetoothService about occurrences of
	 *            events
	 */
	public ConnectThread(Set<BluetoothDevice> devices, UUID uuid,
			BluetoothThreadListener listener) {
		super(listener);
		mmDevices = devices;
		this.uuid = uuid;
	}

	/**
	 * 
	 * @param devices
	 *            A list of possible Masters devices
	 * @param uuid
	 *            Unique UUID for this application
	 * @param listener
	 *            Used to notify the bluetoothService about occurrences of
	 *            events
	 * @param sleep
	 *            Define if the thread should sleep berofe try to connect to
	 *            avoid "colision" device manager
	 */
	public ConnectThread(Set<BluetoothDevice> devices, UUID uuid,
			BluetoothThreadListener listener, boolean sleep) {
		super(listener);
		mmDevices = devices;
		this.uuid = uuid;
		shouldSleep = sleep;
	}

	public void run() {
		BluetoothNetworkManagerService.debugLog("BEGIN mConnectThread");
		setName("ConnectThread");
		// Always cancel discovery because it will slow down a connection
		bluetoothAdapter.cancelDiscovery();

		for (BluetoothDevice device : mmDevices) {
			mmSocket = null;
			Neighborhood neighborhood = new Neighborhood();
			if ((neighborhood.getNumberOfNeighbors() + mmDevices.size()) < BluetoothNetworkManagerService.MAX_BLUETOOTH_CONNECTION) {
				if (!neighborhood.isMyNeighbor(device.getAddress())) {
					// Get a BluetoothSocket for a connection with the
					// given BluetoothDevice
					if (shouldSleep
							&& device.getAddress().compareTo(
									bluetoothAdapter.getName()) >= 0) {
						try {
							if (BuildConfig.DEBUG) {
								BluetoothNetworkManagerService
										.debugLog("Domir " + sleepTime
												+ " milisegundos");
							}

							sleep(sleepTime);
							if (sleepTime > 1000) {
								sleepTime -= 1000;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					try {
						createRfcommSocket(device);
					} catch (IOException e) {
						Log.e(TAG, "create() failed", e);
					}

					// Make a connection to the BluetoothSocket
					try {
						// This is a blocking call and will only return on a
						// successful connection or an exception
						mmSocket.connect();
						BluetoothNetworkManagerService
								.addConnectionSocket(mmSocket);
						mmSocket = null;
					} catch (IOException e) {
						notifier.exceptionHandler(BluetoothNetworkManagerService.UNABLE_TO_CONNECT);
						// Close the socket
						try {
							mmSocket.close();
						} catch (IOException e2) {
							Log.e(TAG,
									"unable to close() socket during connection failure",
									e2);
						}
					}
				}
			}
		}
	}

	/**
	 * Create a insecureRfcommSocket independently of the Android version
	 * 
	 * @param device
	 * @throws IOException
	 *             on error, for example Bluetooth not available, or
	 *             insufficient permissions
	 */
	protected void createRfcommSocket(BluetoothDevice device)
			throws IOException {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
			mmSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
		} else {
			createInsecureRfcommSocket(device);
		}
	}

	/**
	 * Create a insecureRfcommSocket for devices with Android version before 2.3
	 * 
	 * @param device
	 * @throws IOException
	 *             on error, for example Bluetooth not available, or
	 *             insufficient permissions
	 */
	protected void createInsecureRfcommSocket(BluetoothDevice device)
			throws IOException {
		mmSocket = InsecureBluetooth.createRfcommSocketToServiceRecord(device,
				uuid, true);
	}

	/**
	 * Close the connection
	 * 
	 */
	public void cancel() {
		notifier.exceptionHandler(BluetoothNetworkManagerService.UNABLE_TO_CONNECT);
		if (mmSocket != null) {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

}