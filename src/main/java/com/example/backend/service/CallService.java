package com.example.backend.service;

import java.util.List;

import com.example.backend.model.entity.Call;
import com.example.backend.model.request.NewCallRequest;

public interface CallService {

    public Call addNewCall(NewCallRequest newCallRequest, int userId);

    public List<Call> getListCall(int userId);
}
