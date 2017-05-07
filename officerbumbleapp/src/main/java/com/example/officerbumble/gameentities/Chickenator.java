package com.example.officerbumble.gameentities;

import java.util.Random;

import com.example.officerbumble.interfaces.ChickenatorListener;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.gameentities.Criminal.CRIMINAL_WEAPON;

/*
 * Chickenator behavior
 * 
 * Chickenators are either thrown high, or low and will continue flying in a constant trajectory towards Bumble until they 
 * get to a specific part of the screen, at which time they'll give off a visual cue that they're about to attack and then 
 * they'll oscillate to the opposite position on screen and charge faster at the player. Once the chickenator is behind Bumble
 * it'll oscillate back to the original position.
 * 
 */
public class Chickenator extends Weapon {

	private final static float CHICKENATOR_WIDTH = 0.32f;
	private final static float CHICKENATOR_HEIGHT = 0.32f;
		
	// Oscillation Methods.
	public enum CHICKENATOR_POSITION {
		HIGH,
		LOW,		
	}
	
	private enum STATE {
		NORMAL,
		OSCILLATION_WARNING,
		INITIAL_OSCILLATION,
		CHARGING,
		FINAL_OSCILLATION,
		ATTACKING,
		FINISHED
	}
	
	private final static float FEATHER_CHECK_INTERVAL = 100;
	
	private Random m_rand = new Random();
	private final float WARNING_TIME = 100;					// How long is the chickenator warning time in milliseconds?
	private float m_warningStartTime = 0;				// When did the warning start?
	private final float CHARGE_VELOCITY_ADJUSTMENT = 0.0f;  // How much faster should chickenator fly when he's charging?
	private float m_initialVelocity = 0.0f;					// What was the initial velocity of chickenator?
	private float m_activationProximity = 1.6f;		// How close to Bumble should the chickenator be to attack?
	private final float OSCILLATION_COMPLETE_TIME = 250;		// How long it takes to finish oscillating up or down.
	private float m_oscillationStartTime = 0;				// Time the oscillation started.
	private boolean m_isMidOscillation = false;				// Whether or not the weapon is in mid-oscillation.	
	private CHICKENATOR_POSITION m_currentPosition = CHICKENATOR_POSITION.LOW;		// Is the chickenator high or low?
	private float m_oscillationTarget = 0.0f;				// Where do we want to oscillate to?
	private float m_oscillationStart = 0.0f;				// Where did we oscillate from?
	private float m_totalOscillation = 0.0f;				// Total amount we've oscillated.
	private STATE m_currentState = STATE.NORMAL;			// Chickenator's current state.
	private int m_floorLevel = 0;
	private float m_oscillateFrom = 0.0f;
	private float m_oscillateTo = 0.0f;
	private ChickenatorListener m_listener;
	private float m_lastFeatherCheck = 0;
	private int m_featherCount = 0;

	// Some static constants so we're not rebuilding strings every time.
    private static final String ANIMATION_WARNING_LEFT = "CHICKENATOR_LEFT_WARNING";
    private static final String ANIMATION_CHARGE_LEFT = "CHICKENATOR_LEFT_CHARGE";
    private static final String ANIMATION_OSCILLATE_LEFT = "CHICKENATOR_LEFT_OSCILLATE";
    private static final String ANIMATION_ATTACK_LEFT_LOW = "CHICKENATOR_LEFT_ATTACK_LOW";
    private static final String ANIMATION_ATTACK_LEFT_HIGH = "CHICKENATOR_LEFT_ATTACK_HIGH";
    private static final String ANIMATION_WARNING_RIGHT = "CHICKENATOR_RIGHT_WARNING";
    private static final String ANIMATION_CHARGE_RIGHT = "CHICKENATOR_RIGHT_CHARGE";
    private static final String ANIMATION_OSCILLATE_RIGHT = "CHICKENATOR_RIGHT_OSCILLATE";
    private static final String ANIMATION_ATTACK_RIGHT_LOW = "CHICKENATOR_RIGHT_ATTACK_LOW";
    private static final String ANIMATION_ATTACK_RIGHT_HIGH = "CHICKENATOR_RIGHT_ATTACK_HIGH";

    // Animation States for the Chickenator.
    private final String ANIMATION_WARNING;
	private final String ANIMATION_CHARGE;
	private final String ANIMATION_OSCILLATE;
	private final String ANIMATION_ATTACK;
	
