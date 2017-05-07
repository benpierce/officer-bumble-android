package com.example.officerbumble.gameentities;

import com.example.officerbumble.interfaces.RoboThrower2000Listener;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.SoundManager;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteManager;
import com.example.officerbumble.engine.SpriteSheetManager;

public class RoboThrower2000 extends Sprite {

	private enum STATE {
		RESTING,
		THROWING,
		BOWLING
	}
	
	public enum WEAPON_TYPE {
		PIE,
		BOWLING_BALL,
		CHICKENATOR_HIGH,
		CHICKENATOR_LOW
	}
	
	private STATE m_state;
	private SpriteManager m_spriteManager;
	private WEAPON_TYPE m_weaponType;
	private RoboThrower2000Listener m_listener;
	
	public RoboThrower2000(float _x, float _y, float _height, float _width, int _zBufferIndex, boolean _isStationary, float _currentTimeMilliseconds,
						   DeviceDisplay _deviceDisplay, SpriteSheetManager _spriteSheetManager, SpriteManager _spriteManager,
						   String _animationTag, String _tag) {
					super(_x, _y, _height, _width, _zBufferIndex, _isStationary, DIRECTION.RIGHT, false, _deviceDisplay, _spriteSheetManager, _animationTag, _tag);
										
		LoadAnimations();
		super.SetCurrentAnimation("THROWBOT_RESTING", _currentTimeMilliseconds);
				
		m_state = STATE.RESTING;
		m_spriteManager = _spriteManager;						
	}
	
	public void RegisterForEvents(RoboThrower2000Listener _listener) {
		m_listener = _listener;
	}
	
	private void LoadAnimations() {
		super.LoadAnimation("THROWBOT_RESTING");
		super.LoadAnimation("THROWBOT_THROW_HIGH");
		super.LoadAnimation("THROWBOT_THROW_LOW");
	}
	
	public void Throw(float _currentTimeMilliseconds, WEAPON_TYPE _weaponType) {
		m_state = STATE.THROWING;
		m_weaponType = _weaponType;		
		super.SetCurrentAnimation("THROWBOT_THROW_HIGH", _currentTimeMilliseconds);
		SoundManager.PlaySound("ROBOT THROW", false);
	}
	
	public void Bowl(float _currentTimeMilliseconds, WEAPON_TYPE _weaponType) {
		m_state = STATE.BOWLING;
		m_weaponType = _weaponType;
		super.SetCurrentAnimation("THROWBOT_THROW_LOW", _currentTimeMilliseconds);
		SoundManager.PlaySound("ROBOT SHOOT", false);
	}
	
	public void Rest(float _currentTimeMilliseconds) {
		m_state = STATE.RESTING;
		super.SetCurrentAnimation("THROWBOT_RESTING", _currentTimeMilliseconds);
	}
	
	@Override
	public void HandleAnimationEvent(float _currentTimeMilliseconds) {
		if(m_state == STATE.THROWING) {
			m_listener.HandleThrowFinished(_currentTimeMilliseconds, m_weaponType);
		} else if (m_state == STATE.BOWLING) {
			m_listener.HandleBowlFinished(_currentTimeMilliseconds, m_weaponType);
		}
	}
	
	@Override
	public void HandleAnimationFinished(String _tag, float _currentTimeMilliseconds) {
		if(m_state == STATE.THROWING || m_state == STATE.BOWLING) {
			Rest(_currentTimeMilliseconds);
		}
	}
	
}
