package com.mobisec.app2.revlocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        Log.w("registerLocation", "kicking off the MessengerServiceActivity");
        Intent intent = new Intent(this, MessengerServiceActivity.class);
        startActivity(intent);
    }

    /**
     * Called when user presses unregister button in main view
     * @param view
     */
    public void unregisterLocation(View view) {
        Intent intent = new Intent(this, MessengerServiceActivity.class);
        startActivity(intent);
    }

}
