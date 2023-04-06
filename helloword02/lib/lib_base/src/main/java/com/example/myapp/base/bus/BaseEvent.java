package com.example.myapp.base.bus;

public class BaseEvent {
    private String key;
    private Object value;

    public BaseEvent() {
    }

    public BaseEvent(String key) {
        this.key = key;
    }

    public BaseEvent(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
