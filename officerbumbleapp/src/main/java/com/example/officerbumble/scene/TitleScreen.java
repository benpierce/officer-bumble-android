package com.example.officerbumble.scene;

import com.example.officerbumble.interfaces.ButtonListener;
import com.example.officerbumble.interfaces.FacebookListener;
import com.example.officerbumble.services.FacebookServices;
import android.content.Context;
import android.content.Intent;
import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.LeaderboardActivity;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DeviceDisplay.ANCHOR;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.Point2D;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.engine.GameStateManager.DIFFICULTY;
import com.example.officerbumble.gameentities.Button;
import com.example.officerbumble.gameentities.Popup;
import com.example.officerbumble.gameentities.Popup.POPUP_TYPE;
import com.sromku.simple.fb.SimpleFacebook;

public class TitleScreen extends Scene implements ButtonListener, FacebookListener {

	private final float FACEBOOK_BUTTON_HEIGHT = 0.30f;
	private final float FACEBOOK_BUTTON_WIDTH = 0.30f;
	
	private SimpleFacebook m_simpleFacebook;
	private boolean m_lastFBStatus = false;
	private Button m_facebookLoginButton;
	private Button m_facebookLogoutButton;
	private Popup m_facebookPopup;
	private Popup m_facebookErrorPopup;
	private Context m_context;
	private boolean m_showFacebookPrompt = false;
	
	// Constructor
	public TitleScreen(DeviceDisplay _display, SpriteSheetManager _spriteSheetMananger, Timer _realTimer, Timer _gameTimer, SimpleFacebook _simpleFacebook, boolean _showFacebookPrompt, Context _context) {
		super(_display, _spriteSheetMananger, _realTimer, _gameTimer, _context);
		
		m_context = _context;
		m_simpleFacebook = _simpleFacebook;
		m_showFacebookPrompt = _showFacebookPrompt;
	}
	
	@Override
	public void Initialize(SceneConfig _config, DifficultyManager _difficultyManager, GameStateManager _gameStateManager, AD_TYPE _adVisibility, Object _inputListener) {
		super.Initialize(_config, _difficultyManager, _gameStateManager, _adVisibility, _inputListener);
		
		if(m_showFacebookPrompt) {
			m_facebookPopup = super.CreatePopup(POPUP_TYPE.FACEBOOK, 2.0f, 1.2f, true, "Connect with Facebook and play against friends and strangers. How do you stack up against the best officers in the world?", this);
			m_facebookPopup.Open();
		}		
		
		UpdateFacebookButtons();
	}	
	
	public void FacebookError() {
		if(m_facebookPopup != null) {
			m_facebookPopup.Close();
		}
		
		m_facebookErrorPopup = CreatePopup(POPUP_TYPE.OK, 1.75f, 1.25f, true, "Uh oh, there was an error connecting to Facebook. Please ensure that you have an active Internet connection.", this);
		m_facebookErrorPopup.Open();		
	}
	
	
	@Override
	public void UpdateObjects() {
        if(m_lastFBStatus != m_simpleFacebook.isLogin()) {
			UpdateFacebookButtons();
			m_lastFBStatus = m_simpleFacebook.isLogin();
		}
		
		super.UpdateObjects();
	}
	
	// One facebook button, we can just swap the animations.
	private void UpdateFacebookButtons() {
		Point2D coordinates = super.GetDisplay().GetAnchorCoordinates(ANCHOR.BOTTOM_RIGHT, FACEBOOK_BUTTON_WIDTH, FACEBOOK_BUTTON_HEIGHT);
						
		if(m_simpleFacebook.isLogin()) {
			if(m_facebookLoginButton != null) {
				super.RemoveSprite(m_facebookLoginButton.GetTag());
			}
			
			m_facebookLogoutButton = super.CreateButton(coordinates.GetX(), coordinates.GetY(), FACEBOOK_BUTTON_HEIGHT, FACEBOOK_BUTTON_WIDTH, this, "FACEBOOK_LOGOUT_BUTTON", "", "FACEBOOK_LOGOUT_BUTTON");
			super.AddSprite(m_facebookLogoutButton);
		} else {
			if(m_facebookLogoutButton != null) {
				super.RemoveSprite(m_facebookLogoutButton.GetTag());
			}
			
			m_facebookLoginButton = super.CreateButton(coordinates.GetX(), coordinates.GetY(), FACEBOOK_BUTTON_HEIGHT, FACEBOOK_BUTTON_WIDTH, this, "FACEBOOK_BUTTON", "", "FACEBOOK_BUTTON");
			super.AddSprite(m_facebookLoginButton);
		}
	}
	
	private void ShowLeaderboard() {
		// If they're not logged in, we just need to show a popup to inform them they should log in.

        if(!m_simpleFacebook.isLogin()) {
			m_facebookErrorPopup = new Popup(POPUP_TYPE.OK, 1.5f, 0.90f, 0.16f, 0.16f, 1, true, "Leaderboards require you to be logged into Facebook", super.GetDisplay(), super.GetSpriteSheetManager(), super.GetSpriteManager(), super.GetRealTimer(), this);
			m_facebookErrorPopup.Open();
		} else {
			// Store facebookfriends in preferences.
			FacebookServices.SaveFacebookFriends(m_simpleFacebook, m_context);
			
			Intent intent = new Intent(m_context, LeaderboardActivity.class);
	        m_context.startActivity(intent);
		}
    }
		
	@Override
	public void HandleButtonPressed(String _tag, float _currentTimeMilliseconds) {

		// Quit the game.
		if (_tag.equals("BUTTON_QUIT_GAME")) {
			super.HandleSceneQuitGame();			
		}
		
		if(_tag.equals("POPUP_CLOSE_BUTTON")) {
			m_facebookPopup.Close();
		}
		
		if(_tag.equals("BUTTON_OK")) {
			m_facebookErrorPopup.Close();			
		}
		
		// Facebook login		
		if (_tag.equals("FACEBOOK_BUTTON") || _tag.equals("FACEBOOK_POPUP_BUTTON")) {
			FacebookServices.HandleFacebookLogin(m_simpleFacebook, m_context, this);			
		} 
		
		if (_tag.equals("FACEBOOK_LOGOUT_BUTTON")) {
			FacebookServices.HandleFacebookLogout(m_simpleFacebook, m_context, this);
		}
		
		// Handle difficulty selection.
		if (_tag.equals("BUTTON_EASY")) {
			super.HandleDifficultySelected(DIFFICULTY.EASY, _currentTimeMilliseconds);			
		} else if (_tag.equals("BUTTON_NORMAL")) {
			super.HandleDifficultySelected(DIFFICULTY.NORMAL, _currentTimeMilliseconds);
		} else if (_tag.equals("BUTTON_HARD")) {
			super.HandleDifficultySelected(DIFFICULTY.HARD, _currentTimeMilliseconds);
		} else if (_tag.equals("BUTTON_HARDCORE")) {			
			super.HandleDifficultySelected(DIFFICULTY.HARDCORE, _currentTimeMilliseconds);
		} else if (_tag.equals("BUTTON_TRAINING")) {
            super.HandleTrainingSelected();
		} else if (_tag.equals("BUTTON_LEADERBOARD")) {
			ShowLeaderboard();
		}
	}

	@Override
	public void NotifySuccess() {
        UpdateFacebookButtons();
	}

	@Override
	public void NotifyFailure() {
		FacebookError();
	}			
}
