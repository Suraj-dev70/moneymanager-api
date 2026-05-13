package com.moneymanagerproject.service;

import org.springframework.beans.factory.annotation.Value;
import com.moneymanagerproject.dto.AuthDto;
import com.moneymanagerproject.dto.ProfileDto;
import com.moneymanagerproject.entity.ProfileEntity;
import com.moneymanagerproject.repository.ProfileRepository;
import com.moneymanagerproject.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.moneymanagerproject.service.EmailService;
import java.util.Map;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class ProfileService {

    private final EmailService emailService;

    private final ProfileRepository profileRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;
    @Value("${app.activation.url}")
    private  String activationUrl;

    public ProfileDto registerProfile(ProfileDto profileDto) {
        ProfileEntity newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        try {
            String activationLink = activationUrl + "/api/v3.5.13/activate?token="
                    + newProfile.getActivationToken();
            String subject = "Activate Your Money Manager Account";
            String body = "Click to activate: " + activationLink;
            emailService.sendSimpleMail(newProfile.getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
        }

        return toDto(newProfile);
    }

    public ProfileEntity toEntity(ProfileDto profileDto) {
        return ProfileEntity.builder()
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(passwordEncoder.encode(profileDto.getPassword()))
                .profileImageUrl(profileDto.getProfileImageUrl())
                .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();
    }

    public ProfileDto toDto(ProfileEntity profileEntity) {
        return ProfileDto.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }


    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile->{
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public boolean isAccountActivated(String email) {
      return  profileRepository.findByEmail(email)
              .map(ProfileEntity::getIsActive)
              .orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       return profileRepository.findByEmail(authentication.getName())
               .orElseThrow(()-> new UsernameNotFoundException("Profile not found with email="+authentication.getName()));

    }

    public ProfileDto getPublicProfile(String email) {
        ProfileEntity currentProfile =null;
           if(email==null){
             currentProfile=  getCurrentProfile();
           }else{
             currentProfile=  profileRepository.findByEmail(email)
                       .orElseThrow(()-> new UsernameNotFoundException("Profile not found with email="+email));
           }

           return ProfileDto.builder()
                   .id(currentProfile.getId())
                   .fullName(currentProfile.getFullName())
                   .email(currentProfile.getEmail())
                   .profileImageUrl(currentProfile.getProfileImageUrl())
                   .createdAt(currentProfile.getCreatedAt())
                   .updatedAt(currentProfile.getUpdatedAt())
                   .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {
           try{
               authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
               //Generate Jwt token
               String token= jwtUtil.generateToken(authDto.getEmail());
               return Map.of(
                       "token",token,
                       "user",getPublicProfile(authDto.getEmail())
               );
           }catch(Exception e){
            throw new RuntimeException("Invalid email or password");
           }
    }
}
