package com.shyyko.zxing.quick.camera;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.shyyko.zxing.quick.R;

/**
 * Camera preview callback
 */
public class PreviewCallback implements Camera.PreviewCallback {
    private static final String TAG = PreviewCallback.class.getSimpleName();
    /**
     * Xzing multi format reader
     */
    private final MultiFormatReader multiFormatReader = new MultiFormatReader();
    /**
     * Handler to send messages
     *
     * @see CaptureHandler
     */
    private Handler handler;
    /**
     * Camera manager
     */
    private CameraManager cameraManager;

    public PreviewCallback(Handler handler, CameraManager cameraManager) {
        this.handler = handler;
        this.cameraManager = cameraManager;
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        new DecodeAsyncTask(previewSize.width, previewSize.height).execute(bytes);
    }

    /**
     * Asynchronous task for decoding and finding barcode
     */
    private class DecodeAsyncTask extends AsyncTask<byte[], Void, Result> {
        /**
         * Width of image
         */
        private int width;
        /**
         * Height of image
         */
        private int height;

        /**
         * @param width  Width of image
         * @param height Height of image
         */
        private DecodeAsyncTask(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        protected void onPostExecute(Result result) {
            if (result != null) {
                Log.i(TAG, "Decode success.");
                if (handler != null) {
                    Message message = Message.obtain(handler, R.id.decoded);
                    Bundle bundle = new Bundle();
                    bundle.putString(CaptureHandler.DECODED_DATA, result.toString());
                    message.setData(bundle);
                    message.sendToTarget();
                }
            } else {
                Log.i(TAG, "Decode fail.");
                if (handler != null) {
                    Message message = Message.obtain(handler, R.id.decode_failed);
                    message.sendToTarget();
                }
            }
        }

        @Override
        protected Result doInBackground(byte[]... datas) {
            if (!cameraManager.hasCamera()) {
                return null;
            }
            Result rawResult = null;
            final PlanarYUVLuminanceSource source =
                    cameraManager.buildLuminanceSource(datas[0], width,
                            height, cameraManager.getBoundingRect());
            if (source != null) {
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    rawResult = multiFormatReader.decodeWithState(bitmap);
                } catch (ReaderException re) {
                    // continue
                } finally {
                    multiFormatReader.reset();
                }
            }

            return rawResult;
        }
    }
}
