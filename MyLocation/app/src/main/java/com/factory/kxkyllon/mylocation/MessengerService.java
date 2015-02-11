package com.factory.kxkyllon.mylocation;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.ArrayList;

public class MessengerService extends Service {



    public MessengerService() {
    }

    private ArrayList<Messenger> clients = new ArrayList<Messenger>();

    static final int MESSAGE_REGISTER_CLIENT = 1;
    static final int MESSAGE_REQUEST_LOCATION = 2;
    static final int MESSAGE_UNREGISTER_CLIENT = 3;
    static final int MESSAGE_SEND_LOCATION = 4;


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
                    Message locationMessage = Message.obtain(null, MESSAGE_SEND_LOCATION, "everyone your location is...");
                    for (Messenger client: clients) {
                        try {
                            client.send(locationMessage);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case MESSAGE_UNREGISTER_CLIENT:
                    //Client removed from clients list
                    clients.remove(message.replyTo);
                    Toast.makeText(getApplicationContext(), "Client unregistered!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    final Messenger messenger = new Messenger(new MyHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(),"Binding to service", Toast.LENGTH_LONG).show();
        return messenger.getBinder();
    }
}
