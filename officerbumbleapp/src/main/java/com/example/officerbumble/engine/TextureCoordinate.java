package com.example.officerbumble.engine;

public class TextureCoordinate {
	private final float TEXTURE_NORMALIZED_X1;
	private final float TEXTURE_NORMALIZED_X2;
	private final float TEXTURE_NORMALIZED_Y1;
	private final float TEXTURE_NORMALIZED_Y2;
	private final float TEXTURE_CENTERPOINT_X;
	private final float TEXTURE_CENTERPOINT_Y;
	
	public TextureCoordinate(float textureNormalizedX1, float textureNormalizedX2, float textureNormalizedY1, float textureNormalizedY2,
							 float textureCenterPointX, float textureCenterPointY) {
	
		TEXTURE_NORMALIZED_X1 = textureNormalizedX1;
		TEXTURE_NORMALIZED_X2 = textureNormalizedX2;
		TEXTURE_NORMALIZED_Y1 = textureNormalizedY1;
		TEXTURE_NORMALIZED_Y2 = textureNormalizedY2;
		TEXTURE_CENTERPOINT_X = textureCenterPointX;
		TEXTURE_CENTERPOINT_Y = textureCenterPointY;
	}

	public float GetTextureNormalizedX1() {  
        return TEXTURE_NORMALIZED_X1;
    }  

	public float GetTextureNormalizedX2() {  
        return TEXTURE_NORMALIZED_X2;
    }  

	public float GetTextureNormalizedY1() {  
        return TEXTURE_NORMALIZED_Y1;
    }  

	public float GetTextureNormalizedY2() {  
        return TEXTURE_NORMALIZED_Y2;
    }  
	
	public float GetTextureCenterPointX() {
		return TEXTURE_CENTERPOINT_X;
	}
	
	public float GetTextureCenterPointY() {
		return TEXTURE_CENTERPOINT_Y;
	}
}
