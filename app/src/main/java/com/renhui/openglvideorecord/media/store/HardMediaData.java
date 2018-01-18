package com.renhui.openglvideorecord.media.store;

import android.media.MediaCodec;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.ByteBuffer;

/**
 * HardMediaData
 */
public class HardMediaData {

    public int index = -1;
    public ByteBuffer data;
    public MediaCodec.BufferInfo info;

    public HardMediaData(ByteBuffer buffer, MediaCodec.BufferInfo info) {
        this.data = buffer;
        this.info = info;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void copyTo(HardMediaData data) {
        data.index = index;
        data.data.position(0);
        data.data.put(this.data);
        data.info.set(info.offset, info.size, info.presentationTimeUs, info.flags);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public HardMediaData copy() {
        ByteBuffer buffer = ByteBuffer.allocate(data.capacity());
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        HardMediaData data = new HardMediaData(buffer, info);
        copyTo(data);
        return data;
    }
}
