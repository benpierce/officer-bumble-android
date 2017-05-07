package com.example.officerbumble.engine;

/*
 * Timer is always in milliseconds.
 * 
 * Timer Usage:
 * 
 * 1. Used to determine how far something should move, based on an objects direction and velocity.
 * 2. Used to determine which animation frame we should be on.
 * 3. Used to determine how fast to cycle animations (IE: we're scaling the FPS based on this).
 * 
 */
public class Timer {

    private static final float MAXIMUM_NANOSECOND_INTERVAL = 1000000000.0f; // Equals 1 second.
    private static final float ONE_FRAME = 16000000.0f; // 16 ms.
    private static final float NANOSECONDS_IN_ONE_MS = 1000000.0f;      // # of Nanoseconds in 1ms.

	// Current Nanoseconds.
	long m_accumulatedNanoseconds = 0;		    // How many non-paused nanoseconds have been accumulated.
    long m_previousAccumulatedNanoseconds = 0;  // Previously non-paused nanoseconds we had accumulated.
    long m_previousNanoseconds = 0;             // Previous nanosecond we captured.
    boolean m_isPaused = false;                 // Is the Timer paused?

    // Constructor
	public Timer() {
        m_accumulatedNanoseconds = 0;
        m_previousAccumulatedNanoseconds = 0;
        m_previousNanoseconds = System.nanoTime();
        m_isPaused = false;
	}

    // Return the current # of milliseconds we have aggregated.
    public float GetCurrentMilliseconds() {
        return m_accumulatedNanoseconds / NANOSECONDS_IN_ONE_MS;
    }

    // Return the previous # of milliseconds we had aggregated.
    public float GetPreviousMilliseconds() {
        return m_previousAccumulatedNanoseconds / NANOSECONDS_IN_ONE_MS;
    }

    // Returns # of ms since last call to IncrementCurrentMilliseconds.  Example: 16.523 would be
    // in milliseconds.
    public float GetDelta() {
        //return (m_accumulatedNanoseconds - m_previousAccumulatedNanoseconds)  / NANOSECONDS_IN_ONE_MS;
        return ONE_FRAME / NANOSECONDS_IN_ONE_MS;   // This will smooth it out if you're running at 60FPS or close to it.
    }

	public void IncrementCurrentMilliseconds() {
        long now = System.nanoTime();

        // Clean shit up.
        if (!m_isPaused) {

            if (now - m_previousNanoseconds > MAXIMUM_NANOSECOND_INTERVAL) {
                // If we got into here, we're probably in debug mode.
                m_previousAccumulatedNanoseconds = m_accumulatedNanoseconds;
                m_accumulatedNanoseconds += ONE_FRAME;
            }
            else {
                m_previousAccumulatedNanoseconds = m_accumulatedNanoseconds;
                m_accumulatedNanoseconds += (now - m_previousAccumulatedNanoseconds);
            }

            m_previousAccumulatedNanoseconds = m_accumulatedNanoseconds;
            m_accumulatedNanoseconds += (now - m_previousNanoseconds);
        }

        m_previousNanoseconds = now;
	}

	public void Pause() {
        m_isPaused = true;
	}
	
	public void UnPause() {
		m_isPaused = false;
	}
	
	public boolean IsPaused() {
        return m_isPaused;
	}
}
