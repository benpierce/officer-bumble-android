package com.example.officerbumble.gameentities;

import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.interfaces.BumbleListener;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DifficultyConfig;
import com.example.officerbumble.engine.Point2D;
import com.example.officerbumble.engine.SoundManager;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.gameentities.Criminal.CRIMINAL_WEAPON;

public class Bumble extends Sprite {	

	private long MINIMUM_DUCK_TIME = 320;	// How long will Bumble stay ducked if you release the duck button.  This way you can just tap duck without having to hold it.
	
	public enum BUMBLE_STATE
	{
		RUNNING,
		JUMPING,
		DUCKING,
		FALLING_PIE,
		FALLING_BOWLING_BALL,
		FALLING_CHICKENATOR_LOW,
		FALLING_CHICKENATOR_HIGH,
		RIDING_ESCALATOR,
		CAUGHT_CRIMINAL
	}
	
	/********** Invincibility Constants ***********/
	//private final long INVINCIBILITY_LENGTH = 1000;		   // How long you're invisible for after an invicibility event such as getting up after being hit, or getting to the top of an escalator.

	/********** Velocity Constants ***********/	
	//private final float MAX_VELOCITY = 1.8f;
	//private final float MIN_VELOCITY = 0.0f;
	//private final float TIME_TO_REACH_MAX_VELOCITY = 1500.0f;	//  How long it takes for bumble to go from 0 to MAX_VELOCITY in milliseconds 
	//private final float TIME_TO_REACH_MIN_VELOCITY = 1500.0f;	//  How long it takes for bumble to go from MAX_VELOCITY to MIN_VELOCITY in milliseconds.

	/********** Member Variables *********/
	private BUMBLE_STATE m_state;
	private float m_velocity;	// Between 0 and 2 (the screen width) per second.
	private float m_maxVelocity;
	private float m_lastVelocityAdjustmentTime = 0;	// Last time we increased the velocity.
	private int m_floorLevel = 1;
	private float m_invincibilityStart = 0;	// When invinciblity was last granted.
	private BumbleListener m_bumbleListener;
	private float m_initialDuckTime = 0;
	private float m_distanceTravelled = 0.0f;
	
	/********** Control Variables ********/
	private boolean m_duckPrimaryQueued = false;
	private boolean m_jumpPrimaryQueued = false;
	private boolean m_duckSecondaryQueued = false;
	private boolean m_jumpSecondaryQueued = false;
	
	/********** Minimap Variables ********/
	private Point2D m_mapPosition = null;
	
	/********** Animation Constants ******/
	private final String BUMBLE_RUN_RIGHT;
	private final String BUMBLE_RUN_LEFT;
	private final String BUMBLE_JUMP_RIGHT;
	private final String BUMBLE_JUMP_LEFT;
	private final String BUMBLE_DUCK_RIGHT;
	private final String BUMBLE_DUCK_LEFT;
	private final String BUMBLE_FALL_CHICKENATOR_LOW_RIGHT;
	private final String BUMBLE_FALL_CHICKENATOR_LOW_LEFT;
	private final String BUMBLE_FALL_CHICKENATOR_HIGH_RIGHT;
	private final String BUMBLE_FALL_CHICKENATOR_HIGH_LEFT;	
	private final String BUMBLE_FALL_PIE_RIGHT;
	private final String BUMBLE_FALL_PIE_LEFT;
	private final String BUMBLE_FALL_BOWLING_BALL_RIGHT;
	private final String BUMBLE_FALL_BOWLING_BALL_LEFT;	
	private final String BUMBLE_ESCALATOR_RIGHT;
	private final String BUMBLE_ESCALATOR_LEFT;
	private final String BUMBLE_CAUGHT_CRIMINAL_RIGHT;
	private final String BUMBLE_CAUGHT_CRIMINAL_LEFT;
	
	private DifficultyConfig m_difficultyConfig;
		
