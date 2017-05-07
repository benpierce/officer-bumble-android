package com.example.officerbumble.engine;

import com.example.officerbumble.scene.SceneManager;
import com.example.officerbumble.scene.SceneManager.SCENE;
import java.util.Random;

public class GameStateManager {
	public enum DIFFICULTY {
        EASY,
        NORMAL,
        HARD,
        HARDCORE
    }

    // Usually this can be a field rather than a method variable
    Random rand = new Random();

    private boolean m_showTutorial = true;
	private int m_level;
	private long m_score;
	private int m_frameRate;
	private DIFFICULTY m_difficulty = DIFFICULTY.EASY;	
	private int m_remainingLives;
	private DifficultyManager m_difficultyManager;
	private DifficultyConfig m_difficultyConfig;
    private SCENE m_currentScene;
			
	public GameStateManager(DifficultyManager _difficultyManager) {
		m_difficultyManager = _difficultyManager;
		m_difficultyConfig = m_difficultyManager.GetDifficultyConfig(m_difficulty.toString());		
	}	
	
	public void StartNewGame(DIFFICULTY _difficulty, SceneManager _sceneManager) {
		m_difficulty = _difficulty;
		m_difficultyConfig = m_difficultyManager.GetDifficultyConfig(m_difficulty.toString());
		
		RestartGame(_sceneManager);
	}
	
	public void RestartGame(SceneManager _sceneManager) {
		m_remainingLives = m_difficultyConfig.GetStartingLives();
		m_score = 0;		
		m_frameRate = 0;
		m_level = 1;
        m_showTutorial = true;

        m_currentScene = SCENE.SHOPPING_MALL;
        //m_currentScene = SCENE.MUSEUM;
        _sceneManager.QueueScene(m_currentScene, false);
	}

	public void LevelLost(SceneManager _sceneManager) {
        m_showTutorial = false;

		// On easy or normal you get to continue.
		if(m_difficulty == DIFFICULTY.HARDCORE) {
            _sceneManager.QueueScene(SCENE.GAME_OVER_HARDCORE, true);
		} else {
			m_remainingLives--;
			
			// If we're out of lives, then show game over screen.
			// else Now we can show an ad/retry screen.
			if(m_remainingLives == 0) {
				_sceneManager.QueueScene(SCENE.GAME_OVER, true);
			} else {
                _sceneManager.QueueScene(m_currentScene, true);
            }
		}
	}

	public void LevelWon(SceneManager _sceneManager) {
		m_level++;
        m_showTutorial = false;

        m_currentScene = GetNextScene();

        _sceneManager.QueueScene(m_currentScene, true);
	}

    // Will return a different level than the one we're currently on.
    private SCENE GetNextScene() {
        int randomNumber = 0;
        SCENE newScene = m_currentScene;

        while ( m_currentScene == newScene ) {
            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            randomNumber = rand.nextInt((4 - 1) + 1) + 1;

            switch (randomNumber) {
                case 1:
                    newScene = SCENE.SHOPPING_MALL;
                    break;
                case 2:
                    newScene = SCENE.MUSEUM;
                    break;
                case 3:
                    newScene = SCENE.BANK;
                    break;
                case 4:
                    newScene = SCENE.CASINO;
                    break;
                default:
                    newScene = m_currentScene;
                    break;
            }
        }

        return newScene;
    }
	
	public boolean IncrementScore(long _incrementBy) {
		boolean freelife = false;		
		
		long nextFreeLife = (m_score / m_difficultyConfig.GetFreeLifeScore()) * m_difficultyConfig.GetFreeLifeScore() + m_difficultyConfig.GetFreeLifeScore();				
		
		boolean freeLifeCandidate = (m_score < nextFreeLife && m_score + _incrementBy >= nextFreeLife) ? true : false;  
		m_score += _incrementBy;
				
		if (freeLifeCandidate && m_remainingLives < m_difficultyConfig.GetMaxLives()) {
			freelife = true;
			m_remainingLives++;
		}		
		
		return freelife;
	}					
	
	public int GetRemainingLives() {
		return m_remainingLives;
	}
	
	public DIFFICULTY GetDifficulty() {
		return m_difficulty;
	}
	
	public int GetLevel() {
		return m_level;
	}
	
	public long GetScore() {
		return m_score;
	}
		
	public void SetFrameRate(int _frameRate) {
		m_frameRate = _frameRate;
	}
	
	public int GetFrameRate() {
		return m_frameRate;
	}

    public boolean ShowTutorial() { return m_showTutorial; }
	
}
