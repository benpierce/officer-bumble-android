package com.example.officerbumble.interfaces;

import com.example.officerbumble.gameentities.Criminal.CRIMINAL_WEAPON;

public interface CriminalListener {
	public void HandleCriminalAttack(CRIMINAL_WEAPON _weapon, float _velocity, float _currentTimeMilliseconds);
	public void HandleCriminalCaught(float _currentTimeMilliseconds);
}


