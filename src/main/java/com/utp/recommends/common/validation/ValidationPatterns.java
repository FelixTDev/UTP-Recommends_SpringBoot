package com.utp.recommends.common.validation;

public final class ValidationPatterns {

    public static final String UTP_STUDENT_EMAIL = "^U[0-9]{8}@utp\\.edu\\.pe$";
    public static final String UTP_ADMIN_EMAIL = "^[A-Za-z0-9._%+-]+@utp\\.edu\\.pe$";
    public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%&*?_\\-])[A-Za-z\\d!@#$%&*?_\\-]{8,}$";
    public static final String PERSON_NAME = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,100}$";

    private ValidationPatterns() {
    }
}
