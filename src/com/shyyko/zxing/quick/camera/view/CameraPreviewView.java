package com.shyyko.zxing.quick.camera.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.shyyko.zxing.quick.camera.CameraManager;

import java.io.IOException;

/**
 * Camera preview view. Shows camera preview data
 */
public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreviewView.class.getSimpleName();

    /**
     * Surface holder for camera preview data
     */
    private SurfaceHolder surfaceHolder;
    /**
     * Camera manager
     */
    private CameraManager cameraManager;

    public CameraPreviewView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * Setter for camera manager
     * @param cameraManager camera manager to set
     */
    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            cameraManager.setCameraDisplayOrientation((Activity) getContext());
            cameraManager.getCamera().setPreviewDisplay(holder);
            cameraManager.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // empty. Taking care of releasing the Camera preview in activity.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            cameraManager.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            cameraManager.setCameraDisplayOrientation((Activity) getContext());
            cameraManager.getCamera().setPreviewDisplay(surfaceHolder);
            cameraManager.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
