package com.example.officerbumble.engine;

public class TexturedPoint {
	private float m_x; 
	private float m_y;
	private float m_textureX;
	private float m_textureY;
	
	public TexturedPoint(float _x, float _y, float _textureX, float _textureY) {
		m_x = _x;
		m_y = _y;
		m_textureX = _textureX;
		m_textureY = _textureY;
	}
	
	public float GetX() {
		return m_x;
	}
	
	public float GetY() {
		return m_y;
	}
	
	public float GetTextureX() {
		return m_textureX;
	}
	
	public float GetTextureY() {
		return m_textureY;
	}
}