	// x and y are normalized between -1 and 1 and represents the middle point.
	public Bumble(float x, float y, float _height, float _width, int _zBufferIndex, float _creationTimeMilliseconds, DifficultyConfig _difficultyConfig, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, String _animationTag, String _tag)  {
		super(x, y, _height, _width, _zBufferIndex, false, DIRECTION.RIGHT, false, _display, _spriteSheetManager, _animationTag, _tag);
		m_difficultyConfig = _difficultyConfig;
		m_velocity = m_difficultyConfig.GetBumbleMaxVelocity();
		m_maxVelocity = m_difficultyConfig.GetBumbleMaxVelocity();
		m_mapPosition = new Point2D(x, y);
		m_distanceTravelled = x + _width;

		// Animation Constants
		BUMBLE_RUN_RIGHT = _animationTag + "_RUN_RIGHT";
		BUMBLE_RUN_LEFT = _animationTag + "_RUN_LEFT";
		BUMBLE_JUMP_RIGHT = _animationTag + "_JUMP_RIGHT";
		BUMBLE_JUMP_LEFT = _animationTag + "_JUMP_LEFT";
		BUMBLE_DUCK_RIGHT = _animationTag + "_DUCK_RIGHT";
		BUMBLE_DUCK_LEFT = _animationTag + "_DUCK_LEFT";
		BUMBLE_FALL_PIE_RIGHT = _animationTag + "_FALL_PIE_RIGHT";
		BUMBLE_FALL_PIE_LEFT = _animationTag + "_FALL_PIE_LEFT";
		BUMBLE_FALL_CHICKENATOR_LOW_RIGHT = _animationTag + "_FALL_CHICKENATOR_LOW_RIGHT";
		BUMBLE_FALL_CHICKENATOR_LOW_LEFT = _animationTag + "_FALL_CHICKENATOR_LOW_LEFT";
		BUMBLE_FALL_CHICKENATOR_HIGH_RIGHT = _animationTag + "_FALL_CHICKENATOR_HIGH_RIGHT";
		BUMBLE_FALL_CHICKENATOR_HIGH_LEFT = _animationTag + "_FALL_CHICKENATOR_HIGH_LEFT";		
		BUMBLE_FALL_BOWLING_BALL_RIGHT = _animationTag + "_FALL_BOWLING_BALL_RIGHT";
		BUMBLE_FALL_BOWLING_BALL_LEFT = _animationTag + "_FALL_BOWLING_BALL_LEFT";		
		BUMBLE_ESCALATOR_RIGHT = _animationTag + "_ESCALATOR_RIGHT";
		BUMBLE_ESCALATOR_LEFT = _animationTag + "_ESCALATOR_LEFT";
		BUMBLE_CAUGHT_CRIMINAL_RIGHT = _animationTag + "_CAUGHT_CRIMINAL_RIGHT";
		BUMBLE_CAUGHT_CRIMINAL_LEFT = _animationTag + "_CAUGHT_CRIMINAL_LEFT";
				
		LoadAnimations();
	}

    public void Initialize(Timer _gameTimer) {
        Run(_gameTimer.GetCurrentMilliseconds());

        m_lastVelocityAdjustmentTime = _gameTimer.GetCurrentMilliseconds();
    }
	
	private void LoadAnimations() {
		// start x, start y, frames, frames per second, isLooping, TAG
		super.LoadAnimation(BUMBLE_RUN_RIGHT);		
		super.LoadAnimation(BUMBLE_JUMP_RIGHT);
		super.LoadAnimation(BUMBLE_DUCK_RIGHT);
		super.LoadAnimation(BUMBLE_FALL_PIE_RIGHT);
		super.LoadAnimation(BUMBLE_FALL_CHICKENATOR_LOW_RIGHT);
		super.LoadAnimation(BUMBLE_FALL_CHICKENATOR_HIGH_RIGHT);
		super.LoadAnimation(BUMBLE_FALL_BOWLING_BALL_RIGHT);
		super.LoadAnimation(BUMBLE_ESCALATOR_RIGHT);
		super.LoadAnimation(BUMBLE_CAUGHT_CRIMINAL_RIGHT);
		super.LoadAnimation(BUMBLE_RUN_LEFT);		
		super.LoadAnimation(BUMBLE_JUMP_LEFT);
		super.LoadAnimation(BUMBLE_DUCK_LEFT);
		super.LoadAnimation(BUMBLE_FALL_PIE_LEFT);
		super.LoadAnimation(BUMBLE_FALL_CHICKENATOR_LOW_LEFT);
		super.LoadAnimation(BUMBLE_FALL_CHICKENATOR_HIGH_LEFT);
		super.LoadAnimation(BUMBLE_FALL_BOWLING_BALL_LEFT);
		super.LoadAnimation(BUMBLE_ESCALATOR_LEFT);
		super.LoadAnimation(BUMBLE_CAUGHT_CRIMINAL_LEFT);
	}
	
