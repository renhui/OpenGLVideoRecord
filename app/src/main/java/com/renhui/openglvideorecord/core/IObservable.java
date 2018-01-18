package com.renhui.openglvideorecord.core;


public interface IObservable<Type> {

    void addObserver(IObserver<Type> observer);

    void notify(Type type);

}
