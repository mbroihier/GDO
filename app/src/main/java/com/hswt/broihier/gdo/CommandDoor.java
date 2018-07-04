package com.hswt.broihier.gdo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by broihier on 4/10/18.
 */

public class CommandDoor {
    /* CommandDoor class                                                                                            */
    /* ============================================================================================================= */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Description:                                                                                                  */
    /*     This class sends a bluetooth command to the GDO server that will command the door to change its current   */
    /* state.                                                                                                        */
    /*                                                                                                               */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ============================================================================================================= */
    private String TAG = "CommandDoor";
    private String result = "Response Not Received";
    private BluetoothSocket socket;
    private BluetoothDevice device = null;
    private BluetoothAdapter adapter;

    public CommandDoor () {
        adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        if (pairedDevices != null) {
            for (BluetoothDevice info : pairedDevices) {
                String name = info.getName();
                Log.d(TAG, " observed device: " + name);
                if (name.equals("rpi3")) {
                    device = info;
                    Log.d(TAG, "setting device");
                    ParcelUuid[] uuids = device.getUuids();
                    for (ParcelUuid uuid : uuids) {
                        Log.d(TAG, "UUID: " + uuid.toString());
                    }
                    break;
                } else {
                    Log.d(TAG, "UUIDs for device:");
                    ParcelUuid[] uuids = info.getUuids();
                    for (ParcelUuid uuid : uuids) {
                        Log.d(TAG, "UUID: " + uuid.toString());
                    }
                }
            }
        }else {
            Log.d(TAG, "Setting device to talk to: undefined/null");
        }
    }

    public void pushButton() {
	/* toggle - method that sends the push button connand                                                            */
    /* ============================================================================================================= */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Description:                                                                                                  */
    /*     This method does the bluetooth commands to send a push button command.                                    */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Inputs:                                                                                                       */
    /*     Mnemonic      Parameter                      Source                                                       */
    /*   ___________    ___________                     ____________________________________________________________ */
    /*                                                                                                               */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Processing:                                                                                                   */
    /*   setup bluetooth connection;                                                                                 */
    /*   send message;                                                                                               */
    /*   retrieve response and store it;                                                                             */
    /*                                                                                                               */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Outputs:                                                                                                      */
    /*     Mnemonic      Parameter                      Destination                                                  */
    /*   ___________    ___________                     ____________________________________________________________ */
    /*                   push button message            GDO server                                                   */
    /*    result         Server's response              this object                                                  */
    /*                                                                                                               */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ============================================================================================================= */
        String whatsGoingOn = "connecting";
        try {
            Log.d(TAG, "in try");
            UUID uuid = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
            int timeValue = (int)(System.currentTimeMillis() / 1000L);
            Key key = new Key(timeValue);
            Log.d(TAG,key.toString());
            if (key.unlock(key.getKey())) {
                Log.d(TAG,"Key unlocks");
            } else {
                Log.d(TAG, "Error! key does not unlock");
            }
            socket = device.createRfcommSocketToServiceRecord(uuid);
            if (!socket.isConnected()) {
                socket.connect();
            }
            whatsGoingOn = "sending message";
            Log.d(TAG,"connected - now sending message");
            OutputStream out = socket.getOutputStream();
            out.write(key.getKey());

            InputStream in =socket.getInputStream();

            int waitCount = 0;

            while (in.available() <= 0 && waitCount < 300) {
                Thread.sleep(10);
                waitCount++;
            };
            result = "timeout";
            if (waitCount < 300) {
                int byteCount = in.available();
                byte [] buffer = new byte[byteCount];
                in.read(buffer);
                result = new String(buffer, "US-ASCII");
                Log.d(TAG,"new result value: " + result);
            } else {
                Log.d(TAG,"timeout observed");
            }
            socket.close();


        } catch (Exception e) {
            Log.d(TAG, "Got an exception when " + whatsGoingOn);
            e.printStackTrace();
            result = "Communication Failed";
        }
    }

    public String getResult() {
	/* getResult - getter to return GDO server response                                                              */
    /* ============================================================================================================= */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Description:                                                                                                  */
    /*     This method returns the result from the GDO server                                                        */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Inputs:                                                                                                       */
    /*     Mnemonic      Parameter                      Source                                                       */
    /*   ___________    ___________                     ____________________________________________________________ */
    /*                                                                                                               */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Processing:                                                                                                   */
    /*   return price;                                                                                               */
    /*                                                                                                               */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                               */
    /* Outputs:                                                                                                      */
    /*     Mnemonic      Parameter                      Destination                                                  */
    /*   ___________    ___________                     ____________________________________________________________ */
    /*    result         response bytes                 calling object                                               */
    /*                                                                                                               */
    /* ------------------------------------------------------------------------------------------------------------- */
    /* ============================================================================================================= */
        return result;
    }

}
