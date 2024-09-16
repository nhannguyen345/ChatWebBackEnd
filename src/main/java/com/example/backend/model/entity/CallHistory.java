package com.example.backend.model.entity;

// import java.time.Duration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "call_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "call_id", nullable = false)
    private Call call;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    // private Duration callDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallStatus callStatus;

    public enum CallStatus {
        MISSED, COMPLETED, DECLINED
    }

    // getters and setters
}
