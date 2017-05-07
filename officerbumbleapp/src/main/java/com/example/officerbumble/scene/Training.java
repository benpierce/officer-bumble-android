package com.example.officerbumble.scene;

import java.util.ArrayList;
import java.util.List;

import com.example.officerbumble.gameentities.StaticImage;
import com.example.officerbumble.interfaces.BumbleListener;
import com.example.officerbumble.interfaces.ButtonListener;
import com.example.officerbumble.interfaces.ChickenatorListener;
import com.example.officerbumble.interfaces.RoboThrower2000Listener;
import android.content.Context;

import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DeviceDisplay.ANCHOR;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.Point2D;
import com.example.officerbumble.engine.SoundManager;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.Sprite.DIRECTION;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.gameentities.BowlingBall;
import com.example.officerbumble.gameentities.Bumble;
import com.example.officerbumble.gameentities.Bumble.BUMBLE_STATE;
import com.example.officerbumble.gameentities.Button;
import com.example.officerbumble.gameentities.Chickenator.CHICKENATOR_POSITION;
import com.example.officerbumble.gameentities.Criminal.CRIMINAL_WEAPON;
import com.example.officerbumble.gameentities.Pie;
import com.example.officerbumble.gameentities.Popup;
import com.example.officerbumble.gameentities.RoboThrower2000;
import com.example.officerbumble.gameentities.Popup.POPUP_TYPE;
import com.example.officerbumble.gameentities.RoboThrower2000.WEAPON_TYPE;
import com.example.officerbumble.gameentities.Feather;
import com.example.officerbumble.gameentities.TextArea;
import com.example.officerbumble.gameentities.Chickenator;
import com.example.officerbumble.gameentities.Treadmill;
import com.example.officerbumble.gameentities.Weapon;

public class Training extends Scene implements ButtonListener, BumbleListener, RoboThrower2000Listener, ChickenatorListener {
	
	// Training Constants
	private final float RANDOM_ATTACK_CHECK_INTERVAL = 250;	// Milliseconds
	private final float WEAPON_VELOCITY = 1.9f;
	private final int MAX_ATTACK_CHECKS = 6;				// Maximum number of times to randomly check for an attack before it must happen. 
	private final float RANDOM_ATTACK_CHANCE = 0.25f;		// Chance of an attack as a percentage.
    private float TOUCH_WIDTH = 0.60f;
    private float TOUCH_HEIGHT = 0.60f;

	// Game Objects
	public Treadmill m_treadmill;
	public Bumble m_bumble;
	public RoboThrower2000 m_thrower;
    private StaticImage m_tapJump;
    private StaticImage m_tapDuck;
    private Button m_mute;
	private Popup m_popup;
	private TextArea m_objectives;
	private TextArea m_frameRate;
	private float m_lastFrameRateUpdate = 0;
    private boolean m_showFrameRate = false;
	
	// Objective Variables
	private boolean m_randomVelocity = false;
	private int m_weaponsInPlay = 0;
	private int m_consecutiveDucks = 0;
	private int m_consecutiveJumps = 0;
	private boolean m_started = false;
	private boolean m_duckExplainationShown = false;
	private boolean m_pieExplainationShown = false;
	private int m_consecutivePies = 0;
	private int m_consecutiveBowlingBalls = 0;
	private int m_consecutiveChickenators = 0;
	private int m_consecutiveDodges = 0;
	private float m_lastAttackCheckTime = 0;
	private int m_consecutiveChecks = 0;
	
	// Objectives (so we're not hammering the refresh so hard).
	private int m_previousDucks = -1;
	private int m_previousJumps = -1;
	private int m_previousPies = -1;
	private int m_previousBowlingBalls = -1;
	private int m_previousChickenators = -1;
	private int m_previousDodges = -1;
	
	// Training State		
	private enum TRAINING_STATE {
		INITIAL_EXPLAINATION,
		DUCK_EXPLAINATION,
		DUCK_TEST,
		JUMP_EXPLAINATION,
		JUMP_TEST,
		GENERAL_EXPLAINATION,
		PIE_EXPLAINATION,
		PIE_TEST,
		BOWLING_BALL_EXPLAINATION,
		BOWLING_BALL_TEST,
		CHICKENATOR_HIGH_EXPLAINATION,
		CHICKENATOR_HIGH_TEST,
		CHICKENATOR_LOW_EXPLAINATION,
		CHICKENATOR_LOW_TEST,
		RANDOM_EXPLAINATION,
		RANDOM_TEST,
		FINAL_CONGRATS,
		RANDOM_NONSTOP		
	}
	private int m_weaponCount = 0;	
	private TRAINING_STATE m_state = TRAINING_STATE.INITIAL_EXPLAINATION;
	Point2D m_topLeft = super.GetDisplay().GetAnchorCoordinates(ANCHOR.TOP_LEFT, 1.0f, 1.0f);
	
