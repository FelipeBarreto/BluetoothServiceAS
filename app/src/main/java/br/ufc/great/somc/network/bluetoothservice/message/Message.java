package br.ufc.great.somc.network.bluetoothservice.message;

/**
 * A simple text message
 * 
 * @author bruno
 */
public class Message extends BluetoothMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1333088839458359733L;
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public BluetoothTypeMessage getType() {
		return BluetoothTypeMessage.MSG;
	}

	@Override
	public String toString() {
		return super.toString() + " " + content;
	}

}
