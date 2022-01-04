package com.alevikzs.springmultiplecronschedulers.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronProperties {

    private String name;
    private Integer threads;
    private String expression;

}
