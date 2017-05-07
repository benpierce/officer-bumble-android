package com.example.officerbumble.engine;

import android.opengl.GLES20;

public class DeviceDisplay {
	public enum DISPLAY_ORIENTATION {
		PORTRAIT,
		LANDSCAPE
	}
	
	public enum ANCHOR {
		TOP_LEFT,
		TOP_CENTER,
		TOP_RIGHT,
		CENTER_LEFT,
		CENTER,
		CENTER_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_CENTER,
		BOTTOM_RIGHT
	}

    public enum ZPOSITION {
        POPUP_UI(1),
        POPUP(2),
        HUD(4),
        FOREGROUND(8),
        NORMAL(16),
        BACKGROUND_FLOOR(32),
        BACKGROUND_WALL(64),
        BACKGROUND(128),
        UNKNOWN(256);

        private int value;

        private ZPOSITION(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ZPOSITION fromString(String _name) {
            if ( _name.equals("POPUP_UI") ) {
                return ZPOSITION.POPUP_UI;
            } else if ( _name.equals("POPUP") ) {
                return ZPOSITION.POPUP;
            } else if ( _name.equals("HUD") ) {
                return ZPOSITION.HUD;
            } else if ( _name.equals("FOREGROUND") ) {
                return ZPOSITION.FOREGROUND;
            } else if ( _name.equals("NORMAL") ) {
                return ZPOSITION.NORMAL;
            } else if ( _name.equals("BACKGROUND_FLOOR") ) {
                return ZPOSITION.BACKGROUND_FLOOR;
            } else if ( _name.equals("BACKGROUND_WALL") ) {
                return ZPOSITION.BACKGROUND_WALL;
            } else if ( _name.equals("BACKGROUND") ) {
                return ZPOSITION.BACKGROUND;
            } else {
                return ZPOSITION.UNKNOWN;  // Not found!
            }
        }
    };

	private int m_width;
	private int m_height;
	private DISPLAY_ORIENTATION m_displayOrientation;
	private float m_aspectRatio;
	float[] m_projectionMatrix = new float[16];
	private Color m_clearColor;
	private float m_aspectRatioX = 0f;
	private float m_aspectRatioY = 0f;
	
	public DeviceDisplay(int _width, int _height, Color _clearColor) {		
		m_clearColor = _clearColor;
		ChangeDimensions(_width, _height);	
	}
	
	public void SetBackgroundColor(Color _clearColor) {
		m_clearColor = _clearColor;
		SetClearColor(m_clearColor.GetRed(), m_clearColor.GetGreen(), m_clearColor.GetBlue(), m_clearColor.GetAlpha());
	}
	
	public int GetWidth() {
		return m_width;
	}
	
	public int GetHeight() {
		return m_height;	
	}

	public float GetAspectRatio() {
		return m_aspectRatio;
	}

	public float GetAspectRatioX() {
		return m_aspectRatioX;
	}
	
	public float GetAspectRatioY() {
		return m_aspectRatioY;
	}
	
	public DISPLAY_ORIENTATION GetDisplayOrientation() {
		return m_displayOrientation;
	}
	
	private void ChangeDimensions(int _width, int _height) {		
		m_width = _width;
		m_height = _height;
		
		RecalculateDisplayProperties();
		ConfigureDisplay();
	}
	
	private void RecalculateDisplayProperties() {
		m_aspectRatio = CalculateAspectRatio();
		m_displayOrientation = CalculateDisplayOrientation();
		m_projectionMatrix = Matrix.GetOrthographicProjectionMatrix(this);
		
		if(m_displayOrientation == DISPLAY_ORIENTATION.LANDSCAPE) {
			m_aspectRatioX = m_aspectRatio;
		} else {
			m_aspectRatioX = 1;
		}
		
		if(m_displayOrientation == DISPLAY_ORIENTATION.LANDSCAPE) {
			m_aspectRatioY = 1;
		} else {
			m_aspectRatioY = m_aspectRatio;
		}		
	}

    // Returns a % of width of screen (in normalized values 0 to 2) by % of the screen, taking into
    // account the aspect ratio.
    public float GetNormalizedWidthByPercentageOfScreen(float _percentage) {
        return 2.0f * (_percentage / m_aspectRatioX);
    }
	
	private float CalculateAspectRatio() {
		return (m_width > m_height) ? (float) m_width / (float) m_height : (float) m_height / (float) m_width;
	}
	
	private DISPLAY_ORIENTATION CalculateDisplayOrientation() {
		return (m_width > m_height) ? DISPLAY_ORIENTATION.LANDSCAPE : DISPLAY_ORIENTATION.PORTRAIT;
	}
	
	public float[] GetOrthographicProjectionMatrix() {
		return m_projectionMatrix;
	}
	
	private void SetClearColor(float _red, float _green, float _blue, float _alpha) {
		// When we clear the screen, this value will determine what color to clear it to. 
		CoreGraphics.SetClearColor(_red, _green, _blue, _alpha);		
	}
	
	private void SetViewport(int _width, int _height) {
		CoreGraphics.SetViewport(_width, _height);
	}
	
	private void SetBlending() {
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);		
	}
		
	private void ConfigureDisplay() {		
		SetViewport(m_width, m_height);
		SetClearColor(m_clearColor.GetRed(), m_clearColor.GetGreen(), m_clearColor.GetBlue(), m_clearColor.GetAlpha());
		SetBlending();
	}
	
	public Point2D GetAnchorCoordinates(ANCHOR _anchorType, float _width, float _height) {
		return GetAnchorCoordinatesWithBoundaries(_anchorType, GetAspectRatioX() * -1, GetAspectRatioX(), GetAspectRatioY(), GetAspectRatioY() * -1, _width, _height); 
	}
	
	public Point2D GetAnchorCoordinatesWithBoundaries(ANCHOR _anchorType, float _parentLeftmostX, float _parentRightmostX, 
										float _parentTopmostY, float _parentBottommostY, float _width, float _height) {
		Point2D results = null;
		
		float centerX = _parentLeftmostX + (((_parentRightmostX - _parentLeftmostX) / 2.0f) - (_width / 2.0f));
		float centerY = _parentTopmostY - (((_parentTopmostY - _parentBottommostY) / 2.0f) - (_height / 2.0f));
		
		switch(_anchorType) {
			// Top
			case TOP_LEFT:  
				results = new Point2D(_parentLeftmostX, _parentTopmostY);
				break;
			case TOP_CENTER:											
				results = new Point2D(centerX, _parentTopmostY);
				break;				
			case TOP_RIGHT:
				results = new Point2D((_parentRightmostX - _width), _parentTopmostY);
				break;
			
			// Center
			case CENTER_LEFT:
				results = new Point2D(_parentLeftmostX, centerY);
				break;				
			case CENTER:
				results = new Point2D(centerX, centerY);
				break;				
			case CENTER_RIGHT:
				results = new Point2D((_parentRightmostX - _width), centerY);
				break;
				
			// Bottom
			case BOTTOM_LEFT:
				results = new Point2D(_parentLeftmostX, _parentBottommostY + _height);
				break;
			case BOTTOM_CENTER:
				results = new Point2D(centerX, _parentBottommostY + _height);
				break;
			case BOTTOM_RIGHT:
				results = new Point2D((_parentRightmostX - _width), _parentBottommostY + _height);
				break;				
		}

		return results;
	}
	
}
