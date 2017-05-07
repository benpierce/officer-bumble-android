package com.example.officerbumble;

import java.util.ArrayList;

import com.example.officerbumble.engine.GameSharedPreferences;
import com.example.officerbumble.engine.LeaderboardAdapter;
import com.example.officerbumble.engine.LeaderboardData;
import com.example.officerbumble.engine.LeaderboardData.LeaderboardEntry;
import com.example.officerbumble.engine.LeaderboardServices;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;

public class LeaderboardActivity extends Activity
{
	private String m_fbId = "";
	private String m_friends = "";
	
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.leaderboard);
        
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }        
    	
        // Get your fbid and friend Id's
        m_fbId = GameSharedPreferences.ReadString(this, GameSharedPreferences.FBID_PREFERENCE);
        m_friends = GameSharedPreferences.ReadString(this, GameSharedPreferences.FB_FRIENDS_PREFERENCE);
        
        SetLeaderboardData();
    }   
    
    private void SetLeaderboardData() {
    	LeaderboardData data = LeaderboardServices.GetLeaderboardData(m_fbId, m_friends);
    	
    	if(data != null) { 
	    	ListView lview = (ListView) findViewById(R.id.listview);  
	        CheckBox chkHardcore = (CheckBox) findViewById(R.id.hardcoreOnly);
	        CheckBox chkFriendsOnly = (CheckBox) findViewById(R.id.friendsOnly);
	        RadioButton rdHighScore = (RadioButton) findViewById(R.id.radio_byhighscore);
	        RadioButton rdCriminalsCaught = (RadioButton) findViewById(R.id.radio_bycriminalscaught);
	        
	        // Initial view
	        ArrayList<LeaderboardEntry> lbData = data.GetLeaderboard(chkFriendsOnly.isChecked(), chkHardcore.isChecked(), rdHighScore.isChecked());
	        LeaderboardAdapter adapter = new LeaderboardAdapter(this, lbData);
	        lview.setAdapter(adapter);
	        
	        for (LeaderboardEntry entry : lbData) {
	            // START LOADING IMAGES FOR EACH STUDENT
	            entry.loadImage(adapter);
	        }
    	}
    }
    
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
    	SetLeaderboardData();    
    }
    
    public void onRadioButtonClicked(View view) {
    	SetLeaderboardData();
    }
    
    public void onExitClicked(View view) {
		super.finish();    	
    }
}