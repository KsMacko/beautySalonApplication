package com.example.demo;

import com.example.demo.Controllers.Authorisation;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class Demo2ApplicationTests {
    @InjectMocks
    private Authorisation authorisation;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }
    @Test void testToSignInUser_Success() {
        User user = new User();
        user.setLogin("testUser");
        user.setPassword("password");
        user.setHashPassword("encodedPassword");
        when(userRepository.findByLogin(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        ResponseEntity<Map<String, String>> response = authorisation.toSignInUser(user);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Успех");
        assertEquals(ResponseEntity.ok(res), response); }
    @Test
    void testToSignInUser_Failure() {
        User user = new User();
        user.setLogin("testUser");
        user.setPassword("password");
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        ResponseEntity<Map<String, String>> response = authorisation.toSignInUser(user);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Ошибка");
        assertEquals(ResponseEntity.badRequest().body(res), response);
    }
    @Test
    void contextLoads() {
    }

}
