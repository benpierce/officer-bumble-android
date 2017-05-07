package com.example.officerbumble.scene;

import com.example.officerbumble.gameentities.Popup;
import com.example.officerbumble.interfaces.ButtonListener;
import android.content.Context;
import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.engine.Color;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;

public class NextFreeMan extends Scene implements ButtonListener {

	private final static float LETTER_HEIGHT = 0.12f;
	private final static float LETTER_WIDTH = 0.12f;

    private final long m_score;
    private final long m_nextFreeLife;
	
	public NextFreeMan(long _score, long _nextFreeLife, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, Timer _realTimer, Timer _gameTimer, Context _context) {
        super(_display, _spriteSheetManager, _realTimer, _gameTimer, _context);

		_display.SetBackgroundColor(Color.PINK);
        m_score = _score;
        m_nextFreeLife = _nextFreeLife;
	}

    @Override
    public void Initialize(SceneConfig _config, DifficultyManager _difficultyManager, GameStateManager _gameStateManager, AD_TYPE _adVisibility, Object _inputListener) {
        super.Initialize(_config, _difficultyManager, _gameStateManager, _adVisibility, _inputListener);

        StringBuilder sb = new StringBuilder("");
        sb.append("Bumble! You're rocking a score of ");
        sb.append(String.valueOf(m_score));
        sb.append(". Score ");
        sb.append(String.valueOf(m_nextFreeLife));
        sb.append(" more points and we'll award you with a 1UP!");

        Popup popup = super.CreatePopup(Popup.POPUP_TYPE.OK, 1.4f, 1.0f, LETTER_WIDTH, LETTER_HEIGHT, 3, false, sb.toString(), this);
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
