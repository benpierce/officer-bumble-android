package com.example.officerbumble.engine;

import java.io.IOException;
import java.io.StringReader;
import java.util.TreeMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class BadgeManager {
	private TreeMap<Integer, Badge> m_badges;
	
	public BadgeManager() {
		 m_badges = new TreeMap<Integer, Badge>();
	}
	
	public Badge GetNextBadge(int _criminalsCaught) {
		Badge result = null;
		
		for(Integer value : m_badges.keySet()) {
			if(value > _criminalsCaught) {
				result = m_badges.get(value);
				break;
			}
		}
		
		return result;
	}
	
	public Badge QueryBadge(int _criminalsCaught) {
		Badge result = null;
		
		result = m_badges.get(Integer.valueOf(_criminalsCaught));		
		
		return result;
	}
	
	public void Load(Context _context, int _configurationResourceId) throws XmlPullParserException, IOException {
		String configData = TextResourceReader.ReadTextFileFromResource(_context, _configurationResourceId);

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(configData));

		int eventType = xpp.getEventType();
		String elementName = null;

		String badgeName = "";
		int criminalsCaught = 0;
		String animationTag = "";
		
		while (eventType != XmlPullParser.END_DOCUMENT) {

			if (eventType == XmlPullParser.START_TAG) {				
				elementName = xpp.getName();

				if (elementName.equals("Badge")) {
					badgeName = "";
					criminalsCaught = 0;
					// This is the opening difficulty tag.
				} else if (elementName.equals("BadgeName")) {
					badgeName = xpp.nextText();
				} else if (elementName.equals("CriminalsCaught")) {
					criminalsCaught = Integer.parseInt(xpp.nextText());					
				} else if (elementName.equals("AnimationTag")) {
					animationTag = xpp.nextText();
				}
			}

			if (eventType == XmlPullParser.END_TAG) {
				elementName = xpp.getName();

				if (elementName.equals("Badge")) {
					m_badges.put(criminalsCaught, new Badge(badgeName, criminalsCaught, animationTag));				
				}
			}
			
			eventType = xpp.next();
		}
	}
		
	public class Badge {
		private String m_badgeName = "";
		private String m_animationTag = "";
		private int m_criminalsCaught = 0;
		
		public Badge(String _badgeName, int _criminalsCaught, String _animationTag) {
			m_badgeName = _badgeName;
			m_criminalsCaught = _criminalsCaught;
			m_animationTag = _animationTag;
		}
		
		public String GetBadgeName() {
			return m_badgeName;
		}
		
		public int GetCriminalsCaught() {
			return m_criminalsCaught;
		}
		
		public String GetAnimationTag() {
			return m_animationTag;
		}
	}
		
}
