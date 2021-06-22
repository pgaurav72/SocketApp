package com.example.socket;

public interface AttestrFlowxEventListener {

    void connectStatus(Boolean connectionStatus);
    void onSuccess(String response);
    void onFailure(String exception);

}
