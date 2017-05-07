package com.example.officerbumble.interfaces;

import com.example.officerbumble.gameentities.RoboThrower2000.WEAPON_TYPE;

public interface RoboThrower2000Listener {
	public void HandleThrowFinished(float _currentTimeMilliseconds, WEAPON_TYPE _weaponType);
	public void HandleBowlFinished(float _currentTimeMilliseconds, WEAPON_TYPE _weaponType);
}
