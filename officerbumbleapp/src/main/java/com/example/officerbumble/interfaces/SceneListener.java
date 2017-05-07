package com.example.officerbumble.interfaces;

import com.example.officerbumble.MainActivity.AD_TYPE;
import com.example.officerbumble.engine.BadgeManager.Badge;
import com.example.officerbumble.engine.GameStateManager.DIFFICULTY;

public interface SceneListener {
	public void HandleSceneLevelWon(float _currentTimeMilliseconds);
	public void HandleSceneLevelLost(float _currentTimeMilliseconds);
	public void HandleBadgeAwarded(Badge _badge, Badge _nextBadge);
	public void HandleNextBadge(int _criminalsCaught, Badge _nextBadge);
	public void HandleNextFreeMan(long _currentScore, long _nextLifeScore);
	public void HandleSceneDifficultySelected(DIFFICULTY _difficulty, float _currentTimeMilliseconds);
	public void HandleSceneQuitGame();
	public void HandleSceneQuitToTitle();
	public void HandleScoreIncrease(long _incrementBy);
	public void HandleInviteFriends();
	public void HandleRestartGame();
	public void HandleTrainingSelected();
	public void HandleAdVisibility(AD_TYPE _adType);
}
