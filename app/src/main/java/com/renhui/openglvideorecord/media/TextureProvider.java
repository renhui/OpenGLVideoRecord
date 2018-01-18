package com.renhui.openglvideorecord.media;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * 纹理提供者---其实就是相机控制逻辑
 */
public class TextureProvider {

    private final static int cameraId = 1;

    private Camera mCamera;

    private Semaphore mFrameSem;

    // 视频帧监听器
    private SurfaceTexture.OnFrameAvailableListener frameListener = new SurfaceTexture.OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            Log.d("TextureProvider", "onFrameAvailable");
            mFrameSem.drainPermits();
            mFrameSem.release();
        }

    };

    /**
     * 打开视频流数据源（摄像头）
     *
     * @param surface 数据流输出到此
     * @return 视频流的宽高
     */
    public Point open(final SurfaceTexture surface) {
        final Point size = new Point();
        try {
            mFrameSem = new Semaphore(0);
            mCamera = Camera.open(cameraId);
            mCamera.setPreviewTexture(surface);
            surface.setOnFrameAvailableListener(frameListener);
            Camera.Size s = mCamera.getParameters().getPreviewSize();
            mCamera.startPreview();
            size.x = s.height;
            size.y = s.width;
            Log.i("TextureProvider", "Camera Opened");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 关闭视频流数据源
     */
    public void close() {
        mFrameSem.drainPermits();
        mFrameSem.release();

        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    /**
     * 获取一帧数据
     *
     * @return 是否最后一帧
     */
    public boolean frame() {
        try {
            mFrameSem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前帧时间戳
     *
     * @return 时间戳
     */
    public long getTimeStamp() {
        return -1;
    }

    /**
     * 视频流是否是横向的
     *
     * @return true or false
     */
    public boolean isLandscape() {
        return true;
    }

}
