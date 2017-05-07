package com.example.officerbumble.scene;

import java.util.ArrayList;
import java.util.List;

import com.example.officerbumble.gameentities.Toast;
import com.example.officerbumble.interfaces.ButtonListener;
import com.example.officerbumble.interfaces.ChickenatorListener;
import com.example.officerbumble.interfaces.SceneListener;
import android.content.Context;
import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.engine.BadgeManager.Badge;
import com.example.officerbumble.engine.Color;
import com.example.officerbumble.engine.CoreGraphics;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DeviceDisplay.ANCHOR;
import com.example.officerbumble.engine.DifficultyConfig;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.GameStateManager.DIFFICULTY;
import com.example.officerbumble.engine.Point2D;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.Sprite.DIRECTION;
import com.example.officerbumble.engine.SpriteManager;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.TextureHelper;
import com.example.officerbumble.engine.TextureShaderProgram;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.gameentities.BowlingBall;
import com.example.officerbumble.gameentities.Bumble;
import com.example.officerbumble.gameentities.Button;
import com.example.officerbumble.gameentities.Chickenator;
import com.example.officerbumble.gameentities.Chickenator.CHICKENATOR_POSITION;
import com.example.officerbumble.gameentities.Criminal;
import com.example.officerbumble.gameentities.Escalator;
import com.example.officerbumble.gameentities.Exit;
import com.example.officerbumble.gameentities.Pie;
import com.example.officerbumble.gameentities.Popup;
import com.example.officerbumble.gameentities.RoboThrower2000;
import com.example.officerbumble.gameentities.StaticImage;
import com.example.officerbumble.gameentities.TextArea;
import com.example.officerbumble.gameentities.Treadmill;
import com.example.officerbumble.gameentities.Popup.POPUP_TYPE;
import com.example.officerbumble.gameentities.TextArea.FONT_TYPE;
import com.example.officerbumble.scene.SceneItemConfig.SCENE_ITEM_TYPE;

public class Scene {
    // Constants
	private final int WEAPON_ZBUFFER_INDEX = DeviceDisplay.ZPOSITION.NORMAL.getValue();
	private final int TEXT_AREA_ZBUFFER_INDEX = DeviceDisplay.ZPOSITION.NORMAL.getValue();
	private final int BUTTON_ZBUFFER_INDEX = DeviceDisplay.ZPOSITION.NORMAL.getValue();
	private final int STATIC_IMAGE_ZBUFFER_INDEX = DeviceDisplay.ZPOSITION.NORMAL.getValue();
    private final float LOADING_SPINNER_WIDTH = 1.5f;
    private final float LOADING_SPINNER_HEIGHT = 0.375f;

	// Resource and Game Managers.
	private DeviceDisplay m_display;
	private Timer m_realTimer;
	private Timer m_gameTimer;
	private SpriteSheetManager m_spriteSheetManager;
	private SpriteManager m_spriteManager;
	private DifficultyManager m_difficultyManager;
	private DifficultyConfig m_difficultyConfig;
	private GameStateManager m_gameStateManager;
	private TextureShaderProgram m_textureShaderProgram;
	private List<String> m_requiredTextures = new ArrayList<String>();

	// Timing Variables.
	private float m_lastFPSTime = 0;
	private int m_frames = 0;
	private int m_frameRate = 0;
	private boolean m_paused = false;
	private long m_pauseFor = 0;
	private float m_pauseStart = 0;
	private Context m_context;
    private static Sprite m_loadingSpinner;
    private Toast m_toast;

    // Scene specific variables
	private List<SceneListener> m_sceneListeners = new ArrayList<SceneListener>();
	private Point2D m_sceneStart = null;
	private Point2D m_sceneEnd = null;
    private AD_TYPE m_adVisibility = AD_TYPE.NONE;

