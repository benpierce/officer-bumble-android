package com.example.officerbumble.engine;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class DifficultyManager {
	private HashMap<String, DifficultyConfig> m_configs = new HashMap<String, DifficultyConfig>();
	
	public DifficultyConfig GetDifficultyConfig(String _difficultyName) {
		DifficultyConfig config = null;
		
		if(m_configs.containsKey(_difficultyName)) {
			config = m_configs.get(_difficultyName);
		} else {
			throw new RuntimeException("Unable to locate difficulty configuration " + _difficultyName);
		}
		
		return config;
	}
	
	public void Load(Context _context, int _configurationResourceId) throws XmlPullParserException, IOException {
		String configData = TextResourceReader.ReadTextFileFromResource(_context, _configurationResourceId);

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(configData));

		int eventType = xpp.getEventType();
		String elementName = null;

		DifficultyConfig config = null;
		
		while (eventType != XmlPullParser.END_DOCUMENT) {

			if (eventType == XmlPullParser.START_TAG) {				
				elementName = xpp.getName();

				if (elementName.equals("Difficulty")) {
					config = new DifficultyConfig();
					// This is the opening difficulty tag.
				} else if (elementName.equals("DifficultyName")) {
					config.SetDifficultyName(xpp.nextText());
				} else if (elementName.equals("BUMBLE_INVINCIBILITY_LENGTH")) {
					config.SetBumbleInvincibilityLength(Long.parseLong(xpp.nextText()));					
				} else if (elementName.equals("BUMBLE_MAX_VELOCITY")) {
					config.SetBumbleMaxVelocity(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("BUMBLE_MIN_VELOCITY")) {
					config.SetBumbleMinVelocity(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("BUMBLE_TIME_TO_REACH_MAX_VELOCITY")) {
					config.SetBumbleTimeToReachMaxVelocity(Long.parseLong(xpp.nextText()));
				} else if (elementName.equals("BUMBLE_TIME_TO_REACH_MIN_VELOCITY")) {
					config.SetBumbleTimeToReachMinVelocity(Long.parseLong(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_TIME_TO_REACH_MAX_VELOCITY")) {
					config.SetCriminalTimeToReachMaxVelocity(Long.parseLong(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_MAX_VELOCITY")) {
					config.SetCriminalMaxVelocity(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_MIN_VELOCITY")) {
					config.SetCriminalMinVelocity(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_MIN_TIME_BETWEEN_ATTACKS")) {
					config.SetCriminalMinTimeBetweenAttacks(Long.parseLong(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_ATTACK_PERCENTAGE")) {
					config.SetCriminalAttackPercentage(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_ALLOW_SECOND_WIND")) {
					config.SetCriminalAllowSecondWind(Boolean.parseBoolean(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_SECOND_WIND_DISTANCE")) {
					config.SetCriminalSecondWindDistance(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_SECOND_WIND_DURATION")) {
					config.SetCriminalSecondWindDuration(Long.parseLong(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_SECOND_WIND_CHANCE_FLOOR1")) {
					config.SetCriminalSecondWindChanceFloor1(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_SECOND_WIND_CHANCE_FLOOR2")) {
					config.SetCriminalSecondWindChanceFloor2(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_SECOND_WIND_CHANCE_FLOOR3")) {
					config.SetCriminalSecondWindChanceFloor3(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_SECOND_WIND_CHANCE_FLOOR4")) {
					config.SetCriminalSecondWindChanceFloor4(Float.parseFloat(xpp.nextText()));					
				} else if (elementName.equals("CRIMINAL_MAX_SECOND_WINDS")) {
					config.SetCriminalMaxSecondWinds(Integer.parseInt(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_ALLOW_MOCKING")) {
					config.SetCriminalAllowMocking(Boolean.parseBoolean(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_MOCKING_DISTANCE")) {
					config.SetCriminalMockingDistance(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_MOCKING_DISTANCE_END")) {
					config.SetCriminalMockingDistanceEnd(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("ONE_HIT_KILLS")) {
					config.SetOneHitKills(Boolean.parseBoolean(xpp.nextText()));
				} else if (elementName.equals("RANDOM_WEAPON_VELOCITY")) {
					config.SetAllowRandomWeaponVelocity(Boolean.parseBoolean(xpp.nextText()));
				} else if (elementName.equals("PIE_VELOCITY_MAX")) {
					config.SetPieMaximumVelocity(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("PIE_VELOCITY_MIN")) {
					config.SetPieMininumVelocity(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("BOWLING_BALL_VELOCITY_MAX")) {
					config.SetBowlingBallMaximumVelocity(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("BOWLING_BALL_VELOCITY_MIN")) {
					config.SetBowlingBallMinimumVelocity(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("MAX_WEAPONS_IN_ATTACK")) {
					config.SetMaxWeaponsAllowedInAttack(Integer.parseInt(xpp.nextText()));
				} else if (elementName.equals("CHICKENATOR_ATTACK_PERCENTAGE")) {
					config.SetChickenatorAttackPercentage(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("CHICKENATOR_MAX_VELOCITY")) {
					config.SetChickenatorMaximumVelocity(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("CHICKENATOR_MIN_VELOCITY")) {
					config.SetChickenatorMinimumVelocity(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("PIE_ATTACK_PERCENTAGE")) {
					config.SetPieAttackPercentage(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("BOWLING_BALL_ATTACK_PERCENTAGE")) {
					config.SetBowlingBallAttackPercentage(Float.parseFloat(xpp.nextText()));
				} else if (elementName.equals("STARTING_LIVES")) {
					config.SetStartingLives(Integer.parseInt(xpp.nextText()));
				} else if (elementName.equals("MAX_LIVES")) {
					config.SetMaxLives(Integer.parseInt(xpp.nextText()));
				} else if (elementName.equals("FREE_LIFE_SCORE")) {
					config.SetFreeLifeScore(Long.parseLong(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_MAX_SECOND_WINDS_FLOOR1")) {
					config.SetCriminalMaxSecondWindsFloor1(Integer.parseInt(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_MAX_SECOND_WINDS_FLOOR2")) {
					config.SetCriminalMaxSecondWindsFloor2(Integer.parseInt(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_MAX_SECOND_WINDS_FLOOR3")) {
					config.SetCriminalMaxSecondWindsFloor3(Integer.parseInt(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_MAX_SECOND_WINDS_FLOOR4")) {
					config.SetCriminalMaxSecondWindsFloor4(Integer.parseInt(xpp.nextText()));
				} else if (elementName.equals("CRIMINAL_MAX_DISTANCE")) {
					config.SetCriminalMaxDistance(Float.parseFloat(xpp.nextText()));
				}
			}

			if (eventType == XmlPullParser.END_TAG) {
				elementName = xpp.getName();

				if (elementName.equals("Difficulty")) {
					m_configs.put(config.GetDifficultyName(), config);					
				}
			}
			
			eventType = xpp.next();
		}
	}

}
