package com.example.officerbumble.scene;

import java.util.Random;

import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.interfaces.BumbleListener;
import com.example.officerbumble.interfaces.ButtonListener;
import com.example.officerbumble.interfaces.ChickenatorListener;
import com.example.officerbumble.interfaces.CriminalListener;
import android.content.Context;
import com.example.officerbumble.engine.BadgeManager;
import com.example.officerbumble.engine.BadgeManager.Badge;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameSharedPreferences;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.GameStateManager.DIFFICULTY;
import com.example.officerbumble.engine.LeaderboardServices;
import com.example.officerbumble.engine.SoundManager;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.Sprite.DIRECTION;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.gameentities.*;
import com.example.officerbumble.gameentities.Bumble.BUMBLE_STATE;
import com.example.officerbumble.gameentities.Chickenator.CHICKENATOR_POSITION;
import com.example.officerbumble.gameentities.Criminal.CRIMINAL_STATE;
import com.example.officerbumble.gameentities.Criminal.CRIMINAL_WEAPON;
import com.example.officerbumble.gameentities.Popup.POPUP_TYPE;
import com.example.officerbumble.R;
import com.sromku.simple.fb.SimpleFacebook;

public class Game extends Scene implements BumbleListener, CriminalListener, ButtonListener, ChickenatorListener {

    // Game Constants
	private final float CHICKENATOR_ACTIVATION_PROXIMITY = 1.9f;
	private final static int BASE_LEVEL_CLEARED_SCORE = 1000;
	
	// Special game related sprites.
	private Bumble m_bumble = null;
	private Criminal m_criminal = null;
	private Toast m_toast;
	private GameHUD m_HUD;	
	private Popup m_pausePopup;
	private BadgeManager m_badgeManager;
	
	// In game variables
	private int m_weaponCount = 0;
    private long m_frames = 0;

	Random rand = new Random();
	private boolean m_levelLost = false;
    private boolean m_levelLostHandled = false;
    private boolean m_showToast = false;
    private boolean m_gameStarted = false;

	// Performance Related.
	private Sprite[] m_sprites;
	private Sprite m_tempSprite;
	private Weapon m_tempWeapon;
	private Sprite m_weaponToRemove;
	private Chickenator m_tempChickenator;
	private SimpleFacebook m_facebook;
	private Feather m_tempFeather;
	private Feather m_featherToRemove;
	private Context m_context;
	
	public Game(DeviceDisplay _display, SpriteSheetManager _spriteSheetMananger, Timer _realTimer, Timer _gameTimer, BadgeManager _badgeManager, SimpleFacebook _facebook, Context _context) {
		super(_display, _spriteSheetMananger, _realTimer, _gameTimer, _context);		

		m_badgeManager = _badgeManager;
		m_facebook = _facebook;
		m_context = _context;
	}
	
    @Override
    public void Initialize(SceneConfig _config, DifficultyManager _difficultyManager, GameStateManager _gameStateManager, AD_TYPE _adVisibility, Object _inputListener) {
            super.Initialize(_config, _difficultyManager, _gameStateManager, _adVisibility, _inputListener);

		m_bumble = (Bumble) super.GetSprite("BUMBLE");
		m_criminal = (Criminal) super.GetSprite("CRIMINAL");
		m_criminal.RegisterCriminalListener(this);
		m_bumble.RegisterBumbleListener(this);

        float gameMilliseconds = super.GetGameTimer().GetCurrentMilliseconds();

		// Create the HUD, now that we know the scene dimensions.
		m_HUD = new GameHUD(super.GetSceneStart(), super.GetSceneEnd(), gameMilliseconds,
							super.GetDisplay(), super.GetSpriteSheetManager(), 
							super.GetDifficultyConfig(), this);

        // Setup the game's toast.
        m_toast = new Toast(super.GetGameStateManager(), super.GetRealTimer().GetCurrentMilliseconds(), super.GetDisplay(), super.GetSpriteSheetManager(), super.GetSpriteManager());

        SoundManager.PlayMusic(m_context, R.raw.heist);
	}
		
