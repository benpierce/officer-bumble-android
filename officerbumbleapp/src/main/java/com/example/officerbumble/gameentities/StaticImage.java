package com.example.officerbumble.gameentities;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteSheetManager;

public class StaticImage extends Sprite {	
	// x and y are normalized between -1 and 1 and represents the middle point.
	public StaticImage(float _x, float _y, float _height, float _width, int _zBufferIndex, boolean _isStationary, float _creationTimeMilliseconds, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, String _animationTag, String _tag)  {
		super(_x, _y, _height, _width, _zBufferIndex, _isStationary, DIRECTION.RIGHT, false, _display, _spriteSheetManager, _animationTag, _tag);
				
		LoadAnimations();
		super.SetCurrentAnimation(super.GetAnimationTag(), _creationTimeMilliseconds);	// Show the only animation.
	}
	
	private void LoadAnimations() {
		super.LoadAnimation(super.GetAnimationTag());	// Static image animation is always same as its tag.		
	}	
	
	@Override
	public void HandleAnimationFinished(String _tag, float _currentTimeMilliseconds) {
	}			
}
