package com.mindtherobot.samples.tweetservice;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.FEATURE_PROCESS_NAMESPACES;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public final class TweetSearcher {

	private static final String TAG = TweetSearcher.class.getSimpleName();
	
	private static XmlPullParserFactory xmlFactory;
	
	static {
		try {
			xmlFactory = XmlPullParserFactory.newInstance();
		} catch (XmlPullParserException e) {
			throw new RuntimeException(e); // crash if we couldn't create the XPPF
		}
	}
	
	public TweetSearchResult search() throws ClientProtocolException, IOException, XmlPullParserException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet getMethod = new HttpGet("http://search.twitter.com/search.atom?lang=en&q=%23android");
		HttpResponse response = httpClient.execute(getMethod);
		InputStream responseStream = response.getEntity().getContent();
		try {
			XmlPullParser xml = xmlFactory.newPullParser();
			xml.setFeature(FEATURE_PROCESS_NAMESPACES, true);
			xml.setInput(responseStream, "UTF-8");
			
			TweetSearchResult result = new TweetSearchResult();
			
			String author = null;
			String text = null;
			String url = null;
			
			boolean done = false;
			while (! done) {
				switch (xml.next()) {
				case END_TAG:
					if ("feed".equals(xml.getName())) {
						done = true;
					} else if ("entry".equals(xml.getName())) {
						Tweet tweet = new Tweet(author, text, url);
						result.addTweet(tweet);
						
						author = null;
						text = null;
						url = null;
						
						if (result.getTweets().size() >= 5) {
							done = true;
						}
					}
					break;
				case START_TAG:
					String tag = xml.getName();
					if ("title".equals(tag)) {
						text = xml.nextText();
					} else if ("name".equals(tag)) {
						author = xml.nextText();
					} else if ("link".equals(tag) && "text/html".equals(xml.getAttributeValue(0))) {
						url = xml.getAttributeValue(1);
					}
					break;
				}
			}
			
			return result;
		} finally {
			try {
				responseStream.close();
			} catch (Throwable t) {
				Log.w(TAG, "Failed to close stream", t);
			}
		}
	}
}
