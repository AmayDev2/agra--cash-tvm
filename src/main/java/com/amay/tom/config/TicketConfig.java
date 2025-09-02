package com.amay.tom.config;

import com.amay.tom.config.dto.TicketConfigDTO;
import com.amay.tom.utils.env.EnvFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public enum
TicketConfig {
    INSTANT;
    private TicketConfigDTO ticketConfigDTO;

    public void getTicketConfig() {
        ticketConfigDTO = new TicketConfigDTO();///objectMapper.readValue(new File(EnvFile.getTicketConfigFile()),TicketConfigDTO.class);
        // Print station details
        System.out.println(ticketConfigDTO.toString());
    }

    public TicketConfigDTO getProductTypeDefDTO() {
        if (ticketConfigDTO == null) {
            getTicketConfig();
        }
        return ticketConfigDTO;
    }

}
