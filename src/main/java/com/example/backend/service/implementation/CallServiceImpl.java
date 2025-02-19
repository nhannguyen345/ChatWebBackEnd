package com.example.backend.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.Call;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.NewCallRequest;
import com.example.backend.repository.CallRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.CallService;

@Service
public class CallServiceImpl implements CallService {
    @Autowired
    private CallRepository callRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
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

    @Override
    public List<Call> getListCall(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return callRepository.findUserCall(user);
    }
}
