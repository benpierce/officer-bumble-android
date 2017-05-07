package com.example.officerbumble.gameentities;

import java.util.TreeMap;
import com.example.officerbumble.interfaces.ButtonListener;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DeviceDisplay.ANCHOR;
import com.example.officerbumble.engine.DifficultyConfig;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.Point2D;
import com.example.officerbumble.engine.SoundManager;
import com.example.officerbumble.engine.Sprite.DIRECTION;
import com.example.officerbumble.engine.SpriteManager;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.gameentities.TextArea.FONT_TYPE;
import com.example.officerbumble.scene.Scene;

/*
 * This class represents the game HUD (score, frame rate, lives, and mini-map functionality).
 */
public class GameHUD implements ButtonListener {
	// Constants
	private final float HUD_WIDTH = 1.5f;
	private final float HUD_HEIGHT = 0.5f;		
	private final float LETTER_HEIGHT = 0.12f;
	private final float LETTER_WIDTH = 0.12f;	
	private final float MARKER_WIDTH = 0.10f;
	private final float MARKER_HEIGHT = 0.10f;
	private final float LIFE_HEIGHT = 0.15f;
	private final float LIFE_WIDTH = 0.15f;
	private final float PANEL_PADDING = 0.03385f;
	private final float LINE_HEIGHT = 0.007f;
	private final float QUIT_BUTTON_WIDTH = 0.215f;
	private final float QUIT_BUTTON_HEIGHT = 0.215f;
	private final float MUTE_BUTTON_WIDTH = 0.20f;
	private final float MUTE_BUTTON_HEIGHT = 0.20f;
	private final float MINI_MAP_REFRESH_INTERVAL = 100;	// Amount of time to wait to refresh mini-map.
	private DIRECTION m_bumbleMarkerDirection;
	private DIRECTION m_criminalMarkerDirection;
	
	// HUD Components
	private StaticImage m_HUD_Panel = null;
	private StaticImage m_bumbleMarker = null;
	private StaticImage m_criminalMarker = null;
	private Button m_pause = null;
	private Button m_mute = null;
	private StaticImage m_livesLabel = null;
    private StaticImage m_scoreLabel = null;
    private StaticImage m_frameRateLabel = null;

	private TextArea m_score = null;
	private TextArea m_frameRate = null;	
	private TreeMap<Integer, StaticImage> m_lives = new TreeMap<Integer, StaticImage>();
	
	// Map Information
	private Point2D m_mapStart = null;
	private Point2D m_mapEnd = null;

	// References to the various resources.
	private DeviceDisplay m_display;
	private SpriteSheetManager m_spriteSheetManager;
	private DifficultyConfig m_difficultyConfig;
	
	// Performance Related Variables are declared following this comment because we don't want to have the garbage collector
	// constantly running if we create these locally.
	private boolean m_initialized = false;
	private int m_lastLivesCount = 0;
	private long m_lastScore = -99;		 
	private int m_lastFrameRate = -99;
	private float m_lastMiniMapRefresh = 0;
    private boolean m_showFrameRate = false;
	
	// Minimap Variables
	private float availablePanelWidth = 0.0f;
	private float availablePanelHeight = 0.0f;						
	private float adjustedMapHeight = 0.0f;
	private float adjustedMapWidth = 0.0f;
	
	// % of the map covered by Bumble and Criminal.
	private float bumbleXTranslated = 0.0f;  
	private float bumbleYTranslated = 0.0f;
	private float criminalXTranslated = 0.0f;  
	private float criminalYTranslated = 0.0f;
	private float mapBaseY = 0.0f;	
	private float mapBaseX = 0.0f;
	private Point2D m_topLeft;
	private Scene m_scene;
				
