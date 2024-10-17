package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.Call;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.NewCallRequest;
import com.example.backend.repository.CallRepository;
import com.example.backend.repository.UserRepository;

@Service
public class CallService {

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private UserRepository userRepository;

    public Call addNewCall(NewCallRequest newCallRequest, int userId) {
        User caller = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Caller not found"));

        User receiver = userRepository.findByUsername(newCallRequest.getReceiverUsername())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Call call = new Call();
        call.setCaller(caller);
        call.setReceiver(receiver);
        call.setCallStatus(Call.CallStatus.valueOf(newCallRequest.getCallStatus()));
        call.setStartedAt(newCallRequest.getStartedAt());
        call.setEndedAt(newCallRequest.getEndedAt());

        return callRepository.save(call);
    }
}
