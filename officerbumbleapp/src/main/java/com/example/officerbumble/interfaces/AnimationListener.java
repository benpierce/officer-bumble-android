package com.example.officerbumble.interfaces;

public interface AnimationListener {
	public void HandleAnimationFinished(String _tag, float _currentTimeMilliseconds);
	public void HandleAnimationEvent(float _currentTimeMilliseconds);
}
