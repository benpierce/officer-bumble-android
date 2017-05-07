package com.example.officerbumble.gameentities;

import android.util.Log;
import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.EfficientSpriteArray;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.SpriteManager;

public class TextArea {
	//private List<Sprite> m_characters = new ArrayList<Sprite>();	
	private EfficientSpriteArray m_characters;
	private static final String SPACE_CHARACTER = " ";
	
	public enum FONT_TYPE {
		CARTOON
	}

	private float m_x;
	private float m_y;
	private float m_height;
	private float m_width;
	private float m_parentWidth;
	private float m_letterWidth;
	private float m_letterHeight;
	private FONT_TYPE m_type;
	private int m_zBufferIndex;
	private DeviceDisplay m_display;;
	private SpriteSheetManager m_spriteSheetManager;
	private String m_tag;
	private int m_textSize = 0;
	private Sprite[] m_characterBuffer;
			
	// x and y are normalized between -1 and 1 and represents the middle point.
	public TextArea(FONT_TYPE _type, float _x, float _y, float _letterHeight, float _letterWidth, float _parentWidth, int _zBufferIndex, float _creationTimeMilliseconds, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, int _size, String _textValue, String _tag)  {
		m_textSize = _size;
		
		m_characters = new EfficientSpriteArray(m_textSize);		
		m_type = _type;
		m_height = _letterHeight;
		m_width = 0.0f;		
		m_x = _x;
		m_y = _y;		
		m_parentWidth = _parentWidth;
		m_letterWidth = _letterWidth;
		m_letterHeight = _letterHeight;
		m_zBufferIndex = _zBufferIndex; 
		m_display = _display; 
		m_spriteSheetManager = _spriteSheetManager; 
		m_tag = _tag;
		
		InitializeText(_creationTimeMilliseconds);
		SetText(_textValue, _creationTimeMilliseconds);		
	}
	
	public void UpdateText(String _textValue, float _currentTimeMilliseconds, SpriteManager _spriteManager) {
        //_spriteManager.RemoveSprites(m_characters.GetAll());
		SetText(_textValue, _currentTimeMilliseconds);
	}
	
	private void InitializeText(float _creationTimeMilliseconds) {
		FontProperty fontProperty = null;
		String animationTag = "";

		if(m_type == FONT_TYPE.CARTOON) {
			fontProperty = CartoonFont.GetFontProperty(SPACE_CHARACTER);
		}
		animationTag = fontProperty.GetAnimationTag();
		
		// Reserve memory for the characters.
		for(int i = 0; i < m_textSize; i++) {
			StaticImage letter = new StaticImage(0, 0, m_letterHeight, m_letterWidth, m_zBufferIndex, true, _creationTimeMilliseconds, m_display, m_spriteSheetManager, animationTag, m_tag + Integer.toString(i));
			m_characters.Add(letter);
		}		
	}
	
