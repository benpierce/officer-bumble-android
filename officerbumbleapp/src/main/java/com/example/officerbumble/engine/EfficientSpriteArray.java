package com.example.officerbumble.engine;

// Encapsulates an array such that we incur as little garbage collection as possible when performing searches, sorting, 
// retrievals, etc... because an iterator is not used and we're creating a buffer of pre-allocated items rather than
// managing memory.
public class EfficientSpriteArray {
	
	private Sprite[] m_array;

	public EfficientSpriteArray(int _bufferSize) {
        m_array = new Sprite[_bufferSize];
	}
		
	// Adds a new Sprite to the Buffer and to keep it ordered properly during the insertion.
	public void Add(Sprite _sprite) {
		int size = m_array.length;
		boolean isAdded = false;
		
		// If the last item isn't null, then we've hit the limit of our list, so we'll need to add a new one.  Very bad for
		// performance, so let's hope that the array is properly sized for the game.
		if(m_array[m_array.length - 1] != null) {
			Sprite[] newArray = new Sprite[m_array.length + 1];			
			System.arraycopy(m_array, 0, newArray, 0, m_array.length);
			m_array = newArray;
			size = m_array.length;
		}
		
		// Insert the new element ordered by zBufferIndex DESC and TextureId ASC
		for(int i = 0; i < size; i++) {
			if(m_array[i] == null) {
				m_array[i] = _sprite;
				isAdded = true;
				break;
			} else if (_sprite.m_zBufferIndex > m_array[i].m_zBufferIndex) {
				// Shift everything to the right including i.
				System.arraycopy(m_array, i, m_array, i+1, size - i - 1);
				m_array[i] = _sprite;
				isAdded = true;
				break;
			} else if (_sprite.m_zBufferIndex == m_array[i].m_zBufferIndex && _sprite.m_textureId < m_array[i].m_textureId) {
				// Shift everything to the right including i.
				System.arraycopy(m_array, i, m_array, i+1, size - i - 1);
				m_array[i] = _sprite;
				isAdded = true;
				break;
			}
		}	
			
		if(!isAdded) {
			throw new IndexOutOfBoundsException("Something went wrong! EfficientSpriteArray Buffer could not insert value " + _sprite.m_zBufferIndex + ", " + _sprite.m_textureId + " into the array.");
		}
	}
	
	// Removes a Sprite from the Buffer based on its tag.
	public void Remove(String _tag) {
		boolean isFound = false;
		int size = m_array.length;
		
		for(int i = 0; i < size; i++) {
			if(m_array[i] != null && m_array[i].GetTag().equals(_tag)) {
				// Shift everything back and set the very last item to null.
				System.arraycopy(m_array, i + 1, m_array, i, size - i - 1);
				isFound = true;
				break;
			}
		}		
		
		if(isFound) {
			m_array[size - 1] = null;
		}
	}
	
	public void Remove(Sprite _sprite) {
		Remove(_sprite.GetTag());
	}		
	
	public int GetSize() {
		return m_array.length;
	}
	
	// Retrieve a sprite from the buffer based on it's name. Note that the code path used in here is not ideal in that there
	// are two return statements. This was done so that we wouldn't have to create a temporary Sprite object to be returned
	// by the method.
	public Sprite Get(String _tag) {
		int size = m_array.length;
		
		for(int i = 0; i < size; i++) {
			if(m_array[i] != null && m_array[i].GetTag().equals(_tag)) {
				return m_array[i];		
			}
		}
	 
		return null;
	}
	
	public Sprite[] GetAll() {
		return m_array;
	}
	
}
