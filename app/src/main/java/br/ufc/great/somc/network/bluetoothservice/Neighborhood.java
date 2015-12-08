package br.ufc.great.somc.network.bluetoothservice;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * A class that represent the list of neighbors of a specific device. Instances
 * of this class can be sent in bluetooth messages.
 * 
 * @author bruno
 */
public class Neighborhood implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8421522457892231753L;

	private HashMap<String, Device> neighborhood;

	/**
	 * Get a neighborhood of a bluetooth device
	 */
	public Neighborhood() {
		super();
		this.neighborhood = BluetoothNetworkManagerService.getNeighborhood();
	}

	/**
	 * @return bluetooth neigborhood
	 */
	public HashMap<String, Device> getNeighborhood() {
		return neighborhood;
	}

	/**
	 * @param neighbor
	 *            bluetooth neighborhood
	 */
	public void setNeighborhood(HashMap<String, Device> neighbor) {
		this.neighborhood = neighbor;
	}

	/**
	 * Get a neighbor name specified by its mac address
	 * 
	 * @param macAddress
	 *            device addres
	 * @return device name
	 */
	public String getNeighborName(String macAddress) {
		return neighborhood.get(macAddress).getName();
	}

	/**
	 * Get all neighbors' addresses
	 * 
	 * @return all neighbors' address
	 */
	public Set<String> getNeighborhoodAddresses() {
		return neighborhood.keySet();
	}

	/**
	 * Get all neighbors'
	 * 
	 * @return all neighbors' device
	 */
	public Collection<Device> getNeighborhoodDevices() {
		return neighborhood.values();
	}

	/**
	 * @param macAddress
	 *            Device mac address
	 * @return true, if device specified by its mac address is neigborhood
	 *         member. Otherwise, return false
	 */
	public boolean isMyNeighbor(String macAddress) {
		return neighborhood.containsKey(macAddress);
	}

	/**
	 * @return Number of devices
	 */
	public int getNumberOfNeighbors() {
		return neighborhood.size();
	}

	@Override
	public String toString() {
		return neighborhood.toString();
	}
}
