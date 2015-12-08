package br.ufc.great.somc.network.bluetoothservice;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import br.ufc.great.somc.network.bluetoothservice.Device.DeviceRole;
import br.ufc.great.somc.network.bluetoothservice.message.Bye.Reason;
import br.ufc.great.somc.network.bluetoothservice.message.Message;
import br.ufc.great.somc.network.bluetoothservice.threads.ConnectedThread;
import br.ufc.great.somc.network.bluetoothservice.threads.SlaveConnectedThread;

/**
 * Represent the structure responsible to store all connections.
 * 
 * @author bruno
 */
public class ConnectionsPool extends HashMap<String, ConnectedThread> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1657380106953800752L;
	private boolean hasRedundacy = false;

	/**
	 * Add a new connection
	 * 
	 * @param connectedThread
	 *            The connection thread
	 * @see ConnectedThread
	 */
	public synchronized boolean newConnection(
			final ConnectedThread connectedThread) {
		BluetoothNetworkManagerService.debugLog("---");
		ConnectedThread oldThread = get(connectedThread
				.getDestinationMacAddress());
		if (oldThread != null) {
			BluetoothNetworkManagerService.debugLog("Old - "
					+ oldThread.getName() + "alive?" + oldThread.isAlive());
			if (oldThread.isAlive()) {
				return false;
			}
			BluetoothNetworkManagerService.debugLog("Old - Closed");
			oldThread.closeConnection(Reason.ERROR);
		}

		BluetoothNetworkManagerService.debugLog("New - "
				+ connectedThread.getName());
		put(connectedThread.getDestinationMacAddress(), connectedThread);
		connectedThread.start();
		return true;

	}

	/**
	 * 
	 * @param connection
	 * @see ConnectedThread
	 */
	public synchronized void removeConnection(final ConnectedThread connection) {
		BluetoothNetworkManagerService.debugLog(toString() + " " + hasRedundacy
				+ " R");

		if (remove(connection)) {

			if (connection.getDeviceDestinationRole() == DeviceRole.MASTER) {
				SlaveConnectedThread slave = (SlaveConnectedThread) connection;
				if (!slave.isRedundant()
						&& slave.getRedundantLinkToDevice() != null) {
					String redundantDevice = slave.getRedundantLinkToDevice();

					Iterator<String> it = keySet().iterator();
					boolean foundRedundant = false;

					while (it.hasNext()) {
						String key = it.next();
						if (get(key).getDeviceDestinationRole() == DeviceRole.MASTER) {
							SlaveConnectedThread possibleRedundant = (SlaveConnectedThread) get(key);

							if ((!foundRedundant && possibleRedundant
									.getRedundantLinkToDevice() != null)
									&& possibleRedundant
											.getRedundantLinkToDevice()
											.equalsIgnoreCase(redundantDevice)) {
								possibleRedundant.setRedundacy(false);
								foundRedundant = true;
							}
						}
					}
				}
			}
			BluetoothNetworkManagerService.debugLog(toString() + " "
					+ hasRedundacy + " R");
		}

	}

	private boolean remove(final ConnectedThread deadConnection) {

		ConnectedThread storedThread = get(deadConnection
				.getDestinationMacAddress());
		if (storedThread != null
				&& deadConnection.getId() == storedThread.getId()) {
			return remove(deadConnection.getDestinationMacAddress()) == null ? false
					: true;
		}
		BluetoothNetworkManagerService.debugLog("Opa!!!!");
		return false;
	}

	/**
	 * If there are some redundant links connected, this method calculate the
	 * link with more alternatives paths and close it, to free a slot for a new
	 * connection.
	 * 
	 * @param deviceToConnect
	 *            number of devices to connect
	 */
	public void removeRudantLinks(int deviceToConnect) {

		while (hasRedundacy
				&& size() + deviceToConnect > BluetoothNetworkManagerService.MAX_BLUETOOTH_CONNECTION) {
			HashMap<String, SlaveConnectedThread> slaveRedundantThreadMap = new HashMap<String, SlaveConnectedThread>();
			HashMap<String, Integer> slaveRedundantCounter = new HashMap<String, Integer>();

			calculateRedundancyLevel(slaveRedundantThreadMap,
					slaveRedundantCounter);

			boolean flag = removeMajorRedundantLink(slaveRedundantThreadMap,
					slaveRedundantCounter);
			BluetoothNetworkManagerService
					.debugLog("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			BluetoothNetworkManagerService.debugLog(slaveRedundantThreadMap
					.toString());
			BluetoothNetworkManagerService.debugLog(slaveRedundantCounter
					.toString());

			if (!flag) {
				break;
			}

		}
	}

	/**
	 * This method calculate a link with more redundancy.
	 * 
	 * @param slaveRedundantThreadMap
	 *            HashSet to populate with which neighbor(key) it can reach
	 *            using the destination device of a ConnectedThread
	 * @param slaveRedundantCounter
	 *            HashSet to populate with the number of devices that reach a
	 *            specific device(key)
	 */
	public synchronized void calculateRedundancyLevel(
			HashMap<String, SlaveConnectedThread> slaveRedundantThreadMap,
			HashMap<String, Integer> slaveRedundantCounter) {

		Iterator<String> it = keySet().iterator();

		while (it.hasNext()) {
			String key = it.next();
			if (get(key).getDeviceDestinationRole() == DeviceRole.MASTER) {
				SlaveConnectedThread possibleRedundant = (SlaveConnectedThread) get(key);
				String redundantLink = possibleRedundant
						.getRedundantLinkToDevice();

				slaveRedundantThreadMap.put(redundantLink, possibleRedundant);
				int count = slaveRedundantCounter.get(redundantLink) == null ? 0
						: slaveRedundantCounter.get(possibleRedundant
								.getRedundantLinkToDevice());
				slaveRedundantCounter.put(redundantLink, ++count);

			}
		}
		BluetoothNetworkManagerService
				.debugLog("----------------------------------");
		BluetoothNetworkManagerService.debugLog(slaveRedundantThreadMap
				.toString());
		BluetoothNetworkManagerService.debugLog(slaveRedundantCounter
				.toString());

	}

	/**
	 * Remove a link with more redundancy redundancy.
	 * 
	 * @param slaveRedundantThreadMap
	 *            HashSet to populate with which neighbor(key) it can reach
	 *            using the destination device of a ConnectedThread
	 * @param slaveRedundantCounter
	 *            HashSet to populate with the number of devices that reach a
	 *            specific device(key)
	 */
	private boolean removeMajorRedundantLink(
			HashMap<String, SlaveConnectedThread> slaveRedundantThreadMap,
			HashMap<String, Integer> slaveRedundantCounter) {

		Iterator<String> it = slaveRedundantThreadMap.keySet().iterator();
		String moreRedundantKey = null;
		int majorRedundantValue = 0;
		while (it.hasNext()) {
			String key = it.next();
			if (slaveRedundantCounter.get(key) > majorRedundantValue) {
				majorRedundantValue = slaveRedundantCounter.get(key);
				moreRedundantKey = key;
			}
		}

		if (majorRedundantValue == 1) {
			hasRedundacy = false;
			return false;
		} else {
			SlaveConnectedThread redundantConnecton = slaveRedundantThreadMap
					.get(moreRedundantKey);
			if (redundantConnecton != null) {
				redundantConnecton.closeConnection(Reason.REDUNDANT);
			}
			slaveRedundantCounter.put(moreRedundantKey, --majorRedundantValue);
			return true;
		}
	}

	/**
	 * Cancel all connected threads
	 */
	public synchronized void cancelAllConnectedThreads() {
		Collection<ConnectedThread> threadCollection = values();
		Iterator<ConnectedThread> itr = threadCollection.iterator();

		while (itr.hasNext()) {
			itr.next().closeConnection(Reason.ALL_CANCELED);
		}
	}

	/**
	 * @return true, if there is redundant links. Otherwise, return false
	 */
	public boolean hasRedundacy() {
		return hasRedundacy;
	}

	/**
	 * @param hasRedundacy
	 *            true if there is at least one redundant link
	 */
	public synchronized void setHasRedundacy(boolean hasRedundacy) {
		BluetoothNetworkManagerService
				.debugLog(toString() + " " + hasRedundacy);
		this.hasRedundacy = this.hasRedundacy || hasRedundacy;
		BluetoothNetworkManagerService
				.debugLog(toString() + " " + hasRedundacy);
	}

	/**
	 * Send a broadcast message i.e. for all neighbors. If avoidAddress is
	 * specified, the device with this address will not receive this message.
	 * 
	 * @param msg
	 *            Message to be send
	 * @param avoidAddress
	 *            Device address which should not recevie message
	 */
	public synchronized void sendBrodcastMessage(Message msg,
			String avoidAddress) {
		ConnectedThread r;

		Collection<ConnectedThread> threadCollection = values();

		if (values().size() != 0) {
			Iterator<ConnectedThread> itr = threadCollection.iterator();
			while (itr.hasNext()) {
				// Synchronize a copy of the ConnectedThread

				r = itr.next();
				if (avoidAddress != null
						&& r.getDestinationMacAddress().equalsIgnoreCase(
								avoidAddress)) {
					continue;
				}

				// Perform the write unsynchronized
				try {
					r.write(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Send a message to specified destination
	 * 
	 * @param msg
	 *            Message to be send
	 * @see Message
	 */
	public synchronized void sendUnicastMessage(Message msg) {
		ConnectedThread r = get(msg.getDestinationMacAddress());

		if (r != null) {
			try {
				r.write(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
