package com.camera.zhuoxin.camera.view;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    public MySurfaceView(Context context) {
        this(context, null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    SurfaceHolder holder;

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        holder = getHolder();
        holder.setKeepScreenOn(true);
        holder.setFormat(PixelFormat.RGB_888);
        holder.setSizeFromLayout();
        holder.addCallback(this);
    }

    Camera camera;
    Camera.Parameters parameters;

    //获取相机数量
    public int getNumber() {

        int numberOfCameras = camera.getNumberOfCameras();
        if (numberOfCameras == 0) {
            Toast.makeText(getContext(), "您的设备没有照相机", Toast.LENGTH_SHORT).show();
        }

        if (numberOfCameras == 1) {
            return 0;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }

        return 0;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        camera = Camera.open(getNumber());
        parameters = camera.getParameters();
//        List<String> supportedFlashModes = camera.getParameters().getSupportedFlashModes();
//        if (supportedFlashModes != null) {
//            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//        }
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        parameters.setGpsTimestamp(System.currentTimeMillis());
//        parameters.setPictureSize(768, 1280);
//        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
//        parameters.setAutoExposureLock(true);
//        parameters.setJpegQuality(100);
//        parameters.setPictureFormat(ImageFormat.JPEG);
//        parameters.setPreviewSize(768, 1280);
//        parameters.setRecordingHint(true);
//        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
//        parameters.setRotation(getContext().getResources().getConfiguration().orientation);
//        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        camera.setDisplayOrientation(getContext().getResources().getConfiguration().orientation);
        camera.setZoomChangeListener(new Camera.OnZoomChangeListener() {
            @Override
            public void onZoomChange(int zoomValue, boolean stopped, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setZoom(zoomValue);
                camera.setParameters(parameters);
            }
        });
        camera.startPreview();
        camera.startFaceDetection();

    }

    //判断手机是否支持闪光灯
    public static boolean isSupportCameraLedFlash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void setZoom(int zoom) {
        int maxZoom = camera.getParameters().getMaxZoom();
        Camera.Parameters parameters = camera.getParameters();
        if (zoom > maxZoom) {
            return;
        }
        parameters.setZoom(zoom);
        camera.setParameters(parameters);

    }

    public int getMaxZoom() {
        return camera.getParameters().getMaxZoom();
    }
}
