package com.renhui.openglvideorecord.media.store;

import android.util.SparseArray;

import java.util.concurrent.LinkedBlockingQueue;

class Recycler<T> {

    private SparseArray<LinkedBlockingQueue<T>> datas=new SparseArray<>();

    public void put(int index,T t){
        if(datas.indexOfKey(index)<0){
            datas.append(index,new LinkedBlockingQueue<T>());
        }
        datas.get(index).add(t);
    }

    public T poll(int index){
        if(datas.indexOfKey(index)>=0){
            return datas.get(index).poll();
        }
        return null;
    }

    public void clear(){
        datas.clear();
    }

}
