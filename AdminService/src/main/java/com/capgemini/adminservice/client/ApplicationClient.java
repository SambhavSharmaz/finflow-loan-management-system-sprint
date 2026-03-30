package com.capgemini.adminservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "APPLICATION-SERVICE", url = "${APPLICATION_SERVICE_URL:http://localhost:8082}")
public interface ApplicationClient {

    @PutMapping("/applications/{id}/status")
    void updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status
    );

    @GetMapping("/applications/all")
    java.util.List<com.capgemini.adminservice.dto.ApplicationDTO> getAllApplications();
}
