package com.example.officerbumble.scene;

import java.util.TreeMap;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.DeviceDisplay.ANCHOR;
import com.example.officerbumble.engine.Point2D;

public class SceneItemConfig {
	
	// Possible scene items in the game.
	public enum SCENE_ITEM_TYPE {
		STATIC_IMAGE,
		ESCALATOR,
		BUTTON,
		BUMBLE,
		CRIMINAL,
		EXIT, 
		TREADMILL,
		ROBO_THROWER_2000
	}
	
	public enum SCENE_ANCHOR {
		TOP_LEFT,
		TOP_CENTER,
		TOP_RIGHT,
		CENTER_LEFT,
		CENTER,
		CENTER_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_CENTER,
		BOTTOM_RIGHT,
		RIGHT_OF_PREVIOUS,
		LEFT_OF_PREVIOUS,
		ABOVE_PREVIOUS,
		BELOW_PREVIOUS,
		CENTER_BELOW_PREVIOUS,
		CENTER_ABOVE_PREVIOUS
	}

	// Private scene variables.
	private SCENE_ITEM_TYPE m_type;		// Type of scene item we're dealing with.
	private String m_tag;				// A unique tag to identify the scene item.
	private String m_animationTag;		// Which animation this scene item uses.
	private String m_buttonAnimationTag;// If this is a button, which animation should we play when it's pushed?
    private SCENE_ANCHOR m_anchor;		// Where the scene item is anchored relative to the screen.
	private Margin m_margins;			// Margins this scene item should use relative to it's position.
	private float m_x;					// The device X coordinate (probably between -1.4 an 1.4).
	private float m_y;					// The device y coordinate (probably between 1 and 1).
	private float m_height;				// The height the scene item should be displayed.
	private float m_width;				// The width the scene item should be displayed.
	private boolean m_enabled;			// Is the scene item enabled?  If not, we won't load it.
	private int m_zBufferIndex;			// The lower numbered zBufferIndex items will show over top higher numbered items.
	private TreeMap<String, SceneItemConfig> m_children = new TreeMap<String, SceneItemConfig>();	// Store the children.
	
	// Level and ordinal information (easier to debug with and display the scene graph)
	private int m_level;
	private int m_ordinal;
	private String m_levelOrdinal;
		
	//Needs to throw an invalid_scene exception if anything doesn't validate out.
	public SceneItemConfig(String _type, String _tag, String _animationTag, String _buttonAnimationTag, String _anchor, Margin _margins, float _height, float _width, int _zBufferIndex, boolean _enabled, int _level, int _ordinal, DeviceDisplay _deviceDisplay, SceneItemConfig _previousSceneItem, SceneItemConfig _parentSceneItem)
			throws Exception {
		
		ValidateConfiguration(_tag, _type, _anchor, _previousSceneItem, _parentSceneItem);
		
		m_anchor = SCENE_ANCHOR.valueOf(_anchor);				
		m_type = SCENE_ITEM_TYPE.valueOf(_type);		
		m_tag = _tag;
		m_animationTag = _animationTag;
        m_buttonAnimationTag = _buttonAnimationTag;
		m_margins = _margins;
		m_height = _height;
		m_width = _width;
		m_zBufferIndex = _zBufferIndex;
		m_enabled = _enabled;

		// Set the level ordinal.
		m_level = _level;
		m_ordinal = _ordinal;
		m_levelOrdinal = String.format("%07d", _level) + "." + String.format("%07d", _ordinal);			
				
		// Scene calculations
		m_x = CalculateX(_deviceDisplay, _previousSceneItem, _parentSceneItem);
		m_y = CalculateY(_deviceDisplay, _previousSceneItem, _parentSceneItem);		
	}	

