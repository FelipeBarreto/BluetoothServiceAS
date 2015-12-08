package br.ufc.great.somc.network.bluetoothservice;

import android.bluetooth.BluetoothAdapter;

public class ObserverThread extends Thread {

	private BluetoothAdapter mBluetoothAdapter;
	private int waitTime = BluetoothNetworkManagerService.MIN_WAIT_TIME;
	private boolean observing;

	public ObserverThread() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		observing = true;
	}

	@Override
	public void run() {
		while (observing) {
			if (isInterrupted()) {
				break;
			}
			if (!mBluetoothAdapter.isDiscovering()) {
				mBluetoothAdapter.startDiscovery();
				BluetoothNetworkManagerService.debugLog("start discovery...");
			}
			try {
				BluetoothNetworkManagerService.debugLog("waiting...");
				sleep(waitTime);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public void incrementWaitTime() {
		waitTime = 5 * BluetoothNetworkManagerService.MIN_WAIT_TIME;
	}

	public void resetWaitTime() {
		waitTime = BluetoothNetworkManagerService.MIN_WAIT_TIME;
	}

	public int getWaitTime() {
		return waitTime;
	}

}
