package com.example.officerbumble.gameentities;

public class FontProperty {
	
	private String m_character = "";
	private String m_animationTag = "";
	private int m_pixelsBefore = 0;
	private int m_pixelsActual = 0;
	
	public FontProperty(String _character, String _animationTag, int _pixelsBefore, int _pixelsActual) {
		m_character = _character;
		m_animationTag = _animationTag;
		m_pixelsBefore = _pixelsBefore;
		m_pixelsActual = _pixelsActual;
	}
	
	public String GetCharacter() {
		return m_character;
	}
	
	public String GetAnimationTag() {
		return m_animationTag;		
	}
	
	public int GetPixelsBefore() {
		return m_pixelsBefore;
	}
	
	public int GetPixelsActual() {
		return m_pixelsActual;
	}
}
