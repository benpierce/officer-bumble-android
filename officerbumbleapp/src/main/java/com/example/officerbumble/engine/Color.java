package com.example.officerbumble.engine;

public class Color {
	
	// Color constants
	public static final Color LIGHT_GREEN = new Color(0.0f, 0.5f, 0.0f, 0.0f);
	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 0.0f);
	public static final Color PINK = new Color(1.0f, 0.55f, 0.55f, 0.0f);
	
	private float m_red;
	private float m_green;
	private float m_blue;
	private float m_alpha;
	
	public Color(float _red, float _green, float _blue, float _alpha) {
		m_red = _red;
		m_green = _green;
		m_blue = _blue;
		m_alpha = _alpha;
	}
	
	public static Color GetColor(String _color) {
		Color color;
		
		if (_color.toUpperCase().equals("WHITE")) {
			color = Color.WHITE;
		} else if (_color.toUpperCase().equals("PINK")) {
            color = Color.PINK;
        } else if (_color.toUpperCase().equals("LIGHT_GREEN")) {
			color = Color.LIGHT_GREEN;
		} else {
			throw new RuntimeException("Unknown color conversion from color " + _color + "!");
		}
		
		return color;
	}
	
	public float GetRed() {
		return m_red;
	}
	
	public float GetGreen() {
		return m_green;
	}
	
	public float GetBlue() {
		return m_blue;
	}
	
	public float GetAlpha() {
		return m_alpha;
	}
	
}
