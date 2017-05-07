package com.example.officerbumble.gameentities;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.gameentities.Criminal.CRIMINAL_WEAPON;

public class BowlingBall extends Weapon {
	
	private final static float BOWLING_BALL_WIDTH = 0.15f;
	private final static float BOWLING_BALL_HEIGHT = 0.15f;
	
	public BowlingBall(float _x, float _y, float _velocity, DIRECTION _direction,
			   		   int _zBufferIndex, float _creationTimeMilliseconds, DeviceDisplay _display,
			   		   SpriteSheetManager _spriteSheetManager, String _tag) {
		   super(_x, _y, BOWLING_BALL_HEIGHT, BOWLING_BALL_WIDTH, _velocity, _direction, CRIMINAL_WEAPON.BOWLING_BALL, _zBufferIndex, _creationTimeMilliseconds, _display, 
				 _spriteSheetManager, "BOWLING_BALL_" + _direction.toString(), _tag);
	}
	
	public static float GetBowlingBallWidth() {
		return BOWLING_BALL_WIDTH;
	}
	
	public static float GetBowlingBallHeight() {
		return BOWLING_BALL_HEIGHT;
	}
	
}
