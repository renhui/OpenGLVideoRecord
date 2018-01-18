package com.renhui.openglvideorecord.media.store;


/**
 * IStore 文件存储接口
 */
public interface IStore<Track, Data> extends ICloseable {

    /**
     * 增加存储轨道
     *
     * @param track 待存储的内容信息
     * @return 轨道索引
     */
    int addTrack(Track track);

    /**
     * 写入内容到存储中
     *
     * @param track 轨道索引
     * @param data  存储内容，包括内容信息
     * @return 写入结果
     */
    int addData(int track, Data data);

}
