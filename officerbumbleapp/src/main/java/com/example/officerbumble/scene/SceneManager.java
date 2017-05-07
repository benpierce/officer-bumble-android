package com.example.officerbumble.scene;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.officerbumble.interfaces.SceneListener;
import android.content.Context;
import android.util.Log;

import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.engine.BadgeManager;
import com.example.officerbumble.engine.BadgeManager.Badge;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DifficultyManager;
import com.example.officerbumble.engine.GameStateManager;
import com.example.officerbumble.engine.SoundManager;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.engine.TextResourceReader;
import com.example.officerbumble.R;
import com.example.officerbumble.R.raw;
import com.sromku.simple.fb.SimpleFacebook;

public class SceneManager {
	
	// List of all valid scene types that we've got.
	public enum SCENE {
		SPLASH_SCREEN, TITLE, SHOPPING_MALL, BANK, MUSEUM, CASINO, UNIT_TEST, GAME_OVER, GAME_OVER_HARDCORE, TUTORIAL, BADGE, NEXT_BADGE, NEXT_LIFE, FACEBOOK_INVITE, LEVEL_DESIGN
	}

    private int HARDCORE_AD_PERCENTAGE = 33;    // As a %

	// Scene specific requirements:
	private Context m_context;
	private DeviceDisplay m_display;
	private SpriteSheetManager m_spriteSheetManager;
	private DifficultyManager m_difficultyManager;
	private Timer m_realTimer;
	private Timer m_gameTimer;	
	private SimpleFacebook m_simpleFacebook;
	private SceneListener m_sceneListener;
	private GameStateManager m_gameStateManager;
	private BadgeManager m_badgeManager;
	private Scene m_queuedScene;
    private SCENE m_queuedSceneType;
    private int m_drawCount = 0;
    private boolean m_sceneQueued;
    private boolean m_ignoreDrawCount = false;

    // Scene paramater cache
    private boolean m_showFacebookPrompt = false;
    private Badge m_badge;
    private Badge m_nextBadge;
    private int m_criminalsCaught;
    private long m_currentScore;
    private long m_nextLifeScore;

	// All of the scenes loaded into memory.
	private HashMap<String, SceneConfig> m_scenes = new HashMap<String, SceneConfig>();

	public SceneManager(Context _context, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, Timer _realTimer, Timer _gameTimer, SimpleFacebook _simpleFacebook, SceneListener _sceneListener) {
		m_context = _context;
		m_display = _display;
		m_spriteSheetManager = _spriteSheetManager;
		m_realTimer = _realTimer;
		m_gameTimer = _gameTimer;
		m_simpleFacebook = _simpleFacebook;
		m_sceneListener = _sceneListener;
	}
	
	public void Initialize(DifficultyManager _difficultyManager, GameStateManager _gameStateManager, BadgeManager _badgeManager) {
		m_difficultyManager = _difficultyManager;
		m_gameStateManager = _gameStateManager;
		m_badgeManager = _badgeManager;
        m_sceneQueued = false;
	}

	public void LoadBasic() {
		ClearScenes();
		try {
			Load(m_context, R.raw.splashscreen);
		} catch(Exception ex) {
			Log.w("GameRenderer", "Unable to load the scene configuration file! " + ex.getMessage());
		}		
	}

	// Loads all of the scenes from their configuration files. This section will need to change whenever we have a new scene type.
	public void LoadAll() {
		long startTime = System.nanoTime();
		
		// Reload all scenes since they're device display dependent (for layout purposes).
		try {					
			Load(m_context, R.raw.titlescreen);
			Load(m_context, R.raw.gameover);
			Load(m_context, R.raw.shoppingmall);
            Load(m_context, R.raw.museum);
            Load(m_context, R.raw.bank);
            Load(m_context, R.raw.casino);
			Load(m_context, R.raw.tutorial);
            Load(m_context, R.raw.badgeawarded);
            Load(m_context, R.raw.facebookinvite);
            Load(m_context, R.raw.nextbadge);
            Load(m_context, R.raw.nextfreeman);
            Load(m_context, R.raw.leveldesign);
		} catch(Exception ex) {
			Log.e("GameRenderer", "Unable to load the scene configuration file! " + ex.getMessage());
		}
	}

    public void QueueScene(SCENE _sceneType, boolean _ignoreDrawCount) {
        m_queuedSceneType = _sceneType;
        m_sceneQueued = true;
        m_ignoreDrawCount = _ignoreDrawCount;
    }

