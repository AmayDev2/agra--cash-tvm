package com.amay.tom.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FareRowDTO {
    private String source;
    private String destination;
    private double fareAmount;

    // Getters and setters
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public double getFareAmount() { return fareAmount; }
    public void setFareAmount(double fareAmount) { this.fareAmount = fareAmount; }
}

