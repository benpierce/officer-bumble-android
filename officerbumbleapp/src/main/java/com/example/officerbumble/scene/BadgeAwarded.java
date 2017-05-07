package com.example.officerbumble.scene;

import com.example.officerbumble.interfaces.ButtonListener;
import com.example.officerbumble.interfaces.FacebookListener;
import com.example.officerbumble.services.FacebookServices;
import android.content.Context;
import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.engine.BadgeManager.Badge;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.engine.Utility;
import com.example.officerbumble.gameentities.Popup;
import com.example.officerbumble.gameentities.Popup.POPUP_TYPE;
import com.sromku.simple.fb.SimpleFacebook;

public class BadgeAwarded extends Scene implements ButtonListener, FacebookListener {

	private final Badge m_badge;
    private final Badge m_nextBadge;
	private final SimpleFacebook m_simpleFacebook;
	private final Context m_context;

	public BadgeAwarded(Badge _badge, Badge _nextBadge, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, Timer _realTimer, Timer _gameTimer, SimpleFacebook _simpleFacebook, Context _context) {
		   super(_display, _spriteSheetManager, _realTimer, _gameTimer, _context);

		   m_badge = _badge;
           m_nextBadge = _nextBadge;
		   m_simpleFacebook = _simpleFacebook;
		   m_context = _context;
	}

    @Override
    public void Initialize(SceneConfig _config, DifficultyManager _difficultyManager, GameStateManager _gameStateManager, AD_TYPE _adVisibility, Object _inputListener) {
        super.Initialize(_config, _difficultyManager, _gameStateManager, _adVisibility, _inputListener);
        StringBuilder sb = new StringBuilder("");
        sb.append("Congratulations on your ");
        sb.append(String.valueOf(m_badge.GetCriminalsCaught()));
        sb.append(Utility.GetNumericSuffix(m_badge.GetCriminalsCaught()));
        sb.append(" career bust. Those McBurlger Brothers are really starting to get the message. ");
        sb.append("As a token of the forces' appreciation, we're promoting you to ");
        sb.append(m_badge.GetBadgeName());
        sb.append(". ");
        if(m_nextBadge != null) {
            int nextBadgeIn = m_nextBadge.GetCriminalsCaught() - m_badge.GetCriminalsCaught();
            sb.append("Arrest ");
            sb.append(String.valueOf(nextBadgeIn));
            sb.append(" more criminals and there's a ");
            sb.append(m_nextBadge.GetBadgeName());
            sb.append(" promotion in it for you.");
        }

        Popup popup;

        if(m_simpleFacebook.isLogin()) {
            popup = super.CreatePopup(POPUP_TYPE.OK_SHARE, 1.5f, 1.5f, 0.12f, 0.12f, 3, false, sb.toString(), this);
        } else {
            popup = super.CreatePopup(POPUP_TYPE.OK, 1.5f, 1.5f, 0.12f, 0.12f, 3, false, sb.toString(), this);
        }

        popup.Open();
    }

	public void FacebookError() {
		Popup m_facebookErrorPopup = CreatePopup(POPUP_TYPE.OK, 1.75f, 1.25f, true, "Uh oh, there was an error connecting to Facebook. Please ensure that you have an active Internet connection.", this);
		m_facebookErrorPopup.Open();
	}	
	
	@Override
	public void HandleButtonPressed(String _tag, float _currentTimeMilliseconds) {
		// Quit the game.
		if (_tag.equals("BUTTON_OK")) {
            super.HandleSceneWon(_currentTimeMilliseconds);
		} else if (_tag.equals("FACEBOOK_SHARE_BUTTON")) {
			FacebookServices.HandleFacebookPostBadge(m_badge, m_simpleFacebook, m_context, this);
		}
	}

	@Override
	public void NotifySuccess() {
		// Woohoo, it posted!		
	}

	@Override
	public void NotifyFailure() {
		FacebookError();		
	}		
}
