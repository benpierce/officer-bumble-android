package com.example.officerbumble.gameentities;

import java.util.ArrayList;
import java.util.List;

import com.example.officerbumble.interfaces.ButtonListener;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DeviceDisplay.ANCHOR;
import com.example.officerbumble.engine.Point2D;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteManager;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.gameentities.TextArea.FONT_TYPE;

public class Popup {

	private float m_x;
	private float m_y;
	private float m_height;
	private float m_width;
	private boolean m_useOverlay = true;
	private ButtonListener m_listener;
    private int m_zBufferIndex;
	
	private StaticImage m_popup; 
	private TextArea m_textArea;
	private StaticImage m_overlay;
	private List<Button> m_buttons = new ArrayList<Button>();
	private DeviceDisplay m_display;
	private SpriteSheetManager m_spriteSheetManager; 
	private SpriteManager m_spriteManager;
	private Timer m_timer;
    private float m_letterWidth = 0.16f;
    private float m_letterHeight = 0.16f;
	
	public enum POPUP_ANCHOR {
		BOTTOM_LEFT,
		BOTTOM_RIGHT,
		BOTTOM_CENTER,
		TOP_RIGHT
	}
	
	public enum POPUP_TYPE {
		OK,
		FACEBOOK,
		YES_NO,
		PAUSE,
		TRY_AGAIN,
		MAIN_MENU,
        OK_SHARE,
        INVITE_NOTHANKS,
        NOTHANKS,
        NONE
	}
		
	public Popup(POPUP_TYPE _type, float _width, float _height, float _letterWidth, float _letterHeight, int _zBufferIndex, boolean _useOverlay, String _text, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, SpriteManager _spriteManager, Timer _timer, ButtonListener _listener) {
		m_display = _display;
		m_spriteSheetManager = _spriteSheetManager;
		m_timer = _timer;
		m_spriteManager = _spriteManager;
		m_listener = _listener;
		m_useOverlay = _useOverlay;
        m_letterWidth = _letterWidth;
        m_letterHeight = _letterHeight;
        m_zBufferIndex = _zBufferIndex;

        // Never use overlay
        m_useOverlay = false;

		// Always centered on the screen.
		Point2D center = _display.GetAnchorCoordinates(ANCHOR.CENTER, _width, _height);
		Point2D topLeft = _display.GetAnchorCoordinates(ANCHOR.TOP_LEFT, 0f, 0f);
		
		m_x = center.GetX();
		m_y = center.GetY();
		m_height = _height;
		m_width = _width;
		
		m_overlay = new StaticImage(topLeft.GetX() - 0.25f, topLeft.GetY(), 4.0f, 4.0f, 100, true, _timer.GetCurrentMilliseconds(), _display, _spriteSheetManager, "OVERLAY", "GENERIC_POPUP_OVERLAY");
		m_popup = new StaticImage(m_x, m_y, _height, _width, _zBufferIndex + 1, true, _timer.GetCurrentMilliseconds(), _display, _spriteSheetManager, "GENERIC_POPUP", "GENERIC_POPUP");
		float textLength = TextArea.CalculateWidth(FONT_TYPE.CARTOON, _width - 0.1f, m_letterWidth, _text);
		
		if(_type == POPUP_TYPE.PAUSE) { 
			m_textArea = new TextArea(FONT_TYPE.CARTOON, m_x + ((_width / 2.0f) - (textLength / 2.0f)), m_y - 0.07f, m_letterHeight, m_letterWidth, _width - 0.1f, m_zBufferIndex, _timer.GetCurrentMilliseconds(), _display, _spriteSheetManager, _text.length(), _text, "POPUP_TEXT");
		} else {
			m_textArea = new TextArea(FONT_TYPE.CARTOON, m_x + 0.1f, m_y - 0.09f, m_letterHeight, m_letterWidth, _width - 0.1f, m_zBufferIndex, _timer.GetCurrentMilliseconds(), _display, _spriteSheetManager, _text.length(), _text, "POPUP_TEXT");
		}
		
		if(_type == POPUP_TYPE.OK) { 
			AttachButton("BUTTON_OK", "BUTTON_OK_PRESSED", "BUTTON_OK", 0.50f, 0.20f, POPUP_ANCHOR.BOTTOM_CENTER);
		} else if (_type == POPUP_TYPE.MAIN_MENU) {
			AttachButton("BUTTON_MAINMENU", "BUTTON_MAINMENU_PRESSED", "BUTTON_MAINMENU", 0.70f, 0.20f, POPUP_ANCHOR.BOTTOM_CENTER);
		} else if (_type == POPUP_TYPE.TRY_AGAIN) {
			AttachButton("BUTTON_MAINMENU", "BUTTON_MAINMENU_PRESSED", "BUTTON_MAINMENU", 0.70f, 0.20f, POPUP_ANCHOR.BOTTOM_LEFT);
			AttachButton("BUTTON_TRYAGAIN", "BUTTON_TRYAGAIN_PRESSED", "BUTTON_TRYAGAIN", 0.70f, 0.20f, POPUP_ANCHOR.BOTTOM_RIGHT);
		} else if (_type == POPUP_TYPE.PAUSE) {
			AttachButton("BUTTON_MAINMENU", "BUTTON_MAINMENU_PRESSED", "BUTTON_MAINMENU", 0.70f, 0.20f, POPUP_ANCHOR.BOTTOM_LEFT);
			AttachButton("BUTTON_RESUME", "BUTTON_RESUME_PRESSED", "BUTTON_RESUME", 0.70f, 0.20f, POPUP_ANCHOR.BOTTOM_RIGHT);
		} else if(_type == POPUP_TYPE.FACEBOOK) {			
			AttachButton("FACEBOOK_BUTTON", "", "FACEBOOK_POPUP_BUTTON", 0.70f, 0.20f, POPUP_ANCHOR.BOTTOM_CENTER);
			AttachCloseButton();
		} else if (_type == POPUP_TYPE.OK_SHARE) {
            AttachButton("BUTTON_OK", "BUTTON_OK_PRESSED", "BUTTON_OK", 0.50f, 0.20f, POPUP_ANCHOR.BOTTOM_LEFT);
            AttachButton("FACEBOOK_SHARE_BUTTON", "FACEBOOK_SHARE_BUTTON_PRESSED", "FACEBOOK_SHARE_BUTTON", 0.30f, 0.30f, POPUP_ANCHOR.BOTTOM_RIGHT);
        } else if (_type == POPUP_TYPE.INVITE_NOTHANKS) {
            AttachButton("BUTTON_NOTHANKS", "BUTTON_NOTHANKS_PRESSED", "BUTTON_NOTHANKS", 0.70f, 0.20f, POPUP_ANCHOR.BOTTOM_LEFT);
            AttachButton("BUTTON_INVITE", "", "BUTTON_INVITE", 0.30f, 0.30f, POPUP_ANCHOR.BOTTOM_RIGHT);
        } else if (_type == POPUP_TYPE.NOTHANKS) {
            AttachButton("BUTTON_NOTHANKS", "BUTTON_NOTHANKS_PRESSED", "BUTTON_NOTHANKS", 0.70f, 0.20f, POPUP_ANCHOR.BOTTOM_CENTER);
        }
	}
	
