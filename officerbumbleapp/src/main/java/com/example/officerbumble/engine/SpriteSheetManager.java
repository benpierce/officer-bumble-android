package com.example.officerbumble.engine;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

// This class encapsulates the loading of animation configuration files which are files that describe specific textures
// IE: which image file to use, how big the image file is, where the animation frames are located from within the texture, 
// etc...
public class SpriteSheetManager {
	// A list of animation configuration files that have been loaded.
	private ArrayList<Integer> m_loadedFiles = new ArrayList<Integer>();	

	// A list of all animations that have been loaded.
	private HashMap<String, AnimationConfiguration> m_manager = new HashMap<String, AnimationConfiguration>();
	
	// The texture shader program that all animations use.
	private TextureShaderProgram m_textureShaderProgram = null; 
	
	// Application context so we know how to get to drawable resources.
	private Context m_context;
	
	// We need a context to be able to pull the images.
	public SpriteSheetManager(Context _context) {
		m_context = _context;
		
		// We're only using one texture shader program, so we'll load it right away.
	    SetTextureShaderProgram(_context);
	}
	
	private void SetTextureShaderProgram(Context _context) {
		m_textureShaderProgram = new TextureShaderProgram(_context);
	}

    public boolean IsAnimationLoaded(String _tag) {
        boolean isLoaded = false;

        if(m_manager.containsKey(_tag)) {
            int atlasId = m_manager.get(_tag).GetTextureId();
            if(atlasId != 0) {
                isLoaded = true;
            }
        }

        return isLoaded;
    }

	public TextureShaderProgram GetTextureShaderProgram() {
		return m_textureShaderProgram;
	}
					
	public AnimationConfiguration GetAnimationConfiguration(String _name) {
		return m_manager.get(_name);
	}
	
	// Refreshes all of the TextureId's on animations.
	public void RefreshAllTextureIds(Context _context) {
		AnimationConfiguration config;
		int textureId = 0;

        // Just in case we lost our GL context.
        m_context = _context;
        SetTextureShaderProgram(_context);

		for(String key : m_manager.keySet()) {
            // If the texture Id comes out as 0, chances are that we're referencing an animation that
            // doens't have any relevance to this particular scene, so not a big error.
			textureId = TextureHelper.GetTextureId(m_manager.get(key).GetTextureResourceName());
			m_manager.get(key).SetTextureId(textureId);
		}
	}
		
