package com.team1.epilogue.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GoogleUserInfo {
    private String sub;
    private String email;
    @JsonProperty("email_verified")
    private String emailVerified;
    private String name;
    private String picture;
    private String given_name;
    private String family_name;
    private String locale;
    private String aud;
}
