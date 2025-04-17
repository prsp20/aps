package com.prakass.aps.dto;

import lombok.Getter;

@Getter
public enum PasswordType {
    RESET_PASSWORD("resetPassword"),
    CHANGE_PASSWORD("changePassword");

    private final String type;

    PasswordType(String type){
        this.type = type;
    }
}