	public void LoadConfiguration(int _configurationResourceId) 
			throws XmlPullParserException, IOException {
		
		if(!m_loadedFiles.contains(Integer.valueOf(_configurationResourceId))) {
		
			String animationName = "";
			String textureResourceName = "";
			float textureSheetWidth = 0;
			float textureSheetHeight = 0;
			float textureWidth = 0;
			float textureHeight = 0;
			float startX = 0;
			float startY = 0;
			float collisionBufferPercentX1 = 0.0f;
			float collisionBufferPercentX2 = 0.0f;
			float collisionBufferPercentY1 = 0.0f;
			float collisionBufferPercentY2 = 0.0f;
			int frames = 0;
			int framesPerSecond = 0; 
			int repeat = 1;
			boolean stopOnLastFrame = false;
			boolean isLooping = false;
			boolean loadInverse = false;
			int eventFrame = -1;
			boolean notifyWhenFinished = false;
					
			String configData = TextResourceReader.ReadTextFileFromResource (m_context, _configurationResourceId);
					
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		    factory.setNamespaceAware(true);
		    XmlPullParser xpp = factory.newPullParser();
		    xpp.setInput(new StringReader(configData));
		    
		    int elementCount = 0;
		    int eventType = xpp.getEventType();
		    String name = "";
		    
		    while (eventType != XmlPullParser.END_DOCUMENT) {
		    	
		    	if(eventType == XmlPullParser.START_TAG){
		    		name = xpp.getName();
		    		
		    		if (name.equals("Animation")) {
		    			elementCount = 0;
		    			animationName = "";
		    			textureSheetWidth = 0;
		    			textureSheetHeight = 0;
		    			textureWidth = 0;
		    			textureHeight = 0;
		    			startX = 0;
		    			startY = 0;
		    			frames = 0;
		    			repeat = 1;
		    			collisionBufferPercentX1 = 0.0f;
		    			collisionBufferPercentX2 = 0.0f;
		    			collisionBufferPercentY1 = 0.0f;
		    			collisionBufferPercentY2 = 0.0f;
		    			framesPerSecond = 0; 
		    			isLooping = false;
		    			eventFrame = -1;
		    			stopOnLastFrame = false;
		    			loadInverse = false;
		    			notifyWhenFinished = false;
		    		} else if (name.equals("Name")) {
		    			elementCount++;
		    			animationName = xpp.nextText();
		    		} else if (name.equals("TextureResourceName")) {
		    			elementCount++;
		    			textureResourceName = xpp.nextText();
		    		} else if (name.equals("TextureSheetWidth")) {
		    			elementCount++;
		    			textureSheetWidth = Float.parseFloat(xpp.nextText());
		    		} else if (name.equals("TextureSheetHeight")) {
		    			elementCount++;
		    			textureSheetHeight = Float.parseFloat(xpp.nextText());
		    		} else if (name.equals("TextureWidth")) {
		    			elementCount++;
		    			textureWidth = Float.parseFloat(xpp.nextText());
		    		} else if (name.equals("TextureHeight")) {
		    			elementCount++;
		    			textureHeight = Float.parseFloat(xpp.nextText());
		    		} else if (name.equals("StartX")) {
		    			elementCount++;
		    			startX = Float.parseFloat(xpp.nextText());
		    		} else if (name.equals("StartY")) {
		    			elementCount++;
		    			startY = Float.parseFloat(xpp.nextText());
		    		} else if (name.equals("Frames")) {
		    			elementCount++;
		    			frames = Integer.parseInt(xpp.nextText());
		    		} else if (name.equals("FramesPerSecond")) {
		    			elementCount++;
		    			framesPerSecond = Integer.parseInt(xpp.nextText());
		    		} else if (name.equals("Repeat")) {
		    			elementCount++;
		    			repeat = Integer.parseInt(xpp.nextText());
		    		} else if (name.equals("IsLooping")) {
		    			elementCount++;
		    			isLooping = Boolean.parseBoolean(xpp.nextText());
		    		} else if (name.equals("StopOnLastFrame")) {
		    			elementCount++;
		    			stopOnLastFrame = Boolean.parseBoolean(xpp.nextText());
		    		} else if (name.equals("LoadInverse")) {
		    			elementCount++;
		    			loadInverse = Boolean.parseBoolean(xpp.nextText());
		    		} else if (name.equals("EventFrame")) {
		    			elementCount++;
		    			eventFrame = Integer.parseInt(xpp.nextText());
		    		} else if (name.equals("CollisionBufferPercentX1")) {
		    			collisionBufferPercentX1 = Float.parseFloat(xpp.nextText());
		    		} else if (name.equals("CollisionBufferPercentX2")) {
		    			collisionBufferPercentX2 = Float.parseFloat(xpp.nextText());
		    		} else if (name.equals("CollisionBufferPercentY1")) {
		    			collisionBufferPercentY1 = Float.parseFloat(xpp.nextText());
		    		} else if (name.equals("CollisionBufferPercentY2")) {
		    			collisionBufferPercentY2 = Float.parseFloat(xpp.nextText());
		    		} else if (name.equals("NotifyWhenFinished")) {
		    			notifyWhenFinished = Boolean.parseBoolean(xpp.nextText());
		    		}
		    	}
		    	
		    	if(eventType == XmlPullParser.END_TAG) {
		    		if(xpp.getName().equals("Animation")) {
		    		   //int textureId = GetTextureId(textureResourceName);	
		    		   int textureId = 0;	// Textures are lazily loaded, so we have to initialize as 0.
		    			
		    		   // Should have all of the animation configuration variables we need here.
		    		   m_manager.put(animationName, new AnimationConfiguration(animationName, textureResourceName, textureId, textureSheetWidth, textureSheetHeight, textureWidth, textureHeight, startX, startY, frames, framesPerSecond, repeat, isLooping, stopOnLastFrame, loadInverse, notifyWhenFinished, eventFrame, collisionBufferPercentX1, collisionBufferPercentX2, collisionBufferPercentY1, collisionBufferPercentY2));
		    		}
		    	}
		        
		    	eventType = xpp.next();
		    } // End of while.
		    m_loadedFiles.add(Integer.valueOf(_configurationResourceId));
		    
		} // Check of the sprite sheet already existing.
	}
}
