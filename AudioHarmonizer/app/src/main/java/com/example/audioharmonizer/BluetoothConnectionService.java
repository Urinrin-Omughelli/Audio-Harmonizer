package com.example.audioharmonizer;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService{
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "AudioHarmonizer";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //8ce255c0-200a-11e0-ac64-0800200c9a66
    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;
    private ConnectedThread mConnectedThread;
    private BluetoothSocket mSocketToSend;

    //final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            } catch (IOException e) {Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());}
            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread Running.:");
            BluetoothSocket socket = null;
            try {
                Log.d(TAG, "run: RFCOM server socket start...");
                socket = mmServerSocket.accept();
                Log.d(TAG, "run: RFCOM server socket accepted connection.");
            } catch (IOException e) {Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());}
            if (socket != null) {connected(socket, mmDevice);}
            Log.i(TAG, "mAcceptThread is END");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {mmServerSocket.close();
            }
            catch (IOException e) {Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed." + e.getMessage());}
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }


        public void run() {
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread.");
            try {
                Log.d(TAG, "ConnectThread: Trying to Create InsecureRfcommSocket using UUID: " + MY_UUID_INSECURE);
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not Create InsecureRfcommSocket " + e.getMessage());
            }
            mmSocket = tmp;
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                //Possibly delete this
                mSocketToSend = mmSocket;
                Log.d(TAG, "run: ConnectThread connected.");
            } catch (IOException e) {
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to Close Connection in Socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could Not Connect to UUID: " + MY_UUID_INSECURE);
            }

            connected(mmSocket, mmDevice);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            }
            catch (IOException e) {Log.e(TAG, "cancel: close() of mmSocket in ConnectThread failed." + e.getMessage());}
        }
    }


    public synchronized void start() {
        Log.d(TAG, "start:");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startClient: Started.");
        mProgressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth", "Please Wait...", true);
        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                mProgressDialog.dismiss();
            } catch (NullPointerException e) {e.printStackTrace();}


            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            Boolean runningThread = true;
            int batteryLevel;
            int count = 0;
            int[] BL = {0, 0, 0};

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream - BatteryLevel: " + incomingMessage);

                    //Log.d(TAG, "BLEVELLL: " + GlobalClass.getInstance().getBatteryLevel());
                    if(incomingMessage.length() == 1){
                        if (incomingMessage.equals("d")) {
                            batteryLevel = 100 * BL[0] + 10 * BL[1] + BL[2];
                            GlobalClass.getInstance().setBatteryLevel(Integer.toString(batteryLevel));
                            count = 0;

                        } else {
                            BL[count] = Integer.parseInt(incomingMessage);
                            count++;
                        }
                    } else{
                        Log.e(TAG, "Ignore this valye");

                    }

                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading InputStream." + e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to OutputStream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to OutputStream." + e.getMessage());
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    public void write(byte[] out) {
        ConnectedThread r;
        Log.d(TAG, "write: Write Called.");
        mConnectedThread.write(out);
    }

    //possibly delete
    public BluetoothSocket getSocket(){
        return mSocketToSend;
    }


}
