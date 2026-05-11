package com.moneymanagerproject.controller;

import com.moneymanagerproject.dto.AuthDto;
import com.moneymanagerproject.dto.ProfileDto;
import com.moneymanagerproject.entity.ProfileEntity;
import com.moneymanagerproject.service.ProfileService;
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
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto){
        ProfileDto registerProfileDto=profileService.registerProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfileDto);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String  token){
        boolean isActivated=profileService.activateProfile(token);
        if(isActivated){
            return ResponseEntity.status(HttpStatus.CREATED).body("Profile Activated Successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token Not Valid or Used");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDto authDto){
        try{
            if(!profileService.isAccountActivated(authDto.getEmail())){
              return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                      "message", "Account is not activated.Please activate it."
              ));
            }

            Map<String,Object> response=profileService.authenticateAndGenerateToken(authDto);
             return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }


}
