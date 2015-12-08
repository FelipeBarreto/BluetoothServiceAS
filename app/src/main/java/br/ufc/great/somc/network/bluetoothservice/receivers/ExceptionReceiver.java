package br.ufc.great.somc.network.bluetoothservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import br.ufc.great.somc.network.base.BluetoothListener;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;

public class ExceptionReceiver extends BroadcastReceiver {

	/**
	 * Intent used to broacast a exception occurrence
	 */
	public static final String ACTION_BLUETOOTH_SERVICE_EXCEPTION_OCCURRED = "br.ufc.great.semc.network.base.EXCEPTION_OCCURRED";

	/**
	 * Extra used by {@link #ACTION_BLUETOOTH_SERVICE_EXCEPTION_OCCURRED}. This
	 * contains a exception name.
	 */
	public static final String BLUETOOTH_SERVICE_EXCEPTION_NAME = "br.ufc.great.semc.network.base.EXCEPTION_NAME";
	/**
	 * Extra used by {@link #ACTION_BLUETOOTH_SERVICE_EXCEPTION_OCCURRED}. This
	 * contains a exception message.
	 */

	public static final String BLUETOOTH_SERVICE_EXCEPTION_MESSAGE = "br.ufc.great.semc.network.base.EXCEPTION_MESSAGE";

	private BluetoothNetworkManagerService service;

	public ExceptionReceiver(BluetoothNetworkManagerService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String message = intent
				.getStringExtra(BLUETOOTH_SERVICE_EXCEPTION_MESSAGE);
		int code = intent.getIntExtra(BLUETOOTH_SERVICE_EXCEPTION_NAME, -1);

		RemoteCallbackList<BluetoothListener> callbackListeners = service
				.getCallbackListeners();

		synchronized (callbackListeners) {
			int i = callbackListeners.beginBroadcast();
			while (i > 0) {
				i--;
				try {
					callbackListeners.getBroadcastItem(i).onExceptionOcurred(
							code, message);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			callbackListeners.finishBroadcast();

		}
	}

}
