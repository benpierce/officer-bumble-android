package com.example.officerbumble.engine;

public class Point2D {
	private float m_x = 0.0f;
	private float m_y = 0.0f;
	
	public Point2D(float _x, float _y) {
		m_x = _x;
		m_y = _y;
	}
		
	public float GetX() {
		return m_x;
	}
	
	public float GetY() {
		return m_y;
	}
	
	public void SetX(float _x) {
		m_x = _x;
	}
	
	public void SetY(float _y) {
		m_y = _y;
	}
}
