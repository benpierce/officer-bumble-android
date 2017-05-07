package com.example.officerbumble.scene;

import android.content.Context;
import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.engine.Color;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.gameentities.Popup;
import com.example.officerbumble.gameentities.Popup.POPUP_TYPE;
import com.sromku.simple.fb.SimpleFacebook;

import com.example.officerbumble.interfaces.ButtonListener;
import com.example.officerbumble.interfaces.FacebookListener;
import com.example.officerbumble.services.FacebookServices;

public class FacebookInvite extends Scene implements ButtonListener, FacebookListener {
	
	private final static float LETTER_HEIGHT = 0.12f;
	private final static float LETTER_WIDTH = 0.12f;

	private SimpleFacebook m_simpleFacebook;
	private Context m_context;
	
	public FacebookInvite(DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, Timer _realTimer, Timer _gameTimer, SimpleFacebook _simpleFacebook, Context _context) {
		super(_display, _spriteSheetManager, _realTimer, _gameTimer, _context);
	
		super.SetBackgroundColor(Color.PINK);
			
		m_simpleFacebook = _simpleFacebook;
		m_context = _context;
	}

    @Override
    public void Initialize(SceneConfig _config, DifficultyManager _difficultyManager, GameStateManager _gameStateManager, AD_TYPE _adVisibility, Object _inputListener) {
        super.Initialize(_config, _difficultyManager, _gameStateManager, _adVisibility, _inputListener);

        String textString = "Bumble, the town is overrun with McBurgler Brothers. Invite some new recruits from Facebook to help clean up this city!";

        Popup popup;

        if(m_simpleFacebook.isLogin()) {
            popup = super.CreatePopup(Popup.POPUP_TYPE.INVITE_NOTHANKS, 1.4f, 1.10f, LETTER_WIDTH, LETTER_HEIGHT, 3, false, textString, this);
        } else {
            popup = super.CreatePopup(Popup.POPUP_TYPE.NOTHANKS, 1.4f, 1.10f, LETTER_WIDTH, LETTER_HEIGHT, 3, false, textString, this);
        }
        popup.Open();
    }

	public void FacebookError() {
		Popup m_facebookErrorPopup = CreatePopup(POPUP_TYPE.OK, 1.75f, 1.25f, true, "Uh oh, there was an error connecting to Facebook. Please ensure that you have an active Internet connection.", this);
		m_facebookErrorPopup.Open();
	}	
	
	public void FacebookInviteConfirm() {
		Popup m_facebookErrorPopup = CreatePopup(POPUP_TYPE.OK, 1.75f, 1.25f, true, "Great job Bumble! Don't forget to check the leaderboards from time to time to see how you're doing compared to these new recruits.", this);
		m_facebookErrorPopup.Open();
	}		
	
	@Override
	public void HandleButtonPressed(String _tag, float _currentTimeMilliseconds) {
		if(_tag.equals("BUTTON_OK")) {
            super.HandleSceneWon(_currentTimeMilliseconds);
		} else if (_tag.equals("BUTTON_INVITE")) {
			FacebookServices.HandleFriendsInvite(m_simpleFacebook, m_context, this);
		} else if (_tag.equals("BUTTON_NOTHANKS")) {
			super.HandleSceneWon(_currentTimeMilliseconds);
		}
	}

	@Override
	public void NotifySuccess() {
		FacebookInviteConfirm();		
	}

	@Override
	public void NotifyFailure() {
		FacebookError();		
	}
}
