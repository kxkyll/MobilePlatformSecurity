package com.factory.kxkyllon.mylocation;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class MessengerService extends Service {
    public MessengerService() {
    }

    static final int MESSAGE_GET_LOCATION = 1;

    private final class MyHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_GET_LOCATION:
                    //just testing that connection works
                    Toast.makeText(getApplicationContext(), "your location is home!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    final Messenger messenger = new Messenger(new MyHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(),"binding to service", Toast.LENGTH_LONG).show();
        return messenger.getBinder();
    }
}
