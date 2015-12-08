package br.ufc.great.somc.network.bluetoothservice.threads;

import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;
import br.ufc.great.somc.network.bluetoothservice.message.Bye.Reason;

/**
 * The Timer class allows a graceful exit when an application is stalled due to
 * a networking timeout. Once the timer is set, it must be cleared via the
 * reset() method, or the timeout() method is called.
 * <p>
 * The timeout length is customizable, by changing the 'length' property, or
 * through the constructor. The length represents the length of the timer in
 * milliseconds.
 * 
 * @author David Reilly
 */
public class Timer extends Thread {
	/** Rate at which timer is checked */
	protected int checkRate = 100;

	/** Length of timeout */
	private int timeoutLength;

	/** Time elapsed */
	private int timeElapsed;

	private SlaveConnectedThread mThread;

	private boolean hasReceivedHandshake = false;

	private BluetoothNetworkManagerService deviceManager;

	/**
	 * Creates a timer of a specified length
	 * 
	 * @param length
	 *            Amount of time before timeout occurs
	 * @param thread
	 *            SlaveThread to be monitored and has its socket closed when a
	 *            timout occurs
	 * @param deviceManager
	 *            Used to release its barrier that is responsible to concurrency
	 *            control
	 */
	public Timer(int length, SlaveConnectedThread thread,
			BluetoothNetworkManagerService deviceManager) {
		// Assign to member variable
		timeoutLength = length;

		// Set time elapsed
		timeElapsed = 0;

		// Thread do be watched
		mThread = thread;

		this.deviceManager = deviceManager;

	}

	/** Resets the timer back to zero */
	public synchronized void reset() {
		timeElapsed = 0;
	}

	/** Performs timer specific code */
	public void run() {
		// Keep looping

		while (!mThread.receivedHandshake()) {
			// Put the timer to sleep
			try {
				sleep(checkRate);
			} catch (InterruptedException ioe) {
				break;
			}
			if (isInterrupted()) {
				break;
			}

			// Use 'synchronized' to prevent conflicts
			synchronized (this) {
				// Increment time remaining
				timeElapsed += checkRate;

				// Check to see if the time has been exceeded
				if (timeElapsed > timeoutLength) {
					// Trigger a timeout
					timeout();
					break;
				}

			}
			hasReceivedHandshake = mThread.receivedHandshake();
		}

		mThread.setElapsedTime(timeElapsed);
		deviceManager.releaseBarrier();
	}

	/**
	 * When a timeout occurs, release a barrier and notify thread about timeout
	 * occurrence
	 */
	private void timeout() {
		hasReceivedHandshake = false;
		mThread.setTimoutOcurred();
		mThread.closeConnection(Reason.TIMEOUT);
		interrupt();
		BluetoothNetworkManagerService
				.debugLog(" Network timeout occurred.... terminating "
						+ timeElapsed);

	}

	/**
	 * @return true, if the SlaveThread has received a handshake. Otherwise,
	 *         return false
	 */
	public boolean hasReceivedHandshake() {
		return hasReceivedHandshake;
	}

}