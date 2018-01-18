package com.renhui.openglvideorecord.media;

/**
 * MediaConfig 音视频编码信息设置
 */
public class MediaConfig {

    public Video mVideo = new Video();
    public Audio mAudio = new Audio();

    // 视频配置参数
    public class Video {
        public String mime = "video/avc";
        public int width = 368;
        public int height = 640;
        public int frameRate = 24;
        public int iframe = 1;
        public int bitrate = 1177600;
        public int colorFormat;
    }

    // 音频配置参数
    public class Audio {
        public String mime = "audio/mp4a-latm";
        public int sampleRate = 48000;
        public int channelCount = 2;
        public int bitrate = 128000;
        public int profile;
    }

}
