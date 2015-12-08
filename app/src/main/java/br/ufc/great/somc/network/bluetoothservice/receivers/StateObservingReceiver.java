package br.ufc.great.somc.network.bluetoothservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import br.ufc.great.somc.network.base.BluetoothListener;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;

public class StateObservingReceiver extends BroadcastReceiver {

	/**
	 * Intent used to broadcast the time which the service is observing/waiting
	 * to star a new discovery
	 */
	public static final String ACTION_BLUETOOTH_SERVICE_OBSERVING_TIME = "br.ufc.great.semc.network.base.BluetoothNetworkManagerService.OBSERVING_TIME";

	/**
	 * Extra used by {@link #ACTION_BLUETOOTH_SERVICE_OBSERVING_TIME
	 * ACTION_BLUETOOTH_SERVICE_OBSERVING_TIME}. This extra represents current
	 * observer state.
	 */
	public static final String BLUETOOTH_SERVICE_OBSERVING_STATE = "br.ufc.great.semc.network.base.BluetoothNetworkManagerService.OBSERVING_STATE";
	/**
	 * Extra used by {@link #ACTION_BLUETOOTH_SERVICE_OBSERVING_TIME}. This
	 * extra represents the amount of time of the observing procedure.
	 */
	public static final String BLUETOOTH_SERVICE_OBSERVING_EXTRA = "br.ufc.great.semc.network.base.BluetoothNetworkManagerService.OBSERVING_EXTRA";

	private BluetoothNetworkManagerService service;

	public StateObservingReceiver(BluetoothNetworkManagerService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int state = intent.getIntExtra(BLUETOOTH_SERVICE_OBSERVING_STATE, -1);
		int previousState = intent.getIntExtra(
				BLUETOOTH_SERVICE_OBSERVING_EXTRA, -1);

		RemoteCallbackList<BluetoothListener> callbackListeners = service
				.getCallbackListeners();

		synchronized (callbackListeners) {
			int i = callbackListeners.beginBroadcast();
			while (i > 0) {
				i--;
				try {
					callbackListeners.getBroadcastItem(i)
							.onStateObservingChanged(state, previousState);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			callbackListeners.finishBroadcast();
		}
	}
}
