package com.amay.tom.model.faretable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FareRowEntity {
    private String source;
    private String destination;
    private double fareAmount;
    public FareRowEntity() {}
    public FareRowEntity(String source, String destination, double fareAmount) {
        this.source = source;
        this.destination = destination;
        this.fareAmount = fareAmount;
    }
    // Getters and setters
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public double getFareAmount() { return fareAmount; }
    public void setFareAmount(double fareAmount) { this.fareAmount = fareAmount; }
}

