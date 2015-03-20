package com.mindtherobot.samples.tweetservice;

import android.os.Parcel;
import android.os.Parcelable;

public final class Tweet implements Parcelable {
	
	public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
		@Override
		public Tweet createFromParcel(Parcel source) {
			return new Tweet(source);
		}

		@Override
		public Tweet[] newArray(int size) {
			return new Tweet[size];
		}
	};
	
	private String author;
	
	private String text;
	
	private String url;

	public Tweet(String author, String text, String url) {
		this.author = author;
		this.text = text;
		this.url = url;
	}
	
	private Tweet(Parcel source) {
		author = source.readString();
		text = source.readString();
		url = source.readString();
	}

	public String getAuthor() {
		return author;
	}

	public String getText() {
		return text;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(author);
		dest.writeString(text);
		dest.writeString(url);
	}
}