    public void QueueTitleScene(boolean _showFacebookPrompt) {
        QueueScene(SCENE.TITLE, true);
        m_showFacebookPrompt = _showFacebookPrompt;
    }

    public void QueueBadgeAwardedScene(Badge _badge, Badge _nextBadge) {
        QueueScene(SCENE.BADGE, false);
        m_badge = _badge;
        m_nextBadge = _nextBadge;
    }

    public void QueueNextBadgeScene(int _criminalsCaught, Badge _nextBadge) {
        QueueScene(SCENE.NEXT_BADGE, false);
        m_criminalsCaught = _criminalsCaught;
        m_nextBadge = _nextBadge;
    }

    public void QueueNextFreeLifeScene(long _currentScore, long _nextLifeScore){
        QueueScene(SCENE.NEXT_LIFE, false);
        m_currentScore = _currentScore;
        m_nextLifeScore = _nextLifeScore;
    }

    public boolean QueuedSceneReadyToLoad() {
        boolean sceneReady = false;

        if(m_sceneQueued) {
            m_drawCount++;
            if(m_drawCount >= 2 || m_ignoreDrawCount) {
                sceneReady = true;
                m_drawCount = 0;
                m_sceneQueued = false;
            }
        }

        return sceneReady;
    }

    public Scene StartQueuedScene() {
        switch(m_queuedSceneType) {
            case SPLASH_SCREEN:
                StartSplashScreen();
                break;
            case TITLE:
                StartTitleScreen(false);
                break;
            case SHOPPING_MALL:
                StartGame();
                break;
            case MUSEUM:
                StartGame();
                break;
            case BANK:
                StartGame();
                break;
            case CASINO:
                StartGame();
                break;
            case GAME_OVER_HARDCORE:
                StartGameOverHardcoreScreen();
                break;
            case GAME_OVER:
                StartGameOverScreen();
                break;
            case TUTORIAL:
                StartTutorial();
                break;
            case BADGE:
                StartBadgeAwardedScreen(m_badge, m_nextBadge, m_simpleFacebook);
                break;
            case NEXT_BADGE:
                NextBadgeScreen(m_criminalsCaught, m_nextBadge);
                break;
            case NEXT_LIFE:
                NextFreeLifeScreen(m_currentScore, m_nextLifeScore);
                break;
            case FACEBOOK_INVITE:
                FacebookInviteScreen(m_simpleFacebook);
                break;
            case LEVEL_DESIGN:
                LevelDesign();
                break;
        }

        return m_queuedScene;
    }

	private void StartSplashScreen() {
		SplashScreen scene = new SplashScreen(m_display, m_spriteSheetManager, m_realTimer, m_gameTimer, m_context); 		
		scene.Initialize(GetScene(SCENE.SPLASH_SCREEN), m_difficultyManager, m_gameStateManager, AD_TYPE.NONE, m_context);
		
		// Always register parent as a scene listener.
		scene.RegisterSceneListener(m_sceneListener);
		m_sceneListener.HandleAdVisibility(AD_TYPE.NONE);
		SoundManager.StopMusic();

		m_queuedScene = scene;
	}
	
	private void StartTutorial() {
		Training scene = new Training(m_display, m_spriteSheetManager, m_realTimer, m_gameTimer, m_context);
		scene.Initialize(GetScene(SCENE.TUTORIAL), m_difficultyManager, m_gameStateManager, AD_TYPE.NONE, m_context);

		// Always register parent as a scene listener.
		scene.RegisterSceneListener(m_sceneListener);
		m_sceneListener.HandleAdVisibility(AD_TYPE.NONE);
		SoundManager.StopMusic();
		
		System.gc();	// Clean up
		
		m_queuedScene = scene;
	}
	
	private void StartTitleScreen(boolean _showFacebookPrompt) {
		TitleScreen scene = new TitleScreen(m_display, m_spriteSheetManager, m_realTimer, m_gameTimer, m_simpleFacebook, _showFacebookPrompt, m_context);
		scene.Initialize(GetScene(SCENE.TITLE), m_difficultyManager, m_gameStateManager, AD_TYPE.NONE, m_context);
		
		// Always register parent as a scene listener.
		scene.RegisterSceneListener(m_sceneListener);
		m_sceneListener.HandleAdVisibility(AD_TYPE.NONE);
		
		// We can play the opening theme song now that the title screen is up.
		SoundManager.PlayMusic(m_context, raw.badguys);

		System.gc();	// Clean up

		m_queuedScene = scene;
	}
	
