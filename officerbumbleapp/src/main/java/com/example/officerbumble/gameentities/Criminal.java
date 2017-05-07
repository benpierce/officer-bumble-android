package com.example.officerbumble.gameentities;

import java.util.Random;
import java.util.Stack;

import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.interfaces.CriminalListener;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DifficultyConfig;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.Point2D;
import com.example.officerbumble.engine.SoundManager;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteSheetManager;

public class Criminal extends Sprite {

	public enum CRIMINAL_STATE
	{
		RUNNING,
		THROWING_PIE,
		THROWING_CHICKENATOR_HIGH,
		THROWING_CHICKENATOR_LOW,
		THROWING_BOWLING_BALL,
		RIDING_ESCALATOR,
		LAUGHING,
		SCARED,
		CAUGHT,
		SECOND_WIND
	}	
	
	public enum CRIMINAL_WEAPON {
		PIE,
		CHICKENATOR_HIGH,
		CHICKENATOR_LOW,
		BOWLING_BALL
	}
	
	/********** Constants ***************/
	private final float MINIMUM_CHICKENATOR_DISTANCE = 2.0f;	// The minimum distance criminal can throw a chickenator.
	private final long MINIMUM_ATTACK_QUEUE_TIME = 250;		// Min # of milliseconds that must occur between last attack and the time that more attacks can be queued. 
	private final long MUST_ATTACK_TIME = 2000; 	// ms
	private final long ATTACK_CHECK_LENGTH = 1000;  // ms
	private final float SPEED_BOOST_ADVANTAGE = 0.5f;   // Screen coordinates.
	private final static String EASY = "EASY";
	private final static String NORMAL = "NORMAL";
	
	/********** State Variables *********/
	private CRIMINAL_STATE m_state;
	private float m_currentVelocity;	// Between 0 and 2 (the screen width) per second.
	private int m_floorLevel = 1;
	private DifficultyConfig m_difficultyConfig;
	private int m_secondWinds = 0;
	private int m_secondWindsFloor1 = 0;
	private int m_secondWindsFloor2 = 0;
	private int m_secondWindsFloor3 = 0;
	private int m_secondWindsFloor4 = 0;
	private int m_secondWindsAllowed = 0;
	private int m_secondWindsAllowedFloor1 = 0;
	private int m_secondWindsAllowedFloor2 = 0;
	private int m_secondWindsAllowedFloor3 = 0;
	private int m_secondWindsAllowedFloor4 = 0;
	private boolean m_isBumbleInCloseProximity = false;
	private float m_distanceTravelled = 0.0f;
	private float m_maxVelocity = 0.0f;
	private float m_secondWindDistance = 0.0f;
	private float m_criminalAttackPercentage = 0.0f;
	private float m_secondWindChanceFloor1 = 0.0f;
	private float m_secondWindChanceFloor2 = 0.0f;
	private float m_secondWindChanceFloor3 = 0.0f;
	private float m_secondWindChanceFloor4 = 0.0f;
	private boolean m_allowSecondWind = false;
	private float m_secondWindStartTime = 0;
	private float m_secondWindDuration = 0;
	private boolean m_allowCriminalMocking = false;
	private float m_mockingDistance = 0.0f;
	private float m_minTimeBetweenAttacks = 0;
	private float m_chickenatorAttackPercentage = 0.0f;
	private float m_bowlingBallAttackPercentage = 0.0f;
	private float m_pieAttackPercentage = 0.0f;
	private int m_maxWeaponsAllowedInAttack = 0;
	
	private boolean m_allowRandomWeaponVelocity = false;
	private float m_maxBowlingBallVelocity = 0.0f;
	private float m_minBowlingBallVelocity = 0.0f;
	private float m_maxPieVelocity = 0.0f;
	private float m_minPieVelocity = 0.0f;
	private float m_maxChickenatorVelocity = 0.0f;
	private float m_minChickenatorVelocity = 0.0f;
	private float m_bumbleMaximumVelocity = 0.0f;	
	
	/********** Attack Related Variables *********/
	private Stack<CRIMINAL_WEAPON> m_attacks = new Stack<CRIMINAL_WEAPON>();
	private float m_lastAttackTime = 0;
	private boolean m_hasAttacked = false;	
	private float m_lastAttackCheck = 0;
	private float m_maximumWeaponVelocity = 99f;			// Maximum velocity of new weapon attacks can never be greater than the maximum of all active weapons.
	private AttackPatternGenerator m_attackPatternGenerator;
	private float m_animationStartTime = 0;