	public Scene(DeviceDisplay _display, SpriteSheetManager _spriteSheetMananger, Timer _realTimer, Timer _gameTimer, Context _context) {
		m_spriteManager = new SpriteManager(); // Refresh all sprites.
		m_display = _display;
		m_realTimer = _realTimer;
		m_gameTimer = _gameTimer;
		m_context = _context;
		m_spriteSheetManager = _spriteSheetMananger;
		m_textureShaderProgram = _spriteSheetMananger.GetTextureShaderProgram();
		m_paused = false;
		m_spriteManager.Initialize();
	}

	// Add a sprite to the sprite manager -- this would typically be called after the scene has been initialized from file.
	public void AddSprite(Sprite _sprite) {
		m_spriteManager.AddSprite(_sprite);
	}	
	
	public void AddSprites(Sprite[] _sprites) {
		m_spriteManager.AddSprites(_sprites);
	}
	
	// Remove a sprite from the sprite manager -- this would typically be called after the scene has been initialized from file.
	public void RemoveSprite(String _spriteName) {
		m_spriteManager.RemoveSprite(_spriteName);
	}
	
	public void RemoveSprites(Sprite[] _sprites) {
		m_spriteManager.RemoveSprites(_sprites);
	}
	
	public Sprite GetSprite(String _spriteName) {
		return m_spriteManager.GetSprite(_spriteName);
	}
	
	public Sprite[] GetAllSprites() {
		return m_spriteManager.GetAllSprites();
	}

    public boolean SpriteExists(String _tag) {
        if(m_spriteManager.GetSprite(_tag) != null) {
            return true;
        } else {
            return false;
        }
    }

	// Initialize a scene based on a scene configuration (stored in an XML file).
	public void Initialize(SceneConfig _config, DifficultyManager _difficultyManager, GameStateManager _gameStateManager, AD_TYPE _adVisibility, Object _inputListener) {
		// Time the scene was created.
		float currentTimeMilliseconds = m_gameTimer.GetCurrentMilliseconds();

        m_adVisibility = _adVisibility;
		m_difficultyManager = _difficultyManager;
		m_gameStateManager = _gameStateManager;		
		m_requiredTextures = _config.GetRequiredTextures();
		// Possible for either of these to be null if we come in on a splash screen before everything is loaded.
		if(m_difficultyManager != null && m_gameStateManager != null) {
			m_difficultyConfig = m_difficultyManager.GetDifficultyConfig(m_gameStateManager.GetDifficulty().toString());
		}

		// Refresh all loaded textures.
		RefreshTextures();

		// Extract the start position of the scene (bottom left of the display).
		m_sceneStart = new Point2D(_config.GetSceneStart().GetX(), 
								   _config.GetSceneStart().GetY());
		
		// Extract the end position of the scene (top right of the display).
		m_sceneEnd = new Point2D(_config.GetSceneEnd().GetX(), 
								 _config.GetSceneEnd().GetY());

		// Set background color to whatever was configured.
		SetBackgroundColor(_config.GetBackgroundColor());
					
		// Loop through each top-level scene item in the configuration and initialize those sprites. Note that sprites might
		// have their own children sprites, these will be handled with recursive calls to InitializeSprites.
        for (SceneItemConfig value : _config.GetSceneItems().values()) {
			InitializeSprites(value, _inputListener, currentTimeMilliseconds);
		}

        // Loading spinner to show whenever things are loading.
        Point2D center = m_display.GetAnchorCoordinates(ANCHOR.CENTER, LOADING_SPINNER_WIDTH, LOADING_SPINNER_HEIGHT);
        if(m_spriteSheetManager.IsAnimationLoaded("LOADING_SPINNER")) {
            m_loadingSpinner = this.CreateStaticImage(center.GetX(), center.GetY(), LOADING_SPINNER_HEIGHT, LOADING_SPINNER_WIDTH, "LOADING_SPINNER", "LOADING_SPINNER");
        }
	}
				
