package br.ufc.great.somc.network.bluetoothservice.message;

/**
 * The Hello message is a message that starts the handshake protocol. It is sent
 * when a slave device connect with a master. So this device, sent this message
 * and wait a amount of time, TIMOUT, for the HelloReply message.
 * 
 * @author bruno
 */
public class Hello extends HandShakeMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6392315904506818024L;

	@Override
	public BluetoothTypeMessage getType() {
		return BluetoothTypeMessage.HELLO;
	}

}
