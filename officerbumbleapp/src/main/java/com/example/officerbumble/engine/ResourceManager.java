package com.example.officerbumble.engine;
import com.example.officerbumble.R;
import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class ResourceManager {	
	private static ResourceManager instance = null;
	
	// Resources
	private DifficultyManager m_difficultyManager;		
	private SpriteSheetManager m_spriteSheetManager = null;
	private BadgeManager m_badgeManager = null;
			
	private ResourceManager() {
		if (instance != null) {	             
			throw new IllegalStateException("Already instantiated");
	    }
	}

	public static ResourceManager getInstance() {
		// Creating only  when required.
		if (instance == null) {
			instance = new ResourceManager();
	    }
	    
		return instance;	    
	}
	
	public void LoadBasicResources(Context _context) {		
		LoadAnimationConfiguration(_context, R.raw.animationconfigsplashscreen);		
	}
	
	public void LoadResources(Context _context) {
        long startTime = System.nanoTime();
        LoadDifficultyConfig(_context, R.raw.difficultyconfig);
        long loadTime = System.nanoTime() - startTime;
        Log.w("LOAD", "Load Difficulty Config is " + TimeUnit.MILLISECONDS.convert(loadTime, TimeUnit.NANOSECONDS));
        startTime = System.nanoTime();

		LoadSounds(_context);
        loadTime = System.nanoTime() - startTime;
        Log.w("LOAD", "Load Sounds is " + TimeUnit.MILLISECONDS.convert(loadTime, TimeUnit.NANOSECONDS));
        startTime = System.nanoTime();

		LoadAnimationConfiguration(_context, R.raw.animationconfig);
        loadTime = System.nanoTime() - startTime;
        Log.w("LOAD", "Load AnimConfig1 is " + TimeUnit.MILLISECONDS.convert(loadTime, TimeUnit.NANOSECONDS));
        startTime = System.nanoTime();

		LoadAnimationConfiguration(_context, R.raw.animationletters);
        loadTime = System.nanoTime() - startTime;
        Log.w("LOAD", "Load AnimConfig2 Config is " + TimeUnit.MILLISECONDS.convert(loadTime, TimeUnit.NANOSECONDS));
        startTime = System.nanoTime();

		LoadBadgeManager(_context);
        loadTime = System.nanoTime() - startTime;
        Log.w("LOAD", "Load Badge Manager is " + TimeUnit.MILLISECONDS.convert(loadTime, TimeUnit.NANOSECONDS));
        startTime = System.nanoTime();
	}
	
	private void LoadBadgeManager(Context _context) {
		if(m_badgeManager == null) {
			try {
				m_badgeManager = new BadgeManager();
				m_badgeManager.Load(_context, R.raw.badgeconfig);				
			} catch(Exception ex) {
				Log.w("GameRenderer", "Unable to load the badge configuration file! " + ex.getMessage());
			}
		}					
	}
		
	private void LoadDifficultyConfig(Context _context, int _resourceId) {
		if(m_difficultyManager == null) {
			try {
				m_difficultyManager = new DifficultyManager();
				m_difficultyManager.Load(_context, R.raw.difficultyconfig);				
			} catch(Exception ex) {
				Log.w("GameRenderer", "Unable to load the difficulty configuration file! " + ex.getMessage());
			}
		}			
	}
		
	private void LoadAnimationConfiguration(Context _context, int _resourceId) {
		if(m_spriteSheetManager == null) {
			m_spriteSheetManager = new SpriteSheetManager(_context);						
		}					
		
		try {
			m_spriteSheetManager.LoadConfiguration(_resourceId);
		} catch(Exception ex) {
			Log.e("GameRenderer", "Unable to load the animation configuration file! " + ex.getMessage());
		}															
	}
			
	private void LoadSounds(final Context _context) {
        new Thread(new Runnable() {
            public void run() {
                // Load Sounds
                SoundManager.LoadSound("FAILURE", _context, R.raw.failure, 1);
                SoundManager.LoadSound("PROMOTION", _context, R.raw.promotion, 1);
                SoundManager.LoadSound("FREE LIFE", _context, R.raw.freeman, 1);
                SoundManager.LoadSound("BUTTON CLICK", _context, R.raw.button, 1);
                SoundManager.LoadSound("BUMBLE HIT", _context, R.raw.bowling, 1);
                SoundManager.LoadSound("BUMBLE JUMP", _context, R.raw.jump, 1);
                SoundManager.LoadSound("SECOND WIND", _context, R.raw.secondwind, 1);
                SoundManager.LoadSound("SCRAMBLE", _context, R.raw.scramble, 1);
                SoundManager.LoadSound("ROBOT SHOOT", _context, R.raw.robotshoot, 1);
                SoundManager.LoadSound("ROBOT THROW", _context, R.raw.robotthrow, 1);
                SoundManager.LoadSound("DUCK", _context, R.raw.duck, 1);
                SoundManager.LoadSound("YIKES", _context, R.raw.yikes, 1);
                SoundManager.LoadSound("HEAD SHAKE", _context, R.raw.headshake, 1);
                SoundManager.LoadSound("CHICKEN", _context, R.raw.chicken, 1);
                SoundManager.LoadSound("CRIMINAL ESCAPE", _context, R.raw.criminalescape, 1);
                SoundManager.LoadSound("ESCALATOR", _context, R.raw.escalator, 1);
                SoundManager.LoadSound("WINK", _context, R.raw.wink, 1);
                SoundManager.LoadSound("BUMBLE SPLAT", _context, R.raw.splat, 1);
            }
        }).start();
	}
		
	public SpriteSheetManager GetSpriteSheetManager() {
		return m_spriteSheetManager;
	}
	
	public BadgeManager GetBadgeManager() {
		return m_badgeManager;
	}
	
	public DifficultyManager GetDifficultyManager() {
		return m_difficultyManager;
	}	
}