	public void Open() {
		m_spriteManager.AddSprite(m_popup);
		m_spriteManager.AddSprites(m_textArea.GetSprites());
		if(m_useOverlay) {
			m_spriteManager.AddSprite(m_overlay);
		}
		
		for(Sprite button : m_buttons) {
			m_spriteManager.AddSprite(button);
		}		
	}
	
	public void Close() {
		m_spriteManager.RemoveSprite(m_popup.GetTag());
		m_spriteManager.RemoveSprites(m_textArea.GetSprites());
		if(m_useOverlay) {
			m_spriteManager.RemoveSprite(m_overlay.GetTag());
		}
		
		for(Sprite button : m_buttons) {
			m_spriteManager.RemoveSprite(button.GetTag());
		}		
	}				
	
	private void AttachButton(String _animationTag, String _buttonAnimationTag, String _tag, float _width, float _height, POPUP_ANCHOR _anchor) {
		float x;

        Point2D point = GetPopupAnchorCoordinates(_anchor, _width, _height);
		if(_anchor == POPUP_ANCHOR.BOTTOM_LEFT) {
            x = point.GetX() + 0.08f;
        } else if (_anchor == POPUP_ANCHOR.BOTTOM_RIGHT) {
            x = point.GetX() - 0.08f;
        }
        else {
            x = point.GetX();
        }

		Button button = new Button(x, point.GetY() + 0.05f, _height, _width, 1, true, m_timer.GetCurrentMilliseconds(), m_display, m_spriteSheetManager, _animationTag, _buttonAnimationTag, _tag );
		button.RegisterForButtonClicked(m_listener);
		m_buttons.add(button);		
	}		
	
	private void AttachCloseButton() {
		Point2D point = GetPopupAnchorCoordinates(POPUP_ANCHOR.TOP_RIGHT, 0.20f, 0.20f);
		Button button = new Button(point.GetX() + 0.1f, point.GetY() + 0.1f, 0.20f, 0.20f, 1, true, m_timer.GetCurrentMilliseconds(), m_display, m_spriteSheetManager, "POPUP_CLOSE_BUTTON", "", "POPUP_CLOSE_BUTTON");
		button.RegisterForButtonClicked(m_listener);
		m_buttons.add(button);
	}
	
	private Point2D GetPopupAnchorCoordinates(POPUP_ANCHOR _anchor, float _objectWidth, float _objectHeight) {
		Point2D result = null;
		
		if(_anchor == POPUP_ANCHOR.BOTTOM_LEFT) {
			result = new Point2D(m_x, m_y - m_height + _objectHeight);						
		} else if (_anchor == POPUP_ANCHOR.BOTTOM_CENTER) {
			result = new Point2D( (m_x + (m_width / 2.0f) - (_objectWidth / 2.0f)), 
								  (m_y - m_height + _objectHeight));
		} else if (_anchor == POPUP_ANCHOR.BOTTOM_RIGHT) {
			result = new Point2D( (m_x + m_width - _objectWidth), (m_y - m_height + _objectHeight));
		} else if (_anchor == POPUP_ANCHOR.TOP_RIGHT) {
			result = new Point2D( ((m_x + m_width - _objectWidth)), (m_y));
		}
		
		return result;
	}
}