	// Recursively add sprites to the various zBuffer based TreeMap. This ensures that we can display the sprites
	// using the painters algorithm.
	private void InitializeSprites(SceneItemConfig _sceneItem, Object _inputListener, float _currentMilliseconds) {
		// Handle Static Images.
		if (_sceneItem.GetType() == SCENE_ITEM_TYPE.STATIC_IMAGE && _sceneItem.IsEnabled()) {
			StaticImage staticImage = new StaticImage(_sceneItem.GetX(), _sceneItem.GetY(), 
													  _sceneItem.GetHeight(), _sceneItem.GetWidth(), 
													  _sceneItem.GetZBufferIndex(), false, _currentMilliseconds,
													  m_display, m_spriteSheetManager,
													  _sceneItem.GetAnimationTag(), _sceneItem.GetTag());
			m_spriteManager.AddSprite(staticImage);
		}

		if(_sceneItem.GetType() == SCENE_ITEM_TYPE.TREADMILL && _sceneItem.IsEnabled()) {
			Treadmill treadmill = new Treadmill(_sceneItem.GetX(), _sceneItem.GetY(), 
					 _sceneItem.GetHeight(), _sceneItem.GetWidth(),
					 _sceneItem.GetZBufferIndex(), false, _currentMilliseconds,
					 m_display, m_spriteSheetManager,
					 _sceneItem.GetAnimationTag(), _sceneItem.GetTag());
			
			m_spriteManager.AddSprite(treadmill);
		}		
		   
		if(_sceneItem.GetType() == SCENE_ITEM_TYPE.ROBO_THROWER_2000 && _sceneItem.IsEnabled()) {
			RoboThrower2000 thrower = new RoboThrower2000(_sceneItem.GetX(), _sceneItem.GetY(), 
					 _sceneItem.GetHeight(), _sceneItem.GetWidth(),
					 _sceneItem.GetZBufferIndex(), false, _currentMilliseconds,
					 m_display, m_spriteSheetManager, m_spriteManager,
					 _sceneItem.GetAnimationTag(), _sceneItem.GetTag());
			
			m_spriteManager.AddSprite(thrower);
		}
				
		// Handle Level Exit.
		if (_sceneItem.GetType() == SCENE_ITEM_TYPE.EXIT && _sceneItem.IsEnabled()) {
			Exit exit = new Exit(_sceneItem.GetX(), _sceneItem.GetY(), 
								 _sceneItem.GetHeight(), _sceneItem.GetWidth(),
								 _sceneItem.GetZBufferIndex(), _currentMilliseconds,
								 m_display, m_spriteSheetManager,
								 _sceneItem.GetAnimationTag(), _sceneItem.GetTag());
			m_spriteManager.AddSprite(exit);
		}

		// Handl Escalators
		if (_sceneItem.GetType() == SCENE_ITEM_TYPE.ESCALATOR && _sceneItem.IsEnabled()) {			
			Escalator escalator = new Escalator(_sceneItem.GetX(),  _sceneItem.GetY(), 
												_sceneItem.GetHeight(), _sceneItem.GetWidth(),
												_sceneItem.GetZBufferIndex(),
												_currentMilliseconds,
												m_display, m_spriteSheetManager,
												_sceneItem.GetAnimationTag(), _sceneItem.GetTag());
			m_spriteManager.AddSprite(escalator);			
		}

		// Handle Buttons
		if (_sceneItem.GetType() == SCENE_ITEM_TYPE.BUTTON && _sceneItem.IsEnabled()) {
			Button button = new Button(_sceneItem.GetX(), _sceneItem.GetY(), 
									   _sceneItem.GetHeight(), _sceneItem.GetWidth(), 
									   _sceneItem.GetZBufferIndex(), false,
									   _currentMilliseconds, m_display, 
									   m_spriteSheetManager, _sceneItem.GetAnimationTag(), _sceneItem.GetButtonAnimationTag(),
									   _sceneItem.GetTag());
			button.RegisterForButtonClicked(this);
			m_spriteManager.AddSprite(button);
		}

		// Handle Officer Bumble
		if (_sceneItem.GetType() == SCENE_ITEM_TYPE.BUMBLE && _sceneItem.IsEnabled()) {
			Bumble bumble = new Bumble(_sceneItem.GetX(), _sceneItem.GetY(), 
									   _sceneItem.GetHeight(), _sceneItem.GetWidth(), 
									   _sceneItem.GetZBufferIndex(),
									   _currentMilliseconds, m_difficultyConfig, m_display,
									   m_spriteSheetManager,
									   _sceneItem.GetAnimationTag(), _sceneItem.GetTag());
			m_spriteManager.AddSprite(bumble);
		}

		// Handle the criminal.
		if (_sceneItem.GetType() == SCENE_ITEM_TYPE.CRIMINAL && _sceneItem.IsEnabled()) {
			Criminal criminal = new Criminal(_sceneItem.GetX(), _sceneItem.GetY(),
											 _sceneItem.GetHeight(),_sceneItem.GetWidth(), 
											 _sceneItem.GetZBufferIndex(),
											 _currentMilliseconds, m_difficultyConfig, m_display,
											 m_spriteSheetManager, m_gameStateManager,
											 _sceneItem.GetAnimationTag(), _sceneItem.GetTag());
			m_spriteManager.AddSprite(criminal);
		}

		// Handle any children.		
		if (_sceneItem.GetChildren() != null) {
			for (SceneItemConfig value : _sceneItem.GetChildren().values()) {
				InitializeSprites(value, _inputListener, _currentMilliseconds);
			}
		}
	}
	