	/********* Other Variables **********/
	private CriminalListener m_criminalListener;
	Random rand = new Random();
	
	/********* Bumble Knowledge *********/
	private float m_bumbleDistance = 99f;					// Distance to Bumble (affects second winds and whether or not we can attack).	
	private int m_bumbleLevel = 1;
	private float m_bumbleVelocity = 0.0f;
    private float m_slowDownPercentage = 1.0f;  // What % of the regular velocity should criminal drop to on 4th floor?

	/********** Minimap Variables ********/
	private Point2D m_mapPosition = null;
	
	/********** Animation Constants ***********/
	private final String CRIMINAL_RUN_RIGHT;
	private final String CRIMINAL_RUN_LEFT;
	private final String CRIMINAL_CHICKENATOR_HIGH_LEFT;
	private final String CRIMINAL_CHICKENATOR_HIGH_RIGHT;
	private final String CRIMINAL_CHICKENATOR_LOW_LEFT;
	private final String CRIMINAL_CHICKENATOR_LOW_RIGHT;	
	private final String CRIMINAL_PIE_RIGHT;
	private final String CRIMINAL_PIE_LEFT;
	private final String CRIMINAL_ESCALATOR_RIGHT;
	private final String CRIMINAL_ESCALATOR_LEFT;
	private final String CRIMINAL_LAUGHING_RIGHT;
	private final String CRIMINAL_LAUGHING_LEFT;
	private final String CRIMINAL_CAUGHT_RIGHT;
	private final String CRIMINAL_CAUGHT_LEFT;
	private final String CRIMINAL_BOWLING_BALL_RIGHT;
	private final String CRIMINAL_BOWLING_BALL_LEFT;
	private final String CRIMINAL_SCARED_RIGHT;
	private final String CRIMINAL_SCARED_LEFT;
	private final String CRIMINAL_SECOND_WIND_RIGHT;
	private final String CRIMINAL_SECOND_WIND_LEFT;
				
