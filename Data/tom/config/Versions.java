package com.amay.tom.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class Versions {
    private String  version = "0.0.0";
//    private String  buildDate = "2023-10-01";
//    private String  buildTime = "00:00:00";
//    private String  buildNumber = "0";
//    private String  buildCommit = "0000000";
//    private String  buildBranch = "main";

}
