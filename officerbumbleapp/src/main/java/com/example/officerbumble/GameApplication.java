package com.example.officerbumble;

import android.app.Application;
import com.facebook.SessionDefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.utils.Logger;

public class GameApplication extends Application {
	private static final String APP_ID = "1423762594570309";
	private static final String APP_NAMESPACE = "officerbumble";

	@Override
	public void onCreate() {
		super.onCreate();

		// set log to true
		Logger.DEBUG_WITH_STACKTRACE = true;

		// initialize facebook configuration
		Permission[] permissions = new Permission[] { 
				Permission.PUBLIC_PROFILE, 
				Permission.USER_GROUPS,
				Permission.USER_BIRTHDAY, 
				Permission.USER_LIKES, 
				Permission.USER_PHOTOS,
				Permission.USER_VIDEOS,
				Permission.USER_FRIENDS,
				Permission.USER_EVENTS,
				Permission.USER_VIDEOS,
				Permission.USER_RELATIONSHIPS,
				Permission.READ_STREAM, 
				Permission.PUBLISH_ACTION
				};

		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
			.setAppId(APP_ID)
			.setNamespace(APP_NAMESPACE)
			.setPermissions(permissions)
			.setDefaultAudience(SessionDefaultAudience.FRIENDS)
			.setAskForAllPermissionsAtOnce(false)
			.build();

		SimpleFacebook.setConfiguration(configuration);
	}
}