	// Increases the velocity depending on the time.
	public void AdjustVelocity(float _currentTimeMilliseconds) {
		// If we're running velocity can increase.
		if(m_state == BUMBLE_STATE.RUNNING || m_state == BUMBLE_STATE.JUMPING) {
			// Only do this calculation if we're not already at full velocity.
			if (Math.abs(m_velocity) < m_difficultyConfig.GetBumbleMaxVelocity()) {
				// How many milliseconds since the last velocity update?
				float milliseconds = _currentTimeMilliseconds - m_lastVelocityAdjustmentTime;
				
				float velocityIncrease = (milliseconds / m_difficultyConfig.GetBumbleTimeToReachMaxVelocity()) * (m_difficultyConfig.GetBumbleMaxVelocity() - m_difficultyConfig.GetBumbleMinVelocity());
				if (Math.abs(m_velocity) + velocityIncrease > m_difficultyConfig.GetBumbleMaxVelocity()) {
					m_velocity = m_difficultyConfig.GetBumbleMaxVelocity() * ((super.GetDirection() == DIRECTION.LEFT) ? -1.0f : 1.0f);
				} else {
					m_velocity += velocityIncrease * ((super.GetDirection() == DIRECTION.LEFT) ? -1.0f : 1.0f);
				}
			}
		}
		
		// If we're ducking, velocity should slowly decrease due to friction.
		if(m_state == BUMBLE_STATE.DUCKING) {
			// Only do this calculation if we're not already at min velocity.
			if (Math.abs(m_velocity) > m_difficultyConfig.GetBumbleMinVelocity()) {
				// How many milliseconds since the last velocity update?
				float milliseconds = _currentTimeMilliseconds - m_lastVelocityAdjustmentTime;
				
				float velocityDecrease = -1.0f * (milliseconds /  m_difficultyConfig.GetBumbleTimeToReachMinVelocity()) * (m_difficultyConfig.GetBumbleMaxVelocity() - m_difficultyConfig.GetBumbleMinVelocity());
				if (Math.abs(m_velocity) + velocityDecrease < m_difficultyConfig.GetBumbleMinVelocity()) {
					m_velocity = m_difficultyConfig.GetBumbleMinVelocity() * ((super.GetDirection() == DIRECTION.LEFT) ? -1.0f : 1.0f);
				} else {
					m_velocity += velocityDecrease * ((super.GetDirection() == DIRECTION.LEFT) ? -1.0f : 1.0f);
				}
			}
		}
		
		// Set the new velocity adjustment time.
		m_lastVelocityAdjustmentTime = _currentTimeMilliseconds;
	}		
	
	public void UpdateDistanceTravelled(float _distance) {
		m_distanceTravelled += _distance;
	}
	
	public float GetDistanceTravelled() {
		return m_distanceTravelled;
	}	
	
	public float GetVelocity() {		
		return m_velocity;
	}
	
	public float GetMaxVelocity() {
		return m_maxVelocity;
	}
	
	private boolean IsInvincible(float _currentTimeMilliseconds) {
		return (_currentTimeMilliseconds - m_invincibilityStart <= m_difficultyConfig.GetBumbleInvincibilityLength()) ? true : false;
	}
	
	private void StartInvincibility(float _currentTimeMilliseconds) {
		m_invincibilityStart = _currentTimeMilliseconds;
	}
	
	private boolean CanDuck() {
		return (m_state == BUMBLE_STATE.RUNNING || m_state == BUMBLE_STATE.JUMPING);
	}

	private boolean CanJump() {
		return (m_state == BUMBLE_STATE.RUNNING || m_state == BUMBLE_STATE.DUCKING);
	}
	
