package br.ufc.great.somc.network.bluetoothservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.os.RemoteException;
import br.ufc.great.somc.network.base.BluetoothListener;
import br.ufc.great.somc.network.base.BluetoothServiceApi;

public class ServiceBinder extends BluetoothServiceApi.Stub {

	private BluetoothNetworkManagerService service;

	public ServiceBinder(BluetoothNetworkManagerService service) {
		this.service = service;
	}

	@Override
	public void removeListener(BluetoothListener listener)
			throws RemoteException {
		service.removeListener(listener);
	}

	@Override
	public void addListener(BluetoothListener listener) throws RemoteException {
		service.addListener(listener);
	}

	@Override
	public boolean ensureDiscoverable() throws RemoteException {
		return service.ensureDiscoverable();
	}

	@Override
	public void startDiscovery() throws RemoteException {
		service.startDiscovery();
	}

	@Override
	public BluetoothDevice getRemoteDevice(String address)
			throws RemoteException {
		return service.getRemoteDevice(address);
	}

	@Override
	public int getObservingTime() throws RemoteException {
		return service.getObservingTime();
	}

	@Override
	public boolean startObserver() throws RemoteException {
		return service.startObserver();
	}

	@Override
	public void stopObserver() throws RemoteException {
		service.stopObserver();
	}

	@Override
	public int getCurrentState() throws RemoteException {
		return service.getCurrentState();
	}

	@Override
	public void manualConnect(String address) throws RemoteException {
		service.manualConnect(address);
	}

	@Override
	public List<String> getNeighboord() throws RemoteException {
		List<String> list = new ArrayList<String>();
		HashMap<String, Device> hash = BluetoothNetworkManagerService
				.getNeighborhood();
		Set<String> set = hash.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

	@Override
	public String getMyAddress() throws RemoteException {
		return service.getMyAddress();
	}

	@Override
	public void sendBroadcastMessage(String jsonMessage, String avoidAddress)
			throws RemoteException {
		service.sendBroadcastMessage(jsonMessage, avoidAddress);
	}

	@Override
	public void sendUnicastMessage(String jsonMessage, String destinationAddress)
			throws RemoteException {
		service.sendUnicastMessage(jsonMessage, destinationAddress);
	}

}