	public Chickenator(float _x, float _y, float _velocity, DIRECTION _direction, 
					   CHICKENATOR_POSITION _initialPosition, float _oscillateTo, float _activationProximity, int _floorLevel, ChickenatorListener _listener, int _zBufferIndex, float _creationTimeMilliseconds,
				       DeviceDisplay _deviceDisplay, SpriteSheetManager _spriteSheetManager, 
				       String _tag) {
				super(_x, _y, CHICKENATOR_HEIGHT, CHICKENATOR_WIDTH, _velocity, _direction, ((_initialPosition == CHICKENATOR_POSITION.HIGH) ? CRIMINAL_WEAPON.CHICKENATOR_HIGH : CRIMINAL_WEAPON.CHICKENATOR_LOW), _zBufferIndex, _creationTimeMilliseconds, _deviceDisplay, _spriteSheetManager, "CHICKENATOR_" + _direction.toString(), _tag);
								
		m_currentPosition = _initialPosition;
		m_initialVelocity = _velocity;
		m_floorLevel = _floorLevel;
		m_oscillateFrom = _y;
		m_oscillateTo = _oscillateTo;
		m_activationProximity = _activationProximity;
		m_listener = _listener;

        if ( _direction == DIRECTION.LEFT ) {
            ANIMATION_WARNING = ANIMATION_WARNING_LEFT;
            ANIMATION_CHARGE = ANIMATION_CHARGE_LEFT;
            ANIMATION_OSCILLATE = ANIMATION_OSCILLATE_LEFT;
            ANIMATION_ATTACK = (_initialPosition == CHICKENATOR_POSITION.HIGH ? ANIMATION_ATTACK_LEFT_LOW : ANIMATION_ATTACK_LEFT_HIGH);	// If they start out low, the attack will be high and vice versa so we have to swap these.
        } else {
            ANIMATION_WARNING = ANIMATION_WARNING_RIGHT;
            ANIMATION_CHARGE = ANIMATION_CHARGE_RIGHT;
            ANIMATION_OSCILLATE = ANIMATION_OSCILLATE_RIGHT;
            ANIMATION_ATTACK = (_initialPosition == CHICKENATOR_POSITION.HIGH ? ANIMATION_ATTACK_RIGHT_LOW : ANIMATION_ATTACK_RIGHT_HIGH);	// If they start out low, the attack will be high and vice versa so we have to swap these.
        }

		// Load additional animations.  Note that the default animation will be loaded by the parent class.
		super.LoadAnimation(ANIMATION_WARNING);
		super.LoadAnimation(ANIMATION_CHARGE);
		super.LoadAnimation(ANIMATION_OSCILLATE);
		super.LoadAnimation(ANIMATION_ATTACK);
	}
	
	public void BeginAttack(float _currentTimeMilliseconds) {
		m_currentState = STATE.ATTACKING;			
		super.SetCurrentAnimation(ANIMATION_ATTACK, _currentTimeMilliseconds);
		super.SetVelocity(0, 0);
	}
	
	public void EndAttack(float _currentTimeMilliseconds) {
		m_currentState = STATE.FINAL_OSCILLATION;
		super.SetCurrentAnimation(ANIMATION_OSCILLATE, _currentTimeMilliseconds);
		//super.SetVelocity(m_initialVelocity * 2, 0);
        super.SetVelocity(m_initialVelocity, 0);
	}
	
	public boolean IsAttacking() {
		return (m_currentState == STATE.ATTACKING) ? true : false;
	}
	
	public static float GetChickenatorHeight() {
		return CHICKENATOR_HEIGHT;
	}
	
	public static float GetChickenatorWidth() {
		return CHICKENATOR_WIDTH;
	}
	
	// This method is called every frame and will encapsulate all of the chickenators behavior and state changes.
	public void Think(Timer _gameTimer, Bumble _bumble) {
        if ( !_gameTimer.IsPaused() ) {
            float gameTimeMilliseconds = _gameTimer.GetCurrentMilliseconds();

            // Check if we need to transition out of the normal state.
            if (m_currentState == STATE.NORMAL && m_floorLevel == _bumble.GetFloorLevel()) {
                if (_bumble.GetDirection() == DIRECTION.RIGHT && Math.abs(this.GetX() - (_bumble.GetX() + _bumble.GetWidth())) <= m_activationProximity) {
                    m_currentState = STATE.OSCILLATION_WARNING;
                    super.SetCurrentAnimation(ANIMATION_WARNING, gameTimeMilliseconds);
                    m_warningStartTime = gameTimeMilliseconds;
                } else if (_bumble.GetDirection() == DIRECTION.LEFT && Math.abs(_bumble.GetX() - (this.GetX() + this.GetWidth())) <= m_activationProximity) {
                    m_currentState = STATE.OSCILLATION_WARNING;
                    super.SetCurrentAnimation(ANIMATION_WARNING, gameTimeMilliseconds);
                    m_warningStartTime = gameTimeMilliseconds;
                }
            } else if (m_currentState == STATE.OSCILLATION_WARNING && gameTimeMilliseconds - m_warningStartTime >= WARNING_TIME) {
                m_currentState = STATE.INITIAL_OSCILLATION;
                super.SetCurrentAnimation(ANIMATION_OSCILLATE, gameTimeMilliseconds);
                Oscillate(gameTimeMilliseconds);
            }
            // If we're currently in charge mode and we've passed Bumble, then we can oscillate one more time and continue on.
            else if (m_currentState == STATE.CHARGING) {
                if (_bumble.GetDirection() == DIRECTION.RIGHT && super.GetX() + super.GetWidth() < _bumble.GetX()) {
                    m_currentState = STATE.FINAL_OSCILLATION;
                    super.SetCurrentAnimation(ANIMATION_OSCILLATE, gameTimeMilliseconds);
                    Oscillate(gameTimeMilliseconds);
                }
                if (_bumble.GetDirection() == DIRECTION.LEFT && super.GetX() > _bumble.GetX() + _bumble.GetWidth()) {
                    m_currentState = STATE.FINAL_OSCILLATION;
                    super.SetCurrentAnimation(ANIMATION_OSCILLATE, gameTimeMilliseconds);
                    Oscillate(gameTimeMilliseconds);
                }
            } else if (m_currentState == STATE.ATTACKING && gameTimeMilliseconds - m_lastFeatherCheck >= FEATHER_CHECK_INTERVAL) {
                m_lastFeatherCheck = gameTimeMilliseconds;

                // 1 in 4 chance of throwing a feather.
                int num = m_rand.nextInt((4 - 1) + 1) + 1;

                if (num <= 3) {
                    m_listener.ThrowFeather(this, m_featherCount++, gameTimeMilliseconds);
                }
            } else if (m_currentState == STATE.INITIAL_OSCILLATION || m_currentState == STATE.FINAL_OSCILLATION) {
                Oscillate(gameTimeMilliseconds);
            }
        }
	}
	