	private void ValidateConfiguration(String _tag, String _type, String _anchor, SceneItemConfig _previousSceneItem, SceneItemConfig _parentSceneItem) 
			throws Exception {
		
		// If RIGHT_OF_PREVIOUS or ABOVE_PREVIOUS was used, we need a previous scene item, or we should fail.
		if(m_anchor == SCENE_ANCHOR.RIGHT_OF_PREVIOUS && _previousSceneItem == null) {
			throw new Exception("Scene item " + _tag + " was anchored to the right of its sibling, yet no sibling was passed in.");
		}
		if(m_anchor == SCENE_ANCHOR.ABOVE_PREVIOUS && _previousSceneItem == null) {
			throw new Exception("Scene item " + _tag + " was anchored above its sibling, yet no sibling was passed in.");
		}
		if(m_anchor == SCENE_ANCHOR.BELOW_PREVIOUS && _previousSceneItem == null) {
			throw new Exception("Scene item " + _tag + " was anchored below its sibling, yet no sibling was passed in.");
		}
		if(m_anchor == SCENE_ANCHOR.LEFT_OF_PREVIOUS && _previousSceneItem == null) {
			throw new Exception("Scene item " + _tag + " was anchored to the left its sibling, yet no sibling was passed in.");
		}		
		if(m_anchor == SCENE_ANCHOR.CENTER_BELOW_PREVIOUS && _previousSceneItem == null) {			
			throw new Exception("Scene item " + _tag + " was anchored below and center its sibling, yet no sibling was passed in.");
		}
		if(m_anchor == SCENE_ANCHOR.CENTER_ABOVE_PREVIOUS && _previousSceneItem == null) {
			throw new Exception("Scene item " + _tag + " was anchored above and center its sibling, yet no sibling was passed in.");
		}		
		
		// Validate that the scene item type is a valid enum value.
		try {
			SCENE_ITEM_TYPE type = SCENE_ITEM_TYPE.valueOf(_type);
		} catch(IllegalArgumentException ex) {
			throw new Exception("Scene item " + _tag + " was loaded with an invalid scene type of " + _type);
		}
		
		// Validate that the anchor is a valid enum value.
		try {
			SCENE_ANCHOR anchor = SCENE_ANCHOR.valueOf(_anchor);
		} catch(IllegalArgumentException ex) {
			throw new Exception("Scene item " + _tag + " was loaded with an invalid anchor of " + _anchor);
		} 

	}
	
	private float CalculateX(DeviceDisplay _deviceDisplay, SceneItemConfig _previousSceneItem, SceneItemConfig _parentSceneItem) {
		float result = 0.0f;										
		SCENE_ANCHOR localAnchor = m_anchor;
		
		// We just want the center point for X if it's either of these two.
		if(localAnchor == SCENE_ANCHOR.CENTER_ABOVE_PREVIOUS || localAnchor == SCENE_ANCHOR.CENTER_BELOW_PREVIOUS) {
			localAnchor = SCENE_ANCHOR.CENTER;
		}
				
		// Right of previous always means the right of the previous one.
		if(localAnchor == SCENE_ANCHOR.RIGHT_OF_PREVIOUS) {
			result = _previousSceneItem.GetX() + _previousSceneItem.GetWidth();
		} else if (localAnchor == SCENE_ANCHOR.LEFT_OF_PREVIOUS) {
			result = _previousSceneItem.GetX() - m_width;
		} else if (localAnchor == SCENE_ANCHOR.ABOVE_PREVIOUS) {
			result = _previousSceneItem.GetX();
		} else if (localAnchor == SCENE_ANCHOR.BELOW_PREVIOUS) {
			result = _previousSceneItem.GetX();		
		} else {
			ANCHOR anchor = ANCHOR.valueOf(localAnchor.toString());
					
			// If inside a parent, we need to use the parent coordinates as our baseline.
			Point2D point;
			if(_parentSceneItem != null) {
				point = _deviceDisplay.GetAnchorCoordinatesWithBoundaries(anchor, _parentSceneItem.GetX(), 
						_parentSceneItem.GetX() + _parentSceneItem.GetWidth(), 
						_parentSceneItem.GetY(), _parentSceneItem.GetY() - _parentSceneItem.GetHeight(),
						m_width, m_height);
			} else {
				point = _deviceDisplay.GetAnchorCoordinates(anchor, m_width, m_height);
			}
			
			// Now we can set the X position which will be based on the anchor specified + the parent coordinates (if specified). 
			result = point.GetX();
		}
		
		// Now that we have the base position, we can adjust it for margins (note that margins are based off of the
		// parent size and not the entire screen if there's a parent.
		if(_parentSceneItem != null) {
			// Move the item right by the left margin.
			result += m_margins.Left(_parentSceneItem.GetWidth());
			result -= m_margins.Right(_parentSceneItem.GetWidth());
		} else {
			result += m_margins.Left(_deviceDisplay.GetAspectRatioX() * 2);
			result -= m_margins.Right(_deviceDisplay.GetAspectRatioX() * 2);
		}
										
		return result;
	}
	
