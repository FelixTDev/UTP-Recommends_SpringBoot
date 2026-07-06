package com.utp.recommends.common.validation;

import com.utp.recommends.common.exception.BusinessException;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;

public final class SuggestedTeacherNameParser {

    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");
    private static final Pattern TITLE_PREFIX = Pattern.compile("^(?i)(dr\\.|dra\\.|ing\\.|mg\\.|mtro\\.|lic\\.|arq\\.|abg\\.)\\s+.*$");
    private static final Pattern PERSON_NAME_PATTERN = Pattern.compile(ValidationPatterns.PERSON_NAME);
    private static final Set<String> FORBIDDEN_LITERAL_VALUES = Set.of("sin apellido");

    private SuggestedTeacherNameParser() {
    }

    public static SuggestedTeacherName fromRequestFields(String nombres, String apellidos, String legacyValue, String sourceLabel) {
        boolean hasSeparateFields = hasText(nombres) || hasText(apellidos);

        if (hasSeparateFields) {
            if (!hasText(nombres) || !hasText(apellidos)) {
                throw badRequest(sourceLabel + " debe incluir nombres y apellidos por separado");
            }
            return validateAndNormalize(nombres, apellidos, sourceLabel);
        }

        if (!hasText(legacyValue)) {
            throw badRequest(sourceLabel + " es obligatorio");
        }

        return fromLegacyValue(legacyValue, sourceLabel);
    }

    public static SuggestedTeacherName fromLegacyValue(String legacyValue, String sourceLabel) {
        if (!hasText(legacyValue)) {
            throw badRequest(sourceLabel + " es obligatorio");
        }

        String normalized = normalizeSpaces(legacyValue);
        String[] parts = normalized.split("\\|", -1);
        if (parts.length != 2) {
            throw badRequest(sourceLabel + " debe tener el formato exacto nombres|apellidos");
        }

        return validateAndNormalize(parts[0], parts[1], sourceLabel);
    }

    private static SuggestedTeacherName validateAndNormalize(String nombres, String apellidos, String sourceLabel) {
        String normalizedNombres = normalizeSpaces(nombres);
        String normalizedApellidos = normalizeSpaces(apellidos);

        validatePart(normalizedNombres, "nombres", sourceLabel);
        validatePart(normalizedApellidos, "apellidos", sourceLabel);

        return new SuggestedTeacherName(normalizedNombres, normalizedApellidos);
    }

    private static void validatePart(String value, String fieldName, String sourceLabel) {
        if (!hasText(value)) {
            throw badRequest(sourceLabel + " debe incluir " + fieldName + " válidos");
        }
        if (FORBIDDEN_LITERAL_VALUES.contains(value.toLowerCase())) {
            throw badRequest("No se permite usar valores de relleno para " + fieldName);
        }
        if (TITLE_PREFIX.matcher(value).matches()) {
            throw badRequest("No se permiten títulos profesionales en " + fieldName);
        }
        if (!PERSON_NAME_PATTERN.matcher(value).matches()) {
            throw badRequest(sourceLabel + " tiene " + fieldName + " con formato inválido");
        }
    }

    private static String normalizeSpaces(String value) {
        return MULTIPLE_SPACES.matcher(value == null ? "" : value.trim()).replaceAll(" ");
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isBlank();
    }

    private static BusinessException badRequest(String message) {
        return new BusinessException(HttpStatus.BAD_REQUEST, message);
    }
}
