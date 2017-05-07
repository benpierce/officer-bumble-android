package com.example.officerbumble.engine;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import static android.opengl.GLES20.*;


public class TextureHelper {
	
	private static HashMap<String, Integer> m_loadedTextures = new HashMap<String, Integer>();
    private static Bitmap m_bitmap;

    public static boolean ValidateTextures() {
        boolean allFound = true;

        for(String textureName : m_loadedTextures.keySet()) {
            boolean found = glIsTexture(m_loadedTextures.get(textureName));
            if(!found) { allFound = false; }
            Log.e("LIFECYCLE", "Texture " + textureName + " was " + ((found) ? "found" : "not found"));
        }

        return allFound;
    }

    // This method will loop through all of the loaded textures and clear any textures out of
    // class memory where the corresponding texture no longer exists in memory.
    private static void ClearGarbageTextures() {
        List<String> texturesToDelete = new ArrayList<String>();

        for(String textureName : m_loadedTextures.keySet()) {
            boolean found = glIsTexture(m_loadedTextures.get(textureName));
            if(!found) {
                texturesToDelete.add(textureName);
            }
        }

        for(String textureName : texturesToDelete) {
            m_loadedTextures.remove(textureName);
        }
    }

    public static void DumpTexturesLoaded() {
        for(String textureName : m_loadedTextures.keySet()) {
            boolean found = glIsTexture(m_loadedTextures.get(textureName));
            Log.e("LIFECYCLE", "Texture " + textureName + " is " + ((found) ? "loaded into gl" : "not loaded into gl"));
        }
    }

	public static int LoadTexture(Context _context, String _textureName) {
		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);
		
		if(textureObjectIds[0] == 0) {
			return 0;
		}
		
		//Bitmap bitmap = ResourceManager.getInstance().GetBitmap(String.valueOf(resourceId));
		final BitmapFactory.Options options = new BitmapFactory.Options();
		final int resourceId = _context.getResources().getIdentifier(_textureName, "drawable", _context.getPackageName());		
		options.inScaled = false;
		
		m_bitmap = BitmapFactory.decodeResource(_context.getResources(), resourceId, options);
		if(m_bitmap == null) {
			glDeleteTextures(1, textureObjectIds, 0);
			
			return 0;
		}
		
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // Ben: Version 1.1 - GL_LINEAR_MIPMAP_LINEAR was causing tiling issues on Cory's tablet
        //                    because of the non power of 2 texture sizes. Changing to GL_LINEAR
        //                    seems to have fixed it.

		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);   // Version 1.1
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        // Non-Power of 2 Textures ALWAYS have to use GL_CLAMP_TO_EDGE
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		GLUtils.texImage2D(GL_TEXTURE_2D, 0, m_bitmap, 0);
		
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		m_bitmap.recycle();	// Clean up the memory
		
		// Store the texture reference in a list so that we can refer to it later.
		m_loadedTextures.put(_textureName, textureObjectIds[0]);
		return textureObjectIds[0];
	}
	
	public static int GetTextureId(String _textureName) {
		int textureId = 0;
		
		if(m_loadedTextures.containsKey(_textureName)) {
			return m_loadedTextures.get(_textureName);
		}
		
		return textureId;
	}
	
	// 1. Figure out which textures to remove and remove them.
	// 2. Check the glContext for any loaded textures to make sure they're valid.
		// 2.1  If not valid, then reload them.
    // 3. Load new textures.
	public static void LoadTextures(Context _context, List<String> _textureNames) {
		ClearGarbageTextures();  // In case we lost our context.

        List<String> texturesToRemove = GetTexturesToRemove(_textureNames);
		List<String> texturesToAdd = GetTexturesToAdd(_textureNames);
		
		RemoveTextures(texturesToRemove);		
		AddTextures(_context, texturesToAdd);
	}
	
	private static void AddTextures(Context _context, List<String> _texturesToAdd) {
	    long startTime = System.nanoTime();
        long duration = 0;

	    for(String textureName : _texturesToAdd) {
			Log.e("LOAD", "...about to load texture " + textureName + ".");
            LoadTexture(_context, textureName);
            duration = System.nanoTime() - startTime;
            Log.e("LOAD", "...Loaded Texture " + textureName + " in " + TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS) + " ms");
            startTime = System.nanoTime();
		}
	}
	
	private static void RemoveTextures(List<String> _textures) {
		int[] textureIds = new int[_textures.size()];
		int i = 0;
					
		for(String textureName : _textures) {
			textureIds[i++] = m_loadedTextures.get(textureName);
		}

        if(textureIds.length > 0) {
            glDeleteTextures(textureIds.length, textureIds, 0);
            CoreGraphics.CheckGLError("TextureHelper", "FreeTextures");

            for (String textureName : _textures) {
                m_loadedTextures.remove(textureName);
            }
        }
	}
		
	private static List<String> GetTexturesToRemove(List<String> _textureNames) {
		List<String> texturesToRemove = new ArrayList<String>();
		boolean found;
		
		for(String textureName : m_loadedTextures.keySet()) {
			found = false;
			for(String textureToAdd : _textureNames) {
				if(textureToAdd.equals(textureName)) {
					found = true;
					break;
				}
			}
			
			if(!found) {
				texturesToRemove.add(textureName);
			}		
		}		
		
		return texturesToRemove;
	}

	private static List<String> GetTexturesToAdd(List<String> _textureNames) {
		List<String> texturesToRefresh = new ArrayList<String>();
		boolean found;
		
		for(String textureToAdd : _textureNames) {
			found = false;
			for(String textureName : m_loadedTextures.keySet()) {
				if(textureToAdd.equals(textureName)) {
					found = true;
					break;
				}
			}
			
			if(!found) {
				texturesToRefresh.add(textureToAdd);
			}		
		}		
		
		return texturesToRefresh;
	}		
}
