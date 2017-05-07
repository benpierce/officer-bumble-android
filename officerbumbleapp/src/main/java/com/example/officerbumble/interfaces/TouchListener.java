package com.example.officerbumble.interfaces;

public interface TouchListener {
	public boolean HandleTouch(boolean isPrimary, float normalizedX, float normalizedY, float realTimeMilliseconds, float gameTimeMilliseconds);
}
