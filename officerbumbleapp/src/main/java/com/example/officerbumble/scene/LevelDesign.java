package com.example.officerbumble.scene;

import android.content.Context;
import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;

public class LevelDesign extends Scene {

    public LevelDesign(DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, Timer _realTimer, Timer _gameTimer, Context _context) {
        super(_display, _spriteSheetManager, _realTimer, _gameTimer, _context);
    }

    @Override
    public void Initialize(SceneConfig _config, DifficultyManager _difficultyManager, GameStateManager _gameStateManager, AD_TYPE _adVisibility, Object _inputListener) {
        super.Initialize(_config, _difficultyManager, _gameStateManager, _adVisibility, _inputListener);

    }
}
