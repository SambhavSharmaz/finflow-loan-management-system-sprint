package com.capgemini.applicationservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

}