	// x and y are normalized between -1 and 1 and represents the middle point.
	public Criminal(float x, float y, float _height, float _width, int _zBufferIndex, float _creationTimeMilliseconds, DifficultyConfig _difficultyConfig, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, GameStateManager _gameStateManager, String _animationTag, String _tag) {
        super(x, y, _height, _width, _zBufferIndex, false, DIRECTION.RIGHT, false, _display, _spriteSheetManager, _animationTag, _tag);

        m_distanceTravelled = x;
        m_difficultyConfig = _difficultyConfig;
        m_secondWinds = 0;
        m_mapPosition = new Point2D(x, y);
        m_lastAttackTime = _creationTimeMilliseconds;
        m_hasAttacked = false;
        m_isBumbleInCloseProximity = false;
        m_maxVelocity = m_difficultyConfig.GetCriminalMaxVelocity();
        m_secondWindDistance = m_difficultyConfig.GetCriminalSecondWindDistance();
        m_criminalAttackPercentage = m_difficultyConfig.GetCriminalAttackPercentage();
        m_secondWindsAllowed = m_difficultyConfig.GetCriminalMaxSecondWinds();
        m_secondWindsAllowedFloor1 = m_difficultyConfig.GetCriminalMaxSecondWindsFloor1();
        m_secondWindsAllowedFloor2 = m_difficultyConfig.GetCriminalMaxSecondWindsFloor2();
        m_secondWindsAllowedFloor3 = m_difficultyConfig.GetCriminalMaxSecondWindsFloor3();
        m_secondWindsAllowedFloor4 = m_difficultyConfig.GetCriminalMaxSecondWindsFloor4();
        m_secondWindChanceFloor1 = m_difficultyConfig.GetCriminalSecondWindChanceFloor1();
        m_secondWindChanceFloor2 = m_difficultyConfig.GetCriminalSecondWindChanceFloor2();
        m_secondWindChanceFloor3 = m_difficultyConfig.GetCriminalSecondWindChanceFloor3();
        m_secondWindChanceFloor4 = m_difficultyConfig.GetCriminalSecondWindChanceFloor4();
        m_secondWindDuration = m_difficultyConfig.GetCriminalSecondWindDuration();
        m_allowSecondWind = m_difficultyConfig.AllowCriminalSecondWind();
        m_maxBowlingBallVelocity = m_difficultyConfig.GetBowlingBallMaximumVelocity();
        m_minBowlingBallVelocity = m_difficultyConfig.GetBowlingBallMinimumVelocity();
        m_maxPieVelocity = m_difficultyConfig.GetPieMaximumVelocity();
        m_minPieVelocity = m_difficultyConfig.GetPieMininumVelocity();
        m_maxChickenatorVelocity = m_difficultyConfig.GetChickenatorMaximumVelocity();
        m_minChickenatorVelocity = m_difficultyConfig.GetChickenatorMinimumVelocity();
        m_allowRandomWeaponVelocity = m_difficultyConfig.AllowRandomWeaponVelocity();
        m_bumbleMaximumVelocity = m_difficultyConfig.GetBumbleMaxVelocity();
        m_allowCriminalMocking = m_difficultyConfig.AllowCriminalMocking();
        m_mockingDistance = m_difficultyConfig.GetCriminalMockingDistance();
        m_minTimeBetweenAttacks = m_difficultyConfig.GetCriminalMinTimeBetweenAttacks();
        m_chickenatorAttackPercentage = m_difficultyConfig.GetChickenatorAttackPercentage();
        m_bowlingBallAttackPercentage = m_difficultyConfig.GetBowlingBallAttackPercentage();
        m_pieAttackPercentage = m_difficultyConfig.GetPieAttackPercentage();
        m_maxWeaponsAllowedInAttack = m_difficultyConfig.GetMaxWeaponsAllowedInAttack();
        m_currentVelocity = m_maxVelocity * ((super.GetDirection() == DIRECTION.LEFT) ? -1 : 1);
        m_attackPatternGenerator = new AttackPatternGenerator(_gameStateManager.GetLevel(), m_maxWeaponsAllowedInAttack, m_chickenatorAttackPercentage, m_bowlingBallAttackPercentage, m_pieAttackPercentage);

        if (m_difficultyConfig.GetDifficultyName().toUpperCase().equals("EASY")) {
            m_slowDownPercentage = 1.0f;
        } else if (m_difficultyConfig.GetDifficultyName().toUpperCase().equals("NORMAL")) {
            m_slowDownPercentage = 1.0f;
        } else if ( m_difficultyConfig.GetDifficultyName().toUpperCase().equals("HARD")) {
            m_slowDownPercentage = 1.0f;
        } else {
            m_slowDownPercentage = 1.0f;
        }

		// Setup Animation Constants
		CRIMINAL_RUN_RIGHT = _animationTag + "_RUN_RIGHT";
		CRIMINAL_RUN_LEFT = _animationTag + "_RUN_LEFT";
		CRIMINAL_CHICKENATOR_HIGH_LEFT = _animationTag + "_CHICKENATOR_HIGH_RIGHT";
		CRIMINAL_CHICKENATOR_HIGH_RIGHT = _animationTag + "_CHICKENATOR_HIGH_LEFT";
		CRIMINAL_CHICKENATOR_LOW_LEFT = _animationTag + "_CHICKENATOR_LOW_RIGHT";
		CRIMINAL_CHICKENATOR_LOW_RIGHT = _animationTag + "_CHICKENATOR_LOW_LEFT";		
		CRIMINAL_PIE_RIGHT = _animationTag + "_PIE_RIGHT";
		CRIMINAL_PIE_LEFT = _animationTag + "_PIE_LEFT";
		CRIMINAL_ESCALATOR_RIGHT = _animationTag + "_ESCALATOR_RIGHT";
		CRIMINAL_ESCALATOR_LEFT = _animationTag + "_ESCALATOR_LEFT";
		CRIMINAL_LAUGHING_RIGHT = _animationTag + "_LAUGHING_RIGHT";
		CRIMINAL_LAUGHING_LEFT = _animationTag + "_LAUGHING_LEFT";
		CRIMINAL_CAUGHT_RIGHT = _animationTag + "_CAUGHT_RIGHT";
		CRIMINAL_CAUGHT_LEFT = _animationTag + "_CAUGHT_LEFT";
		CRIMINAL_BOWLING_BALL_RIGHT = _animationTag + "_BOWLING_BALL_RIGHT";
		CRIMINAL_BOWLING_BALL_LEFT = _animationTag + "_BOWLING_BALL_LEFT";
		CRIMINAL_SCARED_RIGHT = _animationTag + "_SCARED_RIGHT";
		CRIMINAL_SCARED_LEFT = _animationTag + "_SCARED_LEFT";
		CRIMINAL_SECOND_WIND_RIGHT = _animationTag + "_SECOND_WIND_RIGHT";
		CRIMINAL_SECOND_WIND_LEFT = _animationTag + "_SECOND_WIND_LEFT";
				
		LoadAnimations();		
		ChangeState(CRIMINAL_STATE.RUNNING, _creationTimeMilliseconds);			
	}
		