	@Override
	public boolean HandleTouch(boolean isPrimary, float normalizedX, float normalizedY, float realTimeMilliseconds, float gameTimeMilliseconds) {
			if(normalizedX <= 0) {
				if(isPrimary) {
					m_duckPrimaryQueued = true;
				} else {
					m_duckSecondaryQueued = true;
				}				
			}
			
			if(normalizedX > 0) {
				if(isPrimary) {
					m_jumpPrimaryQueued = true;
					m_duckPrimaryQueued = false;
				} else {
					m_jumpSecondaryQueued = true;
					m_duckSecondaryQueued = false;
				}
			}
			
			// Duck if there's a duck queued.
			if (CanDuck() && (m_duckPrimaryQueued || m_duckSecondaryQueued)){ 
				if(m_state == BUMBLE_STATE.JUMPING) {
					m_jumpPrimaryQueued = false;
					m_jumpSecondaryQueued = false;
				}
				ChangeState(BUMBLE_STATE.DUCKING, gameTimeMilliseconds);
				SoundManager.PlaySound("DUCK", false);
				m_initialDuckTime = gameTimeMilliseconds;
			}	
			
			if (CanJump() && (m_jumpPrimaryQueued || m_jumpSecondaryQueued)) {
				ChangeState(BUMBLE_STATE.JUMPING, gameTimeMilliseconds);			
			}
		
		return false;		// Bumble never handles the touch.
	}
	
	public boolean CanBeHit(float _currentTimeMilliseconds) {
		boolean canBeHit = true;
		   
		if(IsInvincible(_currentTimeMilliseconds) || m_state == BUMBLE_STATE.FALLING_CHICKENATOR_LOW || m_state == BUMBLE_STATE.FALLING_CHICKENATOR_HIGH || m_state == BUMBLE_STATE.FALLING_BOWLING_BALL || m_state == BUMBLE_STATE.FALLING_PIE || m_state == BUMBLE_STATE.RIDING_ESCALATOR){
			canBeHit = false;
		}
		
		return canBeHit;
	}
	
	@Override
	public void HandleTouchRelease(boolean isPrimary, float normalizedX, float normalizedY, float realTimeMilliseconds, float gameTimeMilliseconds) {
			// Jump release while ducking.
					
			// Release duck
			if (normalizedX <= 0) {
				if(isPrimary) {
					m_duckPrimaryQueued = false;
				} else {
					m_duckSecondaryQueued = false;
				}
	
				if(m_state == BUMBLE_STATE.DUCKING && gameTimeMilliseconds >= (m_initialDuckTime + MINIMUM_DUCK_TIME)) {
					Run(gameTimeMilliseconds);
				}
			}
	
			// Release jump
			if(normalizedX > 0) {
				if(isPrimary) {
					m_jumpPrimaryQueued = false;
				} else {
					m_jumpSecondaryQueued = false;
				}
			}
	}
	
	@Override
	public void HandleAnimationFinished(String _tag, float _currentTimeMilliseconds) {
		if(m_state == BUMBLE_STATE.JUMPING) {
			m_jumpPrimaryQueued = false;
			m_jumpSecondaryQueued = false;
			
			if(m_duckPrimaryQueued || m_duckSecondaryQueued) {
				ChangeState(BUMBLE_STATE.DUCKING, _currentTimeMilliseconds);
			}
			else {
				Run(_currentTimeMilliseconds);
			}			
		} else if (m_state == BUMBLE_STATE.FALLING_PIE || m_state == BUMBLE_STATE.FALLING_CHICKENATOR_LOW || m_state == BUMBLE_STATE.FALLING_CHICKENATOR_HIGH || m_state == BUMBLE_STATE.FALLING_BOWLING_BALL) {
			if(m_difficultyConfig.GetDifficultyName().equals("HARDCORE")) {
				m_bumbleListener.HandleBumbleDead(_currentTimeMilliseconds);
			} else {
				m_bumbleListener.HandleBumbleBackUp(_currentTimeMilliseconds);
			}
			StartInvincibility(_currentTimeMilliseconds);
			m_duckPrimaryQueued = false;
			m_jumpPrimaryQueued = false;
			m_duckSecondaryQueued = false;
			m_jumpSecondaryQueued = false;
			
			Run(_currentTimeMilliseconds);
		} 
	}	
	
