package com.example.officerbumble.gameentities;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteSheetManager;

public class Treadmill extends Sprite {

	public Treadmill(float _x, float _y, float _height, float _width, int _zBufferIndex, boolean _isStationary, float _currentTimeMilliseconds,
					 DeviceDisplay _deviceDisplay, SpriteSheetManager _spriteSheetManager,
					 String _animationTag, String _tag) {
		super(_x, _y, _height, _width, _zBufferIndex, _isStationary, DIRECTION.RIGHT, false, _deviceDisplay, _spriteSheetManager, _animationTag, _tag);

		super.LoadAnimation("TREADMILL");
		super.LoadAnimation("TREADMILL_STOPPED");
		
		super.SetCurrentAnimation("TREADMILL", _currentTimeMilliseconds);
	}
	
	public void Stop(float _currentTimeMilliseconds) {
		super.SetCurrentAnimation("TREADMILL_STOPPED", _currentTimeMilliseconds);
	}
	
	public void Start(float _currentTimeMilliseconds) {
		super.SetCurrentAnimation("TREADMILL", _currentTimeMilliseconds);
	}
	
	
	
}
