package br.ufc.great.somc.network.bluetoothservice.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import br.ufc.great.somc.network.base.BluetoothListener;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;

public class StateScanModeReceiver extends BroadcastReceiver {

	private BluetoothNetworkManagerService service;

	public StateScanModeReceiver(BluetoothNetworkManagerService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);
		int previousState = intent.getIntExtra(
				BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, -1);

		RemoteCallbackList<BluetoothListener> callbackListeners = service
				.getCallbackListeners();

		synchronized (callbackListeners) {
			int i = callbackListeners.beginBroadcast();
			while (i > 0) {
				i--;
				try {
					callbackListeners.getBroadcastItem(i)
							.onStateScanModeChanged(state, previousState);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			callbackListeners.finishBroadcast();
		}
	}
}
