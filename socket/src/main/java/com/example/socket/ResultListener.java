package com.example.socket;

public interface ResultListener<T> {
    void listen(T msg);
}
