package br.ufc.great.somc.network.bluetoothservice.threads;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;
import br.ufc.great.somc.network.bluetoothservice.BluetoothThreadListener;
import br.ufc.great.somc.network.bluetoothservice.Device.DeviceRole;
import br.ufc.great.somc.network.bluetoothservice.message.BluetoothMessage;
import br.ufc.great.somc.network.bluetoothservice.message.Bye;
import br.ufc.great.somc.network.bluetoothservice.message.Bye.Reason;
import br.ufc.great.somc.network.bluetoothservice.message.Message;
import br.ufc.great.somc.network.bluetoothservice.message.NeighborhoodRequest;

/**
 * A super class of all bluetooth threads connected. It is responsible to
 * implement the basic creation process. It implements the mechanism that stays
 * listening for bluetooth message and it threat some types of bluetooth
 * message.
 * 
 * @author bruno
 */
public abstract class ConnectedThread extends ConectivityThread {
	protected static final String TIMOUT_MSG = "Disconnected by timeout";
	protected static final String LOST_CONNECTION_MSG = "Device connection was lost";
	private final BluetoothSocket mmSocket;
	protected long lastMessageTime;
	protected ObjectOutputStream objOutStream;
	protected ObjectInputStream objInputStream;
	private boolean timeout = false;
	private boolean redundacy = false;

	protected static final String TAG = ConnectedThread.class.getSimpleName();

