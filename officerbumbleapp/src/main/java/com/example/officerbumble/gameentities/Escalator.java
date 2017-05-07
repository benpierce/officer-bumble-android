package com.example.officerbumble.gameentities;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteSheetManager;

public class Escalator extends Sprite {
	
	private final float ESCALATOR_TRAVERSAL_TIME = 1000;	// Time in milliseconds it takes to traverse the escalator.
	private boolean m_usedByBumble = false;
    private boolean m_usedByCriminal = false;

	// x and y are normalized between -1 and 1 and represents the middle point.
	public Escalator(float _x, float _y, float _height, float _width, int _zBufferIndex, float _creationTimeMilliseconds, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, String _animationTag, String _tag)  {
		super(_x, _y, _height, _width, _zBufferIndex, false, DIRECTION.RIGHT, false, _display, _spriteSheetManager, _animationTag, _tag);
				
		LoadAnimations();
		super.SetCurrentAnimation(super.GetAnimationTag(), _creationTimeMilliseconds);	// Show the only animation.
	}
	
	private void LoadAnimations() {
		super.LoadAnimation(super.GetAnimationTag());	// Static image animation is always same as its tag.		
	}	
	
	public float GetEscalatorTraversalTime() {
		return ESCALATOR_TRAVERSAL_TIME;
	}

    /*
        There's some weirdness going on in the game where the Criminal will re-use the escalator once
        he gets to the top. This results in him going up an extra level right away. It's related to a
        floating point bug where he's technically still touching the escalator when he gets to the top, but
        by such a small amount it's invisible to the naked eye. I haven't been able to track down this
        floating point bug, so for now I'm just setting these flags below so that Bumble and the Criminal
        can only use an escalator once.
     */
    public boolean UsedByBumble() {
        return m_usedByBumble;
    }

    public boolean UsedByCriminal() {
        return m_usedByCriminal;
    }

    public void SetUsedByBumble() {
        m_usedByBumble = true;
    }

    public void SetUsedByCriminal() {
        m_usedByCriminal = true;
    }
		
	@Override
	public void HandleAnimationFinished(String _tag, float _currentTimeMilliseconds) {
	}			

}
