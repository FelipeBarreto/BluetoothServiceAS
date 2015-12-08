package br.ufc.great.somc.network.bluetoothservice.message;

import br.ufc.great.somc.network.bluetoothservice.Neighborhood;

/**
 * Handshake message is used to establish a bluetooth connection between two
 * devices avoid the acceptance problem. When a node connect with a Master, he
 * sends a Hello message that should be answered during the Handshake TIMOUT
 * time, with a HelloReply message.
 * 
 * If it answered the connection is established. Else the connection is closed,
 * and the protocol will try two more times establish this connection in the
 * same way. If it do not have a success the connection will be closed
 * definitely.
 * 
 * The field neighborhood is encapsulate here in this message to be used on the
 * algorithm of master selection without the need of send one more message.
 * 
 * @author bruno
 */
public abstract class HandShakeMessage extends BluetoothMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8866175959069442054L;

	private Neighborhood neighborhood;

	public HandShakeMessage() {
		neighborhood = new Neighborhood();
	}

	public Neighborhood getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(Neighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}

	@Override
	public String toString() {
		return super.toString() + " " + neighborhood.toString();
	}

}
