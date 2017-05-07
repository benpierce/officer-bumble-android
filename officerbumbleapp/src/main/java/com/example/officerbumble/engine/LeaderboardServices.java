package com.example.officerbumble.engine;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.example.officerbumble.engine.GameStateManager.DIFFICULTY;

public class LeaderboardServices {
	public static final String POST_SCORES_URL = "http://www.110lbhulk.com/postscore.php";
	public static final String GET_SCORES_URL = "http://www.110lbhulk.com/getleaderboard.php";
	
	public static boolean PostScore(Context _context) {
		boolean result = false;
	
		String fbId = GameSharedPreferences.ReadString(_context, GameSharedPreferences.FBID_PREFERENCE);
		String fbPic = GameSharedPreferences.ReadString(_context, GameSharedPreferences.PROFILE_PIC_PREFERENCE);
		String firstName = GameSharedPreferences.ReadString(_context, GameSharedPreferences.FIRST_NAME_PREFERENCE);
		String lastName = GameSharedPreferences.ReadString(_context, GameSharedPreferences.LAST_NAME_PREFERENCE);
		long highscoreNormal = GameSharedPreferences.ReadLong(_context, GameSharedPreferences.HIGH_SCORE_PREFERENCE);
		long highscoreHardcore = GameSharedPreferences.ReadLong(_context, GameSharedPreferences.HIGH_SCORE_HARDCORE_PREFERENCE);
		int caughtNormal = GameSharedPreferences.ReadInteger(_context, GameSharedPreferences.CRIMINALS_CAUGHT_PREFERENCE);
		int caughtHardcore = GameSharedPreferences.ReadInteger(_context, GameSharedPreferences.CRIMINALS_CAUGHT_HARDCORE_PREFERENCE);
		
		if(!fbId.equals("")) {
		
			ArrayList<NameValuePair> postParameters;
			StringBuilder sb = new StringBuilder();
	
			sb.append(fbId);
			sb.append("|");
			sb.append(CleanseData(fbPic));
			sb.append("|");
			sb.append(CleanseData(firstName));
			sb.append("|");
			sb.append(CleanseData(lastName));
			sb.append("|");
			sb.append(String.valueOf(highscoreNormal));		
			sb.append("|");
			sb.append(String.valueOf(highscoreHardcore));
			sb.append("|");
			sb.append(String.valueOf(caughtNormal));
			sb.append("|");
			sb.append(String.valueOf(caughtHardcore));		
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(POST_SCORES_URL);		
			    postParameters = new ArrayList<NameValuePair>();
			    postParameters.add(new BasicNameValuePair("scores", URLEncoder.encode(sb.toString(), "UTF-8")));
			    httppost.setEntity(new UrlEncodedFormEntity(postParameters));			
					
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();
				String responseString = EntityUtils.toString(resEntity, "UTF-8").replace("\n", "");
				
				if(responseString.equals("Success")) {
					result = true;
				}
			} catch (ClientProtocolException e) {
				Log.w("Leaderboard", e.getMessage());
			} catch (IOException e) {
				Log.w("Leaderboard", e.getMessage());
			}		
		} else {
			result = true;	// Can't save if we don't have a FacebookId.
		}
		
		return result;
	}
	
	private static String CleanseData(String _data) {
		return _data.replace("}", "").replace("|", "").replace("{", "");
	}
	
	// Saves all leaderboard related values locally. Increments the number of criminals caught by one if this is being called from a "win" scenario.
	public static void SaveLocally(Context _context, GameStateManager _gameStateManager, int _criminalsCaught, int _criminalsCaughtHardcore) {
		
		long highScore = GameSharedPreferences.ReadLong(_context, GameSharedPreferences.HIGH_SCORE_PREFERENCE);

		GameSharedPreferences.WriteInteger(_context, GameSharedPreferences.CRIMINALS_CAUGHT_PREFERENCE, _criminalsCaught);
						
		if(_gameStateManager.GetScore() > highScore) {
			GameSharedPreferences.WriteLong(_context, GameSharedPreferences.HIGH_SCORE_PREFERENCE, _gameStateManager.GetScore());
		}
		
		if(_gameStateManager.GetDifficulty() == DIFFICULTY.HARDCORE) {
			long highScoreHardcore = GameSharedPreferences.ReadLong(_context, GameSharedPreferences.HIGH_SCORE_HARDCORE_PREFERENCE);			
			GameSharedPreferences.WriteInteger(_context, GameSharedPreferences.CRIMINALS_CAUGHT_HARDCORE_PREFERENCE, _criminalsCaughtHardcore);
						
			if(_gameStateManager.GetScore() > highScoreHardcore) {
				GameSharedPreferences.WriteLong(_context, GameSharedPreferences.HIGH_SCORE_HARDCORE_PREFERENCE, _gameStateManager.GetScore());
			}			
		}		
	}		
		
	// _friendIds will be pipe separated.
	public static LeaderboardData GetLeaderboardData(String _fbId, String _friendIds) {
		LeaderboardData data = null;
		
		try {
			ArrayList<NameValuePair> postParameters;
					
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(GET_SCORES_URL);		
		    postParameters = new ArrayList<NameValuePair>();
		    postParameters.add(new BasicNameValuePair("fbid", URLEncoder.encode(_fbId, "UTF-8")));
		    postParameters.add(new BasicNameValuePair("friends", URLEncoder.encode(_friendIds, "UTF-8")));
		    httppost.setEntity(new UrlEncodedFormEntity(postParameters));			
				
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			String responseString = EntityUtils.toString(resEntity, "UTF-8").replace("\n", "");
			
			if(!responseString.equals("Failure")) {
				data = new LeaderboardData(responseString, _fbId);
			}
		} catch (ClientProtocolException e) {
		    Log.w("Leaderboard", e.getMessage());
		} catch (IOException e) {
			Log.w("Leaderboard", e.getMessage());
		}		
				
		return data;
	}		
}
