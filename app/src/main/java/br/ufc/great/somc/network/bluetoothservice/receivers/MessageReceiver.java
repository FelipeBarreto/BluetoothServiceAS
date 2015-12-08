package br.ufc.great.somc.network.bluetoothservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import br.ufc.great.somc.network.base.BluetoothListener;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;

public class MessageReceiver extends BroadcastReceiver {

	/**
	 * Intent used to broadcast about message received/sent
	 */
	public static final String ACTION_BLUETOOTH_SERVICE_MESSAGE = "br.ufc.great.semc.network.base.MESSAGE";

	/**
	 * Extra used by {@link #ACTION_BLUETOOTH_SERVICE_MESSAGE}. This extra
	 * represent a message state.
	 */
	public static final String BLUETOOTH_SERVICE_MESSAGE_STATE = "br.ufc.great.semc.network.base.MESSAGE_STATE";

	/**
	 * Extra used by {@link #ACTION_BLUETOOTH_SERVICE_MESSAGE}. This extra
	 * contains a message content.
	 */
	public static final String BLUETOOTH_SERVICE_MESSAGE_CONTENT = "br.ufc.great.semc.network.base.MESSAGE_CONTENT";

	/**
	 * Indicates that a message has sent.
	 */
	public static final int BLUETOOTH_SERVICE_MESSAGE_SENT = 0;
	/**
	 * Indicates that a message has received.
	 */
	public static final int BLUETOOTH_SERVICE_MESSAGE_RECEIVED = 1;

	private BluetoothNetworkManagerService service;

	public MessageReceiver(BluetoothNetworkManagerService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int state = intent.getIntExtra(BLUETOOTH_SERVICE_MESSAGE_STATE, -1);
		String jsonMessage = intent
				.getStringExtra(BLUETOOTH_SERVICE_MESSAGE_CONTENT);

		RemoteCallbackList<BluetoothListener> callbackListeners = service
				.getCallbackListeners();

		synchronized (callbackListeners) {
			int i = callbackListeners.beginBroadcast();
			while (i > 0) {
				i--;
				try {
					switch (state) {
					case BLUETOOTH_SERVICE_MESSAGE_RECEIVED:
						callbackListeners.getBroadcastItem(i)
								.onMessageReceived(jsonMessage);
						break;

					case BLUETOOTH_SERVICE_MESSAGE_SENT:
						callbackListeners.getBroadcastItem(i).onMessageSent(
								jsonMessage);
						break;

					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			callbackListeners.finishBroadcast();
		}

	}
}
