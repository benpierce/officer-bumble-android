package com.example.officerbumble.engine;

public class DifficultyConfig {		
	private String m_difficultyName = "";
	
	// Bumble related properties.
	private float  BUMBLE_INVINCIBILITY_LENGTH = 1000.0f;
	private float BUMBLE_MAX_VELOCITY = 1.8f;
	private float BUMBLE_MIN_VELOCITY = 0.0f;
	private float BUMBLE_TIME_TO_REACH_MAX_VELOCITY = 1500.0f;	//  How long it takes for bumble to go from 0 to MAX_VELOCITY in milliseconds
	private float BUMBLE_TIME_TO_REACH_MIN_VELOCITY = 1500.0f;	//  How long it takes for bumble to go from MAX_VELOCITY to MIN_VELOCITY in milliseconds.
	
	// Criminal related properties.
	private float CRIMINAL_TIME_TO_REACH_MAX_VELOCITY = 2000.0f;	//  How long it takes for bumble to go from 0 to MAX_VELOCITY in milliseconds
	private float CRIMINAL_MAX_VELOCITY = 1.5f;
	private float CRIMINAL_MIN_VELOCITY = 0.0f;
	private long CRIMINAL_MIN_TIME_BETWEEN_ATTACKS = 1500;		//  The minimum time the criminal must wait to perform an additional attack.	
	private float CRIMINAL_ATTACK_PERCENTAGE = 0.05f;
	private boolean CRIMINAL_ALLOW_SECOND_WIND = true;
	private float CRIMINAL_SECOND_WIND_DISTANCE = 0.3f;
	private long CRIMINAL_SECOND_WIND_DURATION = 1000;
	private float CRIMINAL_SECOND_WIND_CHANCE_FLOOR1 = 0.5f;
	private float CRIMINAL_SECOND_WIND_CHANCE_FLOOR2 = 0.25f;
	private float CRIMINAL_SECOND_WIND_CHANCE_FLOOR3 = 0.10f;
	private float CRIMINAL_SECOND_WIND_CHANCE_FLOOR4 = 0.00f;
	private int CRIMINAL_MAX_SECOND_WINDS = 2;		
	private boolean CRIMINAL_ALLOW_MOCKING = true;
	private float CRIMINAL_MOCKING_DISTANCE = 5.0f;
	private float CRIMINAL_MOCKING_DISTANCE_END = 1.0f;
	private int CRIMINAL_MAX_SECOND_WINDS_FLOOR1 = 0;
	private int CRIMINAL_MAX_SECOND_WINDS_FLOOR2 = 0;
	private int CRIMINAL_MAX_SECOND_WINDS_FLOOR3 = 0;
	private int CRIMINAL_MAX_SECOND_WINDS_FLOOR4 = 0;
	private float CRIMINAL_MAX_DISTANCE = 8.0f;
	
	// General game related properties.
	private boolean ONE_HIT_KILLS = false;
	
	// Weapon specific properties.
	private boolean RANDOM_WEAPON_VELOCITY = false;
	private float PIE_VELOCITY_MAX = 0.8f;
	private float PIE_VELOCITY_MIN = 0.5f;
	private float BOWLING_BALL_VELOCITY_MAX = 0.7f;
	private float BOWLING_BALL_VELOCITY_MIN = 0.5f;
	
	private int MAX_WEAPONS_IN_ATTACK = 2;
	private float CHICKENATOR_ATTACK_PERCENTAGE = 0.2f;
	private float CHICKENATOR_VELOCITY_MAX = 1.0f;
	private float CHICKENATOR_VELOCITY_MIN = 0.5f;
	
	private float PIE_ATTACK_PERCENTAGE = 0.5f;
    private float BOWLING_BALL_ATTACK_PERCENTAGE = 0.3f;
    
    // Lives
    private int STARTING_LIVES = 3;
    private int MAX_LIVES = 5;
    private long FREE_LIFE_SCORE = 100000;
    
	public DifficultyConfig() {		
	}			
	
	/* Property Getters */
	public int GetStartingLives() {
		return STARTING_LIVES;
	}
	
	public int GetMaxLives() {
		return MAX_LIVES;
	}
	
	public long GetFreeLifeScore() {
		return FREE_LIFE_SCORE;
	}
	
	public String GetDifficultyName() {
		return m_difficultyName;
	}
	
