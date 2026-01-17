package utilities;

import java.util.regex.Pattern;

public class FieldValidator {

    // Validazione username
    public static boolean validateUsername(String username) {
        // Regex: solo lettere, numeri, underscore, e punto. Lunghezza 3-30.
        String usernameRegex = "^[a-zA-Z0-9_.]{3,30}$";
        return Pattern.matches(usernameRegex, username);
    }

    // Validazione password
    public static boolean validatePassword(String password) {
        // Regex: almeno una maiuscola, una minuscola, un numero, un carattere speciale. Lunghezza 8-64.
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,64}$";
        return Pattern.matches(passwordRegex, password);
    }

    // Validazione email
    public static boolean validateEmail(String email) {
        // Regex: formato email valido secondo standard base
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    
}

