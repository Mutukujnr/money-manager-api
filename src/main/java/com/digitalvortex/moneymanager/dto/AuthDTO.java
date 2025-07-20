package com.digitalvortex.moneymanager.dto;

import jakarta.persistence.NamedEntityGraph;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthDTO {
    private String email;

    private String password;

    private String token;
}
