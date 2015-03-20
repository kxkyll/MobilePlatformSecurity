package com.mindtherobot.samples.tweetservice;

import com.mindtherobot.samples.tweetservice.TweetSearchResult;
import com.mindtherobot.samples.tweetservice.TweetCollectorListener;

interface TweetCollectorApi {

	TweetSearchResult getLatestSearchResult();
	
	void addListener(TweetCollectorListener listener);

	void removeListener(TweetCollectorListener listener);
}