package com.factory.kxkyllon.securemessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;


public class MyMessage extends Activity {
    public final static String EXTRA_MESSAGE = "com.factory.kxkyllon.securemessage.MESSAGE";
    private final static String KEY_STORE = "AndroidKeyStore";
    private static final String TAG = "SecureMessageApp";

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
        byte[] signedMessage = signData(message.getBytes());
        //intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_MESSAGE, signedMessage);
        startActivity(intent);
    }

    private KeyPair generateKeyPair (){
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
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Enumeration<String> listKeys() {

        try {
            KeyStore keystore = KeyStore.getInstance(KEY_STORE);
            keystore.load(null);
            return keystore.aliases();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] signData(byte[] messageInBytes) {
        try {
            KeyStore keystore = KeyStore.getInstance(KEY_STORE);
            keystore.load(null);
            KeyStore.Entry entry = keystore.getEntry(alias, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w(TAG, "MyMessage Activity: Not PrivateKeyEntry");
                return null;
            }
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
            signature.update(messageInBytes);
            return signature.sign();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }
}
