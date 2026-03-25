package com.capgemini.applicationservice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ApplicationRequest {

    private String fullName;
    private String phone;

    private String company;
    private Double salary;

    private Double amount;
    private Integer tenure;

}
