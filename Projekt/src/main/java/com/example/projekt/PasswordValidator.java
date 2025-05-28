package com.example.projekt;

public class PasswordValidator {
    public static boolean isPasswordValid(String password) {
        if (password == null) return false;
        if (password.length() < 8) return false;

        if (!password.matches(".*[a-z].*")) return false; // mała litera
        if (!password.matches(".*[A-Z].*")) return false; // wielka litera
        if (!password.matches(".*\\d.*")) return false;   // cyfra


        return true;
    }


    public static String getPasswordRequirementsMessage() {
        return "Hasło musi mieć min. 8 znaków, " +
                "zawierać małą literę, wielką literę i cyfrę";
    }
}
