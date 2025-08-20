package me.huypc.elect_shop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huypc.elect_shop.generated.api.AuthApi;
import me.huypc.elect_shop.generated.dto.LoginRequest;
import me.huypc.elect_shop.generated.dto.LoginResponse;
import me.huypc.elect_shop.service.JwtService;
import me.huypc.elect_shop.service.UserService;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<Void> checkAuth() {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<LoginResponse> loginUser(LoginRequest loginRequest) {
        boolean isValidUser = userService.validateUser(loginRequest.getEmail(), loginRequest.getPassword());

        if (!isValidUser) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponse(token, userDetails.getUsername()));
    }

}
