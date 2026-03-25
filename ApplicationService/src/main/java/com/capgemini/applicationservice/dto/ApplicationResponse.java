package com.capgemini.applicationservice.dto;

import com.capgemini.applicationservice.entity.Status;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ApplicationResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String company;
    private Double salary;
    private Double amount;
    private Integer tenure;
    private Status status;

}
