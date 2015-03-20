package com.mindtherobot.samples.tweetservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(TweetCollectorService.class.getName());
        context.startService(serviceIntent); 
	}
}
