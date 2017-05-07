package com.example.officerbumble.engine;

import static android.opengl.GLES20.glUseProgram;


import android.content.Context;

public class ShaderProgram {
	// Uniform constants
	protected static final String U_MATRIX = "u_Matrix";
	protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
	
	// Attribute constants
	protected static final String A_POSITION = "a_Position";
	protected static final String A_COLOR = "a_Color";
	protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
	
	// Shader program
	protected final int program;
	
	protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
		// Compile the shaders and link the program.
		program = ShaderHelper.BuildProgram(TextResourceReader.ReadTextFileFromResource(context, vertexShaderResourceId), 
				TextResourceReader.ReadTextFileFromResource(context, fragmentShaderResourceId));
	}
	
	public void UseProgram() {
		glUseProgram(program);
		CoreGraphics.CheckGLError("ShaderProgram", "UseProgram()");
	}
	
}
