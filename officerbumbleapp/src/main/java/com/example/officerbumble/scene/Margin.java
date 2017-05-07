package com.example.officerbumble.scene;

public class Margin {
	private float m_leftMarginPercent = 0.0f;
	private float m_rightMarginPercent = 0.0f;
	private float m_topMarginPercent = 0.0f;
	private float m_bottomMarginPercent = 0.0f;
	private float m_leftMarginAbsolute = 0.0f;
	private float m_rightMarginAbsolute = 0.0f;
	private float m_topMarginAbsolute = 0.0f;
	private float m_bottomMarginAbsolute = 0.0f;
	
	public Margin(String _leftMargin, String _rightMargin, String _topMargin, String _bottomMargin) {
		if(_leftMargin.contains("%")) {
			m_leftMarginPercent = Float.parseFloat(_leftMargin.replace("%", ""));
		} else if (_leftMargin.length() > 0) {
			m_leftMarginAbsolute = Float.parseFloat(_leftMargin);
		}

		if(_rightMargin.contains("%")) {
			m_rightMarginPercent = Float.parseFloat(_rightMargin.replace("%", ""));
		} else if (_rightMargin.length() > 0) {
			m_rightMarginAbsolute = Float.parseFloat(_rightMargin);
		}
		
		if(_topMargin.contains("%")) {
			m_topMarginPercent = Float.parseFloat(_topMargin.replace("%", ""));
		} else if (_topMargin.length() > 0) {
			m_topMarginAbsolute = Float.parseFloat(_topMargin);
		}
		
		if(_bottomMargin.contains("%")) {
			m_bottomMarginPercent = Float.parseFloat(_bottomMargin.replace("%", ""));
		} else if (_bottomMargin.length() > 0) {
			m_bottomMarginAbsolute = Float.parseFloat(_bottomMargin);
		}
	}
	
	public float Left(float _parentWidth) {
		float result = 0.0f;
		
		if(m_leftMarginPercent != 0) {
			result = _parentWidth * (m_leftMarginPercent / 100.0f);
		}
		
		if(m_leftMarginAbsolute != 0) {
			result = m_leftMarginAbsolute;
		}
		
		return result;
	}
	
	public float Right(float _parentWidth) {
		float result = 0.0f;
		
		if(m_rightMarginPercent != 0) {
			result = _parentWidth * (m_rightMarginPercent / 100.0f);
		}
		
		if(m_rightMarginAbsolute != 0) {
			result = m_rightMarginAbsolute;
		}
		
		return result;				
	}
	
	public float Top(float _parentHeight) {
		float result = 0.0f;
		
		if(m_topMarginPercent != 0) {
			result = _parentHeight * (m_topMarginPercent / 100.0f);
		}
		
		if(m_topMarginAbsolute != 0) {
			result = m_topMarginAbsolute;
		}
		
		return result;
	}
	
	public float Bottom(float _parentHeight) {
		float result = 0.0f;
		
		if(m_bottomMarginPercent != 0) {
			result = _parentHeight * (m_bottomMarginPercent / 100.0f);
		}
		
		if(m_bottomMarginAbsolute != 0) {
			result = m_bottomMarginAbsolute;
		}
		
		return result;				
	}
}
