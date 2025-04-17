package com.prakass.aps.dto;

import lombok.Getter;

@Getter
public enum TokenType {
    RESET_PASSWORD("resetPassword");

    private final String type;

    TokenType(String type){
        this.type = type;
    }
}