	private void LoadAnimations() {				
		// start x, start y, frames, frames per second, isLooping, TAG
		super.LoadAnimation(CRIMINAL_RUN_RIGHT);
		super.LoadAnimation(CRIMINAL_CHICKENATOR_HIGH_RIGHT);	
		super.LoadAnimation(CRIMINAL_CHICKENATOR_LOW_RIGHT);
		super.LoadAnimation(CRIMINAL_BOWLING_BALL_RIGHT);	
		super.LoadAnimation(CRIMINAL_PIE_RIGHT);
		super.LoadAnimation(CRIMINAL_ESCALATOR_RIGHT);
		super.LoadAnimation(CRIMINAL_LAUGHING_RIGHT);
		super.LoadAnimation(CRIMINAL_SCARED_RIGHT);
		super.LoadAnimation(CRIMINAL_CAUGHT_RIGHT);
		super.LoadAnimation(CRIMINAL_SECOND_WIND_RIGHT);
		super.LoadAnimation(CRIMINAL_RUN_LEFT);
		super.LoadAnimation(CRIMINAL_CHICKENATOR_HIGH_LEFT);	
		super.LoadAnimation(CRIMINAL_CHICKENATOR_LOW_LEFT);
		super.LoadAnimation(CRIMINAL_BOWLING_BALL_LEFT);	
		super.LoadAnimation(CRIMINAL_PIE_LEFT);
		super.LoadAnimation(CRIMINAL_ESCALATOR_LEFT);	
		super.LoadAnimation(CRIMINAL_LAUGHING_LEFT);
		super.LoadAnimation(CRIMINAL_SCARED_LEFT);
		super.LoadAnimation(CRIMINAL_CAUGHT_LEFT);
		super.LoadAnimation(CRIMINAL_SECOND_WIND_LEFT);
	}		
		
	public void AdjustMapCoordinates(float _xAdjustment, float _yAdjustment) {
		m_mapPosition.SetX(m_mapPosition.GetX() + _xAdjustment);
		m_mapPosition.SetY(m_mapPosition.GetY() + _yAdjustment);
	}
	
	public Point2D GetMapPosition() {
		return m_mapPosition;
	}
		
	public void SecondWind(float _velocity, float _currentTimeMilliseconds) {
		m_secondWinds++;
        if ( m_floorLevel == 1) {
            m_secondWindsFloor1++;
        } else if ( m_floorLevel == 2 ) {
            m_secondWindsFloor2++;
        } else if ( m_floorLevel == 3 ) {
            m_secondWindsFloor3++;
        } else if ( m_floorLevel == 4 ) {
            m_secondWindsFloor4++;
        }

		m_currentVelocity = _velocity * ((super.GetDirection() == DIRECTION.LEFT) ? -1 : 1);
		m_secondWindStartTime = _currentTimeMilliseconds;
		SoundManager.PlaySound("SECOND WIND", false);
	}
	
	public void UpdateDistanceTravelled(float _distance) {
		m_distanceTravelled += _distance;
	}
	
	public float GetDistanceTravelled() {
		return m_distanceTravelled;
	}
	
	private float GetWeaponVelocity(CRIMINAL_WEAPON _type) {
		float velocity = 0;
		float minVelocity = 0;
		float maxVelocity = 0;
		
		if(_type == CRIMINAL_WEAPON.PIE) {
			minVelocity = m_minPieVelocity;
			maxVelocity = m_maxPieVelocity;
		} else if (_type == CRIMINAL_WEAPON.BOWLING_BALL) {
			minVelocity = m_minBowlingBallVelocity;
			maxVelocity = m_maxBowlingBallVelocity;			
		} else if (_type == CRIMINAL_WEAPON.CHICKENATOR_HIGH || _type == CRIMINAL_WEAPON.CHICKENATOR_LOW) {
			minVelocity = m_minChickenatorVelocity;
			maxVelocity = m_maxChickenatorVelocity;						
		} else {
			minVelocity = m_maximumWeaponVelocity;
			maxVelocity = m_maximumWeaponVelocity;
		}
		
		if(m_allowRandomWeaponVelocity) {
			velocity = rand.nextFloat() * (maxVelocity - minVelocity) + minVelocity;		
		} else {
			velocity = maxVelocity;
		}
				
		if(velocity > m_maximumWeaponVelocity) {
			velocity = m_maximumWeaponVelocity;
		}
			
		return velocity;
	}
	