	// Performance Related Variables
	private Sprite[] m_sprites;
	private Sprite m_tempSprite;
	private Chickenator m_tempChickenator;
	private Feather m_tempFeather;
	private Feather m_featherToRemove;
	
	// Constructor
	public Training(DeviceDisplay _display, SpriteSheetManager _spriteSheetMananger, Timer _realTimer, Timer _gameTimer, Context _context) {
				super(_display, _spriteSheetMananger, _realTimer, _gameTimer, _context);
				
		m_objectives = super.CreateTextArea(m_topLeft.GetX(), m_topLeft.GetY(), 0.16f, 0.16f, 3.0f, 75, "", "Objectives");				
		super.AddSprites(m_objectives.GetSprites());
		m_frameRate = super.CreateTextArea(m_topLeft.GetX(), m_topLeft.GetY() - 0.17f, 0.16f, 0.16f, 2.5f, 25, "", "FrameRate");

        if ( m_showFrameRate ) {
            super.AddSprites(m_frameRate.GetSprites());
        }
	}

	// Get a reference to training objects directly from the scene setup.
	@Override
	public void Initialize(SceneConfig _config, DifficultyManager _difficultyManager, GameStateManager _gameStateManager, AD_TYPE _adVisibility, Object _inputListener) {
		super.Initialize(_config, _difficultyManager, _gameStateManager, _adVisibility, _inputListener);
			
		float currentTime = super.GetGameTimer().GetCurrentMilliseconds();
		
		m_bumble = (Bumble) super.GetSprite("BUMBLE");
		m_bumble.RegisterBumbleListener(this);
		m_treadmill = (Treadmill) super.GetSprite("TREADMILL");
		m_thrower = (RoboThrower2000) super.GetSprite("ROBO_THROWER_2000");
		m_mute = (Button) super.GetSprite("BUTTON_MUTE");
		if(SoundManager.IsMuted()) {
			m_mute.SetCurrentAnimation("BUTTON_MUTE_OFF", currentTime);
		} 
		
		m_thrower.RegisterForEvents(this);
		SoundManager.StopMusic();
					
		CheckState();
	}	
	
