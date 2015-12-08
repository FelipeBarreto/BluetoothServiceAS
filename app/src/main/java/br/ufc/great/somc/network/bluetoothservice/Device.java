package br.ufc.great.somc.network.bluetoothservice;

import java.io.Serializable;

import br.ufc.great.somc.network.bluetoothservice.threads.ConnectedThread;

/**
 * A object that contains the main bluetooth device characteristics and that can
 * be sent in a bluetooth message.
 * 
 * @author bruno
 */
public class Device implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2529873841208574667L;

	/**
	 * Uses a @ConnectedThread to build a device
	 * 
	 * @param connection
	 *            A connectedThread
	 * @see ConnectedThread
	 */
	public Device(ConnectedThread connection) {
		super();
		this.macAddress = connection.getDestinationMacAddress();
		this.name = connection.getDestinationName();
		this.role = connection.getDeviceDestinationRole();
	}

	/**
	 * Enum class to represents the types o bluetooth device connected
	 * 
	 * @author bruno
	 * 
	 */
	public enum DeviceRole {
		/**
		 * MASTER - When a device is a MASTER device of the connection
		 * 
		 */
		MASTER("M"),
		/**
		 * SLAVE - When a device is a SLAVE device of the connection
		 */
		SLAVE("S");
		private String role;

		private DeviceRole(String role) {
			this.role = role;
		}

		@Override
		public String toString() {
			return role;
		}
	}

	private String macAddress;
	private String name;
	private DeviceRole role;

	/**
	 * @return return the device MAC address
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * @param macAddress
	 *            Set the device address
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	/**
	 * @return The the device name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            Set the device name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the device type. MASTER ou SLAVE
	 */
	public DeviceRole getRole() {
		return role;
	}

	/**
	 * @param role
	 *            Set the device role in a connection
	 */
	public void setRole(DeviceRole role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return name + "{" + role.toString() + "}";
	}

}
