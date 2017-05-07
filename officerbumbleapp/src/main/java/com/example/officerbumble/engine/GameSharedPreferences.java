package com.example.officerbumble.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GameSharedPreferences {

	// Used for the Key namespace
	private final static String PREF_KEY = "com.animationtechdemo.app";
				
	// Constants used to access commonly used preferences.
	public static final String FBID_PREFERENCE = "FBID";
	public static final String FIRST_NAME_PREFERENCE = "FIRST_NAME";
	public static final String LAST_NAME_PREFERENCE = "LAST_NAME";
	public static final String PROFILE_PIC_PREFERENCE = "FBPIC";
	public static final String FB_FRIENDS_PREFERENCE = "Friends";
	public static final String CRIMINALS_CAUGHT_PREFERENCE = "CriminalsCaught";
	public static final String CRIMINALS_CAUGHT_HARDCORE_PREFERENCE = "CrimianlsCaughtHardcore";
	public static final String HIGH_SCORE_PREFERENCE = "HighScore";
	public static final String HIGH_SCORE_HARDCORE_PREFERENCE = "HighScoreHardcore";
	public static final String OPEN_COUNT_PREFERENCE = "OPEN_COUNT";
		
	public static void ClearPreferences(Context _context) {
		SharedPreferences settings = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.clear();
		editor.commit();		
	}
		
	public static String ReadString(Context _context, String _key) {
		SharedPreferences settings = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		
		return settings.getString(PREF_KEY + "." + _key, "");
	}
	
	public static void WriteString(Context _context, String _key, String _value) {
		SharedPreferences settings = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString(PREF_KEY + "." + _key, _value);	
		editor.commit();
	}
	
	public static int ReadInteger(Context _context, String _key) {
		SharedPreferences settings = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		
		return settings.getInt(PREF_KEY + "." + _key, 0);
	}
	
	public static void WriteInteger(Context _context, String _key, int _value) {
		SharedPreferences settings = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		
		Editor editor = settings.edit();
		editor.putInt(PREF_KEY + "." + _key, _value);
		editor.commit();
	}
	
	public static long ReadLong(Context _context, String _key) {
		SharedPreferences settings = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		
		return settings.getLong(PREF_KEY + "." + _key, 0);
	}
	
	public static void WriteLong(Context _context, String _key, long _value) {
		SharedPreferences settings = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		
		Editor editor = settings.edit();
		editor.putLong(PREF_KEY + "." + _key, _value);
		editor.commit();
	}
	
	public static boolean ReadBoolean(Context _context, String _key) {
		SharedPreferences settings = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		
		return settings.getBoolean(PREF_KEY + "." + _key, false);
	}
	
	public static void WriteBoolean(Context _context, String _key, boolean _value) {
		SharedPreferences settings = _context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		
		Editor editor = settings.edit();
		editor.putBoolean(PREF_KEY + "." + _key, _value);	
		editor.commit();		
	}
}