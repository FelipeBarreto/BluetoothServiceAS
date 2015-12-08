/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\Android\\workspace\\BluetoothService\\app\\src\\main\\aidl\\br\\ufc\\great\\somc\\network\\base\\BluetoothServiceApi.aidl
 */
package br.ufc.great.somc.network.base;
public interface BluetoothServiceApi extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements br.ufc.great.somc.network.base.BluetoothServiceApi
{
private static final java.lang.String DESCRIPTOR = "br.ufc.great.somc.network.base.BluetoothServiceApi";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an br.ufc.great.somc.network.base.BluetoothServiceApi interface,
 * generating a proxy if needed.
 */
public static br.ufc.great.somc.network.base.BluetoothServiceApi asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof br.ufc.great.somc.network.base.BluetoothServiceApi))) {
return ((br.ufc.great.somc.network.base.BluetoothServiceApi)iin);
}
return new br.ufc.great.somc.network.base.BluetoothServiceApi.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_addListener:
{
data.enforceInterface(DESCRIPTOR);
br.ufc.great.somc.network.base.BluetoothListener _arg0;
_arg0 = br.ufc.great.somc.network.base.BluetoothListener.Stub.asInterface(data.readStrongBinder());
this.addListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeListener:
{
data.enforceInterface(DESCRIPTOR);
br.ufc.great.somc.network.base.BluetoothListener _arg0;
_arg0 = br.ufc.great.somc.network.base.BluetoothListener.Stub.asInterface(data.readStrongBinder());
this.removeListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_sendBroadcastMessage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.sendBroadcastMessage(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_sendUnicastMessage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.sendUnicastMessage(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getCurrentState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurrentState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getNeighboord:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _result = this.getNeighboord();
reply.writeNoException();
reply.writeStringList(_result);
return true;
}
case TRANSACTION_getMyAddress:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getMyAddress();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_startDiscovery:
{
data.enforceInterface(DESCRIPTOR);
this.startDiscovery();
reply.writeNoException();
return true;
}
case TRANSACTION_ensureDiscoverable:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.ensureDiscoverable();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getRemoteDevice:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
android.bluetooth.BluetoothDevice _result = this.getRemoteDevice(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_manualConnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.manualConnect(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getObservingTime:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getObservingTime();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startObserver:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.startObserver();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_stopObserver:
{
data.enforceInterface(DESCRIPTOR);
this.stopObserver();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements br.ufc.great.somc.network.base.BluetoothServiceApi
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void addListener(br.ufc.great.somc.network.base.BluetoothListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_addListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeListener(br.ufc.great.somc.network.base.BluetoothListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void sendBroadcastMessage(java.lang.String jsonMessage, java.lang.String avoidAddress) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(jsonMessage);
_data.writeString(avoidAddress);
mRemote.transact(Stub.TRANSACTION_sendBroadcastMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void sendUnicastMessage(java.lang.String jsonMessage, java.lang.String destinationAddress) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(jsonMessage);
_data.writeString(destinationAddress);
mRemote.transact(Stub.TRANSACTION_sendUnicastMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getCurrentState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<java.lang.String> getNeighboord() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<java.lang.String> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getNeighboord, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArrayList();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getMyAddress() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMyAddress, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void startDiscovery() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startDiscovery, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean ensureDiscoverable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_ensureDiscoverable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.bluetooth.BluetoothDevice getRemoteDevice(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.bluetooth.BluetoothDevice _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_getRemoteDevice, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.bluetooth.BluetoothDevice.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void manualConnect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_manualConnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getObservingTime() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getObservingTime, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean startObserver() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startObserver, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void stopObserver() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopObserver, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_addListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_removeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_sendBroadcastMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_sendUnicastMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getCurrentState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getNeighboord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getMyAddress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_startDiscovery = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_ensureDiscoverable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getRemoteDevice = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_manualConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getObservingTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_startObserver = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_stopObserver = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
}
public void addListener(br.ufc.great.somc.network.base.BluetoothListener listener) throws android.os.RemoteException;
public void removeListener(br.ufc.great.somc.network.base.BluetoothListener listener) throws android.os.RemoteException;
public void sendBroadcastMessage(java.lang.String jsonMessage, java.lang.String avoidAddress) throws android.os.RemoteException;
public void sendUnicastMessage(java.lang.String jsonMessage, java.lang.String destinationAddress) throws android.os.RemoteException;
public int getCurrentState() throws android.os.RemoteException;
public java.util.List<java.lang.String> getNeighboord() throws android.os.RemoteException;
public java.lang.String getMyAddress() throws android.os.RemoteException;
public void startDiscovery() throws android.os.RemoteException;
public boolean ensureDiscoverable() throws android.os.RemoteException;
public android.bluetooth.BluetoothDevice getRemoteDevice(java.lang.String address) throws android.os.RemoteException;
public void manualConnect(java.lang.String address) throws android.os.RemoteException;
public int getObservingTime() throws android.os.RemoteException;
public boolean startObserver() throws android.os.RemoteException;
public void stopObserver() throws android.os.RemoteException;
}
