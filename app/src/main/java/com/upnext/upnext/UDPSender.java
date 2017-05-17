package com.upnext.upnext;

/**
 * Created by Steven on 5/7/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.upnext.upnext.R.id.time;


public class UDPSender extends Service {


    PartyMetadata sendParty;

    int portNumber = 5711;

    DatagramSocket socket = null;

    boolean isSending;

    public UDPSender(PartyMetadata party) throws SocketException, UnknownHostException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sendParty = party;

        isSending = true;

        socket = new DatagramSocket();

        sendUDPBroadcast();

    }

    Thread UDPBroadcastThread;

    void sendUDPBroadcast() {
        UDPBroadcastThread = new Thread(new Runnable() {
            public void run() {
                while (isSending == true) try {
                    byte[] buf = new byte[256];
                    buf = "requesting".getBytes();
                    DatagramPacket requestPacket = new DatagramPacket(buf, buf.length);
                    Log.i("UDP", "waiting for request");
                    socket.receive(requestPacket);
                    Log.i("UDP", "got one");
                    InetAddress requestAddress = requestPacket.getAddress();
                    int requestPort = requestPacket.getPort();
                    Log.i("UDP", "received request from: " + requestAddress + " : " + requestPort);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(outputStream);
                    os.writeObject(sendParty);
                    byte[] data = outputStream.toByteArray();
                    DatagramPacket packet = new DatagramPacket(data, data.length, requestAddress, requestPort);
                    socket.send(packet);
                    Log.i("UDP", "party sent: " + sendParty.getPartyName());
                } catch (Exception e) {
                    Log.i("UDP", "no longer sending UDP broadcasts cause of error " + e.getMessage());
                }
            }
        });
        UDPBroadcastThread.start();
    }

    void stopSend() {
        if(socket != null)
            socket.close();
        isSending = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}