package com.example.officerbumble.scene;

import com.example.officerbumble.gameentities.Popup;
import com.example.officerbumble.interfaces.ButtonListener;
import android.content.Context;

import com.example.officerbumble.engine.BadgeManager.Badge;
import com.example.officerbumble.engine.Color;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.engine.Utility;
import com.example.officerbumble.MainActivity.AD_TYPE;

public class NextBadge extends Scene implements ButtonListener {

	private final static float LETTER_HEIGHT = 0.12f;
	private final static float LETTER_WIDTH = 0.12f;

    private final int m_criminalsCaught;
    private final Badge m_nextBadge;
	
	public NextBadge(int _criminalsCaught, Badge _nextBadge, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, Timer _realTimer, Timer _gameTimer, Context _context) {
        super(_display, _spriteSheetManager, _realTimer, _gameTimer, _context);

		_display.SetBackgroundColor(Color.PINK);
        m_criminalsCaught = _criminalsCaught;
        m_nextBadge = _nextBadge;
	}

    @Override
    public void Initialize(SceneConfig _config, DifficultyManager _difficultyManager, GameStateManager _gameStateManager, AD_TYPE _adVisibility, Object _inputListener) {
        super.Initialize(_config, _difficultyManager, _gameStateManager, _adVisibility, _inputListener);

        StringBuilder sb = new StringBuilder("");
        sb.append("Keep up the good work Bumble! You've caught your ");
        sb.append(String.valueOf(m_criminalsCaught));
        sb.append(Utility.GetNumericSuffix(m_criminalsCaught));
        sb.append(" McBurgler Brother. ");
        if(m_nextBadge != null) {
            int nextBadgeIn = m_nextBadge.GetCriminalsCaught() - m_criminalsCaught;
            sb.append("Arrest ");
            sb.append(String.valueOf(nextBadgeIn));
            sb.append(" more McBurgler ");
            if(nextBadgeIn == 1) {
                sb.append("Brother");
            } else {
                sb.append("Brothers");
            }
            sb.append(" and there's a ");
            sb.append(m_nextBadge.GetBadgeName());
            sb.append(" promotion in it for you!");
        }

        Popup popup = super.CreatePopup(Popup.POPUP_TYPE.OK, 1.6f, 1.20f, LETTER_WIDTH, LETTER_HEIGHT, 3, false, sb.toString(), this);
        popup.Open();
    }
	
	@Override
	public void HandleButtonPressed(String _tag, float _currentTimeMilliseconds) {
		// Quit the game.
		if (_tag.equals("BUTTON_OK")) {
			super.HandleSceneWon(_currentTimeMilliseconds);			
		}
	}		
}
