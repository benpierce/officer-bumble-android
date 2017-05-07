package com.example.officerbumble.interfaces;

public interface TouchReleaseListener {
	public void HandleTouchRelease(boolean isPrimary, float normalizedX, float normalizedY, float realTimeMilliseconds, float gameTimeMilliseconds);
}