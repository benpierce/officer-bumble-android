package com.example.officerbumble.gameentities;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.DeviceDisplay.ANCHOR;
import com.example.officerbumble.engine.SpriteManager;
import com.example.officerbumble.engine.Point2D;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.gameentities.TextArea.FONT_TYPE;

public class Toast {

	// Constants
	private float CHARACTER_HEIGHT = 0.35f;
	private float CHARACTER_WIDTH = 0.35f;
    private float TIMER_CHARACTER_HEIGHT = 0.15f;
    private float TIMER_CHARACTER_WIDTH = 0.15f;
    private float TOUCH_WIDTH = 0.60f;
    private float TOUCH_HEIGHT = 0.60f;
	
	private TextArea m_toastString;		// The actual toast string TextArea.
    private TextArea m_timerString;     // The timer string
    private StaticImage m_tapJump;
    private StaticImage m_tapDuck;

	private float m_displayStart = 0;	// Start time (in ms) the toast should be displayed.
	private long m_displayFor = 0;		// Ms
    private long m_lastSecond = 0;
	private DeviceDisplay m_display;
	private SpriteSheetManager m_spriteSheetManager;
	private SpriteManager m_spriteManager;
    private boolean m_showTimer = false;

	// Whether or not the toast has already been removed.
	private boolean m_isRemoved = false;
	
	public Toast(GameStateManager _gameState, float _currentTimeMilliseconds, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, SpriteManager _spriteManager) {
		m_display = _display;
		m_spriteSheetManager = _spriteSheetManager;
		m_spriteManager = _spriteManager;
	}			
		
	public void RefreshSprites(float _realTimeMilliseconds) {

        // If we should remove the toast text from the screen.
        if (!m_isRemoved && !IsDisplayed(_realTimeMilliseconds)) {
            RemoveAll();
        }

        // May also have to update the timer text.
        if ( !m_isRemoved && m_showTimer ) {
            RefreshTimerText(_realTimeMilliseconds);
        }
    }

    public void DisplayText(String _text, float _realTimeMilliseconds, boolean _showTimer, long _displayLength) {
        m_displayStart = _realTimeMilliseconds;
        m_displayFor = _displayLength;
        m_showTimer = _showTimer;

        float x = (TextArea.CalculateWidth(FONT_TYPE.CARTOON, (m_display.GetAspectRatioX() * 2), CHARACTER_WIDTH, _text) / 2.0f) * -1;
        float y = (CHARACTER_HEIGHT / 2.0f);
        m_toastString = new TextArea(FONT_TYPE.CARTOON, x, y, CHARACTER_HEIGHT, CHARACTER_WIDTH, m_display.GetAspectRatioX() * 2.0f, 2, _realTimeMilliseconds, m_display, m_spriteSheetManager, _text.length(), _text, "TOAST");

        m_isRemoved = false;

        m_spriteManager.AddSprites(m_toastString.GetSprites());

        if ( _showTimer ) {
            RefreshTimerText(_realTimeMilliseconds);
            ShowTapJump(_realTimeMilliseconds);
            ShowTapDuck(_realTimeMilliseconds);
        }
    }

    private void ShowTapJump(float _realTimeMilliseconds) {
        Point2D position = m_display.GetAnchorCoordinates(ANCHOR.CENTER_RIGHT, TOUCH_WIDTH, TOUCH_HEIGHT);

        m_tapJump = new StaticImage(position.GetX(), position.GetY(), TOUCH_WIDTH, TOUCH_HEIGHT, 2, true, _realTimeMilliseconds, m_display, m_spriteSheetManager, "TOUCH_JUMP", "TOUCH_JUMP");
        m_spriteManager.AddSprite(m_tapJump);
    }

    private void ShowTapDuck(float _realTimeMilliseconds) {
        Point2D position = m_display.GetAnchorCoordinates(ANCHOR.CENTER_LEFT, TOUCH_WIDTH, TOUCH_HEIGHT);

        m_tapDuck = new StaticImage(position.GetX(), position.GetY(), TOUCH_WIDTH, TOUCH_HEIGHT, 2, true, _realTimeMilliseconds, m_display, m_spriteSheetManager, "TOUCH_DUCK", "TOUCH_DUCK");
        m_spriteManager.AddSprite(m_tapDuck);
    }

    private void RefreshTimerText(float _realTimeMilliseconds) {
        long currentSecond = (long)((m_displayFor / 1000.0f) - (( _realTimeMilliseconds - m_displayStart) / 1000.0f)) + 1;

        if ( currentSecond != m_lastSecond && currentSecond <= 0) {
            if (m_timerString != null) {
                m_spriteManager.RemoveSprites(m_timerString.GetSprites());
            }
        } else if ( currentSecond != m_lastSecond ) {
            String timerText = String.valueOf(currentSecond);
            float timerx = (TextArea.CalculateWidth(FONT_TYPE.CARTOON, (m_display.GetAspectRatioX() * 2), TIMER_CHARACTER_WIDTH, timerText) / 2.0f) * -1;
            float timery = (CHARACTER_HEIGHT / 2.0f) - (TIMER_CHARACTER_HEIGHT * 2);

            if (m_timerString != null) {
                m_spriteManager.RemoveSprites(m_timerString.GetSprites());
            }
            m_timerString = new TextArea(FONT_TYPE.CARTOON, timerx, timery, TIMER_CHARACTER_HEIGHT, TIMER_CHARACTER_WIDTH, m_display.GetAspectRatioX() * 2.0f, 2, _realTimeMilliseconds, m_display, m_spriteSheetManager, timerText.length(), timerText, "TIMERTEXT");
            m_spriteManager.AddSprites(m_timerString.GetSprites());
        }

        m_lastSecond = currentSecond;
    }

	public void DisplayText(String _text, float _currentTimeMilliseconds, long _displayLength) {
		DisplayText(_text, _currentTimeMilliseconds, false, _displayLength);
	}

	private void RemoveAll() {
        if ( m_toastString != null ) {
            m_spriteManager.RemoveSprites(m_toastString.GetSprites());
        }
        if ( m_timerString != null ) {
            m_spriteManager.RemoveSprites(m_timerString.GetSprites());
        }
        if ( m_tapJump != null ) {
            m_spriteManager.RemoveSprite(m_tapJump.GetTag());
        }
        if ( m_tapDuck != null ) {
            m_spriteManager.RemoveSprite(m_tapDuck.GetTag());
        }

		m_isRemoved = true;
	}
		
	private boolean IsDisplayed(float _currentTimeMilliseconds) {
		return ((_currentTimeMilliseconds - m_displayStart)) <= m_displayFor ? true : false;
	}	
}