	// Check state is responsible for checking to see if goals are met, and if so, transitioning to the next state. This method should
	// be called after every significant event.
	private void CheckState() {
        float gameTimeMilliseconds = super.GetGameTimer().GetCurrentMilliseconds();

		if(m_state == TRAINING_STATE.INITIAL_EXPLAINATION && !m_started) {			
			String textValue = "Welcome to training Officer Bumble. This training simulation will ensure you have the skills to take down even the most hardened criminals.";			
			m_popup = super.CreatePopup(POPUP_TYPE.OK, 2.0f, 1.2f, true, textValue, this);									
			m_bumble.Run(gameTimeMilliseconds);
			m_popup.Open();
			super.Pause();
			m_started = true;
		} else if (m_state == TRAINING_STATE.DUCK_EXPLAINATION && !m_duckExplainationShown) {
			String textValue = "You can duck by tapping anywhere on the left side of your screen. Duck 3 times in a row to continue.";
			m_popup = super.CreatePopup(POPUP_TYPE.OK,  2.0f,  1.1f,  true, textValue,  this);
			m_bumble.Run(gameTimeMilliseconds);
            ShowTapDuck(gameTimeMilliseconds);
			m_popup.Open();
			super.Pause();
			m_duckExplainationShown = true;
		} else if (m_state == TRAINING_STATE.DUCK_TEST && m_consecutiveDucks >= 3) {
			String textValue = "Good job, you've mastered ducking. To jump, tap anywhere on the right side of your screen. Jump 3 times in a row to continue.";
			m_state = TRAINING_STATE.JUMP_EXPLAINATION;
			m_popup = super.CreatePopup(POPUP_TYPE.OK,  2.0f,  1.2f,  true, textValue,  this);			
			m_bumble.Run(gameTimeMilliseconds);
            ShowTapJump(gameTimeMilliseconds);
			m_popup.Open();
			super.Pause();
		} else if (m_state == TRAINING_STATE.JUMP_TEST && m_consecutiveJumps >= 3) {
			String textValue = "I see you've got the controls mastered. In real life things won't be so easy. You'll need to time your jumping and ducking to avoid objects thrown by criminals.";
			m_state = TRAINING_STATE.GENERAL_EXPLAINATION;
			m_popup = super.CreatePopup(POPUP_TYPE.OK,  2.0f,  1.3f,  true, textValue,  this);
			m_bumble.Run(gameTimeMilliseconds);
			m_popup.Open();
			super.Pause();
		} else if (m_state == TRAINING_STATE.PIE_EXPLAINATION && !m_pieExplainationShown) {
			String textValue = "Pies will fly towards your head. You need to avoid pies by ducking. The RoboThrower 2000 � will now hurl pies at you. Duck 3 pies to continue.";			
			m_popup = super.CreatePopup(POPUP_TYPE.OK,  2.0f,  1.2f,  true, textValue,  this);
			m_bumble.Run(gameTimeMilliseconds);
			m_popup.Open();
			super.Pause();
			m_pieExplainationShown = true;
		} else if (m_state == TRAINING_STATE.PIE_TEST && m_consecutivePies < 3 && m_weaponsInPlay == 0) {
			// Attacks are handled by the update.
		} else if (m_state == TRAINING_STATE.PIE_TEST && m_consecutivePies >= 3) {
			String textValue = "Excellent Bumble! Jump bowling balls to avoid being knocked down. Jump 3 bowling balls to continue.";
			m_state = TRAINING_STATE.BOWLING_BALL_EXPLAINATION;
			m_popup = super.CreatePopup(POPUP_TYPE.OK, 2.0f, 1.0f, true, textValue, this);
			m_bumble.Run(gameTimeMilliseconds);
			m_popup.Open();
			super.Pause();
		} else if (m_state == TRAINING_STATE.BOWLING_BALL_TEST && m_consecutiveBowlingBalls < 3 && m_weaponsInPlay == 0) {
			// Attacks handled by the update.
		} else if (m_state == TRAINING_STATE.BOWLING_BALL_TEST && m_consecutiveBowlingBalls >= 3) {
			String textValue = "Perfect! Some of the more cunning McBurgler Brothers will be armed with Chickenators: nasty birds that can go either high or low. Try jumping some low Chickenators.";
			m_state = TRAINING_STATE.CHICKENATOR_HIGH_EXPLAINATION;
			m_popup = super.CreatePopup(POPUP_TYPE.OK, 2.0f, 1.4f, true, textValue, this);
			m_bumble.Run(gameTimeMilliseconds);
			m_popup.Open();
			super.Pause();
		} else if (m_state == TRAINING_STATE.CHICKENATOR_HIGH_TEST && m_consecutiveChickenators < 3 && m_weaponsInPlay == 0) {
			// Attacks handled by the update.
		} else if (m_state == TRAINING_STATE.CHICKENATOR_HIGH_TEST && m_consecutiveChickenators >= 3) {
			String textValue = "Ok you've got those low chickenators mastered, now try ducking some high Chickenators.";
			m_state = TRAINING_STATE.CHICKENATOR_LOW_EXPLAINATION;
			m_popup = super.CreatePopup(POPUP_TYPE.OK, 2.0f, 1.0f, true, textValue, this);
			m_bumble.Run(gameTimeMilliseconds);
			m_popup.Open();
			super.Pause();
		} else if (m_state == TRAINING_STATE.CHICKENATOR_LOW_TEST && m_consecutiveChickenators >= 3) {
			String textValue = "Well done Bumble, there's hope for you yet! Now let's really test your resolve. Dodge 10 random objects in a row without being hit to continue.";
			m_state = TRAINING_STATE.RANDOM_EXPLAINATION;
			m_popup = super.CreatePopup(POPUP_TYPE.OK, 2.0f, 1.3f, true, textValue, this);
			m_bumble.Run(gameTimeMilliseconds);
			m_popup.Open();
			super.Pause();
		} else if (m_state == TRAINING_STATE.CHICKENATOR_LOW_TEST && m_consecutiveChickenators < 3 && m_weaponsInPlay == 0) {
			// Attacks handled by the update.
		} else if (m_state == TRAINING_STATE.RANDOM_TEST && m_consecutiveDodges < 10 && m_weaponsInPlay == 0) {
			// Attacks handled by the update.
		} else if (m_state == TRAINING_STATE.RANDOM_TEST && m_consecutiveDodges >= 10) {
			String textValue = "You've done it! You're now ready to see action. You can report for duty at any time by closing out to the main menu. Until then, you can continue practicing for as long as you wish.";
			m_state = TRAINING_STATE.FINAL_CONGRATS;			
			m_popup = super.CreatePopup(POPUP_TYPE.OK, 2.0f, 1.5f, true, textValue, this);
			m_bumble.Run(gameTimeMilliseconds);
			m_popup.Open();
			super.Pause();
		}
		else if (m_state == TRAINING_STATE.RANDOM_NONSTOP && m_weaponsInPlay == 0) {
			// Attacks handled by the update.			
		}
	}	
		
