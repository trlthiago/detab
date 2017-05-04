package com.detab.detabapp.Providers;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.detab.detabapp.Models.PotholeCollection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.List;

import cz.msebera.android.httpclient.conn.util.InetAddressUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by thiago on 11/04/2017.
 */

public class TCPServerService extends Service
{
    public static String LOG_TAG = "detabapp";

    private final IBinder binder = new TCPServerBinder();

    private Thread ServerThread;
    private PotholeCollection _potholeCollection;
    private GPSTracker _gps;

    public TCPServerService()
    {
//        try
//        {
//            Listen();
//        } catch (Exception e)
//        {
//
//        }
    }

    public String Ping()
    {
        return "pong";
    }

    public String GetLocalIpAddress()
    {
        try
        {
            Enumeration<InetAddress> addresses = NetworkInterface.getByName("wlan0").getInetAddresses();

            while (addresses.hasMoreElements())
            {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet4Address)
                {
                    String ip = address.getHostAddress();
                    return ip;
                }
            }
//
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
//            {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); )
//                {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress() && inetAddress.isLinkLocalAddress())
//                    {
//                        boolean inst = inetAddress instanceof Inet4Address;
//                        //String ip = Formatter.formatIpAddress(inetAddress.hashCode());
//                        String ip = inetAddress.getHostAddress();
//                        Log.d(LOG_TAG, "***** IP=" + ip);
//                        return ip;
//                    }
//                }
//            }
        } catch (SocketException ex)
        {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

    public String GetLocalIP()
    {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
        {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try
        {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex)
        {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

    public void Listen(PotholeCollection potholeCollection, GPSTracker gps)
    {
        _gps = gps;
        _potholeCollection = potholeCollection;

        Log.d(LOG_TAG, "GPS instance on TCPService: " + gps.toString());

        ServerThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    Log.d(LOG_TAG, "Initializing while in thread do startup socket server.");
                    ServerSocket welcomeSocket = null;
                    try
                    {
                        String clientSentence;
                        //String capitalizedSentence;
                        welcomeSocket = new ServerSocket(6789);
                        Log.d(LOG_TAG, "Waiting connection...");
                        Socket connectionSocket = welcomeSocket.accept();
                        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        //DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                        while (true)
                        {
                            Log.d(LOG_TAG, "Waiting notification...");

                            clientSentence = inFromClient.readLine();

                            if(clientSentence == null) clientSentence = "**** NULL clientSentence";

                            Log.v(LOG_TAG, String.format("Received: %s (%s:%s)", clientSentence, _gps.getLatitude(), _gps.getLongitude()));

                            if (clientSentence.startsWith("p"))
                            {
                                clientSentence = clientSentence.replace("p", "");
                                _potholeCollection.DeclareNewPothole(_gps.getLatitude(), _gps.getLongitude(), Double.parseDouble(clientSentence));
                            }

                            _potholeCollection.UpdateTxt(clientSentence);
                            //Log.d(LOG_TAG, "Back to TCP Service flow.");

                            //outToClient.writeBytes(clientSentence);
                        }
                    } catch (Exception e)
                    {
                        if (welcomeSocket != null && !welcomeSocket.isClosed())
                            try
                            {
                                welcomeSocket.close();
                            } catch (IOException e1)
                            {
                                e1.printStackTrace();
                                Log.e(LOG_TAG, e1.getMessage(), e1);
                            }

                        Log.e(LOG_TAG, "Exception "+ e.getMessage());
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }
            }
        });

        ServerThread.start();
    }

    public void UnListen()
    {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        //Toast.makeText(getApplicationContext(), "onBind", Toast.LENGTH_SHORT).show();
        return binder;
    }

    public class TCPServerBinder extends Binder
    {
        public TCPServerService GetService()
        {
            //Toast.makeText(getApplicationContext(), "GetService", Toast.LENGTH_SHORT).show();
            return TCPServerService.this;
        }
    }
}