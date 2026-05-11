package com.moneymanagerproject.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDto {
    private String email;
    private String password;
    private String token;
}
