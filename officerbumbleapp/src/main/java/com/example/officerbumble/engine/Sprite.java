package com.example.officerbumble.engine;

import java.util.HashMap;

import com.example.officerbumble.interfaces.TouchListener;
import com.example.officerbumble.interfaces.TouchReleaseListener;
import com.example.officerbumble.interfaces.AnimationListener;

public class Sprite implements TouchListener, TouchReleaseListener, AnimationListener  {
		 		
	// Sprite Constants
	private final float m_height;
	private final float m_width;
	private float m_x;
	private float m_y;	
	private String m_tag = "";
	private String m_animationTag = "";
	public int m_zBufferIndex;
	private boolean m_isRealTime = false;
	
	// Current Animation Information	 
	private SpriteSheetManager m_spriteSheetManager = null;
	private HashMap<String, Animation> m_animations = new HashMap<String, Animation>();
	private Animation m_currentAnimation = null;	
	private int m_framesPerSecond;	
	private int m_frameTimeMilliseconds;
	private float m_lastFrameUpdateMilliseconds = 0;			// Represents the last time we updated an animation frame.
	public int m_textureId = 0;
		
	// Graphics related members variables. 
	private VertexBuffer2D m_vertexBuffer;
	private DeviceDisplay m_deviceDisplay;
				
	public enum DIRECTION {
		LEFT,
		RIGHT
	}
	
	/********** Waypoint Related Variables ********/
	private DIRECTION m_waypointDirection;
	private boolean m_isWaypointXFinished = true;
	private boolean m_isWaypointYFinished = true;
	private float m_waypointStartTime = 0;
	private float m_waypointTraversalTime = 0;
	private float m_waypointX = 0.0f;
	private float m_waypointY = 0.0f;
	private float m_waypointLastX = 0.0f;
	private float m_waypointLastY = 0.0f;
	public boolean m_isStationary;

	// Performance Caching Variables
	private final float SCREEN_LEFT_BOUNDS;
	private final float SCREEN_RIGHT_BOUNDS;
	private final float SCREEN_TOP_BOUNDS;
	private final float SCREEN_BOTTOM_BOUNDS;	
	private TextureCoordinate m_tempTextureCoordinate;
	private boolean m_isAnimated = false;
	private DIRECTION m_direction;
	
	private int m_vertexCount = 0;
	private boolean m_isDirty = false;
	
	public Sprite(float _x, float _y, float _height, float _width, int _zBufferIndex, boolean _isStationary, DIRECTION _direction, boolean _isRealTime, DeviceDisplay _deviceDisplay, SpriteSheetManager _spriteSheetManager, String _animationTag, String _tag) {
		m_tag = _tag;				
		m_animationTag = _animationTag;
		m_height = _height;
		m_width = _width;
		m_spriteSheetManager = _spriteSheetManager;
		m_deviceDisplay = _deviceDisplay;
		m_x = _x;
		m_y = _y;		
		m_zBufferIndex = _zBufferIndex;
		m_isStationary = _isStationary;
		m_isRealTime = _isRealTime;
		m_isDirty = true;
		m_vertexBuffer = new VertexBuffer2D(24);	// Each quad has 6 verticies with 4 data points each.
		m_direction = _direction;
		
		// Cached Variables for Performance.
		SCREEN_LEFT_BOUNDS = m_deviceDisplay.GetAspectRatioX() * -1; // a is left of b
	    SCREEN_RIGHT_BOUNDS = m_deviceDisplay.GetAspectRatioX(); // a is right of b
	    SCREEN_TOP_BOUNDS = m_deviceDisplay.GetAspectRatioY(); // a is above b
	    SCREEN_BOTTOM_BOUNDS = m_deviceDisplay.GetAspectRatioY() * -1; // a is below b				
	}

	public int GetZBufferIndex() {
		return m_zBufferIndex;
	}
	
	public String GetTag() {
		return m_tag;
	}
	
	public String GetAnimationTag() {
		return m_animationTag;
	}
	
	public DeviceDisplay GetDeviceDisplay() {
		return m_deviceDisplay;
	}

	public boolean UsesRealTime() {
		return m_isRealTime;
	}
	
	public int GetFramesPerSecond() {
		return m_framesPerSecond;
	}
	
	public int GetCurrentFrame() {
		return m_currentAnimation.GetFrame();
	}

	public TextureShaderProgram GetTextureShaderProgram() {
		return m_spriteSheetManager.GetTextureShaderProgram();
	}
	
	//public int GetTexture() {
	//	return m_textureId;
	//}
	