	public void AdjustMapCoordinates(float _xAdjustment, float _yAdjustment) {
		m_mapPosition.SetX(m_mapPosition.GetX() + _xAdjustment);
		m_mapPosition.SetY(m_mapPosition.GetY() + _yAdjustment);
	}
	
	@Override
	public void Update(Timer _realTimer, Timer _gameTimer) {
        super.Update(_realTimer, _gameTimer);

        // If Bumble has been ducking and they've released the button, he should pop up as soon as the minimum ducking time has elapsed.
        if (m_state == BUMBLE_STATE.DUCKING && (!m_duckPrimaryQueued && !m_duckSecondaryQueued) && _gameTimer.GetCurrentMilliseconds() >= (m_initialDuckTime + MINIMUM_DUCK_TIME)) {
            Run(_gameTimer.GetCurrentMilliseconds());
        }
	}
	
	public Point2D GetMapPosition() {
		return m_mapPosition;
	}	
	
	public void CaughtCriminal(float _currentTimeMilliseconds) {
		m_velocity = 0.0f;
		ChangeState(BUMBLE_STATE.CAUGHT_CRIMINAL, _currentTimeMilliseconds);
	}
	
	public void Fall(CRIMINAL_WEAPON _weapon, float _currentTimeMilliseconds) {
		m_velocity = m_difficultyConfig.GetBumbleMinVelocity();
		m_lastVelocityAdjustmentTime = _currentTimeMilliseconds;
		m_duckPrimaryQueued = false;
		m_jumpPrimaryQueued = false;
		m_duckSecondaryQueued = false;
		m_jumpSecondaryQueued = false;
		
		if(_weapon == CRIMINAL_WEAPON.PIE){ 
			ChangeState(BUMBLE_STATE.FALLING_PIE, _currentTimeMilliseconds);
		} else if (_weapon == CRIMINAL_WEAPON.CHICKENATOR_LOW){
			ChangeState(BUMBLE_STATE.FALLING_CHICKENATOR_LOW, _currentTimeMilliseconds);
		} else if (_weapon == CRIMINAL_WEAPON.CHICKENATOR_HIGH){
			ChangeState(BUMBLE_STATE.FALLING_CHICKENATOR_HIGH, _currentTimeMilliseconds);
		} else if (_weapon == CRIMINAL_WEAPON.BOWLING_BALL){
			ChangeState(BUMBLE_STATE.FALLING_BOWLING_BALL, _currentTimeMilliseconds);
		}
	}
	
	/* Waypoint Related Functionality 
	 * 
	 * Have to override WaypointStart because bumble's velocity should drop to 0 when he hits the escalator.
	 * */
	@Override
	public void WaypointStart(float _waypointX, float _waypointY, DIRECTION _waypointDirection, float _waypointTraversalTime, float _waypointStartTime) {
		super.WaypointStart(_waypointX, _waypointY, _waypointDirection, _waypointTraversalTime, _waypointStartTime);
		m_velocity = 0.0f;
	}						
	
	@Override
	public void WaypointEnd(float _startTimeMilliseconds) {
		super.WaypointEnd(_startTimeMilliseconds);

		StartInvincibility(_startTimeMilliseconds);
		m_duckPrimaryQueued = false;
		m_jumpPrimaryQueued = false;
		m_duckSecondaryQueued = false;
		m_jumpSecondaryQueued = false;
	}
	/* End of Waypoint Functionality */
	
	public void IncrementFloorLevel() {
		m_floorLevel++;
	}
	
	public int GetFloorLevel() {
		return m_floorLevel;
	}
	
	public BUMBLE_STATE GetState() {
		return m_state;
	}
				
	public void RideEscalator(float _startTimeMilliseconds) {
		ChangeState(BUMBLE_STATE.RIDING_ESCALATOR, _startTimeMilliseconds);
		SoundManager.PlaySound("ESCALATOR", false);
	}
	
	public void Run(float _startTimeMilliseconds) {
		ChangeState(BUMBLE_STATE.RUNNING, _startTimeMilliseconds);
	}
	
