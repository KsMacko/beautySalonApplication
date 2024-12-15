package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.example.demo.Entities.Client;
import com.example.demo.Entities.Roles;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.ClientRepository;
import com.example.demo.Repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorisationTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private UserRepository userRepository;
    @MockBean private ClientRepository clientRepository;
    @MockBean private PasswordEncoder passwordEncoder;
    @Test
    public void testToSignInUserSuccess() throws Exception {
        User user = new User();
        user.setLogin("testUser");
        user.setPassword("password123");

        User existingUser = new User();
        existingUser.setLogin("testUser");
        existingUser.setHashPassword("encodedPassword");
        existingUser.setRole(Roles.CLIENT);
        when(userRepository.findByLogin(user.getLogin())).thenReturn(existingUser);
        when(passwordEncoder.matches(user.getPassword(), existingUser.getHashPassword())).thenReturn(true);

        this.mockMvc.perform(post("/auth/toSignIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Успех"))
                .andExpect(jsonPath("$.role").value(existingUser.getRole().toString()));
    }

    @Test
    public void testToSignInUserFailure() throws Exception {
        User user = new User();
        user.setLogin("testUser");
        user.setPassword("wrongPassword");

        when(userRepository.findByLogin(user.getLogin())).thenReturn(null);

        this.mockMvc.perform(post("/auth/toSignIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Ошибка"));
    }

    @Test
    public void testToSignUpUserSuccess() throws Exception {
        User user = new User();
        user.setLogin("newUser");
        user.setPassword("password123");

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(clientRepository.save(any(Client.class))).thenReturn(new Client());

        this.mockMvc.perform(post("/auth/toSignUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Успех"));
    }

    @Test
    public void testToSignUpUserFailure() throws Exception {
        User user = new User();
        user.setLogin("newUser");
        user.setPassword("password123");

        when(passwordEncoder.encode(user.getPassword())).thenThrow(new RuntimeException("Encoding error"));
            this.mockMvc.perform(post("/auth/toSignUp")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(user)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andExpect(jsonPath("$.message").value("Ошибка"));
        verify(passwordEncoder, times(1)).encode(user.getPassword());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
