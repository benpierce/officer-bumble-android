package com.example.officerbumble.engine;

import static android.opengl.Matrix.orthoM;

public class Matrix {

	public static float[] GetOrthographicProjectionMatrix(DeviceDisplay _display) {		
		float[] projectionMatrix = new float[16];
		float aspectRatio;
		
		aspectRatio = _display.GetAspectRatio();

        // Figure out our wide screen sizing correction.
        //let targetHeight = TARGET_ASPECT_RATIO.height / TARGET_ASPECT_RATIO.width * self.sceneSize.width
        //wideScreenCorrection = targetHeight / self.sceneSize.height

		if(_display.GetDisplayOrientation() == DeviceDisplay.DISPLAY_ORIENTATION.LANDSCAPE) {
			orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
		} else {
			orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
		}
		
		return projectionMatrix;
	}
}