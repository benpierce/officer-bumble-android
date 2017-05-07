package com.example.officerbumble.services;

import java.util.List;
import com.example.officerbumble.interfaces.FacebookListener;
import android.app.Activity;
import android.content.Context;
import com.example.officerbumble.engine.GameSharedPreferences;
import com.example.officerbumble.engine.Utility;
import com.example.officerbumble.engine.BadgeManager.Badge;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.entities.Profile.Properties;
import com.sromku.simple.fb.listeners.OnFriendsListener;
import com.sromku.simple.fb.listeners.OnInviteListener;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.listeners.OnPublishListener;

// This class is designed to encapsulate all of the Facebook work that Officer Bumble needs to do. 
public class FacebookServices {
		
	// Save your Facebook friends list (the ones that are using this particular application) to shared preferences so that they can be
	// used later on in the Leaderboard.
	public static void SaveFacebookFriends(final SimpleFacebook _simpleFacebook, final Context _context) {	
		
		final OnFriendsListener onFriendsListener = new OnFriendsListener() {         
		    @Override
		    public void onComplete(List<Profile> friends) {
		    	int i = 0;
		    	StringBuilder sb = new StringBuilder();
		    	
		    	for(Profile friend : friends) {
		    		if(i == 0) {
		    			sb.append(friend.getId());
		    		} else {
		    			sb.append("|");
		    			sb.append(friend.getId());
		    		}
		    		
		    		i++;
		    	}
		    	
		    	GameSharedPreferences.WriteString(_context, GameSharedPreferences.FB_FRIENDS_PREFERENCE, sb.toString());
		    }	
		};	
		
		((Activity)_context).runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
				_simpleFacebook.getFriends(onFriendsListener);
		    }
		});				
	}
	
	public static void HandleFacebookLogin(final SimpleFacebook _simpleFacebook, final Context _context, final FacebookListener _listener) {
		final OnLoginListener onLoginListener = new OnLoginListener() {
			@Override
			public void onLogin() {
				GetFacebookProfileId(_simpleFacebook, _context);								
				_listener.NotifySuccess();				
				//m_scene = m_sceneManager.StartTitleScreen(false);				
			}

			@Override
			public void onNotAcceptingPermissions(Permission.Type type) {
				_listener.NotifyFailure();
				//m_scene.FacebookError();
			}
			
			@Override
			public void onThinking() {
				// Doesn't do anything, but the OnLoginListener requires this be implemented.
			}			

			@Override
			public void onException(Throwable throwable) {
				_listener.NotifyFailure();
				//m_scene.FacebookError();
			}

			@Override
			public void onFail(String reason) {
				_listener.NotifyFailure();
				//m_scene.FacebookError();
			}

		};

		_simpleFacebook.login(onLoginListener);
	}

	public static void HandleFacebookLogout(final SimpleFacebook _simpleFacebook, final Context _context, final FacebookListener _listener) {
		final OnLogoutListener onLogoutListener = new OnLogoutListener() {
			@Override
			public void onLogout() {
				GameSharedPreferences.WriteString(_context, GameSharedPreferences.FBID_PREFERENCE, "");
				GameSharedPreferences.WriteString(_context, GameSharedPreferences.PROFILE_PIC_PREFERENCE, "");
				GameSharedPreferences.WriteString(_context, GameSharedPreferences.FIRST_NAME_PREFERENCE, "");
				GameSharedPreferences.WriteString(_context, GameSharedPreferences.LAST_NAME_PREFERENCE, "");
                _listener.NotifySuccess();
			}

			@Override
			public void onThinking() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onException(Throwable throwable) {
				_listener.NotifyFailure();
				//m_scene.FacebookError();
			}

			@Override
			public void onFail(String reason) {
				//m_scene.FacebookError();
				_listener.NotifyFailure();
			}

		};

		_simpleFacebook.logout(onLogoutListener);
	}
		
	public static void HandleFacebookPostBadge(final Badge _badge, final SimpleFacebook _simpleFacebook, final Context _context, final FacebookListener _listener) {
		final OnPublishListener onPublishListener = new OnPublishListener() {
		    @Override
		    public void onComplete(String postId) {
		        _listener.NotifySuccess();
		    }
		    
		    @Override
		    public void onFail(String reason) {
		    	//m_scene.FacebookError();
		    	_listener.NotifyFailure();
		    }
		    
		    @Override 
		    public void onException(Throwable throwable) {
		    	//m_scene.FacebookError();
		    	_listener.NotifyFailure();
		    }
		};
		
		final Feed feed = new Feed.Builder()
	    .setMessage("I've just received a promotion!")
	    .setName("Officer Bumble")
	    .setCaption("Congratulations " + _badge.GetBadgeName() + " Bumble!")
	    .setDescription("I've captured my " + _badge.GetCriminalsCaught() + Utility.GetNumericSuffix(_badge.GetCriminalsCaught()) + " career McBurgler Brother and am now a proud " + _badge.GetBadgeName())
	    .setPicture("http://www.110lbhulk.com/fbimg.png")
	    .setLink("http://www.officerbumble.com")
	    .build();
		
		((Activity)_context).runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
		    	_simpleFacebook.publish(feed, true, onPublishListener);
		    }
		});
	}
				
	public static void GetFacebookProfileId(final SimpleFacebook _simpleFacebook, final Context _context) {
		final OnProfileListener onProfileListener = new OnProfileListener() {         
		    @Override
		    public void onComplete(Profile profile) {
		    	GameSharedPreferences.WriteString(_context, GameSharedPreferences.FBID_PREFERENCE, profile.getId());
		    	GameSharedPreferences.WriteString(_context, GameSharedPreferences.PROFILE_PIC_PREFERENCE, profile.getPicture());
		    	GameSharedPreferences.WriteString(_context, GameSharedPreferences.FIRST_NAME_PREFERENCE, profile.getFirstName());
		    	GameSharedPreferences.WriteString(_context, GameSharedPreferences.LAST_NAME_PREFERENCE, profile.getLastName());
		    }

		    /* 
		     * You can override other methods here: 
		     * onThinking(), onFail(String reason), onException(Throwable throwable)
		     */     
		};

		((Activity)_context).runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
		    	Properties properties = new Properties.Builder()
		        .add(Properties.ID)
		        .add(Properties.PICTURE)
		        .add(Properties.FIRST_NAME)
		        .add(Properties.LAST_NAME)
		        .build();
		    	
		    	_simpleFacebook.getProfile(properties, onProfileListener);
		    }
		});						
	}	
	
	public static void HandleFriendsInvite(final SimpleFacebook _simpleFacebook, final Context _context, final FacebookListener _listener) {
		if(_simpleFacebook.isLogin()) {
			((Activity)_context).runOnUiThread(new Runnable() {		
				@Override
				public void run() {
					OnInviteListener onInviteListener = new OnInviteListener() {
					
					@Override
					public void onFail(String reason) {
						_listener.NotifyFailure();
					}
					
					@Override
					public void onException(Throwable throwable) {
						_listener.NotifyFailure();
					}
					
					@Override
					public void onComplete(List<String> invitedFriends, String requestId) {
						_listener.NotifySuccess();
					}
					
					@Override
					public void onCancel() {
					}
				};
				_simpleFacebook.invite("I'm inviting you to play Officer Bumble. Do you think you're up for the challenge?", onInviteListener, null);					
					
				}
			});
		}
	}	
}
