package com.example.officerbumble.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.util.Log;

import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.GL_FLOAT;

public class VertexBuffer2D {
    // Constants
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	
	private int m_bufferPosition;
	private float[] m_rawVertexData;
	private FloatBuffer m_floatBuffer;
	private int m_size = -1;
    public int m_rawVertexLength = 0;
	
	public VertexBuffer2D(int _size) {
		m_size = _size;
		m_bufferPosition = 0;
		m_rawVertexData = new float[_size];
        m_rawVertexLength = m_rawVertexData.length;
		m_floatBuffer = ByteBuffer.allocateDirect(m_rawVertexData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	public void Clear() {
		m_bufferPosition = 0;
	}
	
	public void AppendVertexArrayFast(VertexBuffer2D _newVertexArray2D) {
		if(_newVertexArray2D != null) {
			if(m_bufferPosition + _newVertexArray2D.m_rawVertexLength > m_size) {
				int newSize = m_bufferPosition + _newVertexArray2D.m_rawVertexLength;
				float[] combinedVertexData = new float[newSize];				
				
				Log.e("VERTEX", "WARNING: Vertex buffer needed to expand from " + m_size + " to " + newSize + "!");

				if(m_rawVertexData != null) {
					System.arraycopy(this.GetRawVertexData(), 0, combinedVertexData, 0, m_bufferPosition);
				}				
				
				System.arraycopy(_newVertexArray2D.GetRawVertexData(), 0, combinedVertexData, m_bufferPosition, _newVertexArray2D.m_rawVertexLength);
				
				m_size = combinedVertexData.length;
				m_rawVertexData = combinedVertexData;
                m_rawVertexLength = m_rawVertexData.length;
				m_bufferPosition = combinedVertexData.length;
				m_floatBuffer = ByteBuffer.allocateDirect(m_rawVertexData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
			} else {
				System.arraycopy(_newVertexArray2D.GetRawVertexData(), 0, m_rawVertexData, m_bufferPosition, _newVertexArray2D.m_rawVertexLength);
				m_bufferPosition += _newVertexArray2D.m_rawVertexLength;
			}
		}
	}
	
	public void AppendVertexArraySlow(VertexBuffer2D _newVertexArray2D) {		
		int newSize = 0;
		int currentLength = 0;
		
		if(m_rawVertexData != null && _newVertexArray2D != null) {		
			newSize = m_rawVertexData.length + _newVertexArray2D.m_rawVertexLength;
			currentLength = m_rawVertexData.length;
		} else { 
			if(_newVertexArray2D != null) {
				newSize = _newVertexArray2D.m_rawVertexLength;
			}
		}
	
		if(newSize != 0) {
			float[] combinedVertexData = new float[newSize];
					
			if(m_rawVertexData != null) {
				System.arraycopy(this.GetRawVertexData(), 0, combinedVertexData, 0, currentLength);
			}
			System.arraycopy(_newVertexArray2D.GetRawVertexData(), 0, combinedVertexData, currentLength, _newVertexArray2D.m_rawVertexLength);
			
			m_rawVertexData = combinedVertexData;
		} 				
	}
	
	public float[] GetRawVertexData() {
		return m_rawVertexData;
	}

	public String GetRawVertexDebugData() {
		String debugString = "";
		
		for(int i=0; i<m_rawVertexData.length; i++) {
			debugString += i + " is " + m_rawVertexData[i] + "\n";
		}
		
		return debugString;
	}
	
	// How many triangles we have.
	public int GetVertexCount() {
		return m_bufferPosition / (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT);		
	}
	
	// How large the float[] array is.
	//public int GetLength() {
	//	return m_rawVertexData.length;
	//}

	public FloatBuffer GetFloatBuffer() {
		return m_floatBuffer.put(m_rawVertexData);
	}
		
	public void SetVertexAttribPointer(FloatBuffer _buffer, int dataOffset, int attributeLocation, int componentCount, int stride) {				
		_buffer.position(dataOffset);
		glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, _buffer);
		//CoreGraphics.CheckGLError("VertexBuffer", "glVertexAttribPointer");
		
		glEnableVertexAttribArray(attributeLocation);
		//CoreGraphics.CheckGLError("VertexBuffer", "glEnableVertexAttribArray");
		
		_buffer.position(0);
	}	
	
	public void BindData(TextureShaderProgram textureProgram) {
		FloatBuffer buffer = GetFloatBuffer();
		
		SetVertexAttribPointer(buffer, 0, textureProgram.GetPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
		//CoreGraphics.CheckGLError("VertexBuffer", "BindData");
		SetVertexAttribPointer(buffer, POSITION_COMPONENT_COUNT, textureProgram.GetTextureCoordinatesAttributeLocation(), TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		//CoreGraphics.CheckGLError("VertexBuffer", "BindData");
	}
	
	public void ClearBuffer() {
		m_rawVertexData = null;
	}
	
	public void SetTexturedQuadValues(float _topLeftX, float _topLeftY, float _topLeftU, float _topLeftV,
									  float _topRightX, float _topRightY, float _topRightU, float _topRightV,														  							
									  float _bottomRightX, float _bottomRightY, float _bottomRightU, float _bottomRightV,
									  float _bottomLeftX, float _bottomLeftY, float _bottomLeftU, float _bottomLeftV) {
		
		// Point 1
		m_rawVertexData[0] = _topLeftX;
		m_rawVertexData[1] = _topLeftY;
		m_rawVertexData[2] = _topLeftU;
		m_rawVertexData[3] = _topLeftV;
		
		// Point 2
		m_rawVertexData[4] = _bottomLeftX;
		m_rawVertexData[5] = _bottomLeftY;
		m_rawVertexData[6] = _bottomLeftU;
		m_rawVertexData[7] = _bottomLeftV;
		
		// Point 3
		m_rawVertexData[8] = _topRightX;
		m_rawVertexData[9] = _topRightY;
		m_rawVertexData[10] = _topRightU;
		m_rawVertexData[11] = _topRightV;
		
		// Point 4
		m_rawVertexData[12] = _topRightX;
		m_rawVertexData[13] = _topRightY;
		m_rawVertexData[14] = _topRightU;
		m_rawVertexData[15] = _topRightV;		

		// Point 5
		m_rawVertexData[16] = _bottomLeftX;
		m_rawVertexData[17] = _bottomLeftY;
		m_rawVertexData[18] = _bottomLeftU;
		m_rawVertexData[19] = _bottomLeftV;		

		// Point 6
		m_rawVertexData[20] = _bottomRightX;
		m_rawVertexData[21] = _bottomRightY;
		m_rawVertexData[22] = _bottomRightU;
		m_rawVertexData[23] = _bottomRightV;				
	}
	
	public void SetTexturedQuad(TexturedPoint _topLeft, TexturedPoint _topRight, TexturedPoint _bottomRight, TexturedPoint _bottomLeft) {
				
		float[] rawVertexData = {
				// Order of coordinates:  X, Y, U, V
				_topLeft.GetX(), _topLeft.GetY(), _topLeft.GetTextureX(), _topLeft.GetTextureY(), 	 	 		  // Top Left
				_bottomLeft.GetX(), _bottomLeft.GetY(), _bottomLeft.GetTextureX(), _bottomLeft.GetTextureY(), 	  // Bottom Left
				_topRight.GetX(), _topRight.GetY(), _topRight.GetTextureX(), _topRight.GetTextureY(), 			  // Top Right
				_topRight.GetX(), _topRight.GetY(), _topRight.GetTextureX(), _topRight.GetTextureY(), 			  // Top Right
				_bottomLeft.GetX(), _bottomLeft.GetY(), _bottomLeft.GetTextureX(), _bottomLeft.GetTextureY(), 	  // Bottom Left				
				_bottomRight.GetX(), _bottomRight.GetY(), _bottomRight.GetTextureX(), _bottomRight.GetTextureY(), // Bottom Right
			};
				
		m_rawVertexData = rawVertexData;
	}
	
}
