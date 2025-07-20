package com.digitalvortex.moneymanager.service;

import com.digitalvortex.moneymanager.dto.AuthDTO;
import com.digitalvortex.moneymanager.dto.ProfileDTO;
import com.digitalvortex.moneymanager.mapper.MapperClass;
import com.digitalvortex.moneymanager.model.Profile;
import com.digitalvortex.moneymanager.repository.ProfileRepository;
import com.digitalvortex.moneymanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO){


      Profile profile =  MapperClass.convertDTOtoEntity(profileDTO);
      profile.setActivationToken(UUID.randomUUID().toString());
      profile.setPassword(passwordEncoder.encode(profileDTO.getPassword()));

        profile = profileRepository.save(profile);
        //set activation link

        String activationLink = activationURL+"/api/v1.0/activate?token="+profile.getActivationToken();

        String subject = "Activate your money manager account";
        String emailBody="click on the following link to activate your account: "+activationLink;

        emailService.sendEmail(profile.getEmail(),subject,emailBody);
        return MapperClass.convertEntityToDTo(profile);

    }

    public boolean activateProfile(String activationToken){
        Optional<Profile> byActivationToken = profileRepository.findByActivationToken(activationToken);

        if(byActivationToken.isPresent()){
            Profile activateProfile = byActivationToken.get();
            activateProfile.setIsActive(true);
            profileRepository.save(activateProfile);

            return true;
        }
        return false;
    }

    public boolean isAccountActive(String email){
    Profile profile = profileRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("username not found"));

        return profile.getIsActive();
    }

    public Profile getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

       String email = authentication.getName();
       return profileRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("profile with the email "+email+" not found"));
    }

    public ProfileDTO getPublicProfile(String email){
        Profile profile = null;

        if(email == null){
            profile = getCurrentProfile();
        }else{
            profile = profileRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("profile with the email "+email+" not found"));
        }

        return MapperClass.convertEntityToDTo(profile);
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {

            System.out.println("User in profile service:"+ authDTO.getEmail());
            System.out.println("password in profile service: {}"+ authDTO.getPassword());
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword())
            );

            // Generate JWT tokens
            String accessToken = jwtUtil.generateToken(authDTO.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(authDTO.getEmail());

            return Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "tokenType", "Bearer",
                    "expiresIn", 86400, // 24 hours in seconds
                    "user", getPublicProfile(authDTO.getEmail())
            );

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        } catch (DisabledException e) {
            throw new RuntimeException("Account is disabled");
        } catch (LockedException e) {
            throw new RuntimeException("Account is locked");
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("User not found");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
}
