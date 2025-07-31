package com.digitalvortex.moneymanager.controller;

import com.digitalvortex.moneymanager.dto.AuthDTO;
import com.digitalvortex.moneymanager.dto.ProfileDTO;
import com.digitalvortex.moneymanager.model.Profile;
import com.digitalvortex.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){

        ProfileDTO registerProfile = profileService.registerProfile(profileDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean activateProfile = profileService.activateProfile(token);

        if(activateProfile){
            return ResponseEntity.ok("account activated successfully");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("activation token not found or has been used");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO){
        try {
            // Check if account is active first
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "account is not active. please activate your account first"
                ));
            }

            // Authenticate and generate tokens
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // FIX: Return proper error response instead of null
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", e.getMessage(),
                    "error", "Authentication failed"
            ));
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getProfile(){
        ProfileDTO publicProfile = profileService.getPublicProfile(null);

        return ResponseEntity.ok(publicProfile);
    }


}
