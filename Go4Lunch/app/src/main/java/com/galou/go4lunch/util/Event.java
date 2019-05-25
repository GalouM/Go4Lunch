package com.galou.go4lunch.util;

/**
 * Created by galou on 2019-05-25
 */
public class Event<T> {

    private T content;
    private Boolean hasBeenHandled = false;

    public Event(T content){
        if(content == null){
            throw new IllegalArgumentException("null values in Event are not allowed.");
        }
        this.content = content;
    }

    public T getContentIfNotHandle(){
        if(hasBeenHandled){
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    public Boolean getHasBeenHandled(){
        return hasBeenHandled;
    }
}
