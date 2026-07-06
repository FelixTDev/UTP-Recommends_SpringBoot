package com.utp.recommends.common.validation;

public record SuggestedTeacherName(
    String nombres,
    String apellidos
) {
    public String toLegacyValue() {
        return nombres + "|" + apellidos;
    }
}
