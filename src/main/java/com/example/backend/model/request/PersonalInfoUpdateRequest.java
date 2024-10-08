package com.example.backend.model.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfoUpdateRequest {
    private int userId;
    private String birthdate;
    private String phone;
    private String address;

    public static LocalDate convertBitrhdate(String birthdateString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(birthdateString, dateTimeFormatter);
    }
}
