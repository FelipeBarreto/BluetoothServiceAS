package br.ufc.great.somc.network.bluetoothservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import br.ufc.great.somc.network.base.BluetoothListener;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;

public class NotNeighborDeviceReceiver extends BroadcastReceiver {

	/**
	 * Intent used to broacast a exception occurrence
	 */
	public static final String ACTION_BLUETOOTH_SERVICE_NOT_NEIGHBOR_FOUND = "br.ufc.great.semc.network.base.BluetoothNetworkManagerService.NOT_NEIGHBOR_FOUND";

	private BluetoothNetworkManagerService service;

	public NotNeighborDeviceReceiver(BluetoothNetworkManagerService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String deviceName = intent
				.getStringExtra(BluetoothNetworkManagerService.BLUETOOTH_SERVICE_DEVICE_NAME);
		String deviceAddress = intent
				.getStringExtra(BluetoothNetworkManagerService.BLUETOOTH_SERVICE_DEVICE_ADDRESS);

		RemoteCallbackList<BluetoothListener> callbackListeners = service
				.getCallbackListeners();

		synchronized (callbackListeners) {
			int i = callbackListeners.beginBroadcast();
			while (i > 0) {
				i--;
				try {
					callbackListeners
							.getBroadcastItem(i)
							.onNotNeighborDeviceFound(deviceName, deviceAddress);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			callbackListeners.finishBroadcast();

		}
	}

}