	/*
	 * Handle all Button Clicks
	 * 
	 * BUTTON_QUIT = The player decided to quit the training simulation
	 * BUTTON_OK = The player would like to skip to the next phase of the tutorial.
	 */
	@Override
	public void HandleButtonPressed(String _tag, float _currentTimeMilliseconds) {
		if(_tag.equals("BUTTON_QUIT")) {			
			super.HandleQuitToTitle();
		} else if(_tag.equals("BUTTON_MUTE")) {
			// If we're not muted, then mute the sound.
			if(SoundManager.IsMuted()) {
				m_mute.SetCurrentAnimation("BUTTON_MUTE_ON", _currentTimeMilliseconds);
				SoundManager.Unmute();
			} else {
				m_mute.SetCurrentAnimation("BUTTON_MUTE_OFF", _currentTimeMilliseconds);
				SoundManager.Mute();
			}
		}		
				
		if(_tag.equals("BUTTON_OK")) {
			if(m_popup != null) {
				m_popup.Close();				
				
				if(m_state == TRAINING_STATE.INITIAL_EXPLAINATION) {
					m_state = TRAINING_STATE.DUCK_EXPLAINATION;					
				} else if(m_state == TRAINING_STATE.DUCK_EXPLAINATION) {
					m_state = TRAINING_STATE.DUCK_TEST;
                    RemoveTapDuck();
					ClearObjectiveCounts();
					super.Unpause();
				} else if(m_state == TRAINING_STATE.JUMP_EXPLAINATION) {
					m_state = TRAINING_STATE.JUMP_TEST;
                    RemoveTapJump();
					ClearObjectiveCounts();
					super.Unpause();
				} else if(m_state == TRAINING_STATE.GENERAL_EXPLAINATION) {
					m_state = TRAINING_STATE.PIE_EXPLAINATION;
				} else if(m_state == TRAINING_STATE.PIE_EXPLAINATION) {
					ClearObjectiveCounts();
					m_state = TRAINING_STATE.PIE_TEST;
					super.Unpause();
				} else if (m_state == TRAINING_STATE.BOWLING_BALL_EXPLAINATION) {
					ClearObjectiveCounts();
					m_state = TRAINING_STATE.BOWLING_BALL_TEST;
					super.Unpause();
				} else if (m_state == TRAINING_STATE.CHICKENATOR_HIGH_EXPLAINATION) {
					ClearObjectiveCounts();
					m_state = TRAINING_STATE.CHICKENATOR_HIGH_TEST;
					super.Unpause();
				} else if (m_state == TRAINING_STATE.CHICKENATOR_LOW_EXPLAINATION) {
					ClearObjectiveCounts();
					m_state = TRAINING_STATE.CHICKENATOR_LOW_TEST;
					super.Unpause();
				} else if (m_state == TRAINING_STATE.RANDOM_EXPLAINATION) {
					ClearObjectiveCounts();
					m_state = TRAINING_STATE.RANDOM_TEST;
					super.Unpause();
				} else if (m_state == TRAINING_STATE.FINAL_CONGRATS) {
					ClearObjectiveCounts();
					m_state = TRAINING_STATE.RANDOM_NONSTOP;
					m_randomVelocity = true;
					super.Unpause();
				} 
			}				
		}
	}

    private void ShowTapJump(float _currentTimeMilliseconds) {
        Point2D position = super.GetDisplay().GetAnchorCoordinates(ANCHOR.CENTER_RIGHT, TOUCH_WIDTH, TOUCH_HEIGHT);

        m_tapJump = new StaticImage(position.GetX(), position.GetY() + 0.30f, TOUCH_WIDTH, TOUCH_HEIGHT, 2, true, _currentTimeMilliseconds, super.GetDisplay(), super.GetSpriteSheetManager(), "TOUCH_JUMP", "TOUCH_JUMP");
        super.GetSpriteManager().AddSprite(m_tapJump);
    }

    private void ShowTapDuck(float _currentTimeMilliseconds) {
        Point2D position = super.GetDisplay().GetAnchorCoordinates(ANCHOR.CENTER_LEFT, TOUCH_WIDTH, TOUCH_HEIGHT);

        m_tapDuck = new StaticImage(position.GetX(), position.GetY() + 0.30f, TOUCH_WIDTH, TOUCH_HEIGHT, 2, true, _currentTimeMilliseconds, super.GetDisplay(), super.GetSpriteSheetManager(), "TOUCH_DUCK", "TOUCH_DUCK");
        super.GetSpriteManager().AddSprite(m_tapDuck);
    }

    private void RemoveTapJump() {
        if ( m_tapJump != null ) {
            super.GetSpriteManager().RemoveSprite(m_tapJump.GetTag());
        }
    }

    private void RemoveTapDuck() {
        if ( m_tapDuck != null ) {
            super.GetSpriteManager().RemoveSprite(m_tapDuck.GetTag());
        }
    }
	