	private void SetText(String _textValue, float _currentTimeMilliseconds) {
		int totalChar = 0;
		float startX = m_x;	// X that we started at.				
		float currentX = m_x;
		float currentY = m_y;
		float maxX = m_x + m_parentWidth;
		float beforeAdjustment = 0;
		FontProperty fontProperty = null;
		float lineWidth = 0.0f;
		String animationTag = "";
		int currentCharacter = 0;
		int currentWord = 0;
		int totalWords = 0;
		String currentChar = "";
		
		if(_textValue.length() > m_textSize) {
			throw new RuntimeException("Text buffer has been overflowed with length " + m_textSize + " trying to set text of size " + _textValue.length());
		}
		
		m_characterBuffer = m_characters.GetAll();
		
		String[] words = _textValue.split(SPACE_CHARACTER);
		totalWords = words.length;
		for(String word : words) {
			currentWord++;
			
			// Calculate the word's width.
			float wordWidth = GetWordLength(m_type, word, m_letterWidth);	// 1 for the space.
			
			// Can the word fit on the rest of the line?
			if(currentX + wordWidth > maxX) {
				// New Line
				if(lineWidth > m_width) {
					m_width = lineWidth;
				}
				lineWidth = 0.0f;
				m_height+= m_letterHeight;
				currentY -= m_letterHeight;
				currentX = startX;
			} else {
				lineWidth += wordWidth;
			}
									
			// Write out the word and increment our positions.
			for(int character = 0; character < word.length(); character++) {
				totalChar++;	// We need a unique # to assign to the sprite tag.															
				currentChar = word.substring(character, character + 1);
				
				if(m_type == FONT_TYPE.CARTOON) {										
					fontProperty = CartoonFont.GetFontProperty(currentChar);
				}
				
				if(fontProperty != null) {
					animationTag = fontProperty.GetAnimationTag();
					beforeAdjustment = GetSpacingAdjustmentBefore(m_type, currentChar, m_letterWidth);
				}								
				
				try {
					float charLength = GetCharacterLength(m_type, currentChar, m_letterWidth);  

					currentX += beforeAdjustment;
															
					//StaticImage letter = new StaticImage(currentX, currentY, m_letterHeight, m_letterWidth, m_zBufferIndex, true, _currentTimeMilliseconds, m_display, m_soundManager, m_spriteSheetManager, animationTag, m_tag + Integer.toString(totalChar));
					m_characterBuffer[currentCharacter].MoveTo(_currentTimeMilliseconds, currentX, currentY);
					m_characterBuffer[currentCharacter].SetCurrentAnimation(animationTag, _currentTimeMilliseconds);
					currentCharacter++;						
					//m_characters.Add(letter);
					
					currentX += charLength;					
					
				} catch(Exception ex) {
					Log.w("DIFF", "Animation " + m_tag + " was not found!");
				}				
			}

			if(lineWidth > m_width) {
				m_width = lineWidth;
			}			
			
			// Always write a space.
			totalChar++;	// We need a unique # to assign to the sprite tag.
			
			// Only add the extra space if there's another word coming.
			if(currentWord != totalWords) {
				if(m_type == FONT_TYPE.CARTOON) {
					fontProperty = CartoonFont.GetFontProperty(SPACE_CHARACTER);
				}
	
				if(fontProperty != null) {
					animationTag = fontProperty.GetAnimationTag();
				}
				
				m_characterBuffer[currentCharacter].MoveTo(_currentTimeMilliseconds, currentX, currentY);
				m_characterBuffer[currentCharacter].SetCurrentAnimation(animationTag, _currentTimeMilliseconds);
				currentCharacter++;
				
				//StaticImage letter = new StaticImage(currentX, currentY, m_letterHeight, m_letterWidth, m_zBufferIndex, true, _currentTimeMilliseconds, m_display, m_soundManager, m_spriteSheetManager, animationTag, m_tag + Integer.toString(totalChar));			
				//m_characters.Add(letter);
				
				currentX += GetCharacterLength(m_type, SPACE_CHARACTER, m_letterWidth);	// The space
			}
					
		}	// End of for loop.	
		
		// Blank out any final characters.
		if(m_type == FONT_TYPE.CARTOON) {
			fontProperty = CartoonFont.GetFontProperty(SPACE_CHARACTER);
		}

		if(fontProperty != null) {
			animationTag = fontProperty.GetAnimationTag();
		}

		for(int i = currentCharacter; i < m_textSize; i++) {
			m_characterBuffer[currentCharacter].SetCurrentAnimation(animationTag, _currentTimeMilliseconds);
			currentCharacter++;
		}
	}
	
	public static float CalculateHeight(FONT_TYPE _type, float _parentWidth, float _letterWidth, float _letterHeight, String _textValue) {
		FontProperty fontProperty = null;
		float currentX = 0.0f;
		float height = _letterHeight;
		float beforeAdjustment = 0;		
		String currentChar = "";
		
		String[] words = _textValue.split(SPACE_CHARACTER);
		for(String word : words) {
			
			// Calculate the word's width.
			float wordWidth = GetWordLength(_type, word, _letterWidth);	// 1 for the space.
			
			// Can the word fit on the rest of the line?
			if(currentX + wordWidth > _parentWidth) {
				height += _letterHeight;
				currentX = 0.0f;
			}
									
			// Write out the word and increment our positions.
			for(int character = 0; character < word.length(); character++) {
				currentChar = word.substring(character, character + 1);
				
				if(_type == FONT_TYPE.CARTOON) {
					fontProperty = CartoonFont.GetFontProperty(currentChar);
				}
				
				if(fontProperty != null) {
					beforeAdjustment = GetSpacingAdjustmentBefore(_type, currentChar, _letterWidth);
				}
				
				float charLength = GetCharacterLength(_type, currentChar, _letterWidth);  

				currentX += beforeAdjustment;					
				currentX += charLength;										
			}					
		}
			
		return height;				
	}
			
