package br.ufc.great.somc.network.bluetoothservice.threads;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;
import br.ufc.great.somc.network.bluetoothservice.BluetoothThreadListener;
import br.ufc.great.somc.network.bluetoothservice.Device.DeviceRole;
import br.ufc.great.somc.network.bluetoothservice.message.BluetoothMessage;
import br.ufc.great.somc.network.bluetoothservice.message.Hello;
import br.ufc.great.somc.network.bluetoothservice.message.HelloReply;

/**
 * A threa class that represent a Master Thread. It is responsible first of all,
 * to wait for Hello message, and as soon as possible, response with a Hello
 * Reply.
 * 
 * @author bruno
 */
public class MasterConnectedThread extends ConnectedThread {
	
	private static final String TAG = MasterConnectedThread.class.getSimpleName();

	public MasterConnectedThread(final BluetoothSocket socket,
			final BluetoothThreadListener listener) {
		super(socket, listener);
		setName("MasterThread - Slave->" + getDestinationName());
	}

	/**
	 * Sends a hello reply message
	 */
	protected void sendHelloReply() {
		HelloReply reply = new HelloReply();

		try {
			write(reply);
		} catch (IOException e) {
			Log.e(TAG, "Exception during SendHelloReply - write", e);
		}
	}

	@Override
	protected boolean processMessage(BluetoothMessage message) {
		boolean caught = super.processMessage(message);
		if (caught)
			return true;

		switch (message.getType()) {
		case HELLO:
			receiveHello((Hello) message);
			sendHelloReply();
			return true;

		default:
			notifier.exceptionHandler(BluetoothNetworkManagerService.UNKNOW_MESSAGE);
			break;
		}
		return false;
	}

	private void receiveHello(Hello message) {
		notifier.onNotNeighborDeviceFound(message.getNeighborhood());
	}

	@Override
	public DeviceRole getDeviceDestinationRole() {
		return DeviceRole.SLAVE;
	}

}