	private void StartGameOverHardcoreScreen() {
		GameOverHardcoreScreen scene = new GameOverHardcoreScreen(m_display, m_spriteSheetManager, m_realTimer, m_gameTimer, m_context);
		scene.Initialize(GetScene(SCENE.GAME_OVER_HARDCORE), m_difficultyManager, m_gameStateManager, AD_TYPE.INTERSTITIAL, this);
		
		// Always register parent as a scene listener.
		scene.RegisterSceneListener(m_sceneListener);

        // People were being spammed badly on hardcore with ads because they lose so quickly, this will ensure that
        // people are only spammed 25% of the time :)
        int num = (int)(Math.random() * 100);
        if ( num <= HARDCORE_AD_PERCENTAGE ) {
            m_sceneListener.HandleAdVisibility(AD_TYPE.INTERSTITIAL);
        }

		SoundManager.StopMusic();
		SoundManager.PlaySound("FAILURE", false);
		
		m_queuedScene = scene;
	}
		
	private void StartGameOverScreen() {
		GameOverScreen scene = new GameOverScreen(m_display, m_spriteSheetManager, m_realTimer, m_gameTimer, m_context);
		scene.Initialize(GetScene(SCENE.GAME_OVER), m_difficultyManager, m_gameStateManager, AD_TYPE.INTERSTITIAL, this);
		
		// Always register parent as a scene listener.
		scene.RegisterSceneListener(m_sceneListener);
		m_sceneListener.HandleAdVisibility(AD_TYPE.INTERSTITIAL);
		SoundManager.StopMusic();
		SoundManager.PlaySound("FAILURE", false);

		m_queuedScene = scene;
	}

	private void StartGame() {
        Game scene = new Game(m_display, m_spriteSheetManager, m_realTimer, m_gameTimer, m_badgeManager, m_simpleFacebook, m_context);
		scene.Initialize(GetScene(m_queuedSceneType), m_difficultyManager, m_gameStateManager, AD_TYPE.NONE, this);

		// Always register parent as a scene listener.
		scene.RegisterSceneListener(m_sceneListener);
		m_sceneListener.HandleAdVisibility(AD_TYPE.NONE);
		
		System.gc();	// Clean up

        scene.StartGame();

		m_queuedScene = scene;
	}
	
	private void StartBadgeAwardedScreen(Badge _badge, Badge _nextBadge, SimpleFacebook _simpleFacebook) {
		BadgeAwarded scene = new BadgeAwarded(_badge, _nextBadge, m_display, m_spriteSheetManager,m_realTimer, m_gameTimer, _simpleFacebook, m_context);
		scene.Initialize(GetScene(SCENE.BADGE), m_difficultyManager, m_gameStateManager, AD_TYPE.INTERSTITIAL, this);
		
		// Always register parent as a scene listener.
		scene.RegisterSceneListener(m_sceneListener);
		m_sceneListener.HandleAdVisibility(AD_TYPE.INTERSTITIAL);
		SoundManager.StopMusic();
		SoundManager.PlaySound("PROMOTION", false);
		
		System.gc();	// Clean up

		m_queuedScene = scene;
	}

	private void FacebookInviteScreen(SimpleFacebook _simpleFacebook) {
		FacebookInvite scene = new FacebookInvite(m_display, m_spriteSheetManager, m_realTimer, m_gameTimer, _simpleFacebook, m_context);
		scene.Initialize(GetScene(SCENE.FACEBOOK_INVITE), m_difficultyManager, m_gameStateManager, AD_TYPE.NONE, this);
		
		// Always register parent as a scene listener.
		scene.RegisterSceneListener(m_sceneListener);
		m_sceneListener.HandleAdVisibility(AD_TYPE.NONE);
		SoundManager.StopMusic();
        SoundManager.PlaySound("PROMOTION", false);
		
		System.gc();	// Clean up

		m_queuedScene = scene;
	}
	