	@Override
	public void HandleAnimationEvent(float _currentTimeMilliseconds) {
		float velocity = 0.0f;
		
		if(m_state == CRIMINAL_STATE.THROWING_BOWLING_BALL) {
			m_attacks.pop();
			velocity = GetWeaponVelocity(CRIMINAL_WEAPON.BOWLING_BALL);
			m_criminalListener.HandleCriminalAttack(CRIMINAL_WEAPON.BOWLING_BALL, velocity, _currentTimeMilliseconds);
			m_lastAttackTime = _currentTimeMilliseconds;
		} else if (m_state == CRIMINAL_STATE.THROWING_PIE) {
			m_attacks.pop();
			velocity = GetWeaponVelocity(CRIMINAL_WEAPON.PIE);
			m_criminalListener.HandleCriminalAttack(CRIMINAL_WEAPON.PIE, velocity, _currentTimeMilliseconds);
			m_lastAttackTime = _currentTimeMilliseconds;
		} else if (m_state == CRIMINAL_STATE.CAUGHT) {
			SoundManager.PlaySound("WINK", false);
		}
	}
	
	@Override
	public void HandleAnimationFinished(String _tag, float _currentTimeMilliseconds) {
		float velocity = 0.0f;

		if (_tag.equals((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_BOWLING_BALL_LEFT : CRIMINAL_BOWLING_BALL_RIGHT))) {
            ChangeState(CRIMINAL_STATE.RUNNING, _currentTimeMilliseconds);
		} else if (_tag.equals((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_PIE_LEFT : CRIMINAL_PIE_RIGHT))) {
            ChangeState(CRIMINAL_STATE.RUNNING, _currentTimeMilliseconds);
		} else if (_tag.equals((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_CHICKENATOR_HIGH_LEFT : CRIMINAL_CHICKENATOR_HIGH_RIGHT))){
			m_attacks.pop();
			velocity = GetWeaponVelocity(CRIMINAL_WEAPON.CHICKENATOR_HIGH);			
			m_criminalListener.HandleCriminalAttack(CRIMINAL_WEAPON.CHICKENATOR_HIGH, velocity, _currentTimeMilliseconds);			
			ChangeState(CRIMINAL_STATE.RUNNING, _currentTimeMilliseconds);
			m_lastAttackTime = _currentTimeMilliseconds;
		} else if (_tag.equals((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_CHICKENATOR_LOW_LEFT : CRIMINAL_CHICKENATOR_LOW_RIGHT))){
			m_attacks.pop();
			velocity = GetWeaponVelocity(CRIMINAL_WEAPON.CHICKENATOR_LOW);			
			m_criminalListener.HandleCriminalAttack(CRIMINAL_WEAPON.CHICKENATOR_LOW, velocity, _currentTimeMilliseconds);			
			ChangeState(CRIMINAL_STATE.RUNNING, _currentTimeMilliseconds);
			m_lastAttackTime = _currentTimeMilliseconds;
		} else if (_tag.equals((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_SCARED_LEFT : CRIMINAL_SCARED_RIGHT))) {
			// Chance to fire second wind.
			if(CanSecondWind()) {
				SecondWind(m_bumbleMaximumVelocity + SPEED_BOOST_ADVANTAGE, _currentTimeMilliseconds);					
				ChangeState(CRIMINAL_STATE.SECOND_WIND, _currentTimeMilliseconds);
			} else {
				ChangeState(CRIMINAL_STATE.RUNNING, _currentTimeMilliseconds);
			}
		} else if (_tag.equals((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_CAUGHT_LEFT : CRIMINAL_CAUGHT_RIGHT ))) {			
			m_criminalListener.HandleCriminalCaught(_currentTimeMilliseconds);			
		}
	}
	
	private void HandleMocking(float _currentTime) {
		// On easy difficulty, the criminal will stop and mock Bumble if he
		// gets too far ahead. Once Bumble catches up a bit,
		// the criminal will take off again.
		if (m_allowCriminalMocking) {								
			if (m_state != CRIMINAL_STATE.LAUGHING && m_state != CRIMINAL_STATE.RIDING_ESCALATOR && m_floorLevel == m_bumbleLevel) {
				if(m_bumbleDistance >= m_mockingDistance) {
					ChangeState(CRIMINAL_STATE.LAUGHING, _currentTime);
					m_attacks.clear();
					m_currentVelocity = 0.0f;
				}
			} else if (m_state == CRIMINAL_STATE.LAUGHING) {
				if(m_bumbleDistance < m_mockingDistance - 0.3f) {
					ChangeState(CRIMINAL_STATE.RUNNING, _currentTime);
					m_currentVelocity = m_maxVelocity * ((super.GetDirection() == DIRECTION.LEFT) ? -1 : 1);
				}
			}
		}
	}
	
