package com.detab.detabapp.Providers;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import static android.content.ContentValues.TAG;

/**
 * Created by thiago on 11/04/2017.
 */

public class TCPServerService extends Service
{
    private final IBinder binder = new TCPServerBinder();

    public TCPServerService()
    {
//        try
//        {
//            //Listen();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
    }

    public String Ping()
    {
        return "pong";
    }

    public String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); )
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("TRL", "***** IP=" + ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex)
        {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

    public void Listen()
    {
        try
        {
            String clientSentence;
            String capitalizedSentence;
            ServerSocket welcomeSocket = new ServerSocket(6789);

            while (true)
            {
                Socket connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                clientSentence = inFromClient.readLine();
                System.out.println("Received: " + clientSentence);
                capitalizedSentence = clientSentence.toUpperCase() + '\n';
                outToClient.writeBytes(capitalizedSentence);
            }
        } catch (Exception e)
        {
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        Toast.makeText(getApplicationContext(), "onBind", Toast.LENGTH_SHORT).show();
        return binder;
    }

    public class TCPServerBinder extends Binder
    {
        public TCPServerService GetService()
        {
            Toast.makeText(getApplicationContext(), "GetService", Toast.LENGTH_SHORT).show();
            return TCPServerService.this;
        }
    }
}