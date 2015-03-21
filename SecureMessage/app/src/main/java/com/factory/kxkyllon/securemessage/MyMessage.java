package com.factory.kxkyllon.securemessage;

import android.app.Activity;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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
    private Boolean keysGenerated = false;
    private String alias = "key";
    private TextView resultText;
    private KeyStore keystore = null;
    private KeyStore.Entry entry = null;
    private Signature signature = null;
    private byte[] messageSignedWith = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (keysGenerated == false) {
            keysGenerated = generateKeyPair();
            Log.w(TAG, "MyMessage Activity, onCreate, keysGenerated: "+keysGenerated);
        }

        setContentView(R.layout.activity_my_message);
        resultText = (TextView) findViewById(R.id.result_text);
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
     *  Called when Button Sign is pressed
     */
    public void signMessage (View view) {

        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        Log.w(TAG, "---------------MyMessage Activity, sendMessage, received message: "+message);
        messageSignedWith = signData(message.getBytes());
        Log.w(TAG, "---------------MyMessage Activity, sendMessage, signedMessage: "+messageSignedWith.toString());
        Log.w(TAG, "---------------MyMessage Activity, sendMessage, signedMessage length: "+messageSignedWith.length);

        resultText.setText("Message: " +message +" is signed with " +messageSignedWith.toString());


    }

    /*
     *  Called when Button Verify is pressed
     */
    public void verifyMessage (View view) {
        String verified = "not verified";
        EditText editText = (EditText) findViewById(R.id.verify_message);
        String message = editText.getText().toString();
        Log.w(TAG, "---------------MyMessage Activity, verifyMessage, received message: "+message);
        boolean b = verifySignature(message.getBytes());
        Log.w(TAG, "---------------MyMessage Activity, sendMessage, verifyMessage: " + b);
        if (b == true){
            verified = "verified!";
        }
        resultText.setText("Message: " +message +" is " +verified);
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
            keystore = KeyStore.getInstance(KEY_STORE);
            keystore.load(null);
            entry = keystore.getEntry(alias, null);
            signature = Signature.getInstance("SHA256withRSA");
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Enumeration<String> listKeys() {

        try {
            if (keystore != null) {
                return keystore.aliases();
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] signData(byte[] messageInBytes) {
        try {
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w(TAG, "MyMessage Activity, signData: Not PrivateKeyEntry");
                return null;
            }
            // initializes the Signature instance using the private key
            if (signature != null) {
                signature.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
                // updates the data to be signed
                signature.update(messageInBytes);
                return signature.sign();
            }
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean verifySignature (byte[] messageToVerify) {
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            Log.w(TAG, "MyMessage Activity, verifySignature: Not PrivateKeyEntry");
            return false;
        }
        try {
            signature.initVerify(((KeyStore.PrivateKeyEntry) entry).getCertificate());
            signature.update(messageToVerify);
            return signature.verify(messageSignedWith);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        return false;
    }
}
