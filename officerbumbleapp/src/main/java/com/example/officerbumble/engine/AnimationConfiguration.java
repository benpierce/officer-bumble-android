package com.example.officerbumble.engine;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class AnimationConfiguration {
	private String m_animationName;
	private int m_frames;
	private int m_framesPerSecond;
	private boolean m_isLooping;
	private boolean m_stopOnLastFrame;
	private List<TextureCoordinate> m_textureCoordinates = new ArrayList<TextureCoordinate>();
	private int m_textureId;
	private int m_repeat = 1;	
	private boolean m_loadInverse = false;
	private boolean m_notifyWhenFinished = true; 
	private int m_eventFrame = -1;
	private float m_collisionBufferPercentX1 = 0.0f;
	private float m_collisionBufferPercentX2 = 0.0f;
	private float m_collisionBufferPercentY1 = 0.0f;
	private float m_collisionBufferPercentY2 = 0.0f;
	private String m_textureResourceName;
	
	public AnimationConfiguration(String _animationName, String _textureResourceName, int _textureId, float _textureSheetWidth, float _textureSheetHeight,
				float _textureWidth, float _textureHeight, float _startX, float _startY, int _frames, int _framesPerSecond, int _repeat, boolean _isLooping, boolean _stopOnLastFrame, boolean _loadInverse, boolean _notifyWhenFinished, int _eventFrame, float _collisionBufferPercentX1, float _collisionBufferPercentX2,
				float _collisionBufferPercentY1, float _collisionBufferPercentY2) {
				
		m_animationName = _animationName;
		m_textureResourceName = _textureResourceName;
		m_frames = _frames;
		m_framesPerSecond = _framesPerSecond;
		m_repeat = _repeat;
		m_isLooping = _isLooping;
		m_stopOnLastFrame = _stopOnLastFrame;
		m_textureId = _textureId;
		m_loadInverse = _loadInverse;
		m_notifyWhenFinished = _notifyWhenFinished;
		m_collisionBufferPercentX1 = _collisionBufferPercentX1;
		m_collisionBufferPercentX2 = _collisionBufferPercentX2;
		m_collisionBufferPercentY1 = _collisionBufferPercentY1;
		m_collisionBufferPercentY2 = _collisionBufferPercentY2;
		m_eventFrame = _eventFrame;
		
		LoadTextureCoordinates(_textureSheetWidth, _textureSheetHeight, _textureHeight, _textureWidth, _startX, _startY, _frames, _loadInverse);
	}
	
	public String GetTextureResourceName() {
		return m_textureResourceName;
	}
	
	public float GetCollisionBufferPercentX1() {
		return m_collisionBufferPercentX1;
	}
	
	public float GetCollisionBufferPercentX2() {
		return m_collisionBufferPercentX2;
	}
	
	public float GetCollisionBufferPercentY1() {
		return m_collisionBufferPercentY1;
	}
	
	public float GetCollisionBufferPercentY2() {
		return m_collisionBufferPercentY2;
	}	
	
	public String GetAnimationName() {
		return m_animationName;
	}
	
	public int GetFrames() {
		return m_frames;
	}
	
	public int GetEventFrame() {
		return m_eventFrame;
	}
	
	public int GetFramesPerSecond() {
		return m_framesPerSecond;
	}
	
	public boolean IsLooping() {
		return m_isLooping;
	}
	
	public boolean StopOnLastFrame() {
		return m_stopOnLastFrame;
	}
	
	public boolean NotifyWhenFinished() {
		return m_notifyWhenFinished;
	}
	
	public boolean IsAnimated() {
		return (m_frames > 1 ? true : false);
	}
	
	public List<TextureCoordinate> GetTextureCoordinates() {
		return m_textureCoordinates;
	}

	public int GetTextureId() {
		return m_textureId;
	}
	
	public void SetTextureId(int _textureId) {
		m_textureId = _textureId;
	}
	
	public int GetRepeat() {
		return m_repeat;
	}
	
	private void LoadTextureCoordinates(float _spriteSheetWidthPixels,float _spriteSheetHeightPixels, float _spriteHeightPixels, float _spriteWidthPixels,  
							float _startX, float _startY, int _frames, boolean _loadInverse) {
								
		// 1. Validate that the _spriteStartPixelX + spriteWidthPixels is not > spriteSheetWidthPixels.
		if((_startX - 1)  + _spriteWidthPixels > _spriteSheetWidthPixels) {			
			throw new RuntimeException("Attempted to load a spritesheet(" + m_animationName + ") from coordinate " + String.valueOf(_startX) + "x, but when the sprite width of " + String.valueOf(_spriteWidthPixels) + " is added to that x coordinate we end up larger than the spritesheet width of " + String.valueOf(_spriteSheetWidthPixels));					
		}						
			
		// 2. Validate that the spriteYStartPixel + spriteHeightPixels is not > spriteSheetHeightPixels.
		if((_startY  - 1) + _spriteHeightPixels > _spriteSheetHeightPixels) {			
			throw new RuntimeException("Attempted to load a spritesheet(" + m_animationName + ") from coordinate " + String.valueOf(_startY) + "y, but when the sprite height of " + String.valueOf(_spriteHeightPixels) + " is added to that y coordinate we end up larger than the spritesheet height of " + String.valueOf(_spriteSheetHeightPixels));					
		}
			
		// 3. Validate that spriteXStartPixel and spriteYStartPixel > 0 as we can't have negative values in our texture. 
		if(_startX  <= 0 || _startY <= 0) {
			throw new RuntimeException("Attemped to load a spritesheet(" + m_animationName + ") from coordinate " + String.valueOf(_startX) + "x and " + String.valueOf(_startY) + "y but they cannot be less than 1!");
		}				
						
		float currentX = _startX - 1;	// Subtracting one because uv coordinates are 0 to 1 based.
		float currentY = _startY - 1;	// Same as above.
			
		// For each frame, start 
		for(int frame = 1; frame <= _frames; frame++) {
			if (currentX + _spriteWidthPixels > _spriteSheetWidthPixels ) {
				currentY += _spriteHeightPixels;
				currentX = 1;
			}		
				
			// If we go over the height of the sprite sheet then something has gone wrong and we need to throw an exception.
			if(currentY + _spriteHeightPixels > _spriteSheetHeightPixels) {
				throw new RuntimeException("Attempted to load a spritesheet(" + m_animationName + ") but we went over the spritesheet height of " + String.valueOf(_spriteSheetHeightPixels) + " pixels."); 
			}
				
			//float texture_U = currentX;
			//float texture_V = currentY;
			
			float textureNormalizedU1 = TranslatePixelToU(currentX, _spriteSheetWidthPixels);
			float textureNormalizedV1 = TranslatePixelToV(currentY, _spriteSheetHeightPixels);
			float textureNormalizedU2 = TranslatePixelToU(currentX + _spriteWidthPixels, _spriteSheetWidthPixels);
			float textureNormalizedV2 = TranslatePixelToV(currentY + _spriteHeightPixels, _spriteSheetHeightPixels);
						
			//float textureNormalizedU1 = texture_U / (_spriteSheetWidthPixels);	
			//float textureNormalizedV1 = texture_V / (_spriteSheetHeightPixels);
			//float textureNormalizedU2 = textureNormalizedU1 + (_spriteWidthPixels / (_spriteSheetWidthPixels)); 
			//float textureNormalizedV2 = textureNormalizedV1 + (_spriteHeightPixels / (_spriteSheetHeightPixels)); 
			float textureXCenterPoint = textureNormalizedU1 + ((textureNormalizedU2 - textureNormalizedU1) / 2.0f);
			float textureYCenterPoint = textureNormalizedV1 + ((textureNormalizedV2 - textureNormalizedV1) / 2.0f);
							
			// Load into array.
			if(!_loadInverse) {
				m_textureCoordinates.add(new TextureCoordinate(textureNormalizedU1, textureNormalizedU2, textureNormalizedV1, textureNormalizedV2, textureXCenterPoint, textureYCenterPoint));
			} else {
				// We want to flip the image horizontally.
				m_textureCoordinates.add(new TextureCoordinate(textureNormalizedU2, textureNormalizedU1, textureNormalizedV1, textureNormalizedV2, textureXCenterPoint, textureYCenterPoint));				
			}
								
			currentX += _spriteWidthPixels;			
		}		
					
		//LogTextureCoordinates(m_textureCoordinates, _spriteSheetWidthPixels, _spriteSheetHeightPixels, _startX, _startY, _frames, "TEXTURELOADER");				
	}
	
	private float TranslatePixelToU(float _pixel, float _textureWidth) {
		return (2.0f * _pixel - 1.0f) / (2.0f * _textureWidth);
	}
	
	private float TranslatePixelToV(float _pixel, float _textureHeight) {
		return (2.0f * _pixel - 1.0f) / (2.0f * _textureHeight);
	}
	
	private static void LogTextureCoordinates(List<TextureCoordinate> _textureCoordinates, float _spriteSheetWidthPixels, float _spriteSheetHeightPixels, float _spriteXStartPixel, float _spriteYStartPixel, int _frames, String _TAG) {
		Log.w(_TAG, "Received a request the load textures for animation for spritesheet that is " + String.valueOf(_spriteSheetWidthPixels) + " wide and " + String.valueOf(_spriteSheetHeightPixels) + " high.");
		Log.w(_TAG, "Frame loading starts at " + String.valueOf(_spriteXStartPixel) + "x, " + String.valueOf(_spriteYStartPixel) + "y.");
		Log.w(_TAG, "***********************************************************************");
						
		for(int frame = 0; frame < _textureCoordinates.size(); frame++) {								
			Log.w(_TAG, "Dumping texture coordinates for frame " + String.valueOf(frame) + " of " + String.valueOf(_frames) + " frames.");
			Log.w(_TAG, "textureNormalizedX1 = " + String.valueOf(_textureCoordinates.get(frame).GetTextureNormalizedX1()) + ", textureNormalizedY1 = " + String.valueOf(_textureCoordinates.get(frame).GetTextureNormalizedY1()));
			Log.w(_TAG, "textureNormalizedX2 = " + String.valueOf(_textureCoordinates.get(frame).GetTextureNormalizedX2()) + ", textureNormalizedY2 = " + String.valueOf(_textureCoordinates.get(frame).GetTextureNormalizedY2()));
			Log.w(_TAG, "textureXCenterPoint = " + String.valueOf(_textureCoordinates.get(frame).GetTextureCenterPointX()) + ", textureYCenterPoint = " + String.valueOf(_textureCoordinates.get(frame).GetTextureCenterPointY()));		
		}
			
		Log.w(_TAG, "***********************************************************************");
	}
	
}