	public void SetMaximumWeaponVelocity(float _maximumWeaponVelocity) {
		m_maximumWeaponVelocity = _maximumWeaponVelocity;
	}
	
	public void SetBumbleKnowledge(Bumble _bumble) {
		m_bumbleDistance = 99f;
				
		if(_bumble.GetFloorLevel() == this.GetFloorLevel()) {
			if(_bumble.GetDirection() == DIRECTION.RIGHT) {
                float criminalAdjustedX = this.GetX() + (this.GetWidth() * this.GetCollisionBufferPercentX1());
                float bumbleAdjustedX = _bumble.GetX() + _bumble.GetWidth() - (_bumble.GetWidth() * _bumble.GetCollisionBufferPercentX2());

				m_bumbleDistance = criminalAdjustedX - bumbleAdjustedX;
			} else {
                float criminalAdjustedX = this.GetX() + this.GetWidth() - (this.GetWidth() * this.GetCollisionBufferPercentX1());
                float bumbleAdjustedX = _bumble.GetX() + (_bumble.GetWidth() * _bumble.GetCollisionBufferPercentX2());

				m_bumbleDistance = bumbleAdjustedX - criminalAdjustedX;
			}
		}
						
		m_bumbleLevel = _bumble.GetFloorLevel();
		m_bumbleVelocity = _bumble.GetVelocity();
	}	
	
	@Override
	public void Update(Timer _realTimer, Timer _gameTimer) {
		super.Update( _realTimer,  _gameTimer);

        if ( !_gameTimer.IsPaused() ) {
            float gameMilliseconds = _gameTimer.GetCurrentMilliseconds();

            HandleMocking(gameMilliseconds);

            if (m_state == CRIMINAL_STATE.SECOND_WIND) {
                if (gameMilliseconds > m_secondWindStartTime + m_secondWindDuration) {
                    ChangeState(CRIMINAL_STATE.RUNNING, gameMilliseconds);
                    m_currentVelocity = m_maxVelocity * ((super.GetDirection() == DIRECTION.LEFT) ? -1 : 1);
                }
            }

            // Check if Bumble is too close for comfort, if so, fire the scared animation.
            if (!CheckIfBumbleClose(gameMilliseconds)) {
                // Only attack if Bumble isn't too close.
                if (CanDoNewAttack()) {
                    if (gameMilliseconds - m_lastAttackCheck >= ATTACK_CHECK_LENGTH) {
                        float randomNum = rand.nextFloat() * (1.0f - 0.0f) + 0.0f;
                        m_lastAttackCheck = gameMilliseconds;

                        boolean mustAttack = false;
                        if (!m_hasAttacked && gameMilliseconds - m_lastAttackTime > MUST_ATTACK_TIME) {
                            mustAttack = true;
                            m_hasAttacked = true;
                        }

                        if (randomNum <= m_criminalAttackPercentage || mustAttack) {
                            QueueAttack(gameMilliseconds);
                        }
                    }
                }

                // If there's anything in the attack queue, then attack if it's been the minimum
                // cooldown after the last attack.
                AttackIfPossible(gameMilliseconds);
            }
        }
	}
	
	// Criminal can only attack if 
	private boolean CanDoNewAttack() {
		return (m_floorLevel != m_bumbleLevel || m_attacks.size() > 0 || m_state == CRIMINAL_STATE.LAUGHING || m_state == CRIMINAL_STATE.RIDING_ESCALATOR || m_bumbleDistance < m_secondWindDistance) ? false : true;
	}
			
