package com.example.officerbumble.engine;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class LeaderboardData {
	// High Score
	private ArrayList<LeaderboardEntry> m_friendsLeaderboardHighscoreNormal = new ArrayList<LeaderboardEntry>();
	private ArrayList<LeaderboardEntry> m_globalLeaderboardHighscoreNormal = new ArrayList<LeaderboardEntry>();
	private ArrayList<LeaderboardEntry> m_friendsLeaderboardHighscoreHardcore = new ArrayList<LeaderboardEntry>();
	private ArrayList<LeaderboardEntry> m_globalLeaderboardHighscoreHardcore = new ArrayList<LeaderboardEntry>();
	
	// Criminals Caught
	private ArrayList<LeaderboardEntry> m_friendsLeaderboardCriminalsCaughtNormal = new ArrayList<LeaderboardEntry>();
	private ArrayList<LeaderboardEntry> m_globalLeaderboardCriminalsCaughtNormal = new ArrayList<LeaderboardEntry>();
	private ArrayList<LeaderboardEntry> m_friendsLeaderboardCriminalsCaughtHardcore = new ArrayList<LeaderboardEntry>();
	private ArrayList<LeaderboardEntry> m_globalLeaderboardCriminalsCaughtHardcore = new ArrayList<LeaderboardEntry>();
	
	public ArrayList<LeaderboardEntry> GetLeaderboard(boolean friendsOnly, boolean hardcore, boolean highscore) {
		if(friendsOnly && highscore && !hardcore) {
			return m_friendsLeaderboardHighscoreNormal;			
		}
		
		if(!friendsOnly && highscore && !hardcore) {
			return m_globalLeaderboardHighscoreNormal;
		}
		
		if(friendsOnly && highscore && hardcore) {
			return m_friendsLeaderboardHighscoreHardcore;
		}
		
		if(!friendsOnly && highscore && hardcore) {
			return m_globalLeaderboardHighscoreHardcore;
		}
		
		if(friendsOnly && !highscore && !hardcore) {
			return m_friendsLeaderboardCriminalsCaughtNormal;
		}
		
		if(!friendsOnly && !highscore && !hardcore) {
			return m_globalLeaderboardCriminalsCaughtNormal;
		}
		
		if(friendsOnly && !highscore && hardcore) {
			return m_friendsLeaderboardCriminalsCaughtHardcore;
		}
		
		if(!friendsOnly && !highscore && hardcore) {
			return m_globalLeaderboardCriminalsCaughtHardcore;
		}		
		
		return null;
	}
	
	// leaderboard groups split by {
	// leaderboard entries split by }
	// leaderboard values are split by |
	public LeaderboardData(String _payLoad, String _fbId) {
		String[] parts = _payLoad.split("\\{");
		int leaderboardId = 0;
		LeaderboardEntry entry;
		
		// Add the headers	
		LeaderboardEntry header = new LeaderboardEntry("RANK||NAME|||HIGH SCORE|CRIMINALS CAUGHT", _fbId);
		m_friendsLeaderboardHighscoreNormal.add(header);
		m_globalLeaderboardHighscoreNormal.add(header);
		m_friendsLeaderboardHighscoreHardcore.add(header);
		m_globalLeaderboardHighscoreHardcore.add(header);
		m_friendsLeaderboardCriminalsCaughtNormal.add(header);
		m_globalLeaderboardCriminalsCaughtNormal.add(header);
		m_friendsLeaderboardCriminalsCaughtHardcore.add(header);
		m_globalLeaderboardCriminalsCaughtHardcore.add(header);
		
		for(String _leaderboard : parts) {
			Log.w("Leaderboard", "Section: " + _leaderboard);
			String[] leaderboardEntries = _leaderboard.split("\\}");
			for(String _entry : leaderboardEntries) {									
				entry = new LeaderboardEntry(_entry, _fbId);
				if(entry != null) {
					switch(leaderboardId) {
						case 0:
							m_friendsLeaderboardHighscoreNormal.add(entry);
							break;
						case 1:
							m_globalLeaderboardHighscoreNormal.add(entry);
							break;
						case 2:
							m_friendsLeaderboardHighscoreHardcore.add(entry);
							break;
						case 3:
							m_globalLeaderboardHighscoreHardcore.add(entry);
							break;
						case 4:
							m_friendsLeaderboardCriminalsCaughtNormal.add(entry);
							break;
						case 5:
							m_globalLeaderboardCriminalsCaughtNormal.add(entry);
							break;
						case 6:
							m_friendsLeaderboardCriminalsCaughtHardcore.add(entry);
							break;
						case 7:
							m_globalLeaderboardCriminalsCaughtHardcore.add(entry);
							break;
					}
				}
			}
					
			leaderboardId++;
		}
	}
	
	// Represents a single leaderboard entry.
	public class LeaderboardEntry {
		private String m_fbId = "";
		private String m_rank = "";
		private String m_firstName = "";
		private String m_lastName = "";
		private String m_profilePic = "";
		private String m_highScore = "";
		private String m_criminalsCaught = "";
		private boolean m_isYou = false;		
		
        private Bitmap image;
        private LeaderboardAdapter sta;		// Reference to the adapter
		
		public LeaderboardEntry(String _serialized, String _fbId) {
			Log.w("Leaderboard", "Unpacking: " + _serialized);
			
			if(_serialized.length() > 0) {
				String[] parts = _serialized.split("\\|");				
				m_rank = parts[0];
				m_fbId = parts[1];
				m_firstName = parts[2];
				m_lastName = parts[3];
				m_profilePic = parts[4];
				m_highScore = parts[5];
				m_criminalsCaught = parts[6];
				if(m_fbId.equals(_fbId)) {
					m_isYou = true;
				}
			}
		}
		
		public boolean IsYou() {
			return m_isYou;
		}
		
		public String GetRank() {
			return m_rank;
		}
		
		public String GetFbId() {
			return m_fbId;
		}
		
		public String GetName() {
			return m_firstName + " " + m_lastName;
		}
		
		public String GetProfilePic() {
			return m_profilePic;
		}
		
		public String GetHighScore() {
			return m_highScore;
		}
				
		public String GetCriminalsCaught() {
			return m_criminalsCaught;
		}				
		
		public Bitmap getImage() {
			return image;
		}		
		
        public void loadImage(LeaderboardAdapter sta) {
            // HOLD A REFERENCE TO THE ADAPTER
            this.sta = sta;
            if (m_profilePic != null && !m_profilePic.equals("")) {
                new ImageLoadTask().execute(m_profilePic);
            }
        }
        
        // ASYNC TASK TO AVOID CHOKING UP UI THREAD
        private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {
     
            @Override
            protected void onPreExecute() {
                Log.i("ImageLoadTask", "Loading image...");
            }
     
            // PARAM[0] IS IMG URL
            protected Bitmap doInBackground(String... param) {
                Log.i("ImageLoadTask", "Attempting to load image URL: " + param[0]);
                try {
                    Bitmap b = getBitmapFromURL(param[0]);
                    return b;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            
            public Bitmap getBitmapFromURL(String src) {
                try {
                    URL url = new URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
     
            protected void onProgressUpdate(String... progress) {
                // NO OP
            }
     
            protected void onPostExecute(Bitmap ret) {
                if (ret != null) {
                    Log.i("ImageLoadTask", "Successfully loaded image");
                    image = ret;
                    if (sta != null) {
                        // WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
                        sta.notifyDataSetChanged();
                    }
                } else {
                    Log.e("ImageLoadTask", "Failed to load image");
                }
            }
        }            
		

	}
		
}	
