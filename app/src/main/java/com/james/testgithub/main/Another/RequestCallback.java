package com.james.testgithub.main.Another;

/**
 * Created by james on 30.01.15.
 */
public abstract class RequestCallback<K> {
    public abstract void onSuccess(K k);
    public abstract void onFailure(int error_code);
}
