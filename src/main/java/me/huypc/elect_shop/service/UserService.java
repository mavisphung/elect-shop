package me.huypc.elect_shop.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import me.huypc.elect_shop.entity.User;

@Service
public class UserService implements UserDetailsService {
    
    private final Map<String, UserDetails> users = new ConcurrentHashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService() {
        // Create some demo users
        users.put("admin@shop.com", User.builder()
                .username("admin@shop.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .build());
        
        users.put("user@shop.com", User.builder()
                .username("user@shop.com")
                .password(passwordEncoder.encode("user123"))
                .role(User.Role.USER)
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }

    public boolean validateUser(String username, String password) {
        UserDetails user = users.get(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }
}
