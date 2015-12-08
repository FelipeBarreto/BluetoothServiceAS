package br.ufc.great.somc.network.bluetoothservice.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import br.ufc.great.somc.network.bluetoothservice.BluetoothNetworkManagerService;
import br.ufc.great.somc.network.bluetoothservice.BluetoothThreadListener;
import br.ufc.great.somc.network.bluetoothservice.Device.DeviceRole;
import br.ufc.great.somc.network.bluetoothservice.Neighborhood;
import br.ufc.great.somc.network.bluetoothservice.message.BluetoothMessage;
import br.ufc.great.somc.network.bluetoothservice.message.Bye.Reason;
import br.ufc.great.somc.network.bluetoothservice.message.HandShakeMessage;
import br.ufc.great.somc.network.bluetoothservice.message.Hello;

/**
 * A thread class that represent a Slave threads.
 * 
 * @author bruno
 */
public class SlaveConnectedThread extends ConnectedThread {

	public static final String TAG = SlaveConnectedThread.class.getSimpleName();

	private static HashMap<String, ArrayList<String>> possibleMaster = new HashMap<String, ArrayList<String>>();
	private static HashSet<String> masterList = new HashSet<String>();
	protected boolean handshake;
	private long elapsedTime;
	private String redundantLinkToDevice = null;

	public SlaveConnectedThread(final BluetoothSocket socket,
			final BluetoothThreadListener listener) {
		super(socket, listener);
		setName("SlaveThread - Master->" + getDestinationName());
		handshake = false;
		sendHello();
	}

	/**
	 * Sends a Hello message
	 */
	protected void sendHello() {
		Hello hello = new Hello();
		try {
			write(hello);
		} catch (IOException e) {
			Log.e(TAG, "Exception during SendHello - write", e);
		}

	}

	/**
	 * Inform is the handshake protocol was conclude with success
	 * 
	 * @return true if the Hello message was answered. Otherwise, returns false.
	 */
	public boolean receivedHandshake() {
		return handshake;
	}

	@Override
	protected boolean processMessage(BluetoothMessage message) {
		boolean caught = super.processMessage(message);
		if (caught)
			return true;

		switch (message.getType()) {
		case HELLO_REPLY:
			handshake = true;
			receiveHelloReply((HandShakeMessage) message);
			return true;

		default:
			notifier.exceptionHandler(BluetoothNetworkManagerService.UNKNOW_MESSAGE);
			break;
		}

		return false;
	}