	// Adjust the chickenator target Y axis because if Bumble goes up an elevator we don't want the Chickenator's target
	// to remain in the same position, otherwise it'll fly to an invalid area.
	public void AdjustTarget(float _verticalOffset) {
		m_oscillationTarget += _verticalOffset;
		m_oscillationStart += _verticalOffset;
		m_oscillateFrom += _verticalOffset;
		m_oscillateTo += _verticalOffset;
	}
		
	// This method will oscillate the chickenator to the opposite position.
	private void Oscillate(float _currentTimeMilliseconds) {
		if(m_isMidOscillation) {								
			float percentageComplete = ((float)(_currentTimeMilliseconds - m_oscillationStartTime) / (float)OSCILLATION_COMPLETE_TIME); 
				
			// How much should we oscillate this frame?
			float oscillateBy = (m_oscillationTarget - m_oscillationStart) * percentageComplete; 				
						
			if(m_oscillationStart < m_oscillationTarget) {
				if(m_oscillationStart + oscillateBy >= m_oscillationTarget) {					
					oscillateBy = m_oscillationTarget - m_oscillationStart;					
					m_isMidOscillation = false;
					m_oscillationStartTime = _currentTimeMilliseconds;
				}												
			} else {
				if(m_oscillationStart + oscillateBy <= m_oscillationTarget) {					
					oscillateBy = m_oscillationTarget - m_oscillationStart;					
					m_isMidOscillation = false;
					m_oscillationStartTime = _currentTimeMilliseconds;
				}					
			}
													
			super.Move(_currentTimeMilliseconds, 0f, oscillateBy - m_totalOscillation);
			
			// If we finished our oscillation, we need to start the charge!
			if(!m_isMidOscillation) {
				if(m_currentState == STATE.INITIAL_OSCILLATION) {
					m_currentState = STATE.CHARGING;
					super.SetCurrentAnimation(ANIMATION_CHARGE, _currentTimeMilliseconds);
					super.SetVelocity(m_initialVelocity + CHARGE_VELOCITY_ADJUSTMENT, 0);
				}				
				if(m_currentState == STATE.FINAL_OSCILLATION) {
					m_currentState = STATE.FINISHED;
					super.SetCurrentAnimation(super.GetAnimationTag(), _currentTimeMilliseconds);
					super.SetVelocity(m_initialVelocity, 0);	
				}
			}
			
			m_totalOscillation = oscillateBy;	
		} else {				
			// Switch directions.				
			m_currentPosition = (m_currentPosition == CHICKENATOR_POSITION.HIGH) ? CHICKENATOR_POSITION.LOW : CHICKENATOR_POSITION.HIGH;
			m_isMidOscillation = true;
			m_oscillationStartTime = _currentTimeMilliseconds;
			m_totalOscillation = 0.0f;
				
			if(m_currentPosition == CHICKENATOR_POSITION.HIGH) {		
				m_oscillationStart = super.GetY();
				m_oscillationTarget = Math.max(m_oscillateFrom, m_oscillateTo);
				
				//m_oscillationTarget = super.GetY() + super.GetHeight();
				//m_oscillationStart = super.GetY();
			} else {
				//m_oscillationTarget = super.GetY() - super.GetHeight();
				m_oscillationStart = super.GetY();
				m_oscillationTarget = Math.min(m_oscillateFrom, m_oscillateTo);

			}
		}			
	}			
}