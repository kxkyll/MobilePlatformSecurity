package com.mobisec.app2.revlocation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import fi.generic.location.myservice.LocService;


public class MessengerServiceActivity extends Activity {
    Messenger messengerService;
    boolean messengerServiceBound = false;

    private ServiceConnection messengerConnection = new ServiceConnection() {

        /**
         * Received when a connection is established with a service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            messengerService = new Messenger(service);
            messengerServiceBound = true;
        }

        /**
         * Connection to service has been disconnected, reference removed
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            messengerService = null;
            messengerServiceBound = false;
        }
    };

    public void askLocation(View view) {
        if (!messengerServiceBound) {
            return;
        }
        Message message = Message.obtain(null, 1, 0, 0);
        try {
            messengerService.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart(){
        Log.w("MessengerServiceActivity", "onStart about to bind to service");
        super.onStart();

        //Intent intent =  new Intent();
        //bindService(intent, messengerConnection, Context.BIND_AUTO_CREATE);
        //bindService(new Intent(this, fi.generic.location.myservice.LocService.class), messengerConnection, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, LocService.class), messengerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (messengerServiceBound) {
            unbindService(messengerConnection);
            messengerServiceBound = false;
        }
    }
}
