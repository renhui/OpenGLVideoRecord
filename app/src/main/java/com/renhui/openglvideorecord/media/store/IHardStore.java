package com.renhui.openglvideorecord.media.store;

import android.media.MediaFormat;

/**
 * IHardStore 硬编码的媒体文件存储器
 */
public interface IHardStore extends IStore<MediaFormat, HardMediaData> {

    /**
     * 设置存储路径
     *
     * @param path 路径
     */
    void setOutputPath(String path);

}