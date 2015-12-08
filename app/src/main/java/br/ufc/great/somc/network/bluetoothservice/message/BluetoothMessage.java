package br.ufc.great.somc.network.bluetoothservice.message;

import java.io.Serializable;

/**
 * A class abstraction of message bluetooth. This class is a super class of all
 * kind of bluetooth message.
 * 
 * @author bruno
 */
public abstract class BluetoothMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5934254872589828757L;

	private String destinationMacAddress;
	private String sourceName;
	private String sourceAddress;
	private long timestamp;
	private int ttl;
	private static final int TTL = 3;

	/**
	 * @author bruno A enum type that represent all types of bluetooth messages
	 */
	public enum BluetoothTypeMessage {

		HELLO("hello"), HELLO_REPLY("hello_reply"), BYE("bye"), NEIGHBORHOOD_REQUEST(
				"neighborhood"), MSG("msg"), EMBEDDED_ROUTING("routing");

		private String type;

		private BluetoothTypeMessage(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return type;
		}

	}

	public BluetoothMessage() {
		this.ttl = TTL;
		this.timestamp = System.currentTimeMillis();
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String macAddress) {
		this.sourceAddress = macAddress;
	}

	public String getDestinationMacAddress() {
		return destinationMacAddress;
	}

	public void setDestinationMacAddress(String destinationMacAddress) {
		this.destinationMacAddress = destinationMacAddress;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	@Override
	public String toString() {
		return getType().toString();
	}

	public abstract BluetoothTypeMessage getType();

}
