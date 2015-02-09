package com.mobisec.app2.mylocationservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class LocationService extends Service {
    static final int MESSAGE_GET_LOCATION = 1;

    public LocationService() {
    }


    private final class MyHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_GET_LOCATION:
                    //nönnönnöö
                    //ei kai me clientilta odoteta mitään viestiä
                    Toast.makeText(getApplicationContext(),"your location is home!",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    final Messenger messenger = new Messenger(new MyHandler());

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        Toast.makeText(getApplicationContext(),"binding to service", Toast.LENGTH_LONG).show();
        return messenger.getBinder();
    }


}
