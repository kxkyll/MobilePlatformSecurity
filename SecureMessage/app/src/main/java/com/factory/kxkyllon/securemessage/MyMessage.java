package com.factory.kxkyllon.securemessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.x500.X500Principal;


public class MyMessage extends Activity {
    public final static String EXTRA_MESSAGE = "com.factory.kxkyllon.securemessage.MESSAGE";
    private final static String KEY_STORE = "AndroidKeyStore";


    private String alias = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_message, menu);
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

    /*
     *  Called when Button Send is pressed
     */
    public void sendMessage (View view) {
        Intent intent = new Intent (this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private KeyPair generateKeyPair (){
        KeyPair keyPair = null;
        Calendar calendar = Calendar.getInstance();
        Date keyGenerationDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        Date keyExpirationDate = calendar.getTime();

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", KEY_STORE);
            keyPairGenerator.initialize(new KeyPairGeneratorSpec.Builder(getApplicationContext())
                    .setAlias(alias)
                    .setStartDate(keyGenerationDate)
                    .setEndDate(keyExpirationDate)
                    .setSerialNumber(BigInteger.valueOf(1))
                    .setSubject(new X500Principal("CN=test1"))
                    .build());
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return keyPair;

    }
}
