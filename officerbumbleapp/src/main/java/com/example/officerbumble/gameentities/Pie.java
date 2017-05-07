package com.example.officerbumble.gameentities;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.gameentities.Criminal.CRIMINAL_WEAPON;

public class Pie extends Weapon {

	private final static float PIE_HEIGHT = 0.25f;
	private final static float PIE_WIDTH = 0.25f;
	
	public Pie(float _x, float _y, float _velocity, DIRECTION _direction,
			   int _zBufferIndex, float _creationTimeMilliseconds, DeviceDisplay _display,
			   SpriteSheetManager _spriteSheetManager, String _tag) {
		   super(_x, _y, PIE_HEIGHT, PIE_WIDTH, _velocity, _direction, CRIMINAL_WEAPON.PIE, _zBufferIndex, _creationTimeMilliseconds, _display, 
				 _spriteSheetManager, "PIE_" + _direction.toString(), _tag);
	}

    public static float GetPieHeight() {
        return PIE_HEIGHT;
    }

    public static float GetPieWidth() {
        return PIE_WIDTH;
    }
}
