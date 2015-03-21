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
import java.security.PrivateKey;
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
    private KeyPair appkeys;
    private Boolean keysGenerated = false;
    private String alias = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keysGenerated = generateKeyPair();
        String s = appkeys.toString();
        Log.w(TAG, "MyMessage Activity, onCreate, appkey: "+s);
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
        Log.w(TAG, "---------------MyMessage Activity, sendMessage, received message: "+message);
        Log.w(TAG, "---------------MyMessage Activity, sendMessage, message in bytes length: "+message.getBytes().length);
        byte[] signedMessage = signData(message.getBytes());
        Log.w(TAG, "---------------MyMessage Activity, sendMessage, signedMessage: "+signedMessage.toString());
        Log.w(TAG, "---------------MyMessage Activity, sendMessage, signedMessage length: "+signedMessage.length);
        //intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_MESSAGE, signedMessage.toString());
        startActivity(intent);
    }

    /*
     *  Called when Button Verify is pressed
     */
    public void verifyMessage (View view) {
        String verified = "Not Verified";
        Intent intent = new Intent (this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        Log.w(TAG, "---------------MyMessage Activity, verifyMessage, received message: "+message);
        Log.w(TAG, "---------------MyMessage Activity, verifyMessage, message in bytes length: " + message.getBytes().length);
        boolean b = verifySignature(message.getBytes());
        Log.w(TAG, "---------------MyMessage Activity, sendMessage, verifyMessage: " + b);
        if (b == true){
            verified = "Verified";
        }
        intent.putExtra(EXTRA_MESSAGE, verified);
        startActivity(intent);
    }


    private Boolean generateKeyPair (){
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
            keyPairGenerator.generateKeyPair();
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return false;
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
                Log.w(TAG, "MyMessage Activity, signData: Not PrivateKeyEntry");
                return null;
            }
            Signature signature = Signature.getInstance("SHA256withRSA");
            Log.w(TAG, "---------------MyMessage Activity, signData, signature: "+signature.toString());
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

    private boolean verifySignature (byte[] messageToVerify) {
        try {
            KeyStore keystore = KeyStore.getInstance(KEY_STORE);
            keystore.load(null);
            KeyStore.Entry entry = keystore.getEntry(alias, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w(TAG, "MyMessage Activity, verifySignature: Not PrivateKeyEntry");
                return false;
            }
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(((KeyStore.PrivateKeyEntry) entry).getCertificate());
            signature.update(messageToVerify);
            return signature.verify(messageToVerify);


        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }
}