	public float GetBumbleInvincibilityLength() {
		return BUMBLE_INVINCIBILITY_LENGTH;
	}
	
	public float GetBumbleMaxVelocity() {
		return BUMBLE_MAX_VELOCITY;
	}
	
	public float GetBumbleMinVelocity() {
		return BUMBLE_MIN_VELOCITY;
	}
	
	public float GetBumbleTimeToReachMaxVelocity() {
		return BUMBLE_TIME_TO_REACH_MAX_VELOCITY;
	}
	
	public float GetBumbleTimeToReachMinVelocity() {
		return BUMBLE_TIME_TO_REACH_MIN_VELOCITY;
	}
	
	public float GetCriminalTimeToReachMaxVelocity() {
		return CRIMINAL_TIME_TO_REACH_MAX_VELOCITY;
	}
	
	public float GetCriminalMaxVelocity() {
		return CRIMINAL_MAX_VELOCITY;
	}
	
	public float GetCriminalMinVelocity() {
		return CRIMINAL_MIN_VELOCITY;
	}
	
	public long GetCriminalMinTimeBetweenAttacks() {
		return CRIMINAL_MIN_TIME_BETWEEN_ATTACKS;
	}
	
	public float GetCriminalAttackPercentage() {
		return CRIMINAL_ATTACK_PERCENTAGE;
	}
	
	public boolean AllowCriminalSecondWind() {
		return CRIMINAL_ALLOW_SECOND_WIND;
	}

	public float GetCriminalSecondWindDistance() {
		return CRIMINAL_SECOND_WIND_DISTANCE;
	}
	
	public long GetCriminalSecondWindDuration() {
		return CRIMINAL_SECOND_WIND_DURATION;
	}
	
	public float GetCriminalSecondWindChanceFloor1() {
		return CRIMINAL_SECOND_WIND_CHANCE_FLOOR1;
	}
	
	public float GetCriminalSecondWindChanceFloor2() {
		return CRIMINAL_SECOND_WIND_CHANCE_FLOOR2;
	}
	
	public float GetCriminalSecondWindChanceFloor3() {
		return CRIMINAL_SECOND_WIND_CHANCE_FLOOR3;
	}
	
	public float GetCriminalSecondWindChanceFloor4() {
		return CRIMINAL_SECOND_WIND_CHANCE_FLOOR4;
	}	
	
	public int GetCriminalMaxSecondWinds() {
		return CRIMINAL_MAX_SECOND_WINDS;
	}
	
	public boolean AllowCriminalMocking() {
		return CRIMINAL_ALLOW_MOCKING;
	}
	
	public float GetCriminalMockingDistance() {
		return CRIMINAL_MOCKING_DISTANCE;
	}
	
	public float GetCriminalMockingDistanceEnd() {
		return CRIMINAL_MOCKING_DISTANCE_END;
	}
	
	// General game related properties.
	public boolean OneHitKills() {
		return ONE_HIT_KILLS;
	}
	
	// Weapon specific properties.
	public boolean AllowRandomWeaponVelocity() {
		return RANDOM_WEAPON_VELOCITY;
	}
	
	public float GetPieMaximumVelocity() {
		return PIE_VELOCITY_MAX;
	}
	
	public float GetPieMininumVelocity() {
		return PIE_VELOCITY_MIN;
	}
	public float GetBowlingBallMaximumVelocity() {
		return BOWLING_BALL_VELOCITY_MAX;
	}
	public float GetBowlingBallMinimumVelocity() {
		return BOWLING_BALL_VELOCITY_MIN;
	}
	public int GetMaxWeaponsAllowedInAttack() {
		return MAX_WEAPONS_IN_ATTACK;
	}
	public float GetChickenatorAttackPercentage() {
		return CHICKENATOR_ATTACK_PERCENTAGE;
	}
	public float GetChickenatorMaximumVelocity() {
		return CHICKENATOR_VELOCITY_MAX;
	}
	public float GetChickenatorMinimumVelocity() {
		return CHICKENATOR_VELOCITY_MIN;
	}
	
	public float GetPieAttackPercentage() {
		return PIE_ATTACK_PERCENTAGE;
	}
	public float GetBowlingBallAttackPercentage() {
		return BOWLING_BALL_ATTACK_PERCENTAGE;
	}
	public long GetNextFreeLifeScore(long _currentScore) {
		long mod = _currentScore % FREE_LIFE_SCORE;
		return FREE_LIFE_SCORE - mod;		
	}

