package com.example.officerbumble.interfaces;

public interface BumbleListener {
	public void HandleBumbleDuckBegin(float _currentTimeMilliseconds);
	public void HandleBumbleDuckEnd(float _currentTimeMilliseconds);
	public void HandleBumbleJumpBegin(float _currentTimeMilliseconds);
	public void HandleBumbleJumpEnd(float _currentTimeMilliseconds);
	public void HandleBumbleDead(float _currentTimeMilliseconds);
	public void HandleBumbleBackUp(float _currentTimeMilliseconds);
}