	@Override
	public void UpdateObjects()
    {
        if ( m_showToast ) {
            if ( super.GetGameStateManager().ShowTutorial() ) {
                m_toast.DisplayText("LEVEL " + super.GetGameStateManager().GetLevel(), super.GetRealTimer().GetCurrentMilliseconds(), true, 3000);
                PauseFor(3000);
            } else {
                m_toast.DisplayText("LEVEL " + super.GetGameStateManager().GetLevel(), super.GetRealTimer().GetCurrentMilliseconds(), 3000);
            }
            m_showToast = false;
        }

        if (!super.IsPaused() && m_gameStarted) {
            CollisionDetection();
            MoveSprites();
        }

        if ( m_gameStarted ) {
            // Refresh sprites that know how to refresh themselves.
            m_toast.RefreshSprites(super.GetRealTimer().GetCurrentMilliseconds());
            m_HUD.UpdateHUD(m_bumble, m_criminal, super.GetSpriteManager(), super.GetGameStateManager(), super.GetGameTimer().GetCurrentMilliseconds());
            super.UpdateObjects();
        }

        m_frames++;
        if ( m_frames >= 1 && !m_gameStarted ) {
            m_gameStarted = true;
        }
	}

    public void StartGame() {
        m_showToast = true;
        m_bumble.Initialize(super.GetGameTimer());
    }

	/*
	 * This function is responsible for handling all game object movements,
	 * keeping in mind that Bumble doesn't actually move anywhere, the camera
	 * moves the game world around him. Because of this, it's necessary to have
	 * special logic that takes into account this camera offset when moving
	 * other objects around the game world.
	 * 
	 * 1. First determine how much the camera should scroll (it will scroll in
	 *    the opposite direction Bumble is moving). 
	 * 
	 * 2. Determine how far the criminal *should* move and then subtract that amount from the camera
	 *    scroll to get the final movement amount. 
	 * 
	 * 3. Determine how far each weapon should have moved and then subtract that amount from the camera scroll to
	 *    get the final movement amount. 
	 * 
	 * 4. Move the background images by the
	 *    camera offset. 
	 * 
	 * 5. Remove any weapons that are offscreen to save memory.
	 */
	private void MoveSprites() {
		float cameraScrollX = 0.0f;
		float cameraScrollY = 0.0f;

		float updateVariance = super.GetGameTimer().GetDelta();
        float gameMilliseconds = super.GetGameTimer().GetCurrentMilliseconds();

		m_weaponToRemove = null;
		m_featherToRemove = null;
		
		// If bumble ends the escalator and changes directions, we need to pan
		// the camera so he's in the right area.

		// Handle Bumble moving on an escalator.
		if (m_bumble.GetState() == BUMBLE_STATE.RIDING_ESCALATOR) {
			if (!m_bumble.IsInWaypoint()) {
				m_bumble.WaypointEnd(gameMilliseconds);
				m_bumble.ChangeDirection();
				m_bumble.Run(gameMilliseconds);
				m_bumble.IncrementFloorLevel();
			} else {
				cameraScrollX = m_bumble.GetNextWaypointX(gameMilliseconds) * -1;
				float moveBy = (cameraScrollX * -1);
				cameraScrollX = 0.0f;				
				m_bumble.Move(gameMilliseconds, moveBy, 0);
				m_bumble.UpdateDistanceTravelled(Math.abs(cameraScrollX));
				cameraScrollY = m_bumble.GetNextWaypointY(gameMilliseconds) * -1;
				m_bumble.AdjustMapCoordinates(moveBy, cameraScrollY * -1);
			}
		}

		// Didn't use an else because the state could change within the above
		// code block.
		if (m_bumble.GetState() != BUMBLE_STATE.RIDING_ESCALATOR) {
			m_bumble.AdjustVelocity(gameMilliseconds);
			cameraScrollX = (m_bumble.GetVelocity() * (updateVariance / 1000.0f)) * -1.0f;
			cameraScrollY = 0.0f;
			m_bumble.UpdateDistanceTravelled(Math.abs(cameraScrollX));
			m_bumble.AdjustMapCoordinates(cameraScrollX * -1, cameraScrollY * -1);
		}

		//m_bumble.Update(_currentTimeMilliseconds);

		// 1. Handle criminal moving up an escalator.
		if (m_criminal.GetState() == CRIMINAL_STATE.RIDING_ESCALATOR) {
			if (!m_criminal.IsInWaypoint()) {
				m_criminal.WaypointEnd(gameMilliseconds);
				m_criminal.ChangeDirection();
				m_criminal.Run(gameMilliseconds);
				m_criminal.IncrementFloorLevel();
			} else {
				float nextWaypointX = m_criminal.GetNextWaypointX(gameMilliseconds);
				float nextWaypointY = m_criminal.GetNextWaypointY(gameMilliseconds);

				float moveX = nextWaypointX + cameraScrollX;
				float moveY = nextWaypointY + cameraScrollY;

				// X movement = currentX - lastX + cameraScrollX
				m_criminal.Move(gameMilliseconds, moveX, moveY);
				m_criminal.UpdateDistanceTravelled(Math.abs(nextWaypointX));
				m_criminal.AdjustMapCoordinates(nextWaypointX, nextWaypointY);
			}
		}

		// 2. Move weapons and backgrounds.
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
						// As Bumble changes levels, it's necessary to adjust that vertical axis target, otherwise Chickenator might
						// fly off screen.
						chickenator.AdjustTarget(cameraScrollY);
					}
					currentWeapon.Move(gameMilliseconds, (currentWeapon.GetVelocity() * (currentWeapon.GetDirection() == DIRECTION.LEFT ? -1 : 1) * (updateVariance / 1000.0f)) + cameraScrollX, cameraScrollY);
					//currentWeapon.Update(_currentTimeMilliseconds);
	
