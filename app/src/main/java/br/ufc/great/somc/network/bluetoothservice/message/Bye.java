package br.ufc.great.somc.network.bluetoothservice.message;


/**
 * Bye message is used to indicate to device to close the bluetooth connection.
 * @author bruno 
 */
public class Bye extends BluetoothMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7191555740773639771L;

	public enum Reason{
		
		ERROR("error"), REDUNDANT("redundant"), MASTER_SELECTION(
				"master selection"), TIMEOUT("timeout"), ALL_CANCELED(
				"All canceled");
		private String type;
		private Reason(String type) 		
		{
			this.type = type;
		}
		
		@Override
		public String toString() {
			return type;
		}
		
	}	
	
	private Reason reason;
	
	public Bye(Reason reason) {
		this.reason = reason;
	}

	@Override
	public BluetoothTypeMessage getType() {
		return BluetoothTypeMessage.BYE;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + reason.toString();
	}

}
