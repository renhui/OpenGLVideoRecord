package com.renhui.openglvideorecord.filter;

import android.content.res.Resources;

import com.renhui.openglvideorecord.base.BaseFilter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * GroupFilter 滤镜组，将多个滤镜串联起来，合并成一个滤镜
 */
public class GroupFilter extends LazyFilter {

    private ArrayList<BaseFilter> mGroup;

    public GroupFilter(Resources resource) {
        super();
    }

    public GroupFilter() {
        super();
    }

    @Override
    protected void initBuffer() {
        super.initBuffer();
        mGroup = new ArrayList<>();
    }

    public void addFilter(final BaseFilter filter) {
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                filter.create();
                filter.sizeChanged(mWidth, mHeight);
                mGroup.add(filter);
            }
        });
    }

    public void addFilter(final int index, final BaseFilter filter) {
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                filter.create();
                filter.sizeChanged(mWidth, mHeight);
                mGroup.add(index, filter);
            }
        });
    }

    public BaseFilter removeFilter(final int index) {
        BaseFilter filter = mGroup.get(index);
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                BaseFilter filter = mGroup.remove(index);
                if (filter != null) {
                    filter.destroy();
                }
            }
        });
        return filter;
    }

    public void removeFilter(final BaseFilter filter) {
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                mGroup.remove(filter);
            }
        });
    }

    public BaseFilter get(int index) {
        return mGroup.get(index);
    }

    public Iterator<BaseFilter> iterator() {
        return mGroup.iterator();
    }

    public boolean isEmpty() {
        return mGroup.isEmpty();
    }

    @Override
    public void draw(int texture) {
        int tempTextureId = texture;
        for (int i = 0; i < mGroup.size(); i++) {
            BaseFilter filter = mGroup.get(i);
            tempTextureId = filter.drawToTexture(tempTextureId);
        }
        super.draw(tempTextureId);
    }

}


