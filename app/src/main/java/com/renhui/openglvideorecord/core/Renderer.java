package com.renhui.openglvideorecord.core;

/**
 * Renderer 渲染接口，渲染的四个接口应该在同一个GL线程中调用
 */
public interface Renderer {

    /**
     * 创建
     */
    void create();

    /**
     * 大小改变
     *
     * @param width  宽度
     * @param height 高度
     */
    void sizeChanged(int width, int height);

    /**
     * 渲染
     *
     * @param texture 输入纹理
     */
    void draw(int texture);

    /**
     * 销毁
     */
    void destroy();
}
