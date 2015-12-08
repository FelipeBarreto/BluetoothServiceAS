/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\Android\\workspace\\BluetoothService\\app\\src\\main\\aidl\\br\\ufc\\great\\somc\\network\\base\\BluetoothListener.aidl
 */
package br.ufc.great.somc.network.base;
public interface BluetoothListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements br.ufc.great.somc.network.base.BluetoothListener
{
private static final java.lang.String DESCRIPTOR = "br.ufc.great.somc.network.base.BluetoothListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an br.ufc.great.somc.network.base.BluetoothListener interface,
 * generating a proxy if needed.
 */
public static br.ufc.great.somc.network.base.BluetoothListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof br.ufc.great.somc.network.base.BluetoothListener))) {
return ((br.ufc.great.somc.network.base.BluetoothListener)iin);
}
return new br.ufc.great.somc.network.base.BluetoothListener.Stub.Proxy(obj);
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
case TRANSACTION_onDeviceFound:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onDeviceFound(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onDeviceConnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onDeviceConnect(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onDeviceDisconnect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onDeviceDisconnect(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onMessageReceived:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onMessageReceived(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onExceptionOcurred:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.onExceptionOcurred(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onNotNeighborDeviceFound:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onNotNeighborDeviceFound(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onMessageSent:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onMessageSent(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onStateChanged:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onStateChanged(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onStateScanModeChanged:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onStateScanModeChanged(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onStateObservingChanged:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onStateObservingChanged(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onStateDiscoveryChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onStateDiscoveryChanged(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements br.ufc.great.somc.network.base.BluetoothListener
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
@Override public void onDeviceFound(java.lang.String name, java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_onDeviceFound, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onDeviceConnect(java.lang.String name, java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_onDeviceConnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onDeviceDisconnect(java.lang.String name, java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_onDeviceDisconnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onMessageReceived(java.lang.String jsonMessage) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(jsonMessage);
mRemote.transact(Stub.TRANSACTION_onMessageReceived, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onExceptionOcurred(int code, java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(code);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_onExceptionOcurred, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onNotNeighborDeviceFound(java.lang.String deviceName, java.lang.String deviceAddress) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(deviceName);
_data.writeString(deviceAddress);
mRemote.transact(Stub.TRANSACTION_onNotNeighborDeviceFound, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onMessageSent(java.lang.String jsonMessage) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(jsonMessage);
mRemote.transact(Stub.TRANSACTION_onMessageSent, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onStateChanged(int state, int previousState) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(state);
_data.writeInt(previousState);
mRemote.transact(Stub.TRANSACTION_onStateChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onStateScanModeChanged(int action, int previousState) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(action);
_data.writeInt(previousState);
mRemote.transact(Stub.TRANSACTION_onStateScanModeChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onStateObservingChanged(int action, int previousState) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(action);
_data.writeInt(previousState);
mRemote.transact(Stub.TRANSACTION_onStateObservingChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onStateDiscoveryChanged(java.lang.String action) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(action);
mRemote.transact(Stub.TRANSACTION_onStateDiscoveryChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onDeviceFound = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onDeviceConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onDeviceDisconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onMessageReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onExceptionOcurred = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onNotNeighborDeviceFound = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onMessageSent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onStateChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_onStateScanModeChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_onStateObservingChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_onStateDiscoveryChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
}
public void onDeviceFound(java.lang.String name, java.lang.String address) throws android.os.RemoteException;
public void onDeviceConnect(java.lang.String name, java.lang.String address) throws android.os.RemoteException;
public void onDeviceDisconnect(java.lang.String name, java.lang.String address) throws android.os.RemoteException;
public void onMessageReceived(java.lang.String jsonMessage) throws android.os.RemoteException;
public void onExceptionOcurred(int code, java.lang.String message) throws android.os.RemoteException;
public void onNotNeighborDeviceFound(java.lang.String deviceName, java.lang.String deviceAddress) throws android.os.RemoteException;
public void onMessageSent(java.lang.String jsonMessage) throws android.os.RemoteException;
public void onStateChanged(int state, int previousState) throws android.os.RemoteException;
public void onStateScanModeChanged(int action, int previousState) throws android.os.RemoteException;
public void onStateObservingChanged(int action, int previousState) throws android.os.RemoteException;
public void onStateDiscoveryChanged(java.lang.String action) throws android.os.RemoteException;
}
