package com.factory.kxkyllon.mylocation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_service);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messenger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart(){
        Log.w("MessengerServiceActivity", "onStart about to bind to service");
        super.onStart();

        bindService(new Intent(this, MessengerService.class), messengerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.w("MessengerServiceActivity", "onStop about to unbind from a service");
        if (messengerServiceBound) {
            unbindService(messengerConnection);
            messengerServiceBound = false;
        }
    }
}