	public GameHUD(Point2D _mapStart, Point2D _mapEnd, float _currentTimeMilliseconds, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, DifficultyConfig _difficultyConfig, Object _parent) {
		Point2D miniMapPosition = _display.GetAnchorCoordinates(ANCHOR.TOP_RIGHT, HUD_WIDTH, HUD_HEIGHT);
		float y = miniMapPosition.GetY();

        m_topLeft = _display.GetAnchorCoordinates(ANCHOR.TOP_LEFT, 0, 0);
		m_scene = (Scene)_parent;
		
		m_difficultyConfig = _difficultyConfig;
		m_display = _display;
		m_spriteSheetManager = _spriteSheetManager;
		m_mapStart = _mapStart;
		m_mapEnd = _mapEnd;

		m_HUD_Panel = new StaticImage(miniMapPosition.GetX(), y, HUD_HEIGHT, HUD_WIDTH, 3, true, _currentTimeMilliseconds, _display, _spriteSheetManager, "HUD_PANEL", "HUD_PANEL");
		m_bumbleMarker = new StaticImage(miniMapPosition.GetX() + PANEL_PADDING, y - HUD_HEIGHT + PANEL_PADDING , MARKER_HEIGHT, MARKER_WIDTH, 2, true, _currentTimeMilliseconds, _display, _spriteSheetManager, "BUMBLE_MARKER_RIGHT", "BUMBLE_MARKER");
		m_criminalMarker = new StaticImage(miniMapPosition.GetX() + PANEL_PADDING, y - HUD_HEIGHT + PANEL_PADDING , MARKER_HEIGHT, MARKER_WIDTH, 2, true, _currentTimeMilliseconds, _display, _spriteSheetManager, "CRIMINAL_MARKER_RIGHT", "CRIMINAL_MARKER");
		m_bumbleMarkerDirection = DIRECTION.RIGHT;
		m_criminalMarkerDirection = DIRECTION.RIGHT;

        float livesY = 0;

        if ( m_showFrameRate ) {
            livesY = y - ((LETTER_HEIGHT * 2) + (PANEL_PADDING * 2));
        } else {
            livesY = y - LETTER_HEIGHT - PANEL_PADDING;
        }

        float livesLabelWidth = LETTER_WIDTH * 4f;
        float scoreLabelWidth = LETTER_WIDTH * 4f;
        float frameRateLabelWidth = LETTER_WIDTH * 4;

        m_livesLabel = new StaticImage(m_topLeft.GetX() + PANEL_PADDING, livesY, LETTER_HEIGHT, livesLabelWidth, 3, true, _currentTimeMilliseconds, _display, _spriteSheetManager, "lblLives", "lblLives");
        m_scoreLabel = new StaticImage(m_topLeft.GetX() + PANEL_PADDING, y, LETTER_HEIGHT, scoreLabelWidth, 3, true, _currentTimeMilliseconds, _display, _spriteSheetManager, "lblScore", "lblScore");
        m_frameRateLabel = new StaticImage(m_topLeft.GetX() + PANEL_PADDING, y - LETTER_HEIGHT - PANEL_PADDING, LETTER_HEIGHT, frameRateLabelWidth, 3, true, _currentTimeMilliseconds, _display, _spriteSheetManager, "lblFrameRate", "lblFrameRate");

        //m_livesLabel = new TextArea(FONT_TYPE.CARTOON, m_topLeft.GetX() + PANEL_PADDING, livesY, LETTER_HEIGHT, LETTER_WIDTH, 2.5f, 2, _currentTimeMilliseconds, m_display, m_spriteSheetManager, 7, "Lives: ", "Lives");
        m_score = new TextArea(FONT_TYPE.CARTOON, m_topLeft.GetX() + PANEL_PADDING + scoreLabelWidth, y - (LETTER_HEIGHT / 4.0f), LETTER_HEIGHT, LETTER_WIDTH, 2.5f, 2, _currentTimeMilliseconds, m_display, m_spriteSheetManager, 7, "", "Score");
		m_frameRate = new TextArea(FONT_TYPE.CARTOON, m_topLeft.GetX() + PANEL_PADDING + frameRateLabelWidth , y - LETTER_HEIGHT - PANEL_PADDING - (LETTER_HEIGHT / 6.0f), LETTER_HEIGHT, LETTER_WIDTH, 2.5f, 2, _currentTimeMilliseconds, m_display, m_spriteSheetManager, 2, "", "Framerate");
		
		float currentX = m_livesLabel.GetX() + m_livesLabel.GetWidth();
		float currentY = m_livesLabel.GetY() + (PANEL_PADDING * 1.5f) - (LETTER_HEIGHT / 4.0f);
		
		for(int i=1; i <= _difficultyConfig.GetMaxLives() - 1; i++) {												
			StaticImage life = new StaticImage(currentX, currentY, LIFE_HEIGHT, LIFE_WIDTH, 2, true, _currentTimeMilliseconds, _display, _spriteSheetManager, "BUMBLE_LIFE", "LIFE" + String.valueOf(i));
			m_lives.put(i, life);
						
			currentX += LIFE_WIDTH;
		}
						
		m_pause = new Button(m_HUD_Panel.GetX() - QUIT_BUTTON_WIDTH - 0.05f, y + 0.01f, QUIT_BUTTON_HEIGHT, QUIT_BUTTON_WIDTH, 2, false, _currentTimeMilliseconds, _display, _spriteSheetManager, "BUTTON_PAUSE", "", "BUTTON_PAUSE");
		m_pause.RegisterForButtonClicked(this);
		
		m_mute = new Button(m_pause.GetX() - MUTE_BUTTON_WIDTH - 0.05f, y, MUTE_BUTTON_HEIGHT, MUTE_BUTTON_WIDTH, 2, false, _currentTimeMilliseconds, _display, _spriteSheetManager, (SoundManager.IsMuted() ? "BUTTON_MUTE_OFF" : "BUTTON_MUTE_ON"), "", "BUTTON_MUTE");
		m_mute.RegisterForButtonClicked(this);
		
		// Setup some calculations for the mini-map as we only need to calculate these once.
		availablePanelWidth = m_HUD_Panel.GetWidth() - (PANEL_PADDING * 2) - MARKER_WIDTH;
		availablePanelHeight = m_HUD_Panel.GetHeight() - (PANEL_PADDING * 2);						
		adjustedMapHeight = (m_mapEnd.GetY() - m_mapStart.GetY() - 1.0f);		// The 1.0f represents the top level (sky) which we don't want in the mini-map.
		adjustedMapWidth = (m_mapEnd.GetX() - m_mapStart.GetX());		
	}
	
