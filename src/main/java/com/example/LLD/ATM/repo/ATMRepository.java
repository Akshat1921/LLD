package com.example.LLD.ATM.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.LLD.ATM.enums.ATMStatus;
import com.example.LLD.ATM.models.ATM;

public class ATMRepository {
    private final Map<String, ATM> atms = new HashMap<>();

    public void save(ATM atm) {
        atms.put(atm.getId(), atm);
    }

    public Optional<ATM> getById(String id) {
        return Optional.ofNullable(atms.get(id));
    }

    public void updateATMStatusById(String id, ATMStatus newStatus) {
        atms.get(id).setStatus(newStatus);
    }
}