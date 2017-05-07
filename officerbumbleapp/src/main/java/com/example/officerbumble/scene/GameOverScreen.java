package com.example.officerbumble.scene;

import com.example.officerbumble.interfaces.ButtonListener;
import android.content.Context;
import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.gameentities.Popup;
import com.example.officerbumble.gameentities.Popup.POPUP_TYPE;

public class GameOverScreen extends Scene implements ButtonListener {

	public GameOverScreen(DeviceDisplay _display, SpriteSheetManager _spriteSheetMananger, Timer _realTimer, Timer _gameTimer, Context _context) {
		   super(_display, _spriteSheetMananger, _realTimer, _gameTimer, _context);
	}

    @Override
    public void Initialize(SceneConfig _config, DifficultyManager _difficultyManager, GameStateManager _gameStateManager, AD_TYPE _adVisibility, Object _inputListener) {
        super.Initialize(_config, _difficultyManager, _gameStateManager, _adVisibility, _inputListener);

        Popup popup = super.CreatePopup(POPUP_TYPE.TRY_AGAIN, 1.6f, 1.2f, 0.14f, 0.14f, 4, false, "Bumble you let that McBurgler Brother get away! Collect your wits and report back to duty as soon as possible!", this);
        popup.Open();
    }

	@Override
	public void HandleButtonPressed(String _tag, float _currentTimeMilliseconds) {

		// Quit the game.
		if (_tag.equals("BUTTON_MAINMENU")) {
			super.HandleQuitToTitle();			
		}
		
		// Quit the game.
		if (_tag.equals("BUTTON_TRYAGAIN")) {
			super.HandleRestartGame();			
		}
		
	}		

}
