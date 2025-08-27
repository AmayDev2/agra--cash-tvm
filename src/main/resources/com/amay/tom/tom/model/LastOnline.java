package com.amay.tom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class LastOnline implements Serializable {
    public LastOnline setSCU(Instant SCU) {
        this.SCU = SCU;
        return this;
    }

    public LastOnline setCCU(Instant CCU) {
        this.CCU = CCU;
        return this;
    }

    private
    Instant SCU;
    private
    Instant CCU;
}
