package br.ufc.great.somc.network.bluetoothservice;

import br.ufc.great.somc.network.bluetoothservice.threads.AcceptThread;
import br.ufc.great.somc.network.bluetoothservice.threads.ConnectThread;
import br.ufc.great.somc.network.bluetoothservice.threads.ConnectedThread;

/**
 * @author bruno A listener implemented by
 *         {@link BluetoothNetworkManagerService} to provide communication among
 *         {@link BluetoothNetworkManagerService} and bluetooth threads.
 * 
 * @see AcceptThread
 * @see ConnectThread
 * @see ConnectedThread
 * 
 */
public interface BluetoothThreadListener {

	public static final String BLUETOOTH_MESSAGE_SOURCE_NAME = "souce_name";
	public static final String BLUETOOTH_MESSAGE_SOURCE_ADDRESS = "source_address";
	public static final String BLUETOOTH_MESSAGE_DESTINATION = "next_hop";
	public static final String BLUETOOTH_MESSAGE_NEXT_HOP = "next_hop";
	public static final String BLUETOOTH_MESSAGE_CONTENT = "content";

	/**
	 * Exception code
	 */
	public static final int UNABLE_TO_CONNECT = 7;
	/**
	 * Exception code
	 */
	public static final int STATE_CHANGED = 8;
	/**
	 * Exception code
	 */
	public static final int CONNECTION_LOST = 9;
	/**
	 * Exception code
	 */
	public static final int TIME_OUT = 10;
	/**
	 * Exception code
	 */
	public static final int UNKNOW_MESSAGE = 11;

	/**
	 * Called when a message is sent
	 * 
	 * @param message
	 */
	void onMessageSent(String jsonMessage);

	/**
	 * Called when a message is received
	 * 
	 * @param message
	 */
	void onMessageReceived(String jsonMessage);

	/**
	 * Called when a device is disconnected
	 * 
	 * @param name
	 *            device name
	 * @param address
	 *            device address
	 */
	void onDeviceDisconnect(String name, String address);

	/**
	 * Called when a device connects
	 * 
	 * @param name
	 *            device name
	 * @param address
	 *            device address
	 */
	void onDeviceConnect(String name, String address);

	/**
	 * Called when a exception occurs
	 * 
	 * @param exception
	 *            Exception code
	 */
	void exceptionHandler(int exception);

	/**
	 * Called when this device receives a helloReply
	 * 
	 * @param neighborhood
	 *            neighborhood of a neighbor connected node
	 */
	void onNotNeighborDeviceFound(Neighborhood neighborhood);

}