	/**
	 * Build the object and gets the ObjectInputStream and ObjectOutputStream of
	 * this socket
	 * 
	 * @param socket
	 *            The socket of the accepted connection
	 * @param listener
	 *            Used to notify the bluetoothService about occurrences of
	 *            events
	 */
	public ConnectedThread(BluetoothSocket socket,
			BluetoothThreadListener listener) {
		super(listener);
		BluetoothNetworkManagerService.debugLog("create ConnectedThread");

		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the BluetoothSocket input and output streams
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
			objOutStream = new ObjectOutputStream(tmpOut);
			objOutStream.writeInt(1);
			objOutStream.flush();
			objInputStream = new ObjectInputStream(tmpIn);
			objInputStream.readInt();
		} catch (IOException e) {
			Log.e(TAG, "temp sockets not created", e);
		}
	}

	public void run() {
		BluetoothNetworkManagerService.debugLog("BEGIN mConnectedThread");
		// Keep listening to the InputStream while connected
		while ((objInputStream != null && objOutStream != null)) {
			try {
				Object obj = objInputStream.readObject();
				if (obj instanceof BluetoothMessage) {
					BluetoothMessage message = (BluetoothMessage) obj;
					processMessage(message);
				} else {
					Log.e("Cast", "Message type: " + obj.toString());
				}
			} catch (IOException e) {
				Log.e(TAG, "disconnected", e);
				exceptionTreatment();
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends the Bye message before closes the socket connection
	 * 
	 * @param reason
	 *            Reason that this connection will be closed
	 */
	public void closeConnection(Reason reason) {
		try {
			sendBye(reason);
			mmSocket.close();
		} catch (IOException e) {
			Log.e(TAG, "close() of connect socket failed", e);
		}
	}

	/**
	 * Closes the socket
	 */
	protected void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			Log.e(TAG, "close() of connect socket failed", e);
		}
	}

	/**
	 * Sends a message of Neighborhood request. This message is populated with
	 * the device neighborhood before it be sent
	 */
	protected void sendNeighborhood() {
		NeighborhoodRequest neighborhoodRequest = new NeighborhoodRequest();

		try {
			write(neighborhoodRequest);
		} catch (IOException e) {
			Log.e(TAG, "Exception during SendNeighborhood - write", e);
		}
	}

	/**
	 * Sends a Bye message
	 */
	protected void sendBye(Reason reason) {
		try {
			Bye bye = new Bye(reason);
			write(bye);
		} catch (IOException e) {
			Log.e(TAG, "Exception during SendBye - write", e);
		}
	}

	/**
	 * Gets the MAC address of the device that this devices is connected
	 * 
	 * @return A string that represent the MAC address
	 */
	public String getDestinationMacAddress() {
		return mmSocket.getRemoteDevice().getAddress();
	}

	/**
	 * Gets the name of the device that this devices is connected
	 * 
	 * @return A string that represent the name
	 */
	public String getDestinationName() {
		return mmSocket.getRemoteDevice().getName();
	}

	/**
	 * Gets the MAC address of this device
	 * 
	 * @return A string that represent the MAC address
	 */
	protected String getMyMacAddress() {
		return bluetoothAdapter.getAddress();
	}

	/**
	 * Gets the MAC address of this device
	 * 
	 * @return A string that represent the MAC address
	 */
	protected String getMyDeviceName() {
		return bluetoothAdapter.getName();
	}

	/**
	 * Set the flag that indicates if the timeout has occurred
	 */
	public void setTimoutOcurred() {
	}

	/**
	 * @return if this thread is a redundant link
	 */
	public boolean isRedundant() {
		return redundacy;
	}

	/**
	 * Set this thread as redundant or not
	 * 
	 * @param redundacy
	 */
	public void setRedundacy(boolean redundacy) {
		this.redundacy = redundacy;
		BluetoothNetworkManagerService.setHasRedundacy(redundacy);
	}

	/**
	 * @param message
	 *            The bluetooth message to be sent
	 * @throws IOException
	 *             if a error occur while writing to the target stream
	 */
	public void write(BluetoothMessage message) throws IOException {
		if (objOutStream != null) {
			BluetoothNetworkManagerService.debugLog("Enviando -> "
					+ message.toString());
			message.setSourceAddress(getMyMacAddress());
			message.setSourceName(getMyDeviceName());

			objOutStream.writeObject(message);
		}
	}

	/**
	 * @param message
	 *            The message to be sent
	 * @throws IOException
	 *             if a error occur while writing to the target stream
	 */
	public void write(Message message) throws IOException {
		BluetoothNetworkManagerService.debugLog("Enviando -> "
				+ message.toString());
		message.setSourceAddress(getMyMacAddress());
		message.setSourceName(getMyDeviceName());
		sendAndNotifyMessage(message);
		if (objOutStream != null) {
			objOutStream.writeObject(message);
		}
	}

	/**
	 * Process a bluetooth message
	 * 
	 * @param message
	 *            The message to be processed
	 * @return true if the message was treated. Otherwise, false
	 */
	protected boolean processMessage(BluetoothMessage message) {
		long timestamp = System.currentTimeMillis();
		lastMessageTime = timestamp;
		BluetoothNetworkManagerService.debugLog("process message "
				+ message.toString());

		switch (message.getType()) {

		case BYE:
			cancel();
			return true;
		case MSG:
			receiveAndNotifyMessage((Message) message);
		case NEIGHBORHOOD_REQUEST:
			return true;

		default:
			break;
		}

		return false;
	}

	protected void sendAndNotifyMessage(Message message) {
		notifier.onMessageSent(message.getContent());
	}

	protected void receiveAndNotifyMessage(Message message) {
		notifier.onMessageReceived(message.getContent());
	}

	/**
	 * @return What kind of role the device has, MASTER or SLAVE
	 */
	public abstract DeviceRole getDeviceDestinationRole();

	/**
	 * Treats some exception, Timeout exception and Connection lost, and notify
	 * the device manager to remove this connection
	 */
	protected void exceptionTreatment() {
		BluetoothNetworkManagerService.removeConnectedThread(this);
		notifier.onDeviceDisconnect(getDestinationName(),
				getDestinationMacAddress());
		if (timeout) {
			notifier.exceptionHandler(BluetoothNetworkManagerService.TIME_OUT);
		} else {
			notifier.exceptionHandler(BluetoothNetworkManagerService.CONNECTION_LOST);
		}
		timeout = true;
	}

	@Override
	public String toString() {
		return getDestinationName() + " " + redundacy + " ";
	}

}