	protected void LoadAnimation(String _name) {		
		
		AnimationConfiguration config = m_spriteSheetManager.GetAnimationConfiguration(_name);
		if(config == null) {
			throw new RuntimeException("Attempted to load animation(" + _name + ") but that animation doesn't exist in the configuration file!");
		}
		
		// If the texture Id is 0, that means that the scene didn't do it's job loading the needed texture into memory, so we need
		// to throw an error since this sprite can't display itself!
		if(config.GetTextureId() == 0) {
			throw new RuntimeException("Attempted to load animation(" + _name + ") but the texture atlas " + config.GetTextureResourceName() + " hasn't been loaded into memory!");
		}
				
		Animation animation = new Animation(config, _name);							
		m_animations.put(_name, animation);
		
		animation.RegisterForAnimationFinishedEvents(this);
	}
	
	public void RefreshTextureId() {
		m_textureId = m_spriteSheetManager.GetAnimationConfiguration(m_currentAnimation.GetName()).GetTextureId();
	}
			
	public void SetCurrentAnimation(String _name, float _startTimeMilliseconds) {
		Animation animation = m_animations.get(_name);

		if(animation == null) {
			// Try to load it.
			LoadAnimation(_name);
			animation = m_animations.get(_name);
		}
		
		if(animation != null) {
			m_currentAnimation = animation;
			m_textureId = m_spriteSheetManager.GetAnimationConfiguration(m_currentAnimation.GetName()).GetTextureId();
			m_isAnimated = m_currentAnimation.IsAnimated();
			m_currentAnimation.ResetAnimation();
			m_framesPerSecond = m_currentAnimation.GetFramesPerSecond();
			m_frameTimeMilliseconds = 1000 / m_framesPerSecond;
			m_lastFrameUpdateMilliseconds = _startTimeMilliseconds;	// Frame has never been updated.
		} else {
			throw new RuntimeException("Attempted to load animation(" + _name + ") but that animation doesn't exist");
		}
		
		m_isDirty = true;
	}
			
	private void HandleAnimation(float _currentMilliseconds) {
		int framesToAdvance = GetFramesToAdvance(_currentMilliseconds);
		
		if(framesToAdvance > 0) 
		{			
			m_currentAnimation.IncrementFrame(framesToAdvance, _currentMilliseconds);			
			m_lastFrameUpdateMilliseconds = _currentMilliseconds;			
			m_isDirty = true;
		}								
	}
				
	// This will refresh the vertex array, assuming that the vertex array represents a textured triangle fan.
	private void RefreshVertexArray() {
		m_tempTextureCoordinate = m_currentAnimation.GetCurrentTextureCoordinate();
		
		m_vertexBuffer.SetTexturedQuadValues(m_x, m_y, m_tempTextureCoordinate.GetTextureNormalizedX1(), m_tempTextureCoordinate.GetTextureNormalizedY1(), 
											 m_x + m_width, m_y, m_tempTextureCoordinate.GetTextureNormalizedX2(), m_tempTextureCoordinate.GetTextureNormalizedY1(), 
											 m_x + m_width, m_y - m_height, m_tempTextureCoordinate.GetTextureNormalizedX2(), m_tempTextureCoordinate.GetTextureNormalizedY2(),
											 m_x, m_y - m_height, m_tempTextureCoordinate.GetTextureNormalizedX1(), m_tempTextureCoordinate.GetTextureNormalizedY2());											
	}

	public boolean IsOnScreen() {
        boolean result = true;

        if ( m_x > SCREEN_RIGHT_BOUNDS ) {
            result = false;
        } else if ( m_x + m_width < SCREEN_LEFT_BOUNDS ) {
            result = false;
        } else if ( m_y - m_height >= SCREEN_TOP_BOUNDS ) {
            result = false;
        } else if ( m_y < SCREEN_BOTTOM_BOUNDS ) {
            result = false;
        } else {
            result = true;
        }

        if ( result == true ) {
            result = true;
        }
        /*
        if (m_x > SCREEN_RIGHT_BOUNDS) return false; // a is right of b
        if ((m_x + m_width) < SCREEN_LEFT_BOUNDS) return false; // a is left of b
	    if ((m_y - m_height) > SCREEN_TOP_BOUNDS) return false; // a is above b
	    if (m_y < SCREEN_BOTTOM_BOUNDS) return false; // a is below b
	    */
	    return result; // boxes overlap
	}
		
	public VertexBuffer2D GetVertexArrayToDraw() {				
		if(IsOnScreen()) {
			// This only has to happen if there's been a change, which in most cases for the HUD won't be the case.
			if(m_isDirty) {
				RefreshVertexArray();		
				m_vertexCount = 24;
				m_isDirty = false;
			}
						
			return m_vertexBuffer;
		}
		
		return null;
	}
					