	// Clears all existing textures and loads what this scene requires.
	public void RefreshTextures() {
		// Refresh the textures in Open GL.
		TextureHelper.LoadTextures(m_context, m_requiredTextures);

		// Loop through every animation and refresh their corresponding texture Id.
		m_spriteSheetManager.RefreshAllTextureIds(m_context);
        m_textureShaderProgram = m_spriteSheetManager.GetTextureShaderProgram();

		// Loop through any existing sprites and update the texture Id's.
		m_spriteManager.RefreshAllTextureIds();
	}
	
	public void RegisterSceneListener(Object _listener) {
		m_sceneListeners.add((SceneListener) _listener);
	}
	
	// Set the background color for this display.
	public void SetBackgroundColor(Color _color) {
		m_display.SetBackgroundColor(_color);
	}

	// Touch events are handled by the sprite manager as it keeps track of which classes have registered as listeners.
	public void HandleTouch(boolean isPrimary, float normalizedX, float normalizedY) {		
		m_spriteManager.HandleTouch(isPrimary, normalizedX, normalizedY, m_realTimer.GetCurrentMilliseconds(), m_gameTimer.GetCurrentMilliseconds(), m_gameTimer.IsPaused());		
	}

	public void HandleTouchRelease(boolean isPrimary, float normalizedX, float normalizedY) {
		m_spriteManager.HandleTouchRelease(isPrimary, normalizedX, normalizedY, m_realTimer.GetCurrentMilliseconds(), m_gameTimer.GetCurrentMilliseconds(), m_gameTimer.IsPaused());		
	}

    public void Step() {
        UpdateObjects();
        DrawScene();
   }

	// Draw the entire scene graph.
	private void DrawScene() {
		CoreGraphics.ClearDisplay();
		m_spriteManager.Draw(m_display, m_textureShaderProgram);
	}
	
	// Update all of the objects in the scene graph and calculate the frame rate.
	//
	// NOTE: Child classes should call UpdateObjects() AFTER all of the movement/collision detection has been completed, otherwise
	// you'll be behind by one from from a display perspective.
	void UpdateObjects() {
		m_spriteManager.UpdateSprites(m_realTimer, m_gameTimer);
		RefreshFrameRate(m_realTimer.GetCurrentMilliseconds());
		
		// If we're currently waiting on a pause, then unpause after the elapsed time.
		if(m_pauseFor > 0) {
			if(m_realTimer.GetCurrentMilliseconds() > m_pauseStart + m_pauseFor) {
				m_pauseFor = 0;
				m_pauseStart = 0;

                if ( !IsPausePopupShown() ) {
                    m_paused = false;
                    m_gameTimer.UnPause();
                }
			}
		}		
	}