	public void UpdateHUD(Bumble _bumble, Criminal _criminal, SpriteManager _spriteManager, GameStateManager _gameState, float _currentTimeMilliseconds) {
		// If the HUD hasn't been initialized, we need to add everything to the sprite manager.
		if(!m_initialized) {
			_spriteManager.AddSprite(m_HUD_Panel);
			_spriteManager.AddSprite(m_bumbleMarker);
			_spriteManager.AddSprite(m_criminalMarker);
			_spriteManager.AddSprite(m_pause);
			_spriteManager.AddSprite(m_mute);
			if(!m_difficultyConfig.GetDifficultyName().equals("HARDCORE")) {
                _spriteManager.AddSprite(m_livesLabel);
				//_spriteManager.AddSprites(m_livesLabel.GetSprites());
			}
            _spriteManager.AddSprite(m_scoreLabel);
			_spriteManager.AddSprites(m_score.GetSprites());

            if ( m_showFrameRate ) {
                _spriteManager.AddSprite(m_frameRateLabel);
                _spriteManager.AddSprites(m_frameRate.GetSprites());
            }
						
			m_initialized = true;
		}
						
		if(m_lastScore != _gameState.GetScore()) {			
			m_score.UpdateText("" + _gameState.GetScore(), _currentTimeMilliseconds, _spriteManager);
			m_lastScore = _gameState.GetScore();																			
		}

        if ( m_showFrameRate ) {
            if (m_lastFrameRate != _gameState.GetFrameRate()) {
                m_frameRate.UpdateText("" + _gameState.GetFrameRate(), _currentTimeMilliseconds, _spriteManager);
                m_lastFrameRate = _gameState.GetFrameRate();
            }
        }
																
		UpdateMiniMap(_bumble, _criminal, _spriteManager, _currentTimeMilliseconds);
		
		if(!m_difficultyConfig.GetDifficultyName().equals("HARDCORE")) {
			if(m_lastLivesCount != _gameState.GetRemainingLives()) {
				m_lastLivesCount = _gameState.GetRemainingLives();
				
				// Remove all the live sprites.
				for(int i = 1; i <= m_difficultyConfig.GetMaxLives(); i++) {
					_spriteManager.RemoveSprite("LIFE" + String.valueOf(i));
				}
				
				// Add back the appropriate amount.
				for(int i = 1; i <= m_lastLivesCount - 1; i++) {
					_spriteManager.AddSprite(m_lives.get(i));
				}
			}
		}
	}	
				
