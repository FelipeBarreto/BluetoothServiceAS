package br.ufc.great.somc.network.bluetoothservice.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import br.ufc.great.somc.network.base.BluetoothListener;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;

public class DeviceFoundReceiver extends BroadcastReceiver {

	private BluetoothNetworkManagerService service;

	public DeviceFoundReceiver(BluetoothNetworkManagerService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			service.addDevice(device);
			
			RemoteCallbackList<BluetoothListener> callbackListeners = service
					.getCallbackListeners();
			
			synchronized (callbackListeners) {
				int i = callbackListeners.beginBroadcast();
				while (i > 0) {
					i--;
					try {
						callbackListeners.getBroadcastItem(i).onDeviceFound(
								device.getName(), device.getAddress());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				callbackListeners.finishBroadcast();
			}
		}
	}

}
