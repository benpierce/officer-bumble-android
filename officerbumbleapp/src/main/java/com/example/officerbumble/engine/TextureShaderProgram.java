package com.example.officerbumble.engine;

import com.example.officerbumble.R;

import android.content.Context;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;

public class TextureShaderProgram extends ShaderProgram {
	// Uniform locations
	private final int uMatrixLocation;
	private final int uTextureUnitLocation;
	
	// Attribute locations
	private final int aPositionLocation;
	private final int aTextureCoordinatesLocation;
    private static boolean m_shitAlreadyHitTheFan = false;
	
	public TextureShaderProgram(Context context) {
		super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
		
		// Retrieve uniform locations for the shader program.
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
		uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
		
		// Retrieve attribute location for the shader program.
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);		
	}
	
	public void SetUniforms(float[] matrix, int textureId) {

        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        CoreGraphics.CheckGLError("TextureShaderProgram", "SetUniforms");

        // Set the active texture unit to texture unit 0
        glActiveTexture(GL_TEXTURE0);
        CoreGraphics.CheckGLError("TextureShaderProgram", "SetUniforms");

		// Bind the text to this unit.
		glBindTexture(GL_TEXTURE_2D, textureId);
		CoreGraphics.CheckGLError("TextureShaderProgram", "SetUniforms");

        // Tell the texture uniform sampler to use this texture in the shader.
        glUniform1i(uTextureUnitLocation, 0);
        CoreGraphics.CheckGLError("TextureShaderProgram", "SetUniforms");

        m_shitAlreadyHitTheFan = true;
	}
	
	public int GetPositionAttributeLocation() {
		return aPositionLocation;
	}
	
	public int GetTextureCoordinatesAttributeLocation() {
		return aTextureCoordinatesLocation;
	}
	
}