	private void UpdateObjectives() {
        float gameTimeMilliseconds = super.GetGameTimer().GetCurrentMilliseconds();

        if ( m_showFrameRate ) {
            if (super.GetGameTimer().GetCurrentMilliseconds() - m_lastFrameRateUpdate > 1000.0) {
                m_frameRate.UpdateText("FrameRate: " + super.GetGameStateManager().GetFrameRate() + " FPS", gameTimeMilliseconds, super.GetSpriteManager());
                m_lastFrameRateUpdate = super.GetGameTimer().GetCurrentMilliseconds();
            }
        }
				
		if(m_state == TRAINING_STATE.DUCK_TEST) {
			if(m_consecutiveDucks != m_previousDucks) {
				m_previousDucks = m_consecutiveDucks;				
				m_objectives.UpdateText("Objective: " + m_consecutiveDucks + "/3 ducks", gameTimeMilliseconds, super.GetSpriteManager());
			}
		}
		else if(m_state == TRAINING_STATE.JUMP_TEST) {
			if(m_consecutiveJumps != m_previousJumps) {
				m_previousJumps = m_consecutiveJumps;
				
				m_objectives.UpdateText("Objective: " + m_consecutiveJumps + "/3 jumps", gameTimeMilliseconds, super.GetSpriteManager());
			}
		}	
		else if(m_state == TRAINING_STATE.PIE_TEST) {
			if(m_consecutivePies != m_previousPies) {
				m_previousPies = m_consecutivePies;

				m_objectives.UpdateText("Objective: " + m_consecutivePies + "/3 pies avoided", gameTimeMilliseconds, super.GetSpriteManager());
			}
		}		
		else if(m_state == TRAINING_STATE.BOWLING_BALL_TEST) {
			if(m_consecutiveBowlingBalls != m_previousBowlingBalls) {
				m_previousBowlingBalls = m_consecutiveBowlingBalls;
				
				m_objectives.UpdateText("Objective: " + m_consecutiveBowlingBalls + "/3 bowling balls avoided", gameTimeMilliseconds, super.GetSpriteManager());
			}			
		}
		else if(m_state == TRAINING_STATE.CHICKENATOR_HIGH_TEST || m_state == TRAINING_STATE.CHICKENATOR_LOW_TEST) {
			if(m_consecutiveChickenators != m_previousChickenators) {
				m_previousChickenators = m_consecutiveChickenators;
				
				m_objectives.UpdateText("Objective: " + m_consecutiveChickenators + "/3 Chickenators avoided", gameTimeMilliseconds, super.GetSpriteManager());
			}			
		}		
		else if(m_state == TRAINING_STATE.RANDOM_TEST) {
			if(m_consecutiveDodges != m_previousDodges) {
				m_previousDodges = m_consecutiveDodges;
				
				m_objectives.UpdateText("Objective: " + m_consecutiveDodges + "/10 successful consecutive dodges", gameTimeMilliseconds, super.GetSpriteManager());
			}			
		}		
		else if(m_state == TRAINING_STATE.RANDOM_NONSTOP) {
			if(m_consecutiveDodges != m_previousDodges) {
				m_previousDodges = m_consecutiveDodges;
				
				m_objectives.UpdateText(m_consecutiveDodges + " successful consecutive dodges", gameTimeMilliseconds, super.GetSpriteManager());
			}			
		}		
	}
	
	private void ClearObjectiveCounts() {
		m_consecutiveDucks = 0;
		m_consecutiveJumps = 0;		
		m_consecutivePies = 0;
		m_consecutiveBowlingBalls = 0;
		m_consecutiveChickenators = 0;	
		m_consecutiveDodges = 0;				
		m_previousDucks = -1;
		m_previousJumps = -1;
		m_previousPies = -1;
		m_previousBowlingBalls = -1;
		m_previousChickenators = -1;
		m_previousDodges = -1;		
	}
	
	private void Attack() {
        float gameTimeMilliseconds = super.GetGameTimer().GetCurrentMilliseconds();

		if(CanAttack(gameTimeMilliseconds)) {
			if(m_state == TRAINING_STATE.PIE_TEST) {
				ThrowPie(gameTimeMilliseconds);
			} else if (m_state == TRAINING_STATE.BOWLING_BALL_TEST) {
				ThrowBowlingBall(gameTimeMilliseconds);
			} else if (m_state == TRAINING_STATE.CHICKENATOR_HIGH_TEST) {
				ThrowChickenatorHigh(gameTimeMilliseconds);
			} else if (m_state == TRAINING_STATE.CHICKENATOR_LOW_TEST) {
				ThrowChickenatorLow(gameTimeMilliseconds);
			} else if (m_state == TRAINING_STATE.RANDOM_TEST || m_state == TRAINING_STATE.RANDOM_NONSTOP) {
				ThrowRandom(gameTimeMilliseconds);
			}
		}
	}

