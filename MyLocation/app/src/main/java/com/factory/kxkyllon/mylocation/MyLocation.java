package com.factory.kxkyllon.mylocation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MyLocation extends Activity {

    private Messenger messengerService;
    private boolean messengerServiceBound = false;
    private ServiceConnection messengerConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_location, menu);
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

    /**
     * Called when user presses register button in main view
     * @param view
     */
    public void registerLocation(View view) {
        /*Log.w("registerLocation", "kicking off the MessengerServiceActivity");
        Intent intent = new Intent(this, MessengerServiceActivity.class);
        startActivity(intent); */

        messengerConnection = new ServiceConnection() {

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

        bindService(new Intent(this, MessengerService.class), messengerConnection, Context.BIND_AUTO_CREATE);


    }

    /**
     * Called when user presses unregister button in main view
     * @param view
     */
    public void unregisterLocation(View view) {
        /*Intent intent = new Intent(this, MessengerServiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("keep", false);
        startActivity(intent); */

        if (messengerServiceBound) {
            unbindService(messengerConnection);
            messengerServiceBound = false;
        }
        Toast.makeText(getApplicationContext(),"Service unbound", Toast.LENGTH_SHORT).show();

    }
}
