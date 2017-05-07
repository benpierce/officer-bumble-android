package com.example.officerbumble;

import android.content.Context;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.example.officerbumble.interfaces.SceneListener;
import com.example.officerbumble.interfaces.AdListener;
import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.engine.BadgeManager.Badge;
import com.example.officerbumble.engine.Color;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.GameSharedPreferences;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.LeaderboardServices;
import com.example.officerbumble.engine.ResourceManager;
import com.example.officerbumble.engine.SoundManager;
import com.example.officerbumble.engine.GameStateManager.DIFFICULTY;
import com.example.officerbumble.scene.*;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.scene.SceneManager.SCENE;
import com.sromku.simple.fb.SimpleFacebook;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

//Tomorrow:  Why is TextureHelper reloading after game restart?  We flushing the textures?
//           Work on getting memory usage and performance better.

public class GameRenderer implements Renderer, SceneListener {

	// Application context (NOT to be confused with the OpenGL context)
	private Context m_context;

	// Timer and frame rate variables.
	private static Timer m_gameTimer;              // Used for game elements, can be paused.
	private static Timer m_realTimer;              // Used for things that shouldn't be affected by pausing (such as popup screens).

	// Game manager objects
	private DeviceDisplay m_display;             // Display properties so that we can do things like adjust for the aspect ratio.
	private static GameStateManager m_gameStateManager; // All game state (IE: score, level, current screen, etc...) are managed here.
	private static SceneManager m_sceneManager;         // Loads all scene data from file and puts it all together.
	private SimpleFacebook m_simpleFacebook;     // Facebook integration.

    private float enterTime = 0f;
    private float exitTime = 0f;

	// Scene related variables
	private static Scene m_scene;  // The scene is basically a list of items to draw and their locations/states.

	// Loading state.
	private enum LOADING_STATE {
		BOOTING, LOADING, LOADED
	}

	private static LOADING_STATE m_loadingState = LOADING_STATE.BOOTING;
	private static AdListener m_adListener;

	// Constructor
	public GameRenderer(Context _context, SimpleFacebook _simpleFacebook, AdListener _adListener) {
		this.m_context = _context;

		m_adListener = _adListener;
		m_simpleFacebook = _simpleFacebook;
	}

	@Override
	// When the surface is first created, we want to load all of our game assets
	// into memory.
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		// Timers only need to be setup once.
        if ( m_gameTimer == null ) {
            m_gameTimer = new Timer();
        }

        if ( m_realTimer == null ) {
            m_realTimer = new Timer();
        }
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		m_display = new DeviceDisplay(width, height, Color.LIGHT_GREEN);
		
		// If the user had previously quit during loadup, we might be stuck in the loading phase. If this is
		// the case, we'll need to restart from the boot phase.
        if(m_loadingState == LOADING_STATE.LOADING) {
			m_loadingState = LOADING_STATE.BOOTING;
		}