	private float CalculateY(DeviceDisplay _deviceDisplay, SceneItemConfig _previousSceneItem, SceneItemConfig _parentSceneItem) {
		float result = 0.0f;
		
		// Right of previous always means the right of the previous one.
		if(m_anchor == SCENE_ANCHOR.RIGHT_OF_PREVIOUS) {
			result = _previousSceneItem.GetY();
		} else if (m_anchor == SCENE_ANCHOR.LEFT_OF_PREVIOUS) {
			result = _previousSceneItem.GetY();
		} else if (m_anchor == SCENE_ANCHOR.ABOVE_PREVIOUS || m_anchor == SCENE_ANCHOR.CENTER_ABOVE_PREVIOUS) {
			result = _previousSceneItem.GetY() + m_height;
		} else if (m_anchor == SCENE_ANCHOR.BELOW_PREVIOUS || m_anchor == SCENE_ANCHOR.CENTER_BELOW_PREVIOUS) {
			result = _previousSceneItem.GetY() - _previousSceneItem.GetHeight();
		} else {
			ANCHOR anchor = ANCHOR.valueOf(m_anchor.toString());
			
			// If inside a parent, we need to use the parent coordinates as our baseline.
			Point2D point;
			if(_parentSceneItem != null) {
				point = _deviceDisplay.GetAnchorCoordinatesWithBoundaries(anchor, _parentSceneItem.GetX(), 
						_parentSceneItem.GetX() + _parentSceneItem.GetWidth(), 
						_parentSceneItem.GetY(), _parentSceneItem.GetY() - _parentSceneItem.GetHeight(),
						m_width, m_height);
			} else {
				point = _deviceDisplay.GetAnchorCoordinates(anchor, m_width, m_height);
			}
			
			// Now we can set the X position which will be based on the anchor specified + the parent coordinates (if specified). 
			result = point.GetY();
		}
		
		// Now that we have the base position, we can adjust it for margins (note that margins are based off of the
		// parent size and not the entire screen if there's a parent.
		if(_parentSceneItem != null) {
			// Move the item right by the left margin.
			result += m_margins.Bottom(_parentSceneItem.GetHeight());
			result -= m_margins.Top(_parentSceneItem.GetHeight());
		} else {
			result += m_margins.Bottom(_deviceDisplay.GetAspectRatioY() * 2);
			result -= m_margins.Top(_deviceDisplay.GetAspectRatioY() * 2);
		}
										
		return result;
	}
		
	// Public Member Variables
	public SCENE_ITEM_TYPE GetType() {
		return m_type;
	}
	
	public String GetTag() {
		return m_tag;
	}
	
	public String GetAnimationTag() {
		return m_animationTag;
	}

    public String GetButtonAnimationTag() { return m_buttonAnimationTag; }
	
	public float GetX() {
		return m_x;
	}
	
	public float GetY() {
		return m_y;
	}

	public float GetHeight() {
		return m_height;
	}
	
	public float GetWidth() {
		return m_width;
	}
	
	public boolean IsEnabled() {
		return m_enabled;
	}
	
	public int GetZBufferIndex() {
		return m_zBufferIndex;
	}
	
	public int GetLevel() {
		return m_level;
	}
	
	public int GetOrdinal() {
		return m_ordinal;
	}
	
	public String GetLevelOrdinal() {
		return m_levelOrdinal;
	}

	public TreeMap<String, SceneItemConfig> GetChildren() {
		return m_children;
	}
	
	public void SetChildren(TreeMap<String, SceneItemConfig> _children) {
		m_children = _children;
	}
	
}