	// If there's anything in the queue, and we've waited the minimum amount of time since the last attack, then 
	// perform the attack.
	private void AttackIfPossible(float _currentTime) {
		if(m_attacks.size() > 0 && m_state == CRIMINAL_STATE.RUNNING && m_bumbleDistance >= m_secondWindDistance) {			
			if(_currentTime - m_lastAttackTime > m_minTimeBetweenAttacks) {
				CRIMINAL_WEAPON weapon = m_attacks.peek();				
				if(weapon == CRIMINAL_WEAPON.CHICKENATOR_HIGH){
					if(CanThrowChickenator()) {
						ChangeState(CRIMINAL_STATE.THROWING_CHICKENATOR_HIGH, _currentTime);
					} else {
						m_attacks.pop();
					}
				} 
				else if (weapon == CRIMINAL_WEAPON.CHICKENATOR_LOW) {
					if(CanThrowChickenator()) {
						ChangeState(CRIMINAL_STATE.THROWING_CHICKENATOR_LOW, _currentTime);
					} else {
						m_attacks.pop();
					}
				}
				else if(weapon == CRIMINAL_WEAPON.PIE){
					ChangeState(CRIMINAL_STATE.THROWING_PIE, _currentTime);
				}
				else if(weapon == CRIMINAL_WEAPON.BOWLING_BALL){
					ChangeState(CRIMINAL_STATE.THROWING_BOWLING_BALL, _currentTime);
				}				
			}
		}
	}
			
	private boolean CanThrowChickenator() {
		return (m_bumbleDistance <= MINIMUM_CHICKENATOR_DISTANCE) || (m_chickenatorAttackPercentage == 0.0f) ? false : true;
	}
	
	// Determine which attack to perform and then add it to the queue based on the attack pattern generator.
	private void QueueAttack(float _currentTime) {
		if(_currentTime - m_lastAttackTime > MINIMUM_ATTACK_QUEUE_TIME) {		
			CRIMINAL_WEAPON[] attackQueue = m_attackPatternGenerator.GenerateAttackPattern(CanThrowChickenator());
			//CRIMINAL_WEAPON[] attackQueue = {CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL, CRIMINAL_WEAPON.BOWLING_BALL};

            for(int i = 0; i < attackQueue.length; i++) {
				m_attacks.push(attackQueue[i]);
			}
		}		
	}
			
	public float GetVelocity() {		
		// On easy and normal, if the criminal is off screen we should give the player a chance to catch up.
		if((m_difficultyConfig.GetDifficultyName().equals(EASY) || m_difficultyConfig.GetDifficultyName().equals(NORMAL)) && !super.IsOnScreen()) {
			return m_currentVelocity * 0.70f;
		} else if ( m_floorLevel == 4 ) {
            return m_currentVelocity * m_slowDownPercentage;
        } else {
			return m_currentVelocity;
		}
	}	
	
	public CRIMINAL_STATE GetState() {
		return m_state;
	}
		
