package com.example.officerbumble.engine;

import com.example.officerbumble.gameentities.Button;
import com.example.officerbumble.gameentities.Bumble;

/*
 * SpriteManager is designed to act as a container for all sprites and to group them by:
 * 
 * 	  a) zOrder
 *    b) Texture
 *    c) Sprite
 *    
 * This grouping first allows us to use the painters algorithm to paint the farthest items first, and within that grouping we
 * have grouped by Texture so that we're setting the texture uniforms as few times as possible (as it's an expensive Open GL call)
 * 
 * To draw all sprites, this class will append all vertex data into a single VertexBuffer2D, but only for items that are 
 * displayed OnScreen. The fewer glDrawArrays calls, the better performance.
 *   
 */
public class SpriteManager {

	private final static int DEFAULT_VERTEX_BUFFER_SIZE = 5000;
	private final static int DEFAULT_SPRITE_BUFFER_SIZE = 250;
    private final static int DEFAULT_BUTTON_BUFFER_SIZE = 10;

	// Performance related instance variables - used to create memory buffers and avoid garbage collection.
	private EfficientSpriteArray m_sprites;
    private EfficientSpriteArray m_buttons;
    private Bumble m_bumble;

	private VertexBuffer2D m_buffer;
	private Sprite[] m_tempSprites;

	public SpriteManager() {		
		m_buffer = new VertexBuffer2D(DEFAULT_VERTEX_BUFFER_SIZE);
	}
	
	// Empties out the internal sprite storage structures.
	public void Initialize() {
        m_sprites = new EfficientSpriteArray(DEFAULT_SPRITE_BUFFER_SIZE);
        m_buttons = new EfficientSpriteArray(DEFAULT_BUTTON_BUFFER_SIZE);
        m_bumble = null;
	}
		
	// Adds or replaces a sprite in the list. 
	public void AddSprite(Sprite _sprite) {
        m_sprites.Add(_sprite);

        if ( _sprite instanceof Bumble ) {
            m_bumble = (Bumble)_sprite;
        }

        if ( _sprite instanceof Button ) {
            m_buttons.Add(_sprite);
        }
	}	
		
	// Removes a sprite if it currently exists.
	public void RemoveSprite(String _tag) {
		m_sprites.Remove(_tag);

        if ( m_bumble != null ) {
            if (_tag.equals(m_bumble.GetTag())) {
                m_bumble = null;
            }
        }

        m_buttons.Remove(_tag);
	}
	
	// Adds an entire list of sprites to the sprite manager.
	public void AddSprites(Sprite[] _sprites) {
		int size = _sprites.length;
		
		for(int i = 0; i < size; i++) {
			AddSprite(_sprites[i]);
		}		
	}
	
	// Removes an entire list of sprites from the sprite manager.
	public void RemoveSprites(Sprite[] _sprites) {
		int size = _sprites.length;
		
		for(int i = 0; i < size; i++) {
			RemoveSprite(_sprites[i].GetTag());
		}		
	}
					
	// Locates a specific sprite.
	public Sprite GetSprite(String _tag) {
		return m_sprites.Get(_tag);
	}
	
	public Sprite[] GetAllSprites() {
		return m_sprites.GetAll();
	}
	
	public void RefreshAllTextureIds() {				
		m_tempSprites = m_sprites.GetAll();
		int size = m_tempSprites.length;
		
		for(int i = 0; i < size; i++) {
			if(m_tempSprites[i] != null) { 
				m_tempSprites[i].RefreshTextureId();
			}
		}
	}
	