    public Timer GetRealTimer() {
		return m_realTimer;
	}
	
	public Timer GetGameTimer() {
		return m_gameTimer;
	}
	
	// Handles logic regarding frame rate tracking.
	public void RefreshFrameRate(float _currentTimeMilliseconds) {
		/* Detect Update Rate */
		if (_currentTimeMilliseconds - m_lastFPSTime >= 1000) {
			m_frameRate = m_frames;

			m_frames = 0;
			m_lastFPSTime = _currentTimeMilliseconds;
		} else {
            m_frames++;
		}		
		
		// Only if game state has been initialized
		if(m_gameStateManager != null) {
			m_gameStateManager.SetFrameRate(m_frameRate);
		}
	}

    public AD_TYPE GetAdVisibility() {
        return m_adVisibility;
    }
	
	/***************** Object Creation *****************
	 *
	 * This is where all objects are created, kind of like a factory.  Keeps the child scenes much cleaner.
	 ***************************************************/	
	// Creates a new popup centered directly on the screen.
	protected Button CreateButton(float _x, float _y, float _height, float _width, ButtonListener _listener, String _animationTag, String _buttonPressedTag, String _tag) {
		Button button = new Button(_x, _y, _height, _width, BUTTON_ZBUFFER_INDEX, false, m_gameTimer.GetCurrentMilliseconds(), m_display, m_spriteSheetManager, _animationTag, _buttonPressedTag, _tag);
		button.RegisterForButtonClicked(_listener);
		
		return button;		
	}
	
	protected StaticImage CreateStaticImage(float _x, float _y, float _height, float _width, String _animationTag, String _tag) {
		return new StaticImage(_x, _y, _height, _width, STATIC_IMAGE_ZBUFFER_INDEX, false, m_gameTimer.GetCurrentMilliseconds(), m_display, m_spriteSheetManager, _animationTag, _tag);		
	}
	
	protected Popup CreatePopup(POPUP_TYPE _type, float _width, float _height, boolean _useOverlay, String _popupText, ButtonListener _listener) {
		return new Popup(_type, _width, _height, 0.16f, 0.16f, 1, _useOverlay, _popupText, m_display, m_spriteSheetManager, m_spriteManager, m_realTimer, _listener);
	}

    protected Popup CreatePopup(POPUP_TYPE _type, float _width, float _height, float _letterWidth, float _letterHeight, int _zBufferIndex, boolean _useOverlay, String _popupText, ButtonListener _listener) {
        return new Popup(_type, _width, _height, _letterWidth, _letterHeight, _zBufferIndex, _useOverlay, _popupText, m_display, m_spriteSheetManager, m_spriteManager, m_realTimer, _listener);
    }
	
	protected TextArea CreateTextArea(float _x, float _y, float _letterHeight, float _letterWidth, float _width, int _maxLength, String _textValue, String _tag) {
		return new TextArea(FONT_TYPE.CARTOON, _x, _y, _letterHeight, _letterWidth, _width, TEXT_AREA_ZBUFFER_INDEX, m_gameTimer.GetCurrentMilliseconds(), m_display, m_spriteSheetManager, _maxLength, _textValue, _tag);
	}
	
	protected Pie CreatePie(float _x, float _y, float _velocity, DIRECTION _direction, String _tag) {
		return new Pie(_x, _y, _velocity, _direction, WEAPON_ZBUFFER_INDEX, m_gameTimer.GetCurrentMilliseconds(), 
					      m_display, m_spriteSheetManager, _tag);		
	}
	
	protected Chickenator CreateChickenator(float _x, float _y, float _velocity, DIRECTION _direction, 				
											CHICKENATOR_POSITION _startingPosition, float _targetY, float _activationProximity, int _floorLevel, ChickenatorListener _listener, String _tag) {		
		return new Chickenator(_x, _y, _velocity, _direction,  
				 			   _startingPosition, _targetY, _activationProximity, _floorLevel, _listener, WEAPON_ZBUFFER_INDEX, 
				 			   m_gameTimer.GetCurrentMilliseconds(), m_display, m_spriteSheetManager, _tag);		
	}
	