	private void NextBadgeScreen(int _criminalsCaught, Badge _nextBadge) {
		NextBadge scene = new NextBadge(_criminalsCaught, _nextBadge, m_display, m_spriteSheetManager, m_realTimer, m_gameTimer, m_context);
		scene.Initialize(GetScene(SCENE.NEXT_BADGE), m_difficultyManager, m_gameStateManager, AD_TYPE.INTERSTITIAL, this);
		
		// Always register parent as a scene listener.
		scene.RegisterSceneListener(m_sceneListener);
		m_sceneListener.HandleAdVisibility(AD_TYPE.INTERSTITIAL);
		SoundManager.StopMusic();
        SoundManager.PlaySound("PROMOTION", false);
		
		System.gc();	// Clean up

		m_queuedScene = scene;
	}	

    private void LevelDesign() {
        LevelDesign scene = new LevelDesign(m_display, m_spriteSheetManager, m_realTimer, m_gameTimer, m_context);
        scene.Initialize(GetScene(SCENE.LEVEL_DESIGN), m_difficultyManager, m_gameStateManager, AD_TYPE.NONE, this);

        // Always register parent as a scene listener.
        scene.RegisterSceneListener(m_sceneListener);
        m_sceneListener.HandleAdVisibility(AD_TYPE.NONE);

        System.gc();	// Clean up
        m_queuedScene = scene;
    }

	private void NextFreeLifeScreen(long _currentScore, long _nextLifeScore) {
		NextFreeMan scene = new NextFreeMan(_currentScore, _nextLifeScore, m_display, m_spriteSheetManager, m_realTimer, m_gameTimer, m_context);
		scene.Initialize(GetScene(SCENE.NEXT_LIFE), m_difficultyManager, m_gameStateManager, AD_TYPE.INTERSTITIAL, this);
		
		// Always register parent as a scene listener.
		scene.RegisterSceneListener(m_sceneListener);
		m_sceneListener.HandleAdVisibility(AD_TYPE.INTERSTITIAL);
		SoundManager.StopMusic();
        SoundManager.PlaySound("PROMOTION", false);
		
		System.gc();	// Clean up

		m_queuedScene = scene;
	}
	
	// Clears all scenes out of memory.
	private void ClearScenes() {
		m_scenes = new HashMap<String, SceneConfig>();
	}
	
	// Load the scene XML file.
	private void Load(Context _context, int _configurationResourceId) throws Exception {
		TreeMap<String, SceneItemConfig> sceneItems = null;
		
		// Scene Variables
		String sceneName = "";
		String backgroundColor = "";
		List<String> textures = new ArrayList<String>();

		// Setup the XML reader.
		String configData = TextResourceReader.ReadTextFileFromResource(_context, _configurationResourceId);		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(configData));

		int eventType = xpp.getEventType();
		String elementName = null;