	// Determine if a particular sprite has gone behind Bumble.
	public boolean IsSpriteBehind(Sprite _sprite) {
		boolean isBehind = false;
		
		if(super.GetDirection() == DIRECTION.RIGHT){
			if (_sprite.GetX() + _sprite.GetWidth() < GetX()) {				
				isBehind = true;
			}			
		} else {
			if (_sprite.GetX() > GetX() + GetWidth()) {
				isBehind = true;
			}			
		}
		
		return isBehind;
	}
	
	public boolean SuccessfullyDucking() {
		return (m_state == BUMBLE_STATE.DUCKING) ? true : false; 					
	}
		
	public boolean SuccessfullyJumping() {
		return (m_state == BUMBLE_STATE.JUMPING) ? true : false;		
	}
	
	private void ChangeState(BUMBLE_STATE state, float _startTimeMilliseconds) {
		if(m_state == BUMBLE_STATE.DUCKING && state != BUMBLE_STATE.DUCKING) {
			m_bumbleListener.HandleBumbleDuckEnd(_startTimeMilliseconds);
		}
		
		if(m_state == BUMBLE_STATE.JUMPING && state != BUMBLE_STATE.JUMPING) {
			m_bumbleListener.HandleBumbleJumpEnd(_startTimeMilliseconds);
		}
		
		if(state == BUMBLE_STATE.DUCKING && m_state != BUMBLE_STATE.DUCKING) {
			m_bumbleListener.HandleBumbleDuckBegin(_startTimeMilliseconds);
		}
		
		if(state == BUMBLE_STATE.JUMPING && m_state != BUMBLE_STATE.JUMPING){
			m_bumbleListener.HandleBumbleJumpBegin(_startTimeMilliseconds);
		}		
		
		m_state = state;				
		
		switch(state) {
			case RUNNING:  
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? BUMBLE_RUN_LEFT : BUMBLE_RUN_RIGHT), _startTimeMilliseconds);				
				break;
			case JUMPING:								
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? BUMBLE_JUMP_LEFT : BUMBLE_JUMP_RIGHT), _startTimeMilliseconds);
				SoundManager.PlaySound("BUMBLE JUMP", false);
				break;
			case DUCKING:				
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? BUMBLE_DUCK_LEFT : BUMBLE_DUCK_RIGHT), _startTimeMilliseconds);
				break;
			case FALLING_BOWLING_BALL:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? BUMBLE_FALL_BOWLING_BALL_LEFT : BUMBLE_FALL_BOWLING_BALL_RIGHT), _startTimeMilliseconds);
				SoundManager.PlaySound("BUMBLE HIT", false);
				break;
			case FALLING_PIE:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? BUMBLE_FALL_PIE_LEFT : BUMBLE_FALL_PIE_RIGHT), _startTimeMilliseconds);
				SoundManager.PlaySound("BUMBLE SPLAT", false);
				break;
			case FALLING_CHICKENATOR_HIGH:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? BUMBLE_FALL_CHICKENATOR_HIGH_LEFT : BUMBLE_FALL_CHICKENATOR_HIGH_RIGHT), _startTimeMilliseconds);
				SoundManager.PlaySound("CHICKEN", false);
				break;	
			case FALLING_CHICKENATOR_LOW:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? BUMBLE_FALL_CHICKENATOR_LOW_LEFT : BUMBLE_FALL_CHICKENATOR_LOW_RIGHT), _startTimeMilliseconds);
				SoundManager.PlaySound("CHICKEN", false);
				break;	
			case RIDING_ESCALATOR:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? BUMBLE_ESCALATOR_LEFT : BUMBLE_ESCALATOR_RIGHT), _startTimeMilliseconds);
				break;
			case CAUGHT_CRIMINAL:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? BUMBLE_CAUGHT_CRIMINAL_LEFT : BUMBLE_CAUGHT_CRIMINAL_RIGHT), _startTimeMilliseconds);
		}
	}	
	
	public void RegisterBumbleListener(BumbleListener _bumbleListener) {
		m_bumbleListener = _bumbleListener;
	}		

	@Override
	public void HandleAnimationEvent(float _currentTimeMilliseconds) {
		
		if(m_state == BUMBLE_STATE.FALLING_PIE) {
			SoundManager.PlaySound("HEAD SHAKE", false);
		}		
	}

}