	protected BowlingBall CreateBowlingBall(float _x, float _y, float _velocity, DIRECTION _direction, String _tag) {
		return new BowlingBall(_x, _y, _velocity, _direction, WEAPON_ZBUFFER_INDEX, m_gameTimer.GetCurrentMilliseconds(), 
					      m_display, m_spriteSheetManager, _tag);		
	}
		
	/***************** Scene Properties *****************/ 	
	protected Point2D GetSceneEnd() {
		return m_sceneEnd;
	}		
	
	// Scene Properties
	protected Point2D GetSceneStart() {
		return m_sceneStart;		
	}
	
	protected DeviceDisplay GetDisplay() {
		return m_display;
	}
		
	protected SpriteSheetManager GetSpriteSheetManager() {
		return m_spriteSheetManager;
	}
	
	protected SpriteManager GetSpriteManager() {
		return m_spriteManager;
	}
	
	protected GameStateManager GetGameStateManager() {
		return m_gameStateManager;
	}
	
	protected DifficultyConfig GetDifficultyConfig() {
		return m_difficultyConfig;
	}
	
	// Scene Event Handlers
	protected void HandleSceneQuitGame() {
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleSceneQuitGame();
		}
	}
	
	protected void HandleQuitToTitle() {
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleSceneQuitToTitle();
		}				
	}

	protected void HandleInviteFriends() {
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleInviteFriends();
		}				
	}
	
	protected void HandleDifficultySelected(DIFFICULTY _difficulty, float _currentTimeMilliseconds) {
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleSceneDifficultySelected(_difficulty, _currentTimeMilliseconds);
		}
	}
	
	protected void HandleSceneWon(float _currentTimeMilliseconds) {
		// Next Level
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleSceneLevelWon(_currentTimeMilliseconds);
		}		
	}

	protected void HandleBadgeAwarded(Badge _badge, Badge _nextBadge) {
		// Next Level
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleBadgeAwarded(_badge, _nextBadge);
		}		
	}

	protected void HandleNextBadge(int _criminalsCaught, Badge _nextBadge) {
		// Next Level
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleNextBadge(_criminalsCaught, _nextBadge);
		}		
	}
	
	protected void HandleNextFreeMan(long _currentScore, long _nextLifeScore) {
		// Next Level
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleNextFreeMan(_currentScore, _nextLifeScore);
		}				
	}

	protected void HandleScoreIncrease(long _incrementBy) {
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleScoreIncrease(_incrementBy);
		}		
	}		
	
	protected void HandleSceneLost(float _currentTimeMilliseconds) {
		// Next Level
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleSceneLevelLost(_currentTimeMilliseconds);
		}		
	}
	
	protected void HandleRestartGame() {
		// Next Level
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleRestartGame();
		}		
	}
	
	protected void HandleTrainingSelected() {
		for (SceneListener listener : m_sceneListeners) {
			listener.HandleTrainingSelected();
		}				
	}
	
	public void Pause() {
        m_pauseStart = 0;
        m_pauseFor = 0;
		m_paused = true;
		m_gameTimer.Pause();
	}

    public void StartLoading() {
        if(m_loadingSpinner != null) {
            m_spriteManager.AddSprite(m_loadingSpinner);
        }
    }

	public boolean IsPaused() {
		return m_paused;
	}
	
	public void PauseFor(long _pauseFor) {		
		m_pauseStart = m_realTimer.GetCurrentMilliseconds();
		m_pauseFor = _pauseFor;
		m_paused = true;
		m_gameTimer.Pause();
	}
	
	protected void Unpause() {
		m_paused = false;
		m_gameTimer.UnPause();
	}

    // Total hack to get things working.
    private boolean IsPausePopupShown() {
        if ( m_spriteManager.GetSprite("PAUSE") != null ) {
            return true;
        } else {
            return false;
        }
    }

	public void FacebookInviteConfirm() {
		// TODO Auto-generated method stub
		
	}
			
}