		while (eventType != XmlPullParser.END_DOCUMENT) {

			if (eventType == XmlPullParser.START_TAG) {
				elementName = xpp.getName();

				if (elementName.equals("Scene")) {
					// Set scene defaults.
					sceneName = "";
					backgroundColor = "WHITE";
					sceneItems = new TreeMap<String, SceneItemConfig>();
					textures = new ArrayList<String>();
				} else if (elementName.equals("Name")) {
					sceneName = xpp.nextText();
				} else if (elementName.equals("BackgroundColor")) {
					backgroundColor = xpp.nextText();				
				} else if (elementName.equals("SceneItems")) {
					sceneItems = GetChildSceneItems(xpp, 1, null);
				} else if (elementName.equals("Texture")) {
					textures.add(xpp.nextText());
				}
			}

			if (eventType == XmlPullParser.END_TAG) {
				elementName = xpp.getName();

				if (elementName.equals("Scene")) {

					SceneConfig sceneConfig = new SceneConfig(sceneName, backgroundColor, sceneItems, textures);					
					DumpSceneMap(sceneConfig);

					m_scenes.put(sceneName, sceneConfig);
				}
			}

			eventType = xpp.next();
		}							
	}
	
	private TreeMap<String, SceneItemConfig> GetChildSceneItems(XmlPullParser _xpp, int _level, SceneItemConfig _parent)
			throws Exception {

		TreeMap<String, SceneItemConfig> _sceneItems = new TreeMap<String, SceneItemConfig>();
		
		// Scene Item initialization Values
		String sceneItemType = "";
		String sceneItemTag = "";
        String sceneItemButtonAnimationTag = "";
		String sceneItemAnimationTag = "";
		float sceneItemHeight = 0.0f;
		float sceneItemWidth = 0.0f;
		int sceneItemZBufferIndex = 99;
		boolean sceneItemEnabled = true;				
		String sceneAnchor = "";		
		String sceneLeftMargin = "";
		String sceneRightMargin = "";
		String sceneTopMargin = "";
		String sceneBottomMargin = "";
        String zBufferName = "";
        DeviceDisplay.ZPOSITION zPosition = DeviceDisplay.ZPOSITION.UNKNOWN;
        int ordinal = 1;
		
		boolean isAlreadyBuilt = false;
		SceneItemConfig previousSceneItem = null;
		SceneItemConfig currentSceneItem = null;		
		
		// XML Parser variables
		String elementName = "";
		int eventType = _xpp.next(); 

		while (eventType != XmlPullParser.END_DOCUMENT) {

			if (eventType == XmlPullParser.START_TAG) {
				elementName = _xpp.getName();

				// If we hit new scene items then that means we're starting some nested processing.  We need to build the parent
				// and then recursively call this procedure.
				if (elementName.equals("SceneItems")) 				
				{		
					Margin sceneMargins = new Margin(sceneLeftMargin, sceneRightMargin, sceneTopMargin, sceneBottomMargin);
					currentSceneItem = new SceneItemConfig(
							sceneItemType, sceneItemTag, sceneItemAnimationTag, sceneItemButtonAnimationTag,
							sceneAnchor, sceneMargins, sceneItemHeight,
							sceneItemWidth, sceneItemZBufferIndex,
							sceneItemEnabled, _level, ordinal,
							m_display, previousSceneItem, _parent);
					
					// Put the completed scene item into the return value.
					TreeMap<String, SceneItemConfig> children = GetChildSceneItems(_xpp, _level + 1, currentSceneItem);

                    currentSceneItem.SetChildren(children);
					
					previousSceneItem = currentSceneItem;
					
					_sceneItems.put(currentSceneItem.GetLevelOrdinal(), currentSceneItem);					

					isAlreadyBuilt = true;
				}
				
				if (elementName.equals("SceneItem")) {
					// Reset Scene Item Variables
					sceneItemType = "";
					sceneItemTag = "";
					sceneItemAnimationTag = "";
                    sceneItemButtonAnimationTag = "";
					sceneItemHeight = 0.0f;
					sceneItemWidth = 0.0f;
					sceneItemZBufferIndex = 99;
					sceneItemEnabled = true;
					sceneAnchor = "";		
					sceneLeftMargin = "";
					sceneRightMargin = "";
					sceneTopMargin = "";
					sceneBottomMargin = "";
				} else if (elementName.equals("Type")) {
					sceneItemType = _xpp.nextText();
				} else if (elementName.equals("Tag")) {
					sceneItemTag = _xpp.nextText();
				} else if (elementName.equals("AnimationTag")) {
					sceneItemAnimationTag = _xpp.nextText();
				} else if (elementName.equals("ButtonAnimationTag")) {
                    sceneItemButtonAnimationTag = _xpp.nextText();
                } else if (elementName.equals("Anchor")) {
					sceneAnchor = _xpp.nextText();
				} else if (elementName.equals("Height")) {
					String text = _xpp.nextText();
					
					if(text.contains("%")) {
						float percentage = Float.parseFloat(text.replace("%", "")) / 100.0f;
						sceneItemHeight = (m_display.GetAspectRatioY() * 2.0f) * percentage;
					} else {
						sceneItemHeight = Float.parseFloat(text);
					}
				} else if (elementName.equals("Width")) {
					String text = _xpp.nextText();
					
					if(text.contains("%")) {
						float percentage = Float.parseFloat(text.replace("%", "")) / 100.0f;
						sceneItemWidth = (m_display.GetAspectRatioX() * 2.0f) * percentage;
					} else {												
						sceneItemWidth = Float.parseFloat(text);
					}															
				} else if (elementName.equals("ZBufferIndex")) {
                    zBufferName = _xpp.nextText();
                    zPosition = DeviceDisplay.ZPOSITION.fromString(zBufferName);
                    if ( zPosition == DeviceDisplay.ZPOSITION.UNKNOWN ) {
                        throw new RuntimeException("Unknown zPosition of " + zBufferName + "!!!");
                    } else {
                        sceneItemZBufferIndex = zPosition.getValue();
                    }
					//sceneItemZBufferIndex = Integer.parseInt(_xpp.nextText());
				} else if (elementName.equals("Enabled")) {
					sceneItemEnabled = Boolean.parseBoolean(_xpp.nextText());
				} else if (elementName.equals("Margin-Left")) {
					sceneLeftMargin = _xpp.nextText();
				} else if (elementName.equals("Margin-Right")) {
					sceneRightMargin = _xpp.nextText();
				} else if (elementName.equals("Margin-Top")) {
					sceneTopMargin = _xpp.nextText();
				} else if (elementName.equals("Margin-Bottom")) {
					sceneBottomMargin = _xpp.nextText();
				}
			} // end of Start Tag Check.

			if (eventType == XmlPullParser.END_TAG) {
				elementName = _xpp.getName();
				
				if (elementName.equals("SceneItems")) {
					break;	// We've got all of the scene items at this level.
				}
				
				if (elementName.equals("SceneItem")) {
					if(!isAlreadyBuilt) {						
						Margin sceneMargins = new Margin(sceneLeftMargin, sceneRightMargin, sceneTopMargin, sceneBottomMargin);
						SceneItemConfig sceneItem = new SceneItemConfig(
								sceneItemType, sceneItemTag, sceneItemAnimationTag, sceneItemButtonAnimationTag,
								sceneAnchor, sceneMargins, sceneItemHeight,
								sceneItemWidth, sceneItemZBufferIndex,
								sceneItemEnabled, _level, ordinal,
								m_display, previousSceneItem, _parent);					
						
						previousSceneItem = sceneItem;
						
						// Put the completed scene item into the return value.
						_sceneItems.put(sceneItem.GetLevelOrdinal(), sceneItem);												
					} else {
						isAlreadyBuilt = false;	// Reset flag. 					
					}
					ordinal++;					
				} // End of Scene Item
			} // End of end tag check.

			eventType = _xpp.next();
		} // End of loop
		
		return _sceneItems;
	}

	private SceneConfig GetScene(SCENE _sceneName) {
		if(m_scenes.size() == 0) {
			throw new RuntimeException("Attempting to retrieve a scene but no scenes have been loaded!");
		}
		
		SceneConfig sceneConfig = m_scenes.get(_sceneName.toString());
		if(sceneConfig == null) {
			throw new RuntimeException("Attempted to load scene " + _sceneName.toString() + " but the scene was not loaded!");
		}
		
		return m_scenes.get(_sceneName.toString());
	}

	// Private Methods
	private void DumpSceneMap(SceneConfig _sceneConfig) throws IOException {
		String sceneText = "**************************************************************************\n"
				+ _sceneConfig.GetName()
				+ "\n"
				+ String.valueOf(_sceneConfig.GetSceneItems().size())
				+ "\n"
				+ "**************************************************************************";
		Log.w("SCENE", sceneText);

		if (_sceneConfig.GetSceneItems() != null) {
			for (String zKey : _sceneConfig.GetSceneItems().keySet()) {
				DumpSceneItem(_sceneConfig.GetSceneItems().get(zKey));
			}
		}

	}

	private void DumpSceneItem(SceneItemConfig _sceneItemConfig) {
		int paddingLength = (_sceneItemConfig.GetLevel() - 1) * 5;
		String sceneItemText = GetPadding(paddingLength)
				+ _sceneItemConfig.GetTag() + "("
				+ _sceneItemConfig.GetLevelOrdinal() + "): "
				+ _sceneItemConfig.GetType().toString() + ", "
				+ _sceneItemConfig.GetAnimationTag() + ", " + "x="
				+ String.valueOf(_sceneItemConfig.GetX()) + ", y="
				+ String.valueOf(_sceneItemConfig.GetY()) + ", height="
				+ String.valueOf(_sceneItemConfig.GetHeight()) + ", width="
				+ String.valueOf(_sceneItemConfig.GetWidth()) + ", z="
				+ String.valueOf(_sceneItemConfig.GetZBufferIndex());
		
		if(_sceneItemConfig.GetChildren() != null) {
			for (String zKey : _sceneItemConfig.GetChildren().keySet()) {
				DumpSceneItem(_sceneItemConfig.GetChildren().get(zKey));
			}
		}

		Log.w("SCENE", sceneItemText);
	}
	

	private String GetPadding(int _chars) {
		if (_chars == 0) {
			return "";
		} else {
			return String.format("%0" + _chars + "d", 0).replace("0", " ");
		}
	}
	
}