	public int GetCriminalMaxSecondWindsFloor1() {
		return CRIMINAL_MAX_SECOND_WINDS_FLOOR1;
	}

	public int GetCriminalMaxSecondWindsFloor2() {
		return CRIMINAL_MAX_SECOND_WINDS_FLOOR2;
	}
	
	public int GetCriminalMaxSecondWindsFloor3() {
		return CRIMINAL_MAX_SECOND_WINDS_FLOOR3;
	}
	
	public int GetCriminalMaxSecondWindsFloor4() {
		return CRIMINAL_MAX_SECOND_WINDS_FLOOR4;
	}	

	public float GetCriminalMaxDistance() {
		return CRIMINAL_MAX_DISTANCE;
	}
	
	/* Property Setters */
	public void SetDifficultyName(String _difficultyName) {
	    m_difficultyName = _difficultyName;
	}
	
	public void SetStartingLives(int _startingLives) {
		STARTING_LIVES = _startingLives;
	}
	
	public void SetMaxLives(int _maxLives) {
		MAX_LIVES = _maxLives;
	}
	
	public void SetFreeLifeScore(long _freeLifeScore) {
		FREE_LIFE_SCORE = _freeLifeScore;
	}		
	public void SetBumbleInvincibilityLength(long _bumbleInvincibilityLength) {
		BUMBLE_INVINCIBILITY_LENGTH = _bumbleInvincibilityLength;
	}
	
	public void SetBumbleMaxVelocity(float _bumbleMaxVelocity) {
		BUMBLE_MAX_VELOCITY = _bumbleMaxVelocity;
	}
	
	public void SetBumbleMinVelocity(float _bumbleMinVelocity) {
		BUMBLE_MIN_VELOCITY = _bumbleMinVelocity;
	}
	
	public void SetBumbleTimeToReachMaxVelocity(long _bumbleTimeToReachMaxVelocity) {
		BUMBLE_TIME_TO_REACH_MAX_VELOCITY = _bumbleTimeToReachMaxVelocity;
	}
	
	public void SetBumbleTimeToReachMinVelocity(long _bumbleTimToReachMinVelocity) {
		BUMBLE_TIME_TO_REACH_MIN_VELOCITY = _bumbleTimToReachMinVelocity;
	}
	
	public void SetCriminalTimeToReachMaxVelocity(long _criminalTimeToReachMaxVelocity) {
		CRIMINAL_TIME_TO_REACH_MAX_VELOCITY = _criminalTimeToReachMaxVelocity;
	}
	
	public void SetCriminalMaxVelocity(float _criminalMaxVelocity) {
		CRIMINAL_MAX_VELOCITY = _criminalMaxVelocity;
	}
	
	public void SetCriminalMinVelocity(float _criminalMinVelocity) {
		CRIMINAL_MIN_VELOCITY = _criminalMinVelocity;
	}
	
	public void SetCriminalMinTimeBetweenAttacks(long _criminalMinTimeBetweenAttacks) {
		CRIMINAL_MIN_TIME_BETWEEN_ATTACKS = _criminalMinTimeBetweenAttacks;
	}
	
	public void SetCriminalAttackPercentage(float _criminalAttackPercentage) {
		CRIMINAL_ATTACK_PERCENTAGE = _criminalAttackPercentage;
	}
	
	public void SetCriminalAllowSecondWind(boolean _allowCriminalSecondWind) {
		CRIMINAL_ALLOW_SECOND_WIND = _allowCriminalSecondWind;
	}

	public void SetCriminalSecondWindDistance(float _criminalSecondWindDistance) {
		CRIMINAL_SECOND_WIND_DISTANCE = _criminalSecondWindDistance;
	}
	
	public void SetCriminalSecondWindDuration(long _criminalSecondWindDuration) {
		CRIMINAL_SECOND_WIND_DURATION = _criminalSecondWindDuration;
	}
	
	public void SetCriminalSecondWindChanceFloor1(float _criminalSecondWindChance) {
		CRIMINAL_SECOND_WIND_CHANCE_FLOOR1 = _criminalSecondWindChance;
	}
	
	public void SetCriminalSecondWindChanceFloor2(float _criminalSecondWindChance) {
		CRIMINAL_SECOND_WIND_CHANCE_FLOOR2 = _criminalSecondWindChance;
	}
	