	private void UpdateMiniMap(Bumble _bumble, Criminal _criminal, SpriteManager _spriteManager, float _currentTimeMilliseconds) {
		// Just so we're not being too expensive with the calcs every frame.
		if(_currentTimeMilliseconds - m_lastMiniMapRefresh > MINI_MAP_REFRESH_INTERVAL) {				
			// % of the map covered by Bumble and Criminal.
			bumbleXTranslated = (_bumble.GetMapPosition().GetX() - m_mapStart.GetX()) / adjustedMapWidth;  
			bumbleYTranslated = (_bumble.GetMapPosition().GetY() - _bumble.GetHeight() - m_mapStart.GetY()) / adjustedMapHeight;
			criminalXTranslated = (_criminal.GetMapPosition().GetX() - m_mapStart.GetX()) / adjustedMapWidth;  
			criminalYTranslated = (_criminal.GetMapPosition().GetY() - _criminal.GetHeight() - m_mapStart.GetY()) / adjustedMapHeight;
			mapBaseY = m_HUD_Panel.GetY() - HUD_HEIGHT + PANEL_PADDING + LINE_HEIGHT + MARKER_HEIGHT;	
			mapBaseX = m_HUD_Panel.GetX() + PANEL_PADDING;

			if(_bumble.GetDirection() != m_bumbleMarkerDirection) {
				// Flip the animation for the marker.
				m_bumbleMarker.SetCurrentAnimation(_bumble.GetDirection() == DIRECTION.LEFT ? "BUMBLE_MARKER_LEFT" : "BUMBLE_MARKER_RIGHT", _currentTimeMilliseconds);
				m_bumbleMarkerDirection = _bumble.GetDirection();
			}
			
			if(_criminal.GetDirection() != m_criminalMarkerDirection) {
				// Flip the animation for the marker.
				m_criminalMarker.SetCurrentAnimation(_criminal.GetDirection() == DIRECTION.LEFT ? "CRIMINAL_MARKER_LEFT" : "CRIMINAL_MARKER_RIGHT", _currentTimeMilliseconds);
				m_criminalMarkerDirection = _criminal.GetDirection();
			}
			
			m_bumbleMarker.MoveTo(_currentTimeMilliseconds, mapBaseX + (availablePanelWidth * bumbleXTranslated), mapBaseY + (availablePanelHeight * bumbleYTranslated));
			m_criminalMarker.MoveTo(_currentTimeMilliseconds, mapBaseX + (availablePanelWidth * criminalXTranslated), mapBaseY + (availablePanelHeight * criminalYTranslated));
			
			m_lastMiniMapRefresh = _currentTimeMilliseconds;
		}
	}

	@Override
	public void HandleButtonPressed(String _tag, float currentTimeMilliseconds) {
		if(_tag.equals("BUTTON_MUTE")) {
			// If we're not muted, then mute the sound.
			if(SoundManager.IsMuted()) {
				m_mute.SetCurrentAnimation("BUTTON_MUTE_ON", currentTimeMilliseconds);
				SoundManager.Unmute();
			} else {
				m_mute.SetCurrentAnimation("BUTTON_MUTE_OFF", currentTimeMilliseconds);
				SoundManager.Mute();
			}			
		}

		if(_tag.equals("BUTTON_PAUSE")) {
            m_scene.Pause();
		}
	}
	
	
}