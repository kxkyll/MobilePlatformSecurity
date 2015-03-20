package com.mindtherobot.samples.tweetservice;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public final class TweetSearchResult implements Parcelable {

	public static final Creator<TweetSearchResult> CREATOR = new Creator<TweetSearchResult>() {
		@Override
		public TweetSearchResult[] newArray(int size) {
			return new TweetSearchResult[size];
		}
		
		@Override
		public TweetSearchResult createFromParcel(Parcel source) {
			return new TweetSearchResult(source);
		}
	};
	
	private List<Tweet> tweets;
	
	public TweetSearchResult() {
		tweets = new ArrayList<Tweet>();
	}
	
	@SuppressWarnings("unchecked")
	private TweetSearchResult(Parcel source) {
		tweets = source.readArrayList(Tweet.class.getClassLoader());
	}
	
	public void addTweet(Tweet tweet) {
		tweets.add(tweet);
	}
	
	public List<Tweet> getTweets() {
		return tweets;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(tweets);
	}

}