	public void SetCriminalSecondWindChanceFloor3(float _criminalSecondWindChance) {
		CRIMINAL_SECOND_WIND_CHANCE_FLOOR3 = _criminalSecondWindChance;
	}
	
	public void SetCriminalSecondWindChanceFloor4(float _criminalSecondWindChance) {
		CRIMINAL_SECOND_WIND_CHANCE_FLOOR4 = _criminalSecondWindChance;
	}	
	
	public void SetCriminalMaxSecondWinds(int _criminalMaxSecondWinds) {
		CRIMINAL_MAX_SECOND_WINDS = _criminalMaxSecondWinds;
	}
	
	public void SetCriminalAllowMocking(boolean _allowCriminalMocking) {
		CRIMINAL_ALLOW_MOCKING = _allowCriminalMocking;
	}
	
	public void SetCriminalMockingDistance(float _criminalMockingDistance) {
		CRIMINAL_MOCKING_DISTANCE = _criminalMockingDistance;
	}
	
	public void SetCriminalMockingDistanceEnd(float _criminalMockingDistanceEnd) {
		CRIMINAL_MOCKING_DISTANCE_END = _criminalMockingDistanceEnd;
	}
	
	// General game related properties.
	public void SetOneHitKills(boolean _oneHitKills) {
		ONE_HIT_KILLS = _oneHitKills;
	}
	
	// Weapon specific properties.
	public void SetAllowRandomWeaponVelocity(boolean _allowRandomWeaponVelocity) {
		RANDOM_WEAPON_VELOCITY = _allowRandomWeaponVelocity;
	}
	
	public void SetPieMaximumVelocity(float _pieMaximumVelocity) {
		PIE_VELOCITY_MAX = _pieMaximumVelocity;
	}
	
	public void SetPieMininumVelocity(float _pieMinimumVelocity) {
		PIE_VELOCITY_MIN = _pieMinimumVelocity;
	}
	
	public void SetBowlingBallMaximumVelocity(float _bowlingBallVelocityMax) {
		BOWLING_BALL_VELOCITY_MAX = _bowlingBallVelocityMax;
	}
	
	public void SetBowlingBallMinimumVelocity(float _bowlingBallVelocityMin) {
		BOWLING_BALL_VELOCITY_MIN = _bowlingBallVelocityMin;
	}
	
	public void SetMaxWeaponsAllowedInAttack(int _maxWeaponsAllowedInAttack) {
		MAX_WEAPONS_IN_ATTACK = _maxWeaponsAllowedInAttack;
	}
	
	public void SetChickenatorAttackPercentage(float _chickenatorAttackPercentage) {
		CHICKENATOR_ATTACK_PERCENTAGE = _chickenatorAttackPercentage;
	}
	
	public void SetChickenatorMaximumVelocity(float _chickenatorVelocityMax) {
		CHICKENATOR_VELOCITY_MAX = _chickenatorVelocityMax;
	}
	
	public void SetChickenatorMinimumVelocity(float _chickenatorVelocityMin) {
		CHICKENATOR_VELOCITY_MIN = _chickenatorVelocityMin;
	}	
	
	public void SetPieAttackPercentage(float _pieAttackPercentage) {
		PIE_ATTACK_PERCENTAGE = _pieAttackPercentage;
	}
	
	public void SetBowlingBallAttackPercentage(float _bowlingBallAttackPercentage) {
		BOWLING_BALL_ATTACK_PERCENTAGE = _bowlingBallAttackPercentage;
	}
	
	public void SetCriminalMaxSecondWindsFloor1(int _value) {
		CRIMINAL_MAX_SECOND_WINDS_FLOOR1 = _value;
	}

	public void SetCriminalMaxSecondWindsFloor2(int _value) {
		CRIMINAL_MAX_SECOND_WINDS_FLOOR2 = _value;
	}
	
	public void SetCriminalMaxSecondWindsFloor3(int _value) {
		CRIMINAL_MAX_SECOND_WINDS_FLOOR3 = _value;
	}
	
	public void SetCriminalMaxSecondWindsFloor4(int _value) {
		CRIMINAL_MAX_SECOND_WINDS_FLOOR4 = _value;
	}	

	public void SetCriminalMaxDistance(float _value) {
		CRIMINAL_MAX_DISTANCE = _value;
	}
	
}