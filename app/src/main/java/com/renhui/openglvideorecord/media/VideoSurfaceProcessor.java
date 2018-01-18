package com.renhui.openglvideorecord.media;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.util.Log;

import com.renhui.openglvideorecord.base.BaseGLSL;
import com.renhui.openglvideorecord.core.FrameBuffer;
import com.renhui.openglvideorecord.core.IObserver;
import com.renhui.openglvideorecord.core.Observable;
import com.renhui.openglvideorecord.core.RenderBean;
import com.renhui.openglvideorecord.core.Renderer;
import com.renhui.openglvideorecord.egl.EGLConfigAttrs;
import com.renhui.openglvideorecord.egl.EGLContextAttrs;
import com.renhui.openglvideorecord.egl.EglHelper;

/**
 * VideoSurfaceProcessor 视频流图像处理类，以{@link TextureProvider}作为视频流图像输入。
 * 通过设置{@link com.renhui.openglvideorecord.core.IObserver}
 * 来接收处理完毕的{@link com.renhui.openglvideorecord.core.RenderBean}，并做相应处理，诸如展示、编码等。
 */
public class VideoSurfaceProcessor extends BaseGLSL {

    private String TAG = getClass().getSimpleName();

    private boolean mGLThreadFlag = false;
    private Thread mGLThread;
    private WrapRenderer mRenderer;
    private Observable<RenderBean> observable;
    private final Object LOCK = new Object();

    private TextureProvider mProvider;

    public VideoSurfaceProcessor() {
        observable = new Observable();
    }

    public void setTextureProvider(TextureProvider provider) {
        this.mProvider = provider;
    }

    public void start() {
        synchronized (LOCK) {
            if (!mGLThreadFlag) {
                if (mProvider == null) {
                    return;
                }
                mGLThreadFlag = true;
                mGLThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        glRun();
                    }
                });
                mGLThread.start();
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        synchronized (LOCK) {
            if (mGLThreadFlag) {
                mGLThreadFlag = false;
                mProvider.close();
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setRenderer(Renderer renderer) {
        mRenderer = new WrapRenderer(renderer);
    }

    private void glRun() {
        EglHelper egl = new EglHelper();
        boolean ret = egl.createGLESWithSurface(new EGLConfigAttrs(), new EGLContextAttrs(), new SurfaceTexture(1));
        if (!ret) {
            //todo 错误处理
            return;
        }
        int mInputSurfaceTextureId = createTextureID();
        SurfaceTexture mInputSurfaceTexture = new SurfaceTexture(mInputSurfaceTextureId);

        Point size = mProvider.open(mInputSurfaceTexture);
        Log.d(TAG, "Provider Opened . data size (x,y)=" + size.x + "/" + size.y);
        if (size.x <= 0 || size.y <= 0) {
            //todo 错误处理
            destroyGL(egl);
            synchronized (LOCK) {
                LOCK.notifyAll();
            }
            return;
        }
        int mSourceWidth = size.x;
        int mSourceHeight = size.y;
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
        //要求数据源提供者必须同步返回数据大小
        if (mSourceWidth <= 0 || mSourceHeight <= 0) {
            error(1, "video source return inaccurate size to SurfaceTextureActuator");
            return;
        }

        if (mRenderer == null) {
            mRenderer = new WrapRenderer(null);
        }
        FrameBuffer sourceFrame = new FrameBuffer();
        mRenderer.create();
        mRenderer.sizeChanged(mSourceWidth, mSourceHeight);
        mRenderer.setFlag(mProvider.isLandscape() ? WrapRenderer.TYPE_CAMERA : WrapRenderer.TYPE_MOVE);

        //用于其他的回调
        RenderBean rb = new RenderBean();
        rb.egl = egl;
        rb.sourceWidth = mSourceWidth;
        rb.sourceHeight = mSourceHeight;
        rb.endFlag = false;
        rb.threadId = Thread.currentThread().getId();
        Log.d(TAG, "Processor While Loop Entry");
        //要求数据源必须同步填充SurfaceTexture，填充完成前等待
        while (!mProvider.frame() && mGLThreadFlag) {
            mInputSurfaceTexture.updateTexImage();
            mInputSurfaceTexture.getTransformMatrix(mRenderer.getTextureMatrix());
            Log.d(TAG, "timestamp:" + mInputSurfaceTexture.getTimestamp());
            sourceFrame.bindFrameBuffer(mSourceWidth, mSourceHeight);
            GLES20.glViewport(0, 0, mSourceWidth, mSourceHeight);
            mRenderer.draw(mInputSurfaceTextureId);
            sourceFrame.unBindFrameBuffer();
            rb.textureId = sourceFrame.getCacheTextureId();
            //接收数据源传入的时间戳
            rb.timeStamp = mProvider.getTimeStamp();
            rb.textureTime = mInputSurfaceTexture.getTimestamp();
            observable.notify(rb);
        }
        Log.d(TAG, "out of gl thread loop");
        synchronized (LOCK) {
            rb.endFlag = true;
            observable.notify(rb);
            mRenderer.destroy();
            destroyGL(egl);
            LOCK.notifyAll();
            Log.d(TAG, "gl thread exit");
        }
    }

    private void destroyGL(EglHelper egl) {
        mGLThreadFlag = false;
        EGL14.eglMakeCurrent(egl.getDisplay(), EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
        EGL14.eglDestroyContext(egl.getDisplay(), egl.getDefaultContext());
        EGL14.eglTerminate(egl.getDisplay());
    }

    public void addObserver(IObserver<RenderBean> observer) {
        observable.addObserver(observer);
    }

    protected void error(int id, String msg) {

    }
}