					// Any weapon that's already past Bumble and is off screen
					// can be removed to save memory and cycles.
					// Have to do some buffering on the weapon removal area for RIGHT because if we remove it too soon it wont' detect that it's behind Bumble and it won't score.
					if (m_bumble.GetDirection() == DIRECTION.RIGHT && m_criminal.GetDirection() == DIRECTION.RIGHT && currentWeapon.GetX() + (currentWeapon.GetWidth() * 2) < super.GetDisplay().GetAspectRatioX() * -1) {
						m_weaponToRemove = currentWeapon;						
					}
					
					if (m_bumble.GetDirection() == DIRECTION.LEFT && m_criminal.GetDirection() == DIRECTION.LEFT && currentWeapon.GetX() > super.GetDisplay().GetAspectRatioX()) {
						m_weaponToRemove = currentWeapon;
					}
	
				} else if (m_tempSprite instanceof StaticImage || m_tempSprite instanceof Escalator || m_tempSprite instanceof Exit || m_tempSprite instanceof Button) {
					if (!m_tempSprite.m_isStationary) {
						m_tempSprite.Move(gameMilliseconds, cameraScrollX, cameraScrollY);
					}
					//sprite.Update(_currentTimeMilliseconds);
				} else if (m_tempSprite instanceof Feather) {
					m_tempFeather = (Feather)m_tempSprite;
					
					if(m_tempFeather.GetY() < super.GetDisplay().GetAspectRatioY() * -1) {
						m_featherToRemove = m_tempFeather;
					} else {
						m_tempFeather.Update(gameMilliseconds, cameraScrollX, cameraScrollY);
					}
				}
			} // End of null check.
		} // End of all sprite loop.

		// 3. If the criminal isn't riding an escalator, then
		if (m_criminal.GetState() != CRIMINAL_STATE.RIDING_ESCALATOR) {
			float criminalMoveX = (m_criminal.GetVelocity() * (updateVariance / 1000.0f));
			float criminalVelocityX = criminalMoveX + cameraScrollX;
			m_criminal.Move(gameMilliseconds, criminalVelocityX, cameraScrollY);
			m_criminal.UpdateDistanceTravelled(Math.abs(criminalMoveX));
			m_criminal.AdjustMapCoordinates(criminalMoveX, 0.0f);
		}

		// Remove off-screen weapons.
		if (m_weaponToRemove != null) {			
			super.RemoveSprite(m_weaponToRemove.GetTag());			

			UpdateMinimumWeaponVelocity();
		}
		
		if(m_featherToRemove != null) {
			super.RemoveSprite(m_featherToRemove.GetTag());
		}

		// So that the criminal is aware of Officer Bumble.
		m_criminal.SetBumbleKnowledge(m_bumble);
	}

	private void CollisionDetection() {
		boolean alreadyHit = false;	
		float gameMilliseconds = super.GetGameTimer().GetCurrentMilliseconds();

		// First check to see if we caught the criminal, if so, we win!
		if (m_bumble.GetFloorLevel() == m_criminal.GetFloorLevel() && m_bumble.IntersectsWith(m_criminal)) {
			if(m_bumble.GetState() != BUMBLE_STATE.CAUGHT_CRIMINAL && m_bumble.GetState() != BUMBLE_STATE.RIDING_ESCALATOR && m_criminal.GetState() != CRIMINAL_STATE.RIDING_ESCALATOR) {
                m_bumble.CaughtCriminal(gameMilliseconds);
                m_criminal.Caught(gameMilliseconds);

                super.HandleScoreIncrease(BASE_LEVEL_CLEARED_SCORE);
			}
		}

		if(m_bumble.GetState() != BUMBLE_STATE.CAUGHT_CRIMINAL) {
			m_sprites = super.GetAllSprites();
			int size = m_sprites.length;
			
			for (int i = 0; i < size; i++) { 
				m_tempSprite = m_sprites[i];
	
				if(m_tempSprite != null) {
					if (m_tempSprite instanceof Escalator) {
						Escalator escalator = (Escalator) m_tempSprite;
		
						if (escalator.IsOnScreen()) {
							if (!m_bumble.IsInWaypoint()) {
								if (m_bumble.IntersectsWith(escalator) && !escalator.UsedByBumble()) {
									m_bumble.WaypointStart((escalator.GetWidth()) * ((m_bumble.GetDirection() == DIRECTION.LEFT) ? -1 : 1), escalator.GetHeight(),
														   (m_bumble.GetDirection() == DIRECTION.LEFT) ? DIRECTION.LEFT : DIRECTION.RIGHT, escalator.GetEscalatorTraversalTime(), gameMilliseconds);
                                    escalator.SetUsedByBumble();
                                    m_bumble.RideEscalator(gameMilliseconds);
								}
							}
						}
		
						if (!m_criminal.IsInWaypoint()) {
							if (m_criminal.IntersectsWith(escalator) && !escalator.UsedByCriminal()) {
								m_criminal.WaypointStart((escalator.GetWidth()) * ((m_criminal.GetDirection() == DIRECTION.LEFT) ? -1 : 1), escalator.GetHeight(),
														 (m_criminal.GetDirection() == DIRECTION.LEFT) ? DIRECTION.LEFT : DIRECTION.RIGHT, escalator.GetEscalatorTraversalTime(), gameMilliseconds);
								escalator.SetUsedByCriminal();
                                m_criminal.RideEscalator(gameMilliseconds);
							}
						}
					} // End of escalator check.
		
					// Criminal exit hit detection
					if (m_tempSprite instanceof Exit) {
						if (m_criminal.IntersectsWith(m_tempSprite)) {
							if(!m_levelLost) {
								SoundManager.PlaySound("CRIMINAL ESCAPE", false);
								m_toast.DisplayText("HE GOT AWAY", super.GetRealTimer().GetCurrentMilliseconds(), 2500);
								super.PauseFor(2500);
								m_levelLost = true;
								break;
							} else {								
								LeaderboardServices.SaveLocally(m_context, super.GetGameStateManager(), GameSharedPreferences.ReadInteger(m_context, GameSharedPreferences.CRIMINALS_CAUGHT_PREFERENCE), GameSharedPreferences.ReadInteger(m_context, GameSharedPreferences.CRIMINALS_CAUGHT_HARDCORE_PREFERENCE));
                                if(!m_levelLostHandled) {
                                    super.HandleSceneLost(gameMilliseconds);
                                }
                                m_levelLostHandled = true;
								break;
							}									
						}
					} // End of Criminal hit detection.
		
					if (m_tempSprite instanceof Weapon && !alreadyHit) {
						
						Weapon weapon = (Weapon) m_tempSprite;
		
						if (weapon.IsOnScreen() && weapon.IsInPlay()) {
							// PIE Hit Detection
							if (weapon.GetWeaponType() == CRIMINAL_WEAPON.PIE) {
								if (m_bumble.CanBeHit(gameMilliseconds) && m_bumble.IntersectsWith(weapon)) {
									if (m_bumble.SuccessfullyDucking()) {
										// Successful duck										
									} else {
										// He's been hit!
										m_bumble.Fall(weapon.GetWeaponType(), gameMilliseconds);
										alreadyHit = true;
										m_weaponToRemove = m_tempSprite;										
										break;
									}
								}
							}
		
							// Chickenator Hit Detection
							if (weapon.GetWeaponType() == CRIMINAL_WEAPON.CHICKENATOR_HIGH || weapon.GetWeaponType() == CRIMINAL_WEAPON.CHICKENATOR_LOW) {
								if (m_bumble.CanBeHit(gameMilliseconds) && m_bumble.IntersectsWith(weapon)) {
									boolean successful = false;
		
									if (weapon.GetY() >= m_bumble.GetY() - (m_bumble.GetHeight() / 3)) {
										// Chickenator is above bumble, so he needs to duck.
										if (m_bumble.SuccessfullyDucking()) {
											successful = true;
										}
									} else if (weapon.GetY() - weapon.GetHeight() < (m_bumble.GetY() - m_bumble.GetHeight()) + (m_bumble.GetHeight() / 3)) {
										// Chickenator is below bumble, so he needs to jump.
										if (m_bumble.SuccessfullyJumping()) {
											successful = true;
										}
									}
		
									if (!successful) {
										// He's been hit!
										m_tempChickenator = (Chickenator)weapon;
										m_bumble.Fall(weapon.GetWeaponType(), gameMilliseconds);
										m_tempChickenator.TakeOutOfPlay();
										m_tempChickenator.BeginAttack(gameMilliseconds);
										
										alreadyHit = true;
										break;										
									}
								}
							} // End of Chickenator hit detection.
		
							// Bowling Ball Hit Detection
							if (weapon.GetWeaponType() == CRIMINAL_WEAPON.BOWLING_BALL) {
								if (m_bumble.CanBeHit(gameMilliseconds) && m_bumble.IntersectsWith(weapon)) {
									if (m_bumble.SuccessfullyJumping()) {
										// Successful jump											
									} else {
										// He's been hit!
										m_bumble.Fall(weapon.GetWeaponType(), gameMilliseconds);
										alreadyHit = true;
										m_weaponToRemove = m_tempSprite;
										break;
									}
								}
							}
		
							// If the weapon has been passed by Bumble, score some points.
							if (!weapon.IsScored() && m_bumble.IsSpriteBehind(weapon)) {
								weapon.SetIsScored();
								if (m_bumble.CanBeHit(gameMilliseconds)) {
									super.HandleScoreIncrease(50);							
								}
							}
		
						} // On Screen If
					} // Weapon If
				} // End of Sprite null check.									
			} // All sprites loop
			
			if(m_weaponToRemove != null) {
				super.RemoveSprite(m_weaponToRemove.GetTag());
				
				UpdateMinimumWeaponVelocity();
			}
		} // Criminal Caught If check
	}
	
	@Override
	public void Pause() {
		m_pausePopup = CreatePopup(POPUP_TYPE.PAUSE, 1.65f, 0.75f, true, "PAUSED", this);
		m_pausePopup.Open();
		
		super.Pause();
	}
	
	@Override
	public void Unpause() {
        for (int i = 0; i <= 5; i++) {
            m_pausePopup.Close();   // I hate myself for this loop.
        }
 		super.Unpause();
	}
	
	// Determines the highest velocity of any weapon that's still in play.  We cannot let a weapon velocity go faster than 
	// any weapons in front of it or we'll get into impossible situations where two weapons are stacked upon each other 
	// and thus unavoidable.
	private float GetMaximumWeaponVelocity() {
		float minVelocity = 99f;

		m_sprites = super.GetAllSprites();
		int size = m_sprites.length;
		
		for (int i = 0; i < size; i++) {
			m_tempSprite = m_sprites[i];
	
			if (m_tempSprite instanceof Weapon || m_tempSprite instanceof Chickenator) {
				m_tempWeapon = (Weapon) m_tempSprite;

				if (m_tempWeapon.GetVelocity() < minVelocity && m_tempWeapon.GetVelocity() != 0 && !m_tempWeapon.IsScored()) {
					minVelocity = m_tempWeapon.GetVelocity();
				}
			}
		}	
			
		return minVelocity;		// Max velocity for a new weapon is the minimum velocity of all weapons.
	}

	// Update the criminals knowledge of weapon velocity.
	private void UpdateMinimumWeaponVelocity() {
		float minimumVelocity = GetMaximumWeaponVelocity();
		m_criminal.SetMaximumWeaponVelocity(minimumVelocity);
	}

	@Override
	public void HandleCriminalAttack(CRIMINAL_WEAPON _weapon, float _velocity, float _currentTimeMilliseconds) {
		m_weaponCount++;

		if (_weapon == CRIMINAL_WEAPON.PIE) {
			float pieX = (m_criminal.GetDirection() == DIRECTION.RIGHT) ? m_criminal.GetX() - (m_criminal.GetWidth() * 0.10f) : m_criminal.GetX() + m_criminal.GetWidth() + (m_criminal.GetWidth() * 0.10f);
			
			Pie pie = super.CreatePie(pieX, 
							  m_criminal.GetY(),
						      _velocity, 
						      (m_criminal.GetDirection() == DIRECTION.RIGHT ? DIRECTION.LEFT : DIRECTION.RIGHT), 								
							  "PIE_" + String.valueOf(m_weaponCount));
			super.AddSprite(pie);			
		} else if (_weapon == CRIMINAL_WEAPON.CHICKENATOR_HIGH || _weapon == CRIMINAL_WEAPON.CHICKENATOR_LOW) {
			float chickenatorY;
			float chickenatorTargetY;
			
			CHICKENATOR_POSITION position = (_weapon == CRIMINAL_WEAPON.CHICKENATOR_HIGH) ? CHICKENATOR_POSITION.HIGH : CHICKENATOR_POSITION.LOW;
			if (position == CHICKENATOR_POSITION.LOW) {
				chickenatorY = m_criminal.GetY() - m_criminal.GetHeight() + Chickenator.GetChickenatorHeight();
				chickenatorTargetY = m_criminal.GetY();
			} else {
				chickenatorY = m_criminal.GetY();
				chickenatorTargetY = m_criminal.GetY() - m_criminal.GetHeight() + Chickenator.GetChickenatorHeight();
			}
			float chickenatorX = (m_criminal.GetDirection() == DIRECTION.RIGHT) ? m_criminal.GetX() : m_criminal.GetX() + m_criminal.GetWidth();
			
			Chickenator chickenator = super.CreateChickenator(chickenatorX,
													 		  chickenatorY, 
													 		  _velocity, 
													 		  (m_criminal.GetDirection() == DIRECTION.RIGHT ? DIRECTION.LEFT : DIRECTION.RIGHT), 
													 		  position,
													 		  chickenatorTargetY,
													 		  CHICKENATOR_ACTIVATION_PROXIMITY,
													 		   m_criminal.GetFloorLevel(), 
													 		   this,
													 		  "CHICKENATOR_" + String.valueOf(m_weaponCount));			
			super.AddSprite(chickenator);
		} else if (_weapon == CRIMINAL_WEAPON.BOWLING_BALL) {
			float ballY = m_criminal.GetY() - m_criminal.GetHeight() + BowlingBall.GetBowlingBallHeight();
			float ballX = (m_criminal.GetDirection() == DIRECTION.RIGHT) ? m_criminal.GetX() + (m_criminal.GetWidth() * 0.10f) : m_criminal.GetX() + m_criminal.GetWidth() - (m_criminal.GetWidth() * 0.10f) ; 
			
			BowlingBall ball = super.CreateBowlingBall(ballX, ballY, 
									 		   _velocity, 
									 		   (m_criminal.GetDirection() == DIRECTION.RIGHT ? DIRECTION.LEFT : DIRECTION.RIGHT), 
									 		   "BOWLING_BALL_" + String.valueOf(m_weaponCount));
			super.AddSprite(ball);
		}
		
		UpdateMinimumWeaponVelocity();
	}

	@Override
	public void HandleButtonPressed(String _tag, float currentTimeMilliseconds) {
		if (_tag.equals("BUTTON_RESUME")) {
			Unpause();
		} else if (_tag.equals("BUTTON_MAINMENU")) {
			Unpause();
			super.HandleQuitToTitle();
		} 
	}

	@Override
	public void HandleCriminalCaught(float _currentTimeMilliseconds) {

		// Handle the saving of leaderboards data.
		int criminalsCaught = GameSharedPreferences.ReadInteger(m_context, GameSharedPreferences.CRIMINALS_CAUGHT_PREFERENCE);
		criminalsCaught++;

		int criminalsCaughtHardcore = GameSharedPreferences.ReadInteger(m_context, GameSharedPreferences.CRIMINALS_CAUGHT_HARDCORE_PREFERENCE);
		if (super.GetGameStateManager().GetDifficulty() == DIFFICULTY.HARDCORE) { criminalsCaughtHardcore++; }
		
		LeaderboardServices.SaveLocally(m_context, super.GetGameStateManager(), criminalsCaught, criminalsCaughtHardcore);
		
		// Do we get a badge?		
		Badge badge = m_badgeManager.QueryBadge(criminalsCaught);
		Badge nextBadge = m_badgeManager.GetNextBadge(criminalsCaught + 1);
		long nextLife = super.GetDifficultyConfig().GetNextFreeLifeScore(super.GetGameStateManager().GetScore());
		
		int randomNum = rand.nextInt((10 - 1) + 1) + 1;

		if(badge != null) {
			super.HandleBadgeAwarded(badge, nextBadge);
		} else if (randomNum == 1 || (randomNum == 2 && !m_facebook.isLogin())) {
			super.HandleNextBadge(criminalsCaught, m_badgeManager.GetNextBadge(criminalsCaught));
		} else if (randomNum == 2 && m_facebook.isLogin()) {
		    // Facebook Invite Friends
			//super.HandleInviteFriends();
            super.HandleNextBadge(criminalsCaught, m_badgeManager.GetNextBadge(criminalsCaught));
		} else if (randomNum == 3 && !super.GetDifficultyConfig().GetDifficultyName().equals("HARDCORE")) {						
			super.HandleNextFreeMan(super.GetGameStateManager().GetScore(), nextLife);
		} else {
			super.HandleSceneWon(_currentTimeMilliseconds);	
		}				
	}

	@Override
	public void HandleBumbleDuckBegin(float _currentTimeMilliseconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void HandleBumbleDuckEnd(float _currentTimeMilliseconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void HandleBumbleJumpBegin(float _currentTimeMilliseconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void HandleBumbleJumpEnd(float _currentTimeMilliseconds) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void HandleBumbleDead(float _currentTimeMilliseconds) {
		// You lost the level because you got hit in hardcore.
		LeaderboardServices.SaveLocally(m_context, super.GetGameStateManager(), GameSharedPreferences.ReadInteger(m_context, GameSharedPreferences.CRIMINALS_CAUGHT_PREFERENCE), GameSharedPreferences.ReadInteger(m_context, GameSharedPreferences.CRIMINALS_CAUGHT_HARDCORE_PREFERENCE));
		super.HandleSceneLost(_currentTimeMilliseconds);		
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
					m_tempChickenator = (Chickenator)m_tempSprite;
					if(m_tempChickenator.IsAttacking()) {
						m_tempChickenator.EndAttack(_currentTimeMilliseconds);
					}
				}
			}
		}
	}

	@Override
	public void ThrowFeather(Chickenator _chickenator, int _featherCount, float _currentTimeMilliseconds) {
		Feather feather = new Feather(_chickenator, 4, false, _currentTimeMilliseconds,
		 		super.GetDisplay(), super.GetSpriteSheetManager(),
		 		"FEATHER1", _chickenator.GetTag() + "_FEATHER_" + String.valueOf(_featherCount));
		super.AddSprite(feather);
	}			
}
