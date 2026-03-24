package com.capgemini.auth_service;

import com.capgemini.auth_service.entity.Role;
import com.capgemini.auth_service.entity.User;
import com.capgemini.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class AuthServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
	}

	@Test
	void signupAndLoginReturnAToken() throws Exception {
		String signupPayload = """
				{
				  "name": "Test User",
				  "email": "test@example.com",
				  "password": "secret123"
				}
				""";

		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(signupPayload))
				.andExpect(status().isOk())
				.andExpect(content().string("User Registered Successfully"));

		String loginPayload = """
				{
				  "email": "test@example.com",
				  "password": "secret123"
				}
				""";

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginPayload))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.role").value("ROLE_USER"))
				.andExpect(jsonPath("$.token").isString());
	}

	@Test
	void adminCanLoginFromSamePortal() throws Exception {
		User admin = new User();
		admin.setName("Admin User");
		admin.setEmail("admin@example.com");
		admin.setPassword(passwordEncoder.encode("admin123"));
		admin.setRole(Role.ROLE_ADMIN);
		userRepository.save(admin);

		String loginPayload = """
				{
				  "email": "admin@example.com",
				  "password": "admin123"
				}
				""";

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginPayload))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
				.andExpect(jsonPath("$.token").isString());
	}
}
