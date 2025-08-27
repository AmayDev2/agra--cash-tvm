package com.amay.tom.model.faretable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FareRow{
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
