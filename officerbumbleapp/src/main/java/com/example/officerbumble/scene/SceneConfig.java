package com.example.officerbumble.scene;

import java.util.List;
import java.util.TreeMap;

import com.example.officerbumble.engine.Color;
import com.example.officerbumble.engine.Point2D;

public class SceneConfig {
	private String m_name;
	private Color m_backgroundColor;
	private TreeMap<String, SceneItemConfig> m_sceneItems;
	private List<String> m_requiredTextures;
	private Point2D m_sceneStart = null;
	private Point2D m_sceneEnd = null;
	
	public SceneConfig(String _name, String _backgroundColor, TreeMap<String, SceneItemConfig> _sceneItems, List<String> _requiredTextures) {
		m_name = _name;
		m_backgroundColor = Color.GetColor(_backgroundColor);
		m_sceneItems = _sceneItems;	
		m_requiredTextures = _requiredTextures;
		InitializeSceneMetadata();
	}
	
	public String GetName() {
		return m_name;
	}		
	
	public Color GetBackgroundColor() {
		return m_backgroundColor;
	}
	
	public Point2D GetSceneStart() {
		return m_sceneStart;
	}
	
	public Point2D GetSceneEnd() {
		return m_sceneEnd;
	}
				
	public List<String> GetRequiredTextures() {
		return m_requiredTextures;
	}
	
	public SceneItemConfig GetSceneItem(String _sceneItemTag) {
		return m_sceneItems.get(_sceneItemTag);
	}
	
	public TreeMap<String, SceneItemConfig> GetSceneItems() {
		return m_sceneItems;
	}
	
	private void InitializeSceneMetadata() {
		float minX = 0.0f;
		float maxX = 0.0f;
		float minY = 0.0f;
		float maxY = 0.0f;		
		
		if (m_sceneItems != null) {
			for (String zKey : m_sceneItems.keySet()) {
				SceneItemConfig config = m_sceneItems.get(zKey);
				if(config.GetX() < minX) {
					minX = config.GetX();
				}
				if(config.GetX() + config.GetWidth() > maxX) {
					maxX = config.GetX() + config.GetWidth();
				}
				if(config.GetY() - config.GetHeight() < minY) {
					minY = config.GetY() - config.GetHeight();
				}
				if(config.GetY() > maxY) {
					maxY = config.GetY();
				}								
			}			
		}
		
		m_sceneStart = new Point2D(minX, minY);
		m_sceneEnd = new Point2D(maxX, maxY);		
	}
		
}
