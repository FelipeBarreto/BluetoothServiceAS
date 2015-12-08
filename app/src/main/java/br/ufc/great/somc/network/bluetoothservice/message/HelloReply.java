package br.ufc.great.somc.network.bluetoothservice.message;

/**
 * The hello reply message is used to answer the hello message and conclude the
 * proccess of handshake defined by our handshake protocol.
 * 
 * @author bruno
 */
public class HelloReply extends HandShakeMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7701413392512287230L;

	@Override
	public BluetoothTypeMessage getType() {
		return BluetoothTypeMessage.HELLO_REPLY;
	}

}