	// Based on the variance in time and frames per second, determine how many frames we need to advance.
	private int GetFramesToAdvance(float _currentMilliseconds) {
		int framesToAdvance = 0;
		
		// If the sprite isn't animated, we never want to change it's frame, even if it has other frames.
		if (m_isAnimated) {
			float millisecondsBetweenCalls = _currentMilliseconds - m_lastFrameUpdateMilliseconds;
			if(millisecondsBetweenCalls > m_frameTimeMilliseconds) {			
				framesToAdvance = (int) (millisecondsBetweenCalls / m_frameTimeMilliseconds);
			}
		}
				
		return framesToAdvance;
	}
	
	public void Update(Timer _realTimer, Timer _gameTimer) {
		if(m_isAnimated) {
            if (m_isRealTime) {
                HandleAnimation(_realTimer.GetCurrentMilliseconds());
            } else {
                HandleAnimation(_gameTimer.GetCurrentMilliseconds());
            }
        }
	}
	
	public void Move(float _currentMilliseconds, float _xOffset, float _yOffset) {
		m_x = m_x + _xOffset;
		m_y = m_y + _yOffset;
		m_isDirty = true;
	}
	
	public void MoveTo(float _currentMilliseconds, float _x, float _y) {
		m_x = _x;
		m_y = _y;
		m_isDirty = true;
	}
			
	public float GetX() {
		return m_x;		
	}
	
	public float GetY() {
		return m_y;		
	}		
	
	public float GetXNormalized() {
		return m_x / m_deviceDisplay.GetAspectRatioX();
	}
	
	public float GetYNormalized() {
		return m_y / m_deviceDisplay.GetAspectRatioY();
	}
	
	public float GetWidth() {
		return m_width;
	}
	
	public float GetWidthNormalized() {
		return m_width / m_deviceDisplay.GetAspectRatioX();
	}
	
	public float GetHeightNormalized() {
		return m_height / m_deviceDisplay.GetAspectRatioY();
	}
	
	public float GetHeight() {
		return m_height;
	}
	
	public DIRECTION GetDirection() {
		return m_direction;
	}
	
	public void ChangeDirection() {
		m_direction = (m_direction == DIRECTION.LEFT) ? DIRECTION.RIGHT : DIRECTION.LEFT;
	}
	
	@Override
	public void HandleTouchRelease(boolean isPrimary, float normalizedX, float normalizedY, float realTimeMilliseconds, float gameTimeMilliseconds) {
		// Default do unhandled		
	}

	@Override
	public boolean HandleTouch(boolean isPrimary, float normalizedX, float normalizedY, float realTimeMilliseconds, float gameTimeMilliseconds) {
		// Default do unhandled
		
		return false;
	}

	@Override
	public void HandleAnimationFinished(String _tag, float _currentTimeMilliseconds) {
		// Default to unhandled		
	}

	private float Round(float _val, double _precision) {
        float precision = 100000.0f;

		float roundOff = (float) (Math.floor(_val * precision + .5) / precision);

		return roundOff;
	}

	public float GetAnimationCompletePercentage() {
		return m_currentAnimation.GetFrame() / m_currentAnimation.GetFrames();
	}
	
	public float GetCollisionBufferPercentX1() {
		return m_currentAnimation.GetCollisionBufferPercentX1();
	}
	
	public float GetCollisionBufferPercentX2() {
		return m_currentAnimation.GetCollisionBufferPercentX2();
	}
	
	public float GetCollisionBufferPercentY1() {
		return m_currentAnimation.GetCollisionBufferPercentY1();
	}
	
	public float GetCollisionBufferPercentY2() {
		return m_currentAnimation.GetCollisionBufferPercentY2();
	}	
	
