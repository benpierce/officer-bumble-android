package com.example.officerbumble.engine;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glGetIntegerv;

import java.nio.IntBuffer;

public class CoreGraphics {

	public static void SetClearColor(float _red, float _green, float _blue, float _alpha) {
		glClearColor(_red, _green, _blue, _alpha);
	}

	public static void SetViewport(int _width, int _height) {
		glViewport(0, 0, _width, _height);
	}
	
	public static void ClearDisplay() {
        glClear(GL_COLOR_BUFFER_BIT);
	}
	
	public static boolean HasGLProgram() {
		IntBuffer buffer = IntBuffer.allocate(1);
		boolean result = false;
		
		glGetIntegerv(GL_CURRENT_PROGRAM, buffer);
		if(buffer.get(0) != 0) {
			result = true;
		}
		
		return result;
	}
	
	public static void DrawArrays(int _vertexCount) {
		glDrawArrays(GL_TRIANGLES, 0, _vertexCount);		
	}
	
	public static void SetBlending() {
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);		
		glEnable(GL_BLEND);		
	}
	
	public static boolean CheckGLError(String _tag, String _location) {
	    return false;

        // Commented out for perforomance.
        /*
        int error;
        boolean result = false;

	    while ((error = glGetError()) != GL_NO_ERROR) {
	            Log.e(_tag, "glError " + _location + " " + TranslateErrorCode(error));
            result = true;
	    }

        return result;
        */
	}	
	
	private static String TranslateErrorCode(int _errorCode) {
		String translation;
		
		switch (_errorCode) {
        	case GL_NO_ERROR:  
        		translation = "No Error";
                break;
        	case GL_INVALID_ENUM:
        		translation = "Invalid Enum";
        		break;
        	case GL_INVALID_VALUE:
        		translation = "Invalid Value - Numeric argument is out of range";
        		break;
        	case GL_INVALID_OPERATION:
        		translation = "Invalid Operation - The specified operationis not allowed in the current state.";
        		break;
        	case GL_INVALID_FRAMEBUFFER_OPERATION:
        		translation = "Invalid Framebuffer Operation: The framebuffer object is not complete.";
        		break;        		
        	case GL_OUT_OF_MEMORY:
        		translation = "Out of Memory: There is not enough memory left to execute the command.";
        		break;        		
        	default:
        	    translation = "Unknown error code " + String.valueOf(_errorCode);
        	    break;        	    
		 }
		
		 return translation;
	}
	
}