		InitializeGame();
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
            if (m_sceneManager.QueuedSceneReadyToLoad()) {
                m_scene = m_sceneManager.StartQueuedScene();
            } else {
                m_gameTimer.IncrementCurrentMilliseconds();
                m_realTimer.IncrementCurrentMilliseconds();

                // Step through one frame.
                if (m_scene != null) {
                    m_scene.Step();
                }

                // Transition is BOOTING (splash screen start), LOADING (loading resources), and LOADED (game is in play).
                if (m_loadingState == LOADING_STATE.LOADING) {
                    ResourceManager.getInstance().LoadResources(m_context);

                    if (m_gameStateManager == null) {
                        m_gameStateManager = new GameStateManager(ResourceManager.getInstance().GetDifficultyManager());
                    }

                    m_sceneManager.LoadAll();
                    m_sceneManager.Initialize(ResourceManager.getInstance().GetDifficultyManager(), m_gameStateManager, ResourceManager.getInstance().GetBadgeManager());

                    // Once game loading is finished we can create the first scene.
                    GameLoadingFinished();
                    m_loadingState = LOADING_STATE.LOADED;
                } else {
                    m_loadingState = (m_loadingState == LOADING_STATE.BOOTING) ? LOADING_STATE.LOADING : LOADING_STATE.LOADED;
                }
            }

    }

    public void UpdateContext(Context _context) {
        m_context = _context;
    }

	// Initialize everything that we need for a basic startup (to display the
	// splash screen).
	private void InitializeGame() {
		ResourceManager.getInstance().LoadBasicResources(m_context);

        // We have to load the scene manager here because we won't have the
		// height and width in onSurfaceCreated and
		// we need that so the SceneManager can figure out layout specifics.
		if (m_sceneManager == null) {
			m_sceneManager = new SceneManager(m_context, m_display,
					                          ResourceManager.getInstance().GetSpriteSheetManager(),
					                          m_realTimer, m_gameTimer,
					                          m_simpleFacebook, this);
			m_sceneManager.LoadBasic();
		}

	    if(m_scene != null) {
            if(m_scene instanceof Game && m_loadingState == LOADING_STATE.LOADED) {
				m_scene.Pause();
			}

            // Why's this reloading textures on restart?  Should already be resident.
			m_scene.RefreshTextures();
            m_adListener.HandleAdVisibility(m_scene.GetAdVisibility());
		} else {
            //m_sceneManager.StartSplashScreen(); // Start the splash screen.
            m_sceneManager.QueueScene(SCENE.SPLASH_SCREEN, true);
		}
	}

	// Gets called when the game is finished loading and it's time to transition
	// from the splash screen to the main title screen.
	private void GameLoadingFinished() {
        int openCount = GameSharedPreferences.ReadInteger(m_context, GameSharedPreferences.OPEN_COUNT_PREFERENCE);

		// If this is the first time using the application, then prompt for
		// Facebook, then every 4 times so we don't annoy them.
		if ((openCount == 0 || openCount % 4 == 0) && !m_simpleFacebook.isLogin()) {
			m_sceneManager.QueueTitleScene(true);
		} else {
            m_sceneManager.QueueTitleScene(false);
		}

		GameSharedPreferences.WriteInteger(m_context, GameSharedPreferences.OPEN_COUNT_PREFERENCE, ++openCount);
	}

	public void handleTouchPress(boolean isPrimary, float normalizedX,
			float normalizedY) {
		// Notify the scene that the touch press event happened.
		m_scene.HandleTouch(isPrimary, normalizedX, normalizedY);
	}

	public void handleTouchRelease(boolean isPrimary, float normalizedX,
			float normalizedY) {
		m_scene.HandleTouchRelease(isPrimary, normalizedX, normalizedY);
	}

	public void handleTouchDrag(boolean isPrimary, float normalizedX,
			float normalizedY) {
		// Not used for this game.
	}

	@Override
	public void HandleSceneDifficultySelected(DIFFICULTY _difficulty, float _currentTimeMilliseconds) {
        m_scene.StartLoading();   // Show loading spinner on current scene.

        m_gameStateManager.StartNewGame(_difficulty, m_sceneManager);
	}

	@Override
	public void HandleRestartGame() {
		m_gameStateManager.RestartGame(m_sceneManager);
	}

	@Override
	public void HandleTrainingSelected() {
        m_scene.StartLoading();
        //m_sceneManager.QueueScene(SCENE.LEVEL_DESIGN, false);
        m_sceneManager.QueueScene(SCENE.TUTORIAL, false);
	}

	@Override
	public void HandleSceneLevelLost(float _currentTimeMilliseconds) {
		PostToLeaderboard();
        m_gameStateManager.LevelLost(m_sceneManager);
	}

	@Override
	public void HandleSceneLevelWon(float _currentTimeMilliseconds) {
		PostToLeaderboard();
        m_gameStateManager.LevelWon(m_sceneManager);
	}

	private void PostToLeaderboard() {
		Thread t1 = new Thread(new Runnable() {
		     public void run()
		     {
		    	 LeaderboardServices.PostScore(m_context);
		     }});  
		t1.start();		
	}
	
	@Override
	public void HandleSceneQuitGame() {
		System.exit(0);
	}

	@Override
	public void HandleSceneQuitToTitle() {
        m_sceneManager.QueueTitleScene(false);
	}

	@Override
	public void HandleScoreIncrease(long _incrementBy) {
		boolean freelife = m_gameStateManager.IncrementScore(_incrementBy);
		if (freelife) {
			SoundManager.PlaySound("FREE LIFE", false);
		}
	}

	@Override
	public void HandleBadgeAwarded(Badge _badge, Badge _nextBadge) {
        m_sceneManager.QueueBadgeAwardedScene(_badge, _nextBadge);
	}

	@Override
	public void HandleAdVisibility(AD_TYPE _adType) {
		Log.w("ADMOB", "HandleAdVisibility Called " + _adType.toString());
        m_adListener.HandleAdVisibility(_adType);
	}

	@Override
	public void HandleNextBadge(int _criminalsCaught, Badge _nextBadge) {
        m_sceneManager.QueueNextBadgeScene(_criminalsCaught, _nextBadge);
	}

	@Override
	public void HandleNextFreeMan(long _currentScore, long _nextLifeScore) {
        m_sceneManager.QueueNextFreeLifeScene(_currentScore, _nextLifeScore);
	}
	
	@Override
	public void HandleInviteFriends() {
        m_sceneManager.QueueScene(SCENE.FACEBOOK_INVITE, false);
	}

}