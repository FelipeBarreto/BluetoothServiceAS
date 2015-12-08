package br.ufc.great.somc.network.bluetoothservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import br.ufc.great.somc.network.base.BluetoothListener;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;

public class DevicesConnectionReceiver extends BroadcastReceiver {

	/**
	 * Intent used to broadcast a updates aboout a device connection
	 */
	public static final String ACTION_BLUETOOTH_SERVICE_DEVICE_CONNECTION = "br.ufc.great.semc.network.base.BluetoothNetworkManagerService.DEVICE_CONNECTION";

	/**
	 * Extra used by {@link #ACTION_BLUETOOTH_SERVICE_DEVICE_CONNECTION}. This
	 * extra represents a connection state.
	 */
	public static final String BLUETOOTH_SERVICE_DEVICE_CONNECTION_STATE = "br.ufc.great.semc.network.base.BluetoothNetworkManagerService.DEVICE_CONNECTION_STATE";

	/**
	 * Indicates a new connection with a device
	 */
	public static final int BLUETOOTH_SERVICE_DEVICE_CONNECTED = 0;
	/**
	 * Indicates a desconnection with a device
	 */
	public static final int BLUETOOTH_SERVICE_DEVICE_DISCONNECTED = 1;

	private BluetoothNetworkManagerService service;

	public DevicesConnectionReceiver(BluetoothNetworkManagerService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int state = intent.getIntExtra(
				BLUETOOTH_SERVICE_DEVICE_CONNECTION_STATE, -1);
		String name = intent
				.getStringExtra(BluetoothNetworkManagerService.BLUETOOTH_SERVICE_DEVICE_NAME);
		String address = intent
				.getStringExtra(BluetoothNetworkManagerService.BLUETOOTH_SERVICE_DEVICE_ADDRESS);

		RemoteCallbackList<BluetoothListener> callbackListeners = service
				.getCallbackListeners();

		synchronized (callbackListeners) {
			int i = callbackListeners.beginBroadcast();
			while (i > 0) {
				i--;
				try {
					switch (state) {
					case BLUETOOTH_SERVICE_DEVICE_CONNECTED:
						callbackListeners.getBroadcastItem(i).onDeviceConnect(
								name, address);
						break;

					case BLUETOOTH_SERVICE_DEVICE_DISCONNECTED:
						callbackListeners.getBroadcastItem(i)
								.onDeviceDisconnect(name, address);
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
