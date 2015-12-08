package br.ufc.great.somc.network.bluetoothservice.threads;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;
import br.ufc.great.somc.network.bluetoothservice.BluetoothThreadListener;
import br.ufc.great.somc.network.bluetoothservice.InsecureBluetooth;

/**
 * The acceptThread is a thread responsible to listen for request of connection.
 * It is started by devices that intend to be a Master. Once the threads receive
 * the connection request, it tries to open a BluetoothSocket, without pair
 * devices. If it succeed, a MasterThread is created in this side of connection.
 * 
 * @author bruno
 */
public class AcceptThread extends ConectivityThread {
	// The local server socket
	private final BluetoothServerSocket mmServerSocket;

	private static final String TAG = AcceptThread.class.getSimpleName();

	/**
	 * Constructor responsible to create the object and start the listening for
	 * request connection. Important: This class tries to create just insecure
	 * bluetooth connection, without pair devices
	 * 
	 * @param bluetoothAdapter
	 * @param name
	 *            The application name
	 * @param uuid
	 *            Unique UUID for this application
	 * @param listener
	 *            Used to notify the bluetoothService about occurrences of
	 *            events
	 */
	public AcceptThread(String name, UUID uuid, BluetoothThreadListener listener) {
		super(listener);
		BluetoothServerSocket tmp = null;
		// Create a new listening server socket
		try {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
				tmp = this.bluetoothAdapter
						.listenUsingInsecureRfcommWithServiceRecord(name, uuid);
			} else {
				tmp = InsecureBluetooth.listenUsingRfcommWithServiceRecord(
						bluetoothAdapter, name, uuid, true);
			}
		} catch (IOException e) {
			Log.e(TAG, "listen() failed", e);
		}
		mmServerSocket = tmp;
	}

	public void run() {
		BluetoothNetworkManagerService.debugLog("BEGIN mAcceptThread" + this);
		setName("AcceptThread");
		BluetoothSocket socket = null;

		// Listen to the server socket if we're not connected
		while (true) {
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				socket = mmServerSocket.accept();
			} catch (IOException e) {
				Log.e(TAG, "accept() failed", e);
				break;
			}

			// If a connection was accepted
			if (socket != null) {
				connected(socket);
			}

		}
		BluetoothNetworkManagerService.debugLog("END mAcceptThread");
	}

	/**
	 * Close the server socket
	 */
	public void cancel() {
		BluetoothNetworkManagerService.debugLog("cancel " + this);

		try {
			mmServerSocket.close();
		} catch (IOException e) {
			Log.e(TAG, "close() of server failed", e);
		}
	}

	/**
	 * Once the request of connection is accepted, this method create the
	 * MasterConnectedThread
	 * 
	 * @param socket
	 *            The socket of the accept connection
	 */
	public synchronized void connected(BluetoothSocket socket) {
		ConnectedThread connectedThread = new MasterConnectedThread(socket,
				notifier);
		if (BluetoothNetworkManagerService.addConnectedThread(connectedThread)) {
			BluetoothDevice remoteDevice = socket.getRemoteDevice();
			notifier.onDeviceConnect(remoteDevice.getName(),
					remoteDevice.getAddress());
		}

	}
}