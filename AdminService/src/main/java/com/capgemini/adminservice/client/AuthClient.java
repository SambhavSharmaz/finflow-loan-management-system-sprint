package com.capgemini.adminservice.client;

import com.capgemini.adminservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "AUTH-SERVICE", url = "${AUTH_SERVICE_URL:http://localhost:8081}")
public interface AuthClient {

    @GetMapping("/auth/users")
    List<UserDTO> getAllUsers();

    @PutMapping("/auth/users/{id}")
    UserDTO updateUser(@PathVariable("id") Long id, @RequestParam("role") String role);
}
