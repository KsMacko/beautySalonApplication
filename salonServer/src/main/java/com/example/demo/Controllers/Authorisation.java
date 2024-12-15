package com.example.demo.Controllers;

import com.example.demo.Entities.Client;
import com.example.demo.Entities.Roles;
import com.example.demo.Entities.Sex;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.ClientRepository;
import com.example.demo.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class Authorisation {
    @Autowired UserRepository userRepository;
    @Autowired ClientRepository clientRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/toSignIn")
    public ResponseEntity<Map<String, String>> toSignInUser(@RequestBody User user){
        Map<String, String> response = new HashMap<>();
        User existingUser = userRepository.findByLogin(user.getLogin());
        if (existingUser != null &&
                passwordEncoder.matches(user.getPassword(), existingUser.getHashPassword())) {
            response.put("message", "Успех");
            response.put("role", existingUser.getRole().toString());
            return ResponseEntity.ok(response);
        }else {
            response.put("message", "Ошибка");
            return ResponseEntity.badRequest().body(response);}
    }

    @PostMapping("/toSignUp")
    public ResponseEntity<Map<String, String>> toSignUpUser(@RequestBody User user){
        Map<String, String> response = new HashMap<>();
        try {
            user.setHashPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Roles.CLIENT);
            userRepository.save(user);
            Client client = new Client();
            client.setUser(user);
            client.setClientLogin(user.getLogin());
            client.setAvatarPath("C:\\Users\\Солнышко\\Documents\\GitHub\\salonClient\\src\\main\\resources\\com\\example\\salonclient\\defAvatar.jpg");
            client.setSex(Sex.FEMALE);
            client.setName("");
            client.setTelephone("");
            clientRepository.save(client);
            response.put("message", "Успех");
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            response.put("message", "Ошибка");
            return ResponseEntity.badRequest().body(response);
        }
    }
}