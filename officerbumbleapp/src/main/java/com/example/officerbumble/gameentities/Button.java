package com.example.officerbumble.gameentities;

/*
====================================================================================================
  Button

  This class encasulates logic for three kinds of buttons:

  1. A normal button which is just an image that immediately responds to a press event.
  2. An animated button which launches a secondary animation when clicked. The press event fires after the
     secondary animation finishes playing.
  3. A toggle button which is handled by the calling scene simply changing the current animation.
====================================================================================================
*/

import java.util.ArrayList;
import java.util.List;

import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.interfaces.ButtonListener;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.SoundManager;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteSheetManager;

public class Button extends Sprite {

	private List<ButtonListener> m_buttonListeners = new ArrayList<ButtonListener>();   // List of all listeners for this button.
	private boolean m_pressed = false;                                     // Indicates if we're in the middle of a button press event.
    private String m_pressedAnimationTag = "";                             // If this button should play a secondary animation, this is the animation name.
    private final static long PRESS_ANIMATION_LENGTH = 250;                // # of ms that a button should be in the pressed state before it fires the button pressed event.
	private float m_pressedStart = 0;                                      // When the button button was originally pressed (prior to the secondary animation playing).

    // Constructor
	public Button(float x, float y, float _buttonHeight, float _buttonWidth, int _zBufferIndex, boolean _isRealTime, float _creationTimeMilliseconds, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, String _animationTag, String _pressedAnimationTag, String _tag)  {
		super(x, y, _buttonHeight, _buttonWidth, _zBufferIndex, true, DIRECTION.RIGHT, _isRealTime, _display, _spriteSheetManager, _animationTag, _tag);
							
		m_pressed = false;
        m_pressedAnimationTag = _pressedAnimationTag;

		LoadAnimations();		
		super.SetCurrentAnimation(_animationTag, _creationTimeMilliseconds);				
	}

    // Allows a client to register for button click events.
	public void RegisterForButtonClicked(Object _listener) {
		m_buttonListeners.add((ButtonListener) _listener);
	}

	@Override
	public boolean HandleTouch(boolean isPrimary, float normalizedX, float normalizedY, float realTimeMilliseconds, float gameTimeMilliseconds) {
		boolean isHandled = false;
		
		if (IsClicked(normalizedX, normalizedY)) {						
			
			if(!m_pressed) 								
			{
				isHandled = true;
				SoundManager.PlaySound("BUTTON CLICK", false);

                // If there's no secondary animation, notify listeners that button was clicked.
                if ( m_pressedAnimationTag.length() == 0 ) {
                    HandleButtonClicked(super.GetTag(), realTimeMilliseconds);
                } else {
                    // If there's a secondary animation, play the animation (notify will follow after the animation).
                    super.SetCurrentAnimation(m_pressedAnimationTag, realTimeMilliseconds);
                    m_pressedStart = realTimeMilliseconds;
                    m_pressed = true;
                }
			}			
		}		
		
		return isHandled;
	}

    // Notify all listeners about the button being pressed.
	public void HandleButtonClicked(String _tag, float _currentTimeMilliseconds) {
		for (ButtonListener listener : m_buttonListeners) {
			listener.HandleButtonPressed(super.GetTag(), _currentTimeMilliseconds);
		}
	}

    @Override
    public void Update(Timer _realTimer, Timer _gameTimer) {
        super.Update(_realTimer, _gameTimer);

        // Figure out the delta since the button was pressed.
        float delta = _realTimer.GetCurrentMilliseconds() - m_pressedStart;

        // If the delta has passed, we can notify the listeners.
        if ( m_pressed && delta >= PRESS_ANIMATION_LENGTH ) {
            m_pressed = false;
            HandleButtonClicked(super.GetTag(), _realTimer.GetCurrentMilliseconds());
        }
    }

    // Load all of the button related animations.
    private void LoadAnimations() {
        super.LoadAnimation(super.GetAnimationTag());       // Our main animation that is required.

        if ( m_pressedAnimationTag.length() > 0 ) {         // Secondary animation that's optional.
            super.LoadAnimation(m_pressedAnimationTag);
        }
    }

    // Determine if the button was pressed or not.
    private boolean IsClicked(float clickX, float clickY) {
        boolean clicked = false;

        if(clickX >= GetXNormalized() && clickY <= GetYNormalized() &&
                clickX <= GetXNormalized() + GetWidthNormalized() && clickY >= GetYNormalized() - GetHeightNormalized()) {
            clicked = true;
        }

        return clicked;
    }
}