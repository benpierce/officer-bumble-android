package com.example.officerbumble.engine;

import java.util.ArrayList;
import java.util.List;

import com.example.officerbumble.interfaces.AnimationListener;

/*
	An instance of this class represents a single animation (pulled from a texture atlas). Animations are owned by their
	respective Sprite categories.  
*/
public class Animation {

	// Constants
	private final String m_name;					// Used to refer to a particular animation.
	
	// Class variables
	private List<TextureCoordinate> m_textureCoordinates = new ArrayList<TextureCoordinate>();	// For each frame in the animation, the texture coordinates will reside in this list. 		
	private int m_framesPerSecond;			// Number of frames that this animation requires per second.
	private int m_currentFrame;				// The current frame of animation we're on (one based).
	private int m_frames; 					// Number of frames in the animation.
	private int m_repeat;					// Number of times to repeat the cycle.
	private int m_currentCycle;				// Number of times the animation has cycled.
	private boolean m_isLooping;			// Set to false if the animation should only play once. 
	private boolean m_isAnimated;			// Determines if the animation is one frame or more.
	private int m_eventFrame;
	private boolean m_notifyWhenFinished;
	private boolean m_stopOnLastFrame;
	private List<AnimationListener> m_animationListeners = new ArrayList<AnimationListener>();	// Callbacks for animation finished.
	private float m_collisionBufferPercentX1 = 0.0f;
	private float m_collisionBufferPercentX2 = 0.0f;
	private float m_collisionBufferPercentY1 = 0.0f;
	private float m_collisionBufferPercentY2 = 0.0f;
	private boolean m_isAnimationEndFired = false;
	private boolean m_isAnimationEventFired = false;
	
	// Performance Related Variables
	private int m_textureCoordinateSize = 0;
		
	public Animation(AnimationConfiguration _animationConfiguration, String _name) {
		// Store class variables.
		m_name = _name;
		m_framesPerSecond = _animationConfiguration.GetFramesPerSecond();	
		m_currentFrame = 0;
		m_currentCycle = 0;
		m_frames = _animationConfiguration.GetFrames();
		m_repeat = _animationConfiguration.GetRepeat();
		m_isLooping = _animationConfiguration.IsLooping();
		m_stopOnLastFrame = _animationConfiguration.StopOnLastFrame();
		m_collisionBufferPercentX1 = _animationConfiguration.GetCollisionBufferPercentX1();
		m_collisionBufferPercentX2 = _animationConfiguration.GetCollisionBufferPercentX2();
		m_collisionBufferPercentY1 = _animationConfiguration.GetCollisionBufferPercentY1();
		m_collisionBufferPercentY2 = _animationConfiguration.GetCollisionBufferPercentY2();
		m_eventFrame = _animationConfiguration.GetEventFrame();
		m_notifyWhenFinished = _animationConfiguration.NotifyWhenFinished();
		
		// Cache all of the texture points.
		m_textureCoordinates = _animationConfiguration.GetTextureCoordinates();
		m_textureCoordinateSize = m_textureCoordinates.size();
		m_isAnimated = _animationConfiguration.IsAnimated();
		m_isAnimationEndFired = false;
		m_isAnimationEventFired = false;
	}
		
	public void ResetAnimation() {
		m_currentFrame = 0;
		m_isAnimationEndFired = false;
		m_isAnimationEventFired = false;
	}
	
	public void SetFrame(int _frame) {
		m_currentFrame = _frame - 1;
	}
	
	public int GetFrame() {
		return m_currentFrame;
	}
	
	public int GetFrames() {
		return m_frames;
	}
	
	public float GetCollisionBufferPercentX1() {
		return m_collisionBufferPercentX1;
	}
	
	public float GetCollisionBufferPercentX2() {
		return m_collisionBufferPercentX2;
	}
	
	public float GetCollisionBufferPercentY1() {
		return m_collisionBufferPercentY1;
	}
	
	public float GetCollisionBufferPercentY2() {
		return m_collisionBufferPercentY2;
	}	
	
	public void IncrementFrame(int _numberOfFrames, float _currentMilliseconds) {
		int framesToAdvance = 0;
		boolean isAnimationStopped = false;

		if(m_stopOnLastFrame && m_currentFrame >= m_frames - 1) {
			m_currentFrame = m_frames - 1;   // Stay on last frame.
			// Do nothing
		} else {
			// If we've gone over the max frames, we need to cycle it so we don't go outside array bounds.
			if(_numberOfFrames > m_frames) {
				framesToAdvance = (_numberOfFrames % m_frames);
			} else {
				framesToAdvance = _numberOfFrames;
			}
			
			if(m_currentFrame + framesToAdvance > m_frames - 1) {
				if(m_isLooping) {
					m_currentFrame = m_currentFrame + framesToAdvance - m_frames;
				} else
				{
					if(++m_currentCycle >= m_repeat) {
						isAnimationStopped = true;
						m_currentCycle = 0;
                        m_currentFrame = m_frames - 1;
					} else {
                        m_currentFrame = 1;
                    }
				}
			} else {
				m_currentFrame = m_currentFrame + framesToAdvance;
			}				
							
			if (isAnimationStopped && !m_isAnimationEndFired && m_notifyWhenFinished) {
				FireAnimationFinishedMessage(_currentMilliseconds);
				m_isAnimationEndFired = true;
			}
			
			if(!isAnimationStopped && !m_isAnimationEventFired && m_currentFrame + 1 >= m_eventFrame) {
				FireAnimationEvent(_currentMilliseconds);
				m_isAnimationEventFired = true;
			}
		}
	}
	
	public void FireAnimationFinishedMessage(float _currentMilliseconds) {
		for (AnimationListener listener : m_animationListeners) {
			listener.HandleAnimationFinished(m_name, _currentMilliseconds);
		}		
	}
	
	public void FireAnimationEvent(float _currentMilliseconds) {
		for (AnimationListener listener: m_animationListeners) {
			listener.HandleAnimationEvent(_currentMilliseconds);
		}
	}
	
	public void RegisterForAnimationFinishedEvents(Object _listener) {
		m_animationListeners.add((AnimationListener) _listener);		
	}
	
	public void IncrementFrame(float _currentMilliseconds) {
		IncrementFrame(1, _currentMilliseconds);
	}
	
	public TextureCoordinate GetCurrentTextureCoordinate() {
		if(m_currentFrame >= m_textureCoordinateSize) {
			throw new RuntimeException("Attemped to load texture coordinates for frame " + String.valueOf(m_currentFrame) + " in " + m_name + " but the maximum frame is " + String.valueOf(m_textureCoordinates.size()) + ".");
		}
			
		return m_textureCoordinates.get(m_currentFrame);		
	}
	
	public String GetName() {
		return m_name;
	}
	
	public int GetFramesPerSecond() {
		return m_framesPerSecond;
	}
		
	public boolean IsAnimated() {
		return m_isAnimated;
	}
			
}