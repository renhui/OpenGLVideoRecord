package com.renhui.openglvideorecord.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.renhui.openglvideorecord.base.BaseFilter;

/**
 * Oes纹理绘制滤镜
 */
public class OesFilter extends BaseFilter {

    private static final String vertexCode = "attribute vec4 aVertexCo;\n" +
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

    private static final String fragmentCode = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCo;\n" +
            "uniform samplerExternalOES uTexture;\n" +
            "void main() {\n" +
            "    gl_FragColor = texture2D( uTexture, vTextureCo);\n" +
            "}";


    public OesFilter() {
        super(null, vertexCode, fragmentCode);
    }

    @Override
    protected void onBindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(mGLTexture, 0);
    }
}
