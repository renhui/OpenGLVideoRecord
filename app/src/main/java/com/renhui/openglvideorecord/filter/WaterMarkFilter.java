package com.renhui.openglvideorecord.filter;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class WaterMarkFilter extends LazyFilter {

    private int[] viewPort = new int[4];
    private int[] markPort = new int[4];
    private final LazyFilter mark = new LazyFilter() {

        @Override
        protected void onClear() {

        }
    };
    private int markTextureId = -1;

    public WaterMarkFilter setMarkPosition(final int x, final int y, final int width, final int height) {
        markPort[0] = x;
        markPort[1] = y;
        markPort[2] = width;
        markPort[3] = height;
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                mark.sizeChanged(width, height);
            }
        });
        return this;
    }

    public WaterMarkFilter setMark(final Bitmap bmp) {
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                if (bmp != null) {
                    if (markTextureId == -1) {
                        markTextureId = createTextureID(false);
                    } else {
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, markTextureId);
                    }
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
                    bmp.recycle();
                } else {
                    if (markTextureId != -1) {
                        GLES20.glDeleteTextures(1, new int[]{markTextureId}, 0);
                    }
                }
            }
        });
        return this;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mark.create();
    }

    @Override
    protected void onDraw() {
        super.onDraw();
        if (markTextureId != -1) {
            GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewPort, 0);
            GLES20.glViewport(markPort[0], mHeight - markPort[3] - markPort[1], markPort[2], markPort[3]);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
            mark.draw(markTextureId);
            GLES20.glDisable(GLES20.GL_BLEND);

            GLES20.glViewport(viewPort[0], viewPort[1], viewPort[2], viewPort[3]);
        }
    }
}