	/**
	 * This method waits for all Hello reply message. Based on theses message it
	 * selects the devices such connection will continue on. It uses the
	 * algorithm a selectBestMaster. With the return of this algorithm, it
	 * select one devices in random way of each entry of the hashset returned.
	 * 
	 * @param message
	 *            A handshake message
	 */
	protected void receiveHelloReply(HandShakeMessage message) {
		populatePossibleMasters(message.getNeighborhood());
		boolean shouldDisconnect = true;
		try {
			sleep(BluetoothNetworkManagerService.TIMEOUT - elapsedTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		HashSet<String> masters = selectTheBestMaster();

		if (!masters.isEmpty()) {
			for (String master : masters) {
				if (master.equalsIgnoreCase(getDestinationMacAddress())) {
					shouldDisconnect = false;
				}
			}

			if (shouldDisconnect) {
				sendBye(Reason.MASTER_SELECTION);
			}
		}

	}

	/**
	 * Using the structure created bty the method populatePossibleMasters this
	 * method selects the masters in this way: For each entry of this structure
	 * it select in a random way one device. For example:
	 * 
	 * A -> d1, d2, d3 B -> d1, d3 C -> d2, d3
	 * 
	 * In this scenario, one of possible returns of this method could be d1, d2
	 * or d2, d3 and more.
	 * 
	 * @return A list of the selected masters
	 */
	private HashSet<String> selectTheBestMaster() {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> possibleMasterClone = new HashMap<String, ArrayList<String>>(
				possibleMaster.size());

		// REAL Cloning possibleMasters
		Set<String> possibleMastersMac = possibleMaster.keySet();
		Iterator<String> possibleMasterMacIterator = possibleMastersMac
				.iterator();
		while (possibleMasterMacIterator.hasNext()) {
			String masterMac = possibleMasterMacIterator.next();
			ArrayList<String> masterNeighbors = (ArrayList<String>) possibleMaster
					.get(masterMac).clone();
			possibleMasterClone.put(masterMac, masterNeighbors);
		}

		ArrayList<String> redundantLinks = removeRedundantDevices(possibleMasterClone);

		BluetoothNetworkManagerService.debugLog("masters("
				+ getDestinationName() + ") " + possibleMasterClone.toString());

		Set<String> neighborOfNeighborMAC = possibleMasterClone.keySet();

		int i = 0;
		while (!neighborOfNeighborMAC.isEmpty()
				&& masterList.size() < BluetoothNetworkManagerService.MIN_NEIGHBORS) {

			Iterator<String> it = neighborOfNeighborMAC.iterator();
			ArrayList<String> removableMasters = new ArrayList<String>();

			while (it.hasNext()) {
				String key = it.next();
				ArrayList<String> neighbors = possibleMasterClone.get(key);
				if (!neighbors.isEmpty()) {
					String possibleMasterMAC = neighbors.remove((int) (Math
							.floor((Math.random() * neighbors.size()))));
					if (getDestinationMacAddress().equalsIgnoreCase(
							possibleMasterMAC)) {
						setRedundantLinkToDevice(key);
						if (i > 0 || redundantLinks.contains(possibleMasterMAC)) {
							setRedundacy(true);
						}
					}
					masterList.add(possibleMasterMAC);
				} else {
					removableMasters.add(key);
				}
			}

			for (String key : removableMasters) {
				possibleMasterClone.remove(key);
			}
			i++;
		}
		return masterList;
	}

	/**
	 * Remove redundant devices to free some slot connection to new devices
	 * 
	 * @param possibleMasterClone
	 */
	private ArrayList<String> removeRedundantDevices(
			HashMap<String, ArrayList<String>> possibleMasterClone) {
		Set<String> neighboorAddress = possibleMasterClone.keySet();
		Iterator<String> it = neighboorAddress.iterator();
		ArrayList<String> redundantLinks = new ArrayList<String>();

		while (it.hasNext()) {
			String masterMac = it.next();
			for (String neighbor : masterList) {
				if (possibleMasterClone.get(masterMac).contains(neighbor)) {
					redundantLinks.add(masterMac);

					break;
				}
			}
		}

		if (BluetoothNetworkManagerService.getConnectedThreads().size() > BluetoothNetworkManagerService.MIN_NEIGHBORS) {
			for (String redundantMac : redundantLinks) {
				possibleMasterClone.remove(redundantMac);
			}
		}

		return redundantLinks;
	}

	@Override
	public DeviceRole getDeviceDestinationRole() {
		return DeviceRole.MASTER;
	}

	/**
	 * Create or update a list of possible Masters. To do this, it is looked for
	 * the neighbors of its neighbors. So, it create a structure like this:
	 * 
	 * A -> d1, d2, d3 B -> d1, d3 C -> d2, d3
	 * 
	 * Where A, B and C are neighbors of this device neighbors and d1, d2, and
	 * d3 are this device neighbors and the possible masters
	 * 
	 * @param neighborhood
	 *            The neighborhood of the connected device
	 */
	private void populatePossibleMasters(Neighborhood neighborhood) {
		if (neighborhood.getNumberOfNeighbors() == 1) {
			synchronized (possibleMaster) {
				ArrayList<String> neighbor = new ArrayList<String>();
				neighbor.add(getDestinationMacAddress());
				possibleMaster.put(getDestinationMacAddress(), neighbor);
			}
		} else {

			String myMac = getMyMacAddress();
			Set<String> neighborhoodAddress = neighborhood
					.getNeighborhoodAddresses();

			Iterator<String> it = neighborhoodAddress.iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (!key.equalsIgnoreCase(myMac)) {
					synchronized (possibleMaster) {
						if (possibleMaster.containsKey(key)) {
							possibleMaster.get(key).add(
									getDestinationMacAddress());
						} else {
							Neighborhood myNeighborhood = new Neighborhood();
							if (!myNeighborhood.isMyNeighbor(key)) {
								ArrayList<String> neighbor = new ArrayList<String>(
										1);
								neighbor.add(getDestinationMacAddress());
								possibleMaster.put(key, neighbor);
							}
						}
					}
				}
			}
		}

	}

	/**
	 * When a exception occur with a specific device, this method is called to
	 * remove the device of the list of possible Master.
	 * 
	 * @return
	 */
	private boolean removeElegibleMaster() {
		synchronized (possibleMaster) {
			Set<String> neighborOfNeighborMAC = possibleMaster.keySet();
			Iterator<String> it = neighborOfNeighborMAC.iterator();
			Set<String> removableNeighbors = new HashSet<String>();
			while (it.hasNext()) {
				String mac = it.next();
				ArrayList<String> commonNeighbors = possibleMaster.get(mac);
				if (commonNeighbors.remove((getDestinationMacAddress()))) {
					if (commonNeighbors.isEmpty()) {
						removableNeighbors.add(mac);
					}
				}
			}
			for (String neighbor : removableNeighbors) {
				possibleMaster.remove(neighbor);
			}

		}
		return false;
	}

	@Override
	protected void exceptionTreatment() {
		super.exceptionTreatment();
		removeElegibleMaster();
	}

	/**
	 * @param elapsedTime
	 *            Time elapsed since the handshake starts
	 */
	public void setElapsedTime(final long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	/**
	 * @return A device's mac address which there is a redundant path to reach
	 *         it
	 */
	public String getRedundantLinkToDevice() {
		return redundantLinkToDevice;
	}

	private void setRedundantLinkToDevice(final String redundantLinkToDevice) {
		this.redundantLinkToDevice = redundantLinkToDevice;
	}

	@Override
	public String toString() {
		return super.toString() + redundantLinkToDevice;
	}

}