	private boolean CanAttack(float _gameTimeMilliseconds) {
		float checkInterval = _gameTimeMilliseconds - m_lastAttackCheckTime;
		boolean result = false;
		
		// Can only attack if the interval time is large enough and there are no other weapons in play.
		if(checkInterval >= RANDOM_ATTACK_CHECK_INTERVAL && m_weaponsInPlay == 0 && m_bumble.GetState() != BUMBLE_STATE.FALLING_BOWLING_BALL && m_bumble.GetState() != BUMBLE_STATE.FALLING_CHICKENATOR_HIGH && m_bumble.GetState() != BUMBLE_STATE.FALLING_CHICKENATOR_LOW && m_bumble.GetState() != BUMBLE_STATE.FALLING_PIE) {
			// Can only attack if we're in the right state for it.
			if(m_state == TRAINING_STATE.PIE_TEST || m_state == TRAINING_STATE.BOWLING_BALL_TEST || m_state == TRAINING_STATE.CHICKENATOR_HIGH_TEST || m_state == TRAINING_STATE.CHICKENATOR_LOW_TEST || m_state == TRAINING_STATE.RANDOM_TEST || m_state == TRAINING_STATE.RANDOM_NONSTOP) {
				m_consecutiveChecks++;
				if(m_consecutiveChecks >= MAX_ATTACK_CHECKS) {
					// Have to attack since we've waited too long.
					result = true;
					m_consecutiveChecks = 0;				
				} else {
					// Attack is based on chance.				
					double pickedNumber = Math.random();
					if(pickedNumber <= RANDOM_ATTACK_CHANCE) {
						result = true;
						m_consecutiveChecks = 0;
					}
				}
			}
		
			m_lastAttackCheckTime = _gameTimeMilliseconds;
		}				
		
		return result;
	}
			
	// Handle all updates to the sprite objects (movement and collision detection generally).	
	@Override
	public void UpdateObjects() {
		Attack();
		CollisionDetection();
		MoveSprites();
		UpdateObjectives();
		CheckState();
		
		// We have to do this last, otherwise sprites will always be one frame behind their collision detection/movements.
		super.UpdateObjects();
	}	
	
	private void MoveSprites() {
		float updateVariance = super.GetGameTimer().GetDelta();
        float gameTimeMilliseconds = super.GetGameTimer().GetCurrentMilliseconds();
		List<String> weaponsToRemove = new ArrayList<String>();		

		m_sprites = super.GetAllSprites();
		int size = m_sprites.length;
		
		for (int i = 0; i < size; i++) {
			m_tempSprite = m_sprites[i];

			if(m_tempSprite != null) {
				if (m_tempSprite instanceof Weapon) {
					Weapon currentWeapon = (Weapon) m_tempSprite;
	
					if (m_tempSprite instanceof Chickenator) {
						Chickenator chickenator = (Chickenator) m_tempSprite;
						chickenator.Think(super.GetGameTimer(), m_bumble);
					}
					currentWeapon.Move(gameTimeMilliseconds, (currentWeapon.GetVelocity() * (currentWeapon.GetDirection() == DIRECTION.LEFT ? -1 : 1) * (updateVariance / 1000.0f)), 0f);
	
					// Any weapon that's already past Bumble and is off screen
					// can be removed to save memory and cycles.
					if (currentWeapon.GetX() < super.GetDisplay().GetAspectRatioX() * -1) {
						m_consecutiveDodges++;
						m_weaponsInPlay--;
						if(currentWeapon instanceof Pie) {
							m_consecutivePies++;
							m_consecutiveBowlingBalls = 0;
							m_consecutiveChickenators = 0;						
						} else if (currentWeapon instanceof BowlingBall) {
							m_consecutivePies = 0;
							m_consecutiveBowlingBalls++;
							m_consecutiveChickenators = 0;						
						} else if (currentWeapon instanceof Chickenator) {
							m_consecutivePies = 0;
							m_consecutiveBowlingBalls = 0;
							if(currentWeapon.IsInPlay()) {
								m_consecutiveChickenators++;	
							}
						}
						weaponsToRemove.add(currentWeapon.GetTag());
					}				
				} // Check for weapon.
				else if (m_tempSprite instanceof Feather) {
					m_tempFeather = (Feather)m_tempSprite;
					
					if(m_tempFeather.GetY() < super.GetDisplay().GetAspectRatioY() * -1) {
						m_featherToRemove = m_tempFeather;
					} else {
						m_tempFeather.Update(gameTimeMilliseconds, 0, 0);
					}
				}
			} // End of sprite null check.
		} // End of all sprite loop.
		
		// Remove any sprites that have gone offscreen.
		if(weaponsToRemove.size() > 0) {
			for(String key : weaponsToRemove) {
				super.RemoveSprite(key);
			}						
		}		
		
		if(m_featherToRemove != null) {
			super.RemoveSprite(m_featherToRemove.GetTag());
		}
	}	
	
	private void ThrowRandom(float _currentTimeMilliseconds) {
		int weapon = 1 + (int)(Math.random() * ((4 - 1) + 1));
		
		switch(weapon) {
			case 1:
				ThrowPie(_currentTimeMilliseconds);
				break;
			case 2:
				ThrowBowlingBall(_currentTimeMilliseconds);
				break;
			case 3:
				ThrowChickenatorHigh(_currentTimeMilliseconds);
				break;
			case 4:
				ThrowChickenatorLow(_currentTimeMilliseconds);
				break;
		}
	}
	
	private void ThrowPie(float _currentTimeMilliseconds) {
		m_weaponsInPlay++;
		m_thrower.Throw(_currentTimeMilliseconds, WEAPON_TYPE.PIE);		
	}
	
