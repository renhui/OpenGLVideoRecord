package com.renhui.openglvideorecord.filter;

import com.renhui.openglvideorecord.base.BaseFilter;

/**
 * LazyFilter 绘制原始纹理的Filter,通过矩阵提供旋转缩放等功能
 */
public class LazyFilter extends BaseFilter {

    private static final String vertexCode =
            "attribute vec4 aVertexCo;\n" +
                    "attribute vec2 aTextureCo;\n" +
                    "uniform mat4 uVertexMatrix;\n" +
                    "uniform mat4 uTextureMatrix;\n" +
                    "\n" +
                    "varying vec2 vTextureCo;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    gl_Position = uVertexMatrix*aVertexCo;\n" +
                    "    vTextureCo = (uTextureMatrix*vec4(aTextureCo,0,1)).xy;\n" +
                    "}";

    private static final String fragmentCode =
            "precision mediump float;\n" +
                    "varying vec2 vTextureCo;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D( uTexture, vTextureCo);\n" +
                    "}";


    public LazyFilter() {
        super(null, vertexCode, fragmentCode);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
