package br.ufc.great.somc.network.bluetoothservice.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import br.ufc.great.somc.network.base.BluetoothListener;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;

public class StateDiscoveryReceiver extends BroadcastReceiver {

	private BluetoothNetworkManagerService service;

	public StateDiscoveryReceiver(BluetoothNetworkManagerService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			service.connect();
		}

		RemoteCallbackList<BluetoothListener> callbackListeners = service
				.getCallbackListeners();

		synchronized (callbackListeners) {
			int i = callbackListeners.beginBroadcast();
			while (i > 0) {
				i--;
				try {
					callbackListeners.getBroadcastItem(i)
							.onStateDiscoveryChanged(action);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			callbackListeners.finishBroadcast();
		}
	}
}
