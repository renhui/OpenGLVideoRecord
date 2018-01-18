package com.renhui.openglvideorecord.core;

import com.renhui.openglvideorecord.egl.EglHelper;

public class RenderBean {

    public EglHelper egl;
    public int sourceWidth;
    public int sourceHeight;
    public int textureId;
    public boolean endFlag;

    public long timeStamp;
    public long textureTime;

    public long threadId;

}
