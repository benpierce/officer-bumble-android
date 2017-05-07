package com.example.officerbumble.gameentities;

import java.util.Random;

import com.example.officerbumble.engine.EfficientSpriteArray;

public class FeatherEmitter {
		
	private final static int FEATHER_CREATION_CHANGE = 30;	// %
	
	private EfficientSpriteArray m_feathers;
	private Chickenator m_chickenator;
	private int m_maxFeatherCount;	
	private Random m_rand = new Random();
	
	public FeatherEmitter(Chickenator _chickenator) {
		m_maxFeatherCount = m_rand.nextInt((10 - 1) + 1) + 1;		
		m_feathers = new EfficientSpriteArray(m_maxFeatherCount);
		m_chickenator = _chickenator;
	}
	
	public void CreateFeather() {
		// 30% chance of creating a feather.
		int num = m_rand.nextInt((100 - 1) + 1) + 1;
		if(num <= FEATHER_CREATION_CHANGE) {
			
		}
	}
	
	
	
	
	
	
}
