package com.upnext.upnext;

/**
 * Created by Steven on 5/7/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.format.Formatter;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import static android.R.id.message;



public class UDPListener extends Service {

    PartyMetadata incomingParty;

    int portNumber = 5711;
    InetAddress ipAddress;

    //DatagramSocket socket = new DatagramSocket(null);
    DatagramSocket socket;

    public UDPListener() throws IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ipAddress = getLocalAddress();
        socket = new DatagramSocket(4200);
        //ipAddress = InetAddress.getByName("localhost");
        Log.i("UDP", "got local ip: " + Formatter.formatIpAddress(ipAddress.hashCode()));

    }

    Thread UDPBroadcastThread;

    public void listenForUDPBroadcast() {
        UDPBroadcastThread = new Thread(new Runnable() {
            public void run() {
                byte[] incomingData = new byte[1024];
                DatagramPacket packet = new DatagramPacket(incomingData, incomingData.length);
                    try {

                        Log.i("UDP", "sending request");
                        byte[] buf = new byte[256];
                        DatagramPacket requestPacket = new DatagramPacket(buf, buf.length, ipAddress, portNumber);
                        socket.send(requestPacket);
                        socket.setSoTimeout(2000); // timeout in ms
                        Log.i("UDP", "waiting for package...");
                        socket.receive(packet);
                        Log.i("UDP", "package received");
                        byte[] data = packet.getData();
                        ByteArrayInputStream in = new ByteArrayInputStream(data);
                        ObjectInputStream is = new ObjectInputStream(in);
                        try {
                            incomingParty = (PartyMetadata) is.readObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        Log.i("UDP", "party received, name: " + incomingParty.getPartyName());
                    } catch (SocketTimeoutException e) {
                        Log.i("UDP","no response");
                    } catch (Exception e) {
                        Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                    }
                }
        });
        UDPBroadcastThread.start();
    }

    void stopReceive() {
        if (socket != null)
            socket.close();
    }

    public String getPartyName() {
        try {
            return incomingParty.getPartyName();
        } finally {
            return null;
        }
    }

    private InetAddress getLocalAddress()throws IOException {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("SALMAN", ex.toString());
        }
        return null;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}