	private void ThrowBowlingBall(float _currentTimeMilliseconds) {
		m_weaponsInPlay++;
		m_thrower.Bowl(_currentTimeMilliseconds, WEAPON_TYPE.BOWLING_BALL);		
	}
	
	private void ThrowChickenatorHigh(float _currentTimeMilliseconds) {
		m_weaponsInPlay++;
		m_thrower.Throw(_currentTimeMilliseconds, WEAPON_TYPE.CHICKENATOR_HIGH);
	}
	
	private void ThrowChickenatorLow(float _currentTimeMilliseconds) {
		m_weaponsInPlay++;
		m_thrower.Bowl(_currentTimeMilliseconds, WEAPON_TYPE.CHICKENATOR_LOW);
	}

	@Override
	public void HandleBumbleDuckBegin(float _currentTimeMilliseconds) {
		m_treadmill.Stop(_currentTimeMilliseconds);	
	}

	@Override
	public void HandleBumbleDuckEnd(float _currentTimeMilliseconds) {
		m_treadmill.Start(_currentTimeMilliseconds);
		m_consecutiveDucks++;
		m_consecutiveJumps = 0;
	}

	@Override
	public void HandleThrowFinished(float _currentTimeMilliseconds, WEAPON_TYPE _weaponType) {
		m_weaponCount++;		
				
		// Create the thrown object and fire it off towards Bumble.
		if(_weaponType == WEAPON_TYPE.PIE) {			
			Pie pie = super.CreatePie(m_thrower.GetX(), m_thrower.GetY() - (Pie.GetPieHeight() / 3.0f),
									GetVelocity(WEAPON_TYPE.PIE), DIRECTION.LEFT, 									
									"PIE_" + String.valueOf(m_weaponCount));
			super.AddSprite(pie);		
		}
		
		if(_weaponType == WEAPON_TYPE.CHICKENATOR_HIGH) {
			float chickenatorY = m_thrower.GetY();
			float chickenatorTargetY = m_thrower.GetY() - m_thrower.GetHeight() + Chickenator.GetChickenatorHeight();
			
			Chickenator chickenator = super.CreateChickenator(m_thrower.GetX(), chickenatorY, 
													 GetVelocity(WEAPON_TYPE.CHICKENATOR_HIGH), DIRECTION.LEFT, 
													 CHICKENATOR_POSITION.HIGH,
													 chickenatorTargetY,
													 1.0f, 1, this, // Activity proximation, floor level 
													 "CHICKENATOR_" + String.valueOf(m_weaponCount));
			super.AddSprite(chickenator);			
		}
	}

	@Override
	public void HandleBowlFinished(float _currentTimeMilliseconds, WEAPON_TYPE _weaponType) {
		m_weaponCount++;
		
		if(_weaponType == WEAPON_TYPE.BOWLING_BALL){
			float ballY = m_thrower.GetY() - m_thrower.GetHeight() + (BowlingBall.GetBowlingBallHeight() * 1.1f);
			float ballX = m_thrower.GetX(); 

			BowlingBall ball = super.CreateBowlingBall(ballX, ballY, 
									 GetVelocity(WEAPON_TYPE.BOWLING_BALL), DIRECTION.LEFT, 
									 "BOWLING_BALL_" + String.valueOf(m_weaponCount));
			super.AddSprite(ball);			
		}
		
		if(_weaponType == WEAPON_TYPE.CHICKENATOR_LOW) {
			float chickenatorY = m_thrower.GetY() - m_thrower.GetHeight() + Chickenator.GetChickenatorHeight();
			float chickenatorTargetY = m_thrower.GetY();
			
			Chickenator chickenator = super.CreateChickenator(m_thrower.GetX(), chickenatorY, 
					 GetVelocity(WEAPON_TYPE.CHICKENATOR_LOW), DIRECTION.LEFT, 
					 CHICKENATOR_POSITION.LOW,
					 chickenatorTargetY,
					 1.0f, 1, this, // Activity proximation, floor level 
					 "CHICKENATOR_" + String.valueOf(m_weaponCount));
			
			super.AddSprite(chickenator);					
		}		
	}

	private float GetVelocity(WEAPON_TYPE _type) {
		float result = WEAPON_VELOCITY;
		
		if(m_randomVelocity) {
			// Between 1.5 and 3.0
			result = (float) (Math.random() * (3.5f - 1.5f) + 1.5f);
			if(_type == WEAPON_TYPE.CHICKENATOR_HIGH || _type == WEAPON_TYPE.CHICKENATOR_LOW && result > 2.0f) {
				result = 2.0f;	// have to cap chickenator to be fair.
			}
		}
		
		return result;
	}
	
