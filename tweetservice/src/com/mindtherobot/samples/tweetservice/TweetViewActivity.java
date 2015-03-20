package com.mindtherobot.samples.tweetservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

public class TweetViewActivity extends Activity {

	private static final String TAG = TweetViewActivity.class.getSimpleName();
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "Service connection established");
	
			// that's how we get the client side of the IPC connection
			api = TweetCollectorApi.Stub.asInterface(service);
			try {
				api.addListener(collectorListener);
			} catch (RemoteException e) {
				Log.e(TAG, "Failed to add listener", e);
			}
			
			updateTweetView();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "Service connection closed");			
		}
	};
	
	private TweetCollectorApi api;
	
	private TextView tweetView;
	
	private Handler handler;
	
	private TweetCollectorListener.Stub collectorListener = new TweetCollectorListener.Stub() {
		@Override
		public void handleTweetsUpdated() throws RemoteException {
			updateTweetView();
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        handler = new Handler(); // handler will be bound to the current thread (UI)
        
        tweetView = (TextView) findViewById(R.id.tweet_view);
        
        Intent intent = new Intent(TweetCollectorService.class.getName());
        
        // start the service explicitly. 
        // otherwise it will only run while the IPC connection is up.        
        startService(intent); 
        
        bindService(intent, serviceConnection, 0);

        Log.i(TAG, "Activity created");
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		try {
			api.removeListener(collectorListener);
			unbindService(serviceConnection);
		} catch (Throwable t) {
			// catch any issues, typical for destroy routines
			// even if we failed to destroy something, we need to continue destroying
			Log.w(TAG, "Failed to unbind from the service", t);
		}
		
		Log.i(TAG, "Activity destroyed");
	}
	
	private void updateTweetView() {
		// doing this in a Handler allows to call this method safely from any thread
		// see Handler docs for more info
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TweetSearchResult result = api.getLatestSearchResult();

					if (result.getTweets().isEmpty()) {
						tweetView.setText("Sorry, no tweets yet");
					} else {
						StringBuilder builder = new StringBuilder();
						for (Tweet tweet : result.getTweets()) {
							builder.append(String.format("<br><b>%s</b>: %s<br>", 
														 tweet.getAuthor(),
														 tweet.getText()));
						}
	
						tweetView.setText(Html.fromHtml(builder.toString()));
					}
				} catch (Throwable t) {
					Log.e(TAG, "Error while updating the UI with tweets", t);
				}
			}
		});
	}
}