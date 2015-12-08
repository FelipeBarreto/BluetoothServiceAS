package br.ufc.great.somc.network.bluetoothservice.message;

import br.ufc.great.somc.network.bluetoothservice.Neighborhood;

/**
 * A message to request the list of neighbors of one specific device. It is used
 * also to send to a device a list of neighbors of the sender device.
 * 
 * @author bruno
 */
public class NeighborhoodRequest extends BluetoothMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6539429775502868541L;

	private Neighborhood neighborhood;

	public NeighborhoodRequest() {
		this.neighborhood = new Neighborhood();
	}

	@Override
	public BluetoothTypeMessage getType() {
		return BluetoothTypeMessage.NEIGHBORHOOD_REQUEST;
	}

	@Override
	public String toString() {
		return super.toString() + " "
				+ neighborhood.getNeighborhoodDevices().toString();
	}

	public Neighborhood getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(Neighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}
}
