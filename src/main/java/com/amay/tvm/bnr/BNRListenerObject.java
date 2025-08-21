package com.amay.tvm.bnr;

import jakarta.persistence.Access;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class BNRListenerObject {
    private int insertedAmount;
    private int remainedAmount;
    private String informationMessage;
    private List<Integer> allowedNotes;
    private BNRStatus status;
}