	private void CollisionDetection() {
		boolean alreadyHit = false;
        float gameTimeMilliseconds = super.GetGameTimer().GetCurrentMilliseconds();

		List<String> spritesToRemove = new ArrayList<String>();
		
		m_sprites = super.GetAllSprites();
		int size = m_sprites.length;
		
		for (int i = 0; i < size; i++) { 
			m_tempSprite = m_sprites[i];
			
			if(m_tempSprite != null) {
				if (m_tempSprite instanceof Weapon && !alreadyHit) {
					Weapon weapon = (Weapon) m_tempSprite;
		
					if (weapon.IsOnScreen() && weapon.IsInPlay()) {
						// PIE Hit Detection
						if (weapon.GetWeaponType() == CRIMINAL_WEAPON.PIE) {
							if (m_bumble.CanBeHit(gameTimeMilliseconds) && m_bumble.IntersectsWith(weapon)) {
								if (m_bumble.GetState() == BUMBLE_STATE.DUCKING) {
									// Successful duck
								} else {
									// He's been hit!
									m_consecutiveDodges = 0;
									m_bumble.Fall(weapon.GetWeaponType(), gameTimeMilliseconds);
									alreadyHit = true;
									spritesToRemove.add(m_tempSprite.GetTag());			
									break;
								}
							}
						}
		
						// Chickenator Hit Detection
						if (weapon.GetWeaponType() == CRIMINAL_WEAPON.CHICKENATOR_HIGH || weapon.GetWeaponType() == CRIMINAL_WEAPON.CHICKENATOR_LOW) {
							if (m_bumble.CanBeHit(gameTimeMilliseconds) && m_bumble.IntersectsWith(weapon)) {
								boolean successful = false;
		
								if (weapon.GetY() >= m_bumble.GetY() - (m_bumble.GetHeight() / 3)) {
									// Chickenator is above bumble, so he needs to duck.
									if (m_bumble.GetState() == BUMBLE_STATE.DUCKING) {
										successful = true;
									}
								} else if (weapon.GetY() - weapon.GetHeight() < (m_bumble.GetY() - m_bumble.GetHeight()) + (m_bumble.GetHeight() / 3)) {
									// Chickenator is below bumble, so he needs to jump.
									if (m_bumble.GetState() == BUMBLE_STATE.JUMPING) {
										successful = true;
									}
								}
		
								if (!successful) {
									// He's been hit!
									m_tempChickenator = (Chickenator)weapon;
                                    m_bumble.Fall(weapon.GetWeaponType(), gameTimeMilliseconds);
                                    m_tempChickenator.TakeOutOfPlay();
                                    m_tempChickenator.BeginAttack(gameTimeMilliseconds);
									m_consecutiveDodges = 0;
								    alreadyHit = true;
                                    break;
                                }
							}
						} // End of Chickenator hit detection.
		
						// Bowling Ball Hit Detection
						if (weapon.GetWeaponType() == CRIMINAL_WEAPON.BOWLING_BALL) {
							if (m_bumble.CanBeHit(gameTimeMilliseconds) && m_bumble.IntersectsWith(weapon)) {
								if (m_bumble.GetState() == BUMBLE_STATE.JUMPING) {
									// Successful jump
								} else {
									// He's been hit!
									m_consecutiveDodges = 0;
									m_bumble.Fall(weapon.GetWeaponType(), gameTimeMilliseconds);
									alreadyHit = true;
									spritesToRemove.add(m_tempSprite.GetTag());
									break;
								}
							}
						}
			
					} // On Screen If
				} // Weapon If
			} // Sprite null check.	
		} // All sprites loop
		
		// Cleanup Sprites
		if(spritesToRemove.size() > 0) {
			for (String tag : spritesToRemove) {
				super.RemoveSprite(tag);
				m_weaponsInPlay--;
			}						
		}		
		
	}
	
	@Override
	public void HandleBumbleJumpBegin(float _currentTimeMilliseconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void HandleBumbleJumpEnd(float _currentTimeMilliseconds) {
		m_consecutiveDucks = 0;
		m_consecutiveJumps++;
	}

	@Override
	public void HandleBumbleDead(float _currentTimeMilliseconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void HandleBumbleBackUp(float _currentTimeMilliseconds) {
		// Any attacking chickenators need to fly away.
		m_sprites = super.GetAllSprites();
		int size = m_sprites.length;
		
		for (int i = 0; i < size; i++) { 
			m_tempSprite = m_sprites[i];

			if(m_tempSprite != null) {
				if (m_tempSprite instanceof Chickenator) {
					Chickenator chickenator = (Chickenator)m_tempSprite;
					if(chickenator.IsAttacking()) {
						chickenator.EndAttack(_currentTimeMilliseconds);
					}
				}
			}
		}		
	}

	@Override
	public void ThrowFeather(Chickenator _chickenator, int _featherCount,
			float _currentTimeMilliseconds) {
		Feather feather = new Feather(_chickenator, 5, false, _currentTimeMilliseconds,
		 		super.GetDisplay(), super.GetSpriteSheetManager(),
		 		"FEATHER1", _chickenator.GetTag() + "_FEATHER_" + String.valueOf(_featherCount));
		super.AddSprite(feather);		
	}

}
