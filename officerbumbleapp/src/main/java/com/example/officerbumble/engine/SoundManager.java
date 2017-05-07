package com.example.officerbumble.engine;

import java.util.HashMap;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.content.Context;
import android.util.Log;

public class SoundManager {
	private static SoundPool m_soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
	private static HashMap<String, Integer> m_soundMap = new HashMap<String, Integer>();
	private static MediaPlayer mp = new MediaPlayer();
	private static boolean m_muted = false;
	
	public SoundManager() {
	}
	
	public static void LoadSound(String _name, Context _context, int _resourceId, int _priority) {
		int soundId;
		soundId = m_soundPool.load(_context, _resourceId, _priority);
		m_soundMap.put(_name, soundId);		
	}
	
	public static void PlaySound(String _name, boolean _loop) {		
		if(!m_muted) {
			Integer soundId = m_soundMap.get(_name);
			if(soundId != null) {
				m_soundPool.play(soundId.intValue(), 100.0f, 100.0f, 1, (_loop) ? 1 : 0, 1.0f);
			} 
		}
	}
	
	public static boolean IsMuted() {
		return m_muted;
	}
	
	public static void PlayMusic(Context _context, int _resourceId) {
		if(!m_muted) {
            StopMusic(); // If any is playing.

			mp = MediaPlayer.create(_context, _resourceId);
			mp.setLooping(true);
			mp.start();
		}
	}
	
	public static void PauseMusic() {
		if(mp != null && mp.isPlaying()) {
			mp.pause();
		}
	}
	
	public static void StopMusic() {
		if(mp != null && mp.isPlaying()) {
			mp.stop();
            mp = null;
		}
	}
	
	public static void ResumeMusic() {
		if(mp != null && !m_muted) {
			mp.start();
		}
	}
	
	public static void Mute() {
		m_muted = true;
		PauseMusic();
	}
	
	public static void Unmute() {
		m_muted = false;
		ResumeMusic();
	}
		
	// Completely clears all loaded sounds.
	public static void Clear() {		
		m_soundPool.release();
		m_soundPool = null;
	}
}
