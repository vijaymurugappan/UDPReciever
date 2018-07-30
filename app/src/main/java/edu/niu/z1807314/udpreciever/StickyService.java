package edu.niu.z1807314.udpreciever;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;


public class StickyService extends Service {

    String recievedMsg;
    int servPort = 11111;
    byte[] message = new byte[15000];
    String ipAddress = "127.0.0.1";
    private static final String TAG = "VijayM";
    TimerTask timerTask;
    Timer t = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
                            WifiManager.WifiLock lock = wifi.createWifiLock("lock");
                            lock.acquire();
                            DatagramPacket p = new DatagramPacket(message, message.length);
                            DatagramSocket s = new DatagramSocket(servPort, InetAddress.getByName(ipAddress));
                            s.setBroadcast(true);
                            s.setReuseAddress(true);
                            s.receive(p);
                            lock.release();
                            recievedMsg = new String(p.getData()).trim();
                            Log.d(TAG, "Datagram: " + recievedMsg);
                            s.close();
                        } catch (SocketException e) {
                            Log.e(TAG, e.toString());
                        } catch (IOException ie) {
                            Log.e(TAG, ie.toString());
                        }
                    }
                });
                thread.start();
            }
        };
        t = new Timer();
        t.schedule(timerTask,0,2000);
        return Service.START_STICKY;
    }
}
