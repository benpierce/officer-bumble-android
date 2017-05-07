package com.example.officerbumble.scene;

import android.content.Context;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;

public class SplashScreen extends Scene {

	public SplashScreen(DeviceDisplay _display, SpriteSheetManager _spriteSheetMananger, Timer _realTimer, Timer _gameTimer, Context _context) {
		super(_display, _spriteSheetMananger, _realTimer, _gameTimer, _context);
	}

}
