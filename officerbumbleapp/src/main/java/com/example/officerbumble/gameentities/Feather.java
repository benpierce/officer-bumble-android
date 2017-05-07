package com.example.officerbumble.gameentities;

import java.util.Random;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteSheetManager;

public class Feather extends Sprite {

	private final static float FEATHER_HEIGHT = 0.10f;
	private final static float FEATHER_WIDTH = 0.10f;
	
	private Random m_rand = new Random();
	
	// Physics Constraints
	private static final float MIN_VELOCITY_UP = 0.3f;
	private static final float MAX_VELOCITY_UP = 0.4f;
	private static final float MIN_VELOCITY_DOWN = 0.10f;
	private static final float MAX_VELOCITY_DOWN = 0.25f;
	private static final float MIN_VELOCITY_HORIZONTAL = 0.50f;
	private static final float MAX_VELOCITY_HORIZONTAL = 1.00f;
	private static final float MIN_AMPLITUDE = 0.1f;
	private static final float MAX_AMPLITUDE = 0.5f;
	private static final float MAX_TARGETY_OFFSET = 0.07f;
	private static final float MAX_TARGETX_OFFSET = 0.20f;
	
	// Random variables to make each feather different.
	private float m_velocityUp = 0.0f;
	private float m_velocityDown = 0.0f;
	private float m_velocityHorizontal = 0.0f;
	private float m_amplitude = 0.0f;
	private float m_targetY = 0.0f;
	private float m_targetX = 0.0f;
	private float m_startingX = 0.0f;
	
	private float m_minX = 0.0f;
	private float m_maxX = 0.0f;
	private DIRECTION m_direction = DIRECTION.LEFT;
	
	private enum STATE {
		ARCING,
		FLOATING
	}	
	private STATE m_state;
	private float m_lastUpdateTime;
	
	public Feather(Chickenator _chickenator, int _zBufferIndex, boolean _isStationary, float _currentTimeMilliseconds,
			 		DeviceDisplay _deviceDisplay, SpriteSheetManager _spriteSheetManager,
			 		String _animationTag, String _tag) {
		super(_chickenator.GetX(), _chickenator.GetY(), FEATHER_HEIGHT, FEATHER_WIDTH, _zBufferIndex, _isStationary, DIRECTION.RIGHT, false, _deviceDisplay, _spriteSheetManager, _animationTag, _tag);

		// Randomize the feather we're going to use.
		int featherNum = m_rand.nextInt((5 - 1) + 1) + 1;
		super.LoadAnimation("FEATHER" + featherNum);
		super.SetCurrentAnimation("FEATHER" + featherNum, _currentTimeMilliseconds);
		m_state = STATE.ARCING;
		
		AssignCharacteristics(_chickenator, _currentTimeMilliseconds);		
	}
	
	private void AssignCharacteristics(Chickenator _chickenator, float _currentTimeMilliseconds) {
		float x = GetRandomDecimal(_chickenator.GetX(), _chickenator.GetX() + _chickenator.GetWidth());
		float y = GetRandomDecimal(_chickenator.GetY() - _chickenator.GetHeight() + (super.GetHeight() * 2), _chickenator.GetY() - super.GetHeight() + (super.GetHeight() * 2));
		
		super.MoveTo(_currentTimeMilliseconds, x, y);
		
		m_startingX = x;
		m_velocityUp = GetRandomDecimal(MIN_VELOCITY_UP, MAX_VELOCITY_UP);
		m_velocityDown = GetRandomDecimal(MIN_VELOCITY_DOWN, MAX_VELOCITY_DOWN) * -1;
		m_velocityHorizontal = GetRandomDecimal(MIN_VELOCITY_HORIZONTAL, MAX_VELOCITY_HORIZONTAL);
		m_amplitude = GetRandomDecimal(MIN_AMPLITUDE, MAX_AMPLITUDE);
		m_targetY = y + GetRandomDecimal(0.0f, MAX_TARGETY_OFFSET);
		m_targetX = x + GetRandomDecimal(MAX_TARGETX_OFFSET * -1, MAX_TARGETX_OFFSET);		
		m_lastUpdateTime = _currentTimeMilliseconds;
		m_minX = x - m_amplitude;
		m_maxX = x + m_amplitude;
		m_direction = (m_targetX < x) ? DIRECTION.LEFT : DIRECTION.RIGHT;
	}
	
	// Feathers will handle their own movement updates.
	public void Update(float _gameTimeMilliseconds, float _cameraOffsetX, float _cameraOffsetY) {
		float variance = ((_gameTimeMilliseconds - m_lastUpdateTime) / 1000.0f);
		float newX = 0.0f;
		float newY = 0.0f;
		
		m_targetY += _cameraOffsetY;
		m_targetX += _cameraOffsetX;
		m_startingX += _cameraOffsetX;
		m_minX += _cameraOffsetX;
		m_maxX += _cameraOffsetX;
		
		if(m_state == STATE.ARCING && super.GetY() >= m_targetY) {
			if(m_targetX < m_startingX && super.GetX() <= m_targetX) {
				m_state = STATE.FLOATING;
			} else if (m_targetX >= m_startingX && super.GetX() >= m_targetX) {
				m_state = STATE.FLOATING;
			}
		}
		
		if(m_state == STATE.ARCING) {
			newX = ((super.GetX() < m_targetX) ? 1 : -1) * (m_velocityUp * variance);						
			newY = m_velocityUp * variance;
			
			super.Move(_gameTimeMilliseconds, newX + _cameraOffsetX, newY + _cameraOffsetY);			
		} else if (m_state == STATE.FLOATING) {
			newY = m_velocityDown * variance;
			if(m_direction == DIRECTION.LEFT && super.GetX() <= m_minX) {
				m_direction = DIRECTION.RIGHT;
			} else if (m_direction == DIRECTION.RIGHT && super.GetX() >= m_maxX) {
				m_direction = DIRECTION.LEFT;
			}
			
			newX = (m_velocityHorizontal * variance) * ((m_direction == DIRECTION.LEFT) ? -1 : 1);
			
			super.Move(_gameTimeMilliseconds, newX + _cameraOffsetX, newY + _cameraOffsetY);
		}
		
		m_lastUpdateTime = _gameTimeMilliseconds;
	}
	
	private float GetRandomDecimal(float _boundary1, float _boundary2) {
		return m_rand.nextFloat() * (Math.max(_boundary1, _boundary2) - Math.min(_boundary1, _boundary2)) + Math.min(_boundary1, _boundary2);
	}
}