	// Draw the sprites. Note that we have to loop through each of the zBuffers descending, then loop through each 
	// of the textureIds in order to correctly draw based on the painters algorithm and to allow multiple textures.
	public void Draw(DeviceDisplay _display, TextureShaderProgram _program) {
		int size = m_sprites.GetSize();
		int zBufferIndex = -1;
		int textureId = -1;
		int lastZBufferIndex = -1;
		int lastTextureId = -1;
        int loop = 1;
        boolean programUsed = false;

        long startTime = System.nanoTime();

		m_tempSprites = m_sprites.GetAll();
		for(int i = 0; i < size; i++) {
			if(m_tempSprites[i] != null) {
                startTime = System.nanoTime();
				zBufferIndex = m_tempSprites[i].m_zBufferIndex;
				//m_tempSprites[i].GetZBufferIndex();
				textureId = m_tempSprites[i].m_textureId;

				if(zBufferIndex != lastZBufferIndex || textureId != lastTextureId) {
					// We've hit a new SpriteBatch, so write everything out only if this isn't our first time through the loop, in 
					// which case nothing will be there to Draw.
					if(lastZBufferIndex != -1) {
						int vertexCount = m_buffer.GetVertexCount();
						m_buffer.BindData(_program);
						
						CoreGraphics.DrawArrays(vertexCount);
					} // End lastZBufferIndex check.
					
					if(textureId != lastTextureId) {
                        if ( !programUsed ) {
                            _program.UseProgram();
                            programUsed = true;
                        }
                        _program.SetUniforms(_display.GetOrthographicProjectionMatrix(), textureId);

						lastTextureId = textureId;
					}
										
					lastZBufferIndex = zBufferIndex;					
					m_buffer.Clear();					
				} // End New Batch Check
								
				m_buffer.AppendVertexArrayFast(m_tempSprites[i].GetVertexArrayToDraw());

                loop++;
			} // End null check.
		} // End for loop
		
		if(lastZBufferIndex != -1) {	
			int vertexCount = m_buffer.GetVertexCount();
			m_buffer.BindData(_program);			
			CoreGraphics.DrawArrays(vertexCount);		
		} // End lastZBufferIndex check.
	}
	
	// Calls the update method on every sprite.
	public void UpdateSprites(Timer _realTimer, Timer _gameTimer) {
		int size = m_sprites.GetSize();
		m_tempSprites = m_sprites.GetAll();
		
		for(int i = 0; i < size; i++) {
			if(m_tempSprites[i] != null) {
				m_tempSprites[i].Update(_realTimer, _gameTimer);
			}
		}		
	}
				
	// Loop through each delegate and notify them that a sprite was touched.
	public void HandleTouch(boolean _isPrimary, float _normalizedX, float _normalizedY, float _realTimeMilliseconds, float _gameTimeMilliseconds, boolean _isGamePaused) {
        int size = m_buttons.GetSize();
        boolean handled = false;
        m_tempSprites = m_buttons.GetAll();

        // Loop through all buttons.
        for (int i = 0; i < size; i++) {
            if (m_tempSprites[i] != null && (!_isGamePaused || (_isGamePaused && m_tempSprites[i].UsesRealTime()))) {
                if(m_tempSprites[i].HandleTouch(_isPrimary, _normalizedX, _normalizedY, _realTimeMilliseconds, _gameTimeMilliseconds)) {
                    handled = true;
                    break;	// Otherwise the m_tempSprites array could grow (like on a pause) and then the next element will be the same as the one we're already on!
                }
            }
        }

        // Must be Bumble if this hasn't been handled yet.
        if ( !handled ) {
            if( !_isGamePaused && m_bumble != null ) {
                m_bumble.HandleTouch(_isPrimary, _normalizedX, _normalizedY, _realTimeMilliseconds, _gameTimeMilliseconds);
            }
        }
	}
		
	// Loop through each delegate and notify them that a sprite was untouched.
	public void HandleTouchRelease(boolean _isPrimary, float _normalizedX, float _normalizedY, float _realTimeMilliseconds, float _gameTimeMilliseconds, boolean _isGamePaused) {

        if( !_isGamePaused && m_bumble != null ) {
            m_bumble.HandleTouchRelease(_isPrimary, _normalizedX, _normalizedY, _realTimeMilliseconds, _gameTimeMilliseconds);
        }

	}	
}
