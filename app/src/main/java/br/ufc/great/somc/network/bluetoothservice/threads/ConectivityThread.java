package br.ufc.great.somc.network.bluetoothservice.threads;

import android.bluetooth.BluetoothAdapter;
import br.ufc.great.somc.network.bluetoothservice.BluetoothThreadListener;

/**
 * A super class of all bluetooth threads that perform some connectivity
 * function. It contains all method related a send message among the bluetooth
 * threads and the device manager.
 * 
 * @author bruno
 */
public abstract class ConectivityThread extends Thread {

	protected BluetoothAdapter bluetoothAdapter;
	protected BluetoothThreadListener notifier;

	/**
	 * @param listener
	 *            Used to notify the bluetoothService about occurrences of
	 *            events
	 * @param bluetoothAdapter
	 *            The bluetooth adapter of the device
	 */
	public ConectivityThread(BluetoothThreadListener listener) {
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.notifier = listener;
	}
}