	// Calculates the actual width of the textarea.
	public static float CalculateWidth(FONT_TYPE _type, float _parentWidth, float _letterWidth, String _textValue) {
		FontProperty fontProperty = null;
		float result = 0.0f;
		float currentX = 0.0f;
		float lineWidth = 0.0f;		
		float beforeAdjustment = 0;		
		String currentChar = "";
		
		String[] words = _textValue.split(SPACE_CHARACTER);
		for(String word : words) {
			
			// Calculate the word's width.
			float wordWidth = GetWordLength(_type, word, _letterWidth);	// 1 for the space.
			
			// Can the word fit on the rest of the line?
			if(currentX + wordWidth > _parentWidth) {
				// New Line
				if(lineWidth > _parentWidth) {
					result = lineWidth;
				}
				lineWidth = 0.0f;
				currentX = 0.0f;
			} else {
				lineWidth += wordWidth;
			}
									
			// Write out the word and increment our positions.
			for(int character = 0; character < word.length(); character++) {
				currentChar = word.substring(character, character + 1);
				
				if(_type == FONT_TYPE.CARTOON) {
					fontProperty = CartoonFont.GetFontProperty(currentChar);
				}
				
				if(fontProperty != null) {
					beforeAdjustment = GetSpacingAdjustmentBefore(_type, currentChar, _letterWidth);
				}
				
				float charLength = GetCharacterLength(_type, currentChar, _letterWidth);  

				currentX += beforeAdjustment;					
				currentX += charLength;										
			}

			if(lineWidth > result) {
				result = lineWidth;
			}			
		}
			
		return result;		
	}
	
	private static float GetSpacingAdjustmentAfter(FONT_TYPE _type, String _character, float _letterWidth) {
		FontProperty fontProperty = null;
		float result = 0;
		
		switch(_type) {
			case CARTOON : 
				fontProperty = CartoonFont.GetFontProperty(_character);
				break;
		}
		
		if(fontProperty != null) {
			// What percentage of the natural pixels size is it shrunk by?
			float reduceBy = fontProperty.GetPixelsActual() / 64.0f;
			result = _letterWidth * reduceBy;
		} else {
			result = 0.0f;
		}
		
		return result;				
	}
	
	private static float GetSpacingAdjustmentBefore(FONT_TYPE _type, String _character, float _letterWidth) {
		FontProperty fontProperty = null;
		float result = 0;
		
		switch(_type) {
			case CARTOON : 
				fontProperty = CartoonFont.GetFontProperty(_character);
				break;
		}
		
		if(fontProperty != null) {
			// What percentage of the natural pixels size is it shrunk by?
			float reduceBy = fontProperty.GetPixelsBefore() / 64.0f;
			result = _letterWidth * reduceBy;
		} else {
			result = 0.0f;
		}
		
		return result;		
	}
			
	private static float GetCharacterLength(FONT_TYPE _type, String _character, float _letterWidth) {
		FontProperty fontProperty = null;
		float result = 0f;

		switch(_type) {
			case CARTOON : 
				fontProperty = CartoonFont.GetFontProperty(_character);
				break;
		}
		
		if(fontProperty != null) {
			result+= GetSpacingAdjustmentAfter(_type, _character, _letterWidth);
		}			
		
		return result;
	}
	
	private static float GetWordLength(FONT_TYPE _type, String _text, float _letterWidth) {
		float result = 0f;
		
		for(int character = 0; character < _text.length(); character++) {
			result += GetCharacterLength(_type, _text.substring(character, character + 1), _letterWidth);
		}

		result += GetCharacterLength(_type, SPACE_CHARACTER, _letterWidth);
		
		return result;
	}
	
	public Sprite[] GetSprites() {
		return m_characters.GetAll();
	}
	
	public float GetX() {
		return m_x;
	}
	
	public float GetY() {
		return m_y;
	}
	
	public float GetWidth() {
		return m_width;
	}
	
	public float GetHeight() {
		return m_height;
	}

}
