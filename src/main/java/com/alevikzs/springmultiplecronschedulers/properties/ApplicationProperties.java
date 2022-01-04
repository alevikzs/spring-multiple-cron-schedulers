package com.alevikzs.springmultiplecronschedulers.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private Integer awaitTerminationSeconds;
    private Integer taskExecutionSeconds;
    private List<CronProperties> crons;

}
