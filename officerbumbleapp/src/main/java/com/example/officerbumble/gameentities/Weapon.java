package com.example.officerbumble.gameentities;

import com.example.officerbumble.engine.DeviceDisplay;
import com.example.officerbumble.engine.Sprite;
import com.example.officerbumble.engine.SpriteSheetManager;
import com.example.officerbumble.engine.Timer;
import com.example.officerbumble.gameentities.Criminal.CRIMINAL_WEAPON;

public class Weapon extends Sprite {
    private float m_velocity = 0.0f;
    private CRIMINAL_WEAPON m_weaponType;
    private float m_maxVelocity = 0.0f;
    private boolean m_isScored = false;
    private boolean m_isInPlay = true;

    // x and y are normalized between -1 and 1 and represents the middle point.
    public Weapon(float _x, float _y, float _height, float _width, float _velocity, DIRECTION _direction, CRIMINAL_WEAPON _weaponType, int _zBufferIndex, float _creationTimeMilliseconds, DeviceDisplay _display, SpriteSheetManager _spriteSheetManager, String _animationTag, String _tag) {
        super(_x, _y, _height, _width, _zBufferIndex, false, _direction, false, _display, _spriteSheetManager, _animationTag, _tag);

        m_maxVelocity = _velocity;
        m_velocity = _velocity;
        m_weaponType = _weaponType;
        m_isInPlay = true;
        m_isScored = false;

        LoadAnimations();
        super.SetCurrentAnimation(super.GetAnimationTag(), _creationTimeMilliseconds);    // Show the only animation.
    }

    private void LoadAnimations() {
        super.LoadAnimation(super.GetAnimationTag());    // Static image animation is always same as its tag.
    }

    protected void LoadAnimation(String _animationName) {
        super.LoadAnimation(_animationName);    // Static image animation is always same as its tag.
    }

    public float GetMaxVelocity() {
        return m_maxVelocity;
    }

    public CRIMINAL_WEAPON GetWeaponType() {
        return m_weaponType;
    }

    public float GetVelocity() {
        return m_velocity;
    }

    public void SetVelocity(float _velocityX, float _velocityY) {
        m_velocity = _velocityX;
    }

    public void SetIsScored() {
        m_isScored = true;
    }

    public boolean IsScored() {
        return m_isScored;
    }

    public void TakeOutOfPlay() {
        m_isInPlay = false;
    }

    public boolean IsInPlay() {
        return m_isInPlay;
    }

    @Override
    public void HandleAnimationFinished(String _tag, float _currentTimeMilliseconds) {
    }

    @Override
    public void Update(Timer _realTimer, Timer _gameTimer) {
        if (!_gameTimer.IsPaused()) {
            super.Update(_realTimer, _gameTimer);
        }
    }
}
