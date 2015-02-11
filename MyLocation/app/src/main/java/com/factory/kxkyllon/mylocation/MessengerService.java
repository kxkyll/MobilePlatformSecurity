package com.factory.kxkyllon.mylocation;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MessengerService extends Service {



    public MessengerService() {
    }

    private ArrayList<Messenger> clients = new ArrayList<Messenger>();

    static final int MESSAGE_REGISTER_CLIENT = 1;
    static final int MESSAGE_REQUEST_LOCATION = 2;
    static final int MESSAGE_UNREGISTER_CLIENT = 3;
    static final int MESSAGE_SEND_LOCATION = 4;
    Handler timerHandler = new Handler();
    //Timer timer = new Timer();
    //TimerTask sendLocation = new SendLocationTask();
    int test = 0;

    private final class MyHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_REGISTER_CLIENT:
                    //Registered client added to a clients list
                    clients.add(message.replyTo);
                    //Toast sent to client for testing that connection works
                    Toast.makeText(getApplicationContext(), "Client registered!", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_REQUEST_LOCATION:
                    //If Client wants to request location more frequently, send location to this client only
                    Toast.makeText(getApplicationContext(), "Location request received!", Toast.LENGTH_SHORT).show();
                    Message locationToClient = Message.obtain(null, MESSAGE_SEND_LOCATION, "your location is...");
                    try {
                        message.replyTo.send(locationToClient);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case MESSAGE_SEND_LOCATION:
                    //Send location to all clients
                    Toast.makeText(getApplicationContext(), "Sending location in a minute!", Toast.LENGTH_SHORT).show();
                    sendLocationToClients();
                    break;
                case MESSAGE_UNREGISTER_CLIENT:
                    //Client removed from clients list
                    clients.remove(message.replyTo);
                    if (clients.isEmpty()) {
                        timerHandler.removeCallbacks(timer);
                    }

                    Toast.makeText(getApplicationContext(), "Client unregistered!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    private void sendLocationToClients() {
        test++;
        Message locationMessage = Message.obtain(null, MESSAGE_SEND_LOCATION);
        Bundle bundle = new Bundle();
        bundle.putString("loc", "everyone your location is..."+test);

        for (Messenger client: clients) {
            try {
                client.send(locationMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    /*class SendLocationTask extends TimerTask {

        public void run() {
            sendLocationToClients();
        }
    }
    */

    final Messenger messenger = new Messenger(new MyHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(),"Binding to service", Toast.LENGTH_LONG).show();
        timerHandler.postDelayed(timer, 5000);
        //timer.scheduleAtFixedRate(sendLocation, 5000, 1000);
        return messenger.getBinder();
    }

    Runnable timer = new Runnable() {

        @Override
        public void run() {
            sendLocationToClients();
            timerHandler.postDelayed(this, 1000);
        }
    };


}