	public boolean IntersectsWith(Sprite _sprite) {
        float thisX1 = Round(GetX() + (GetWidth() * (this.GetDirection() == DIRECTION.RIGHT ? GetCollisionBufferPercentX1() : GetCollisionBufferPercentX2())), 5);
		float thisX2 = Round(GetX() + GetWidth() - (GetWidth() * (this.GetDirection() == DIRECTION.RIGHT ? GetCollisionBufferPercentX2() : GetCollisionBufferPercentX1())), 5);
		float thisY1 = Round(GetY() - (GetHeight() * GetCollisionBufferPercentY1()), 5);
		float thisY2 = Round(GetY() - GetHeight() + (GetHeight() * GetCollisionBufferPercentY2()), 5);
		
		float spriteX1 = Round(_sprite.GetX() + (_sprite.GetWidth() * (this.GetDirection() == DIRECTION.RIGHT ? _sprite.GetCollisionBufferPercentX1() : _sprite.GetCollisionBufferPercentX2())), 5);
		float spriteX2 = Round(_sprite.GetX() + _sprite.GetWidth() - (_sprite.GetWidth() * (this.GetDirection() == DIRECTION.RIGHT ? _sprite.GetCollisionBufferPercentX2() : _sprite.GetCollisionBufferPercentX1())), 5);
		float spriteY1 = Round(_sprite.GetY() - (_sprite.GetHeight() * _sprite.GetCollisionBufferPercentY1()), 5);
		float spriteY2 = Round(_sprite.GetY() - _sprite.GetHeight() + (_sprite.GetHeight() * _sprite.GetCollisionBufferPercentY2()), 5);

	    if (thisX2 <= spriteX1) return false; // a is left of b
	    if (thisX1 >= spriteX2) return false; // a is right of b
	    if (thisY2 >= spriteY1) return false; // a is above b
	    if (thisY1 <= spriteY2) return false; // a is below b
	    return true; // boxes overlap				
	}
	
	/*
	 * Waypoint Functionality
	 */
	public float GetWaypointStartTime() {
		return m_waypointStartTime;
	}
		
	public float GetWaypointX() {
		return m_waypointX;
	}
	
	public float GetWaypointY() {
		return m_waypointY;
	}
	
	public float GetWaypointTraversalTime() {
		return m_waypointTraversalTime;
	}
			
	public boolean IsInWaypoint() {
		if(!m_isWaypointYFinished || !m_isWaypointXFinished) {
			return true;
		} else {
			return false;
		}
	}
			
	public void WaypointStart(float _waypointX, float _waypointY, DIRECTION _waypointDirection, float _waypointTraversalTime, float _waypointStartTime) {
		m_waypointDirection = _waypointDirection;
		m_isWaypointXFinished = false;
		m_isWaypointYFinished = false;
		m_waypointX = _waypointX;
		m_waypointY = _waypointY;
		m_waypointLastX = 0.0f;
		m_waypointLastY = 0.0f;
		m_waypointTraversalTime = _waypointTraversalTime;
		m_waypointStartTime = _waypointStartTime;
	}
	
	public void WaypointEnd(float _startTimeMilliseconds) {
		m_isWaypointXFinished = true;
		m_isWaypointYFinished = true;
		m_waypointStartTime = 0;
		m_waypointTraversalTime = 0;
		m_waypointX = 0.0f;
		m_waypointY = 0.0f;				
	}
	
	public float GetNextWaypointX(float _currentTimeMilliseconds) {
		float nextX = 0.0f;
		float moveBy = 0.0f;
						
		if(!m_isWaypointXFinished) {
			float percentageComplete = (_currentTimeMilliseconds - m_waypointStartTime) / m_waypointTraversalTime;
			if(percentageComplete >= 1.0f) {
				percentageComplete = 1.0f;
				m_isWaypointXFinished = true;
			}
			
			nextX = (m_waypointX * percentageComplete);						
			
			if(m_waypointDirection == DIRECTION.RIGHT){ 
				if(nextX > m_waypointX) {
					nextX = m_waypointX;
				}
			}
			
			if(m_waypointDirection == DIRECTION.LEFT){
				if(nextX < m_waypointX) {
					nextX = m_waypointX;
				}
			}
			
			if(m_waypointX == m_waypointLastX) {
				nextX = m_waypointX;
			}

			if(m_waypointDirection == DIRECTION.RIGHT) { 
				moveBy = nextX - m_waypointLastX;
			} else {
				moveBy = (Math.abs(nextX) - Math.abs(m_waypointLastX)) * -1;
			}
			
			m_waypointLastX = nextX;
		}
		
		return moveBy;
	}
	
	public float GetNextWaypointY(float _currentTimeMilliseconds) {
		float nextY = 0.0f;
		float moveBy = 0.0f;
		
		if(!m_isWaypointYFinished) {
			float percentageComplete = (_currentTimeMilliseconds - m_waypointStartTime) / m_waypointTraversalTime;
						
			if(percentageComplete >= 1.0f) {
				percentageComplete = 1.0f;				
				m_isWaypointYFinished = true;
			}
	
			nextY = (m_waypointY * percentageComplete);
			if(nextY > m_waypointY) {
				nextY = m_waypointY;
			}
			
			moveBy = nextY - m_waypointLastY;			
								
			m_waypointLastY = nextY;
		}				
		
		return moveBy;		
	}

	@Override
	public void HandleAnimationEvent(float _currentTimeMilliseconds) {
		// Default to unhandled			
	}

}