	public void ChangeState(CRIMINAL_STATE state, float _startTimeMilliseconds) {
		m_state = state;
		
		switch(state) {
			case RUNNING:  
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_RUN_LEFT : CRIMINAL_RUN_RIGHT), _startTimeMilliseconds);				
				break;
			case THROWING_PIE:
                m_animationStartTime = _startTimeMilliseconds;
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_PIE_LEFT : CRIMINAL_PIE_RIGHT), _startTimeMilliseconds);
				break;
			case THROWING_CHICKENATOR_HIGH:
                m_animationStartTime = _startTimeMilliseconds;
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_CHICKENATOR_HIGH_LEFT : CRIMINAL_CHICKENATOR_HIGH_RIGHT), _startTimeMilliseconds);
				break;
			case THROWING_CHICKENATOR_LOW:
                m_animationStartTime = _startTimeMilliseconds;
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_CHICKENATOR_LOW_LEFT : CRIMINAL_CHICKENATOR_LOW_RIGHT), _startTimeMilliseconds);
				break;
			case THROWING_BOWLING_BALL:
                m_animationStartTime = _startTimeMilliseconds;
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_BOWLING_BALL_LEFT : CRIMINAL_BOWLING_BALL_RIGHT), _startTimeMilliseconds);				
				break;	
			case RIDING_ESCALATOR:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_ESCALATOR_LEFT : CRIMINAL_ESCALATOR_RIGHT), _startTimeMilliseconds);
				break;
			case LAUGHING:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_LAUGHING_LEFT : CRIMINAL_LAUGHING_RIGHT), _startTimeMilliseconds);
				break;		
			case CAUGHT:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_CAUGHT_LEFT : CRIMINAL_CAUGHT_RIGHT), _startTimeMilliseconds);
				SoundManager.PlaySound("SCRAMBLE", false);
				break;
			case SCARED:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_SCARED_LEFT : CRIMINAL_SCARED_RIGHT), _startTimeMilliseconds);
				SoundManager.PlaySound("YIKES", false);
				break;
			case SECOND_WIND:
				super.SetCurrentAnimation((super.GetDirection() == DIRECTION.LEFT ? CRIMINAL_SECOND_WIND_LEFT : CRIMINAL_SECOND_WIND_RIGHT), _startTimeMilliseconds);
				break;
		}
	}
			
	/* Waypoint Related Functionality 
	 * 
	 * Have to override WaypointStart because the criminal's velocity should drop to 0 when he hits the escalator.
	 * */
	@Override
	public void WaypointStart(float _waypointX, float _waypointY, DIRECTION _waypointDirection, float _waypointTraversalTime, float _waypointStartTime) {
		super.WaypointStart(_waypointX, _waypointY, _waypointDirection, _waypointTraversalTime, _waypointStartTime);
		m_currentVelocity = 0.0f;
	}						
	/* End of Waypoint Functionality */	
	
	public DIRECTION GetDirection() {
		return super.GetDirection();
	}
	
	public void IncrementFloorLevel() {
		m_floorLevel++;
	}
	
	public int GetFloorLevel() {
		return m_floorLevel;
	}
					
	public void Run(float _currentTimeMilliseconds) {
		ChangeState(CRIMINAL_STATE.RUNNING, _currentTimeMilliseconds);
	}
	
	public void Caught(float _currentTimeMilliseconds) {
		m_currentVelocity = 0.0f;
		ChangeState(CRIMINAL_STATE.CAUGHT, _currentTimeMilliseconds);
	}
	
	public void RideEscalator(float _startTimeMilliseconds) {
		ChangeState(CRIMINAL_STATE.RIDING_ESCALATOR, _startTimeMilliseconds);
		m_attacks.clear();	// We don't anything hanging out in the queue when we get off the escalator.
	}
	
	public void RegisterCriminalListener(CriminalListener _criminalListener) {
		m_criminalListener = _criminalListener;
	}
	
	// This method checks to see if Bumble is too close for comfort (IE: the criminal can no longer attack). If so, 
	// we always want to play a scared animation. Because we don't want this scared animation being constantly fired,
	// we need to keep track of when Bumble enters this distance and when he leaves this distance. The criminal is
	// only to fire this scared animation once per trigger.
	private boolean CheckIfBumbleClose(float _currentTimeMilliseconds) {
		boolean result = false;
		
		if(m_bumbleDistance <= m_secondWindDistance) {

			result = true;
			if(!m_isBumbleInCloseProximity && m_state == CRIMINAL_STATE.RUNNING) {
				m_isBumbleInCloseProximity = true;
				ChangeState(CRIMINAL_STATE.SCARED, _currentTimeMilliseconds);
			}
		} else {
			m_isBumbleInCloseProximity = false;
		}
		
		return result;
	}
		
	// Criminal can only speed boost if he's running, hasn't used all his speed boosts, and hasn't used up his speed boost by floor
	// allotment.
	private boolean CanSecondWind() {
		boolean canSecondWind = false;	
		boolean secondWindsLeftOnFloor = false;
		float secondWindChance = 0.0f;
		
		if((m_state == CRIMINAL_STATE.RUNNING || m_state == CRIMINAL_STATE.SCARED) && m_secondWinds < m_secondWindsAllowed && m_allowSecondWind) {
			if(m_floorLevel == 1 && m_secondWindsFloor1 < m_secondWindsAllowedFloor1) {
				secondWindsLeftOnFloor = true;
				secondWindChance = m_secondWindChanceFloor1;
			} else if (m_floorLevel == 2 && m_secondWindsFloor2 < m_secondWindsAllowedFloor2) {
				secondWindsLeftOnFloor = true;
				secondWindChance = m_secondWindChanceFloor2;
			} else if (m_floorLevel == 3 && m_secondWindsFloor3 < m_secondWindsAllowedFloor3) {
				secondWindsLeftOnFloor = true;
				secondWindChance = m_secondWindChanceFloor3;
			} else if (m_floorLevel == 4 && m_secondWindsFloor4 < m_secondWindsAllowedFloor4) {
				secondWindsLeftOnFloor = true;
				secondWindChance = m_secondWindChanceFloor4;
			}
			
			if(secondWindsLeftOnFloor) {
				float number = rand.nextFloat();
			
				if(number < secondWindChance) {
					canSecondWind = true;
				}
			}
		}
		
		return canSecondWind;
	}

	@Override
	public void ChangeDirection() {
		super.ChangeDirection();
		
		m_currentVelocity = m_maxVelocity * ((super.GetDirection() == DIRECTION.LEFT) ? -1 : 1); 
	}
		
}
