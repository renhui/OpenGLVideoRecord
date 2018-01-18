package com.renhui.openglvideorecord.core;

import java.util.ArrayList;


public class Observable<Type> implements IObservable<Type> {

    private ArrayList<IObserver<Type>> temp;

    @Override
    public void addObserver(IObserver<Type> observer) {
        if(temp==null){
            temp=new ArrayList<>();
        }
        temp.add(observer);
    }

    public void clear(){
        if(temp!=null){
            temp.clear();
            temp=null;
        }
    }

    @Override
    public void notify(Type type) {
        for (IObserver<Type> t:temp){
            t.onCall(type);
        }
    }

}