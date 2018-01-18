package com.renhui.openglvideorecord;

import com.renhui.openglvideorecord.core.Renderer;
import com.renhui.openglvideorecord.media.SoundRecorder;
import com.renhui.openglvideorecord.media.SurfaceEncoder;
import com.renhui.openglvideorecord.media.SurfaceShower;
import com.renhui.openglvideorecord.media.TextureProvider;
import com.renhui.openglvideorecord.media.VideoSurfaceProcessor;
import com.renhui.openglvideorecord.media.store.IHardStore;
import com.renhui.openglvideorecord.media.store.StrengthenMp4MuxStore;

public class CameraRecorder {

    private VideoSurfaceProcessor mTextureProcessor;
    private TextureProvider mTextureProvider;
    private SurfaceShower mShower;
    private SurfaceEncoder mSurfaceStore;
    private IHardStore mMuxer;

    private SoundRecorder mSoundRecord;

    public CameraRecorder() {
        //用于视频混流和存储
        mMuxer = new StrengthenMp4MuxStore(true);

        //用于预览图像
        mShower = new SurfaceShower();
        mShower.setOutputSize(720, 1280);

        //用于编码图像
        mSurfaceStore = new SurfaceEncoder();
        mSurfaceStore.setStore(mMuxer);

        //用于音频
        mSoundRecord = new SoundRecorder(mMuxer);

        mTextureProvider = new TextureProvider();

        //用于处理视频图像
        mTextureProcessor = new VideoSurfaceProcessor();
        mTextureProcessor.setTextureProvider(mTextureProvider);
        mTextureProcessor.addObserver(mShower);
        mTextureProcessor.addObserver(mSurfaceStore);
    }

    public void setRenderer(Renderer renderer) {
        mTextureProcessor.setRenderer(renderer);
    }

    /**
     * 设置预览对象，必须是{@link android.view.Surface}、{@link android.graphics.SurfaceTexture}或者
     * {@link android.view.TextureView}
     *
     * @param surface 预览对象
     */
    public void setSurface(Object surface) {
        mShower.setSurface(surface);
    }

    /**
     * 设置录制的输出路径
     *
     * @param path 输出路径
     */
    public void setOutputPath(String path) {
        mMuxer.setOutputPath(path);
    }

    /**
     * 设置预览大小
     *
     * @param width  预览区域宽度
     * @param height 预览区域高度
     */
    public void setPreviewSize(int width, int height) {
        mShower.setOutputSize(width, height);
    }

    /**
     * 打开数据源
     */
    public void open() {
        mTextureProcessor.start();
    }

    /**
     * 关闭数据源
     */
    public void close() {
        mTextureProcessor.stop();
        stopRecord();
    }

    /**
     * 打开预览
     */
    public void startPreview() {
        mShower.open();
    }

    /**
     * 关闭预览
     */
    public void stopPreview() {
        mShower.close();
    }

    /**
     * 开始录制
     */
    public void startRecord() {
        mSurfaceStore.open();
        mSoundRecord.start();
    }

    /**
     * 关闭录制
     */
    public void stopRecord() {
        mSoundRecord.stop();
        mSurfaceStore.close();
        mMuxer.close();
    }

}
