package com.shyyko.zxing.quick.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.shyyko.zxing.quick.camera.CameraManager;

/**
 * View for displaying bounds for active camera region
 */
public class BoundingView extends View {
    /**
     * Camera manager
     */
    private CameraManager cameraManager;

    public BoundingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets camera manger
     * @param cameraManager
     */
    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cameraManager != null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setARGB(110, 110, 110, 50);
            Rect boundingRect = cameraManager.getBoundingRectUi(canvas.getWidth(), canvas.getHeight());
            canvas.drawRect(boundingRect, paint);
        }
    }
}
