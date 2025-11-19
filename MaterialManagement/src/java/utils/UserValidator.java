package utils;

import dal.UserDAO;
import entity.User;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UserValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    private static final String PHONE_REGEX = "^\\+?[0-9]{1,20}$";
    private static final int USERNAME_MAX_LENGTH = 50;
    private static final int FULLNAME_MAX_LENGTH = 100;
    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int PHONE_MAX_LENGTH = 20;

    public static Map<String, String> validateForCreateUser(User user, UserDAO userDAO, String fullName,
                                                           String dateOfBirth, String gender, String departmentId, String userPicture) {
        Map<String, String> errors = new HashMap<>();

        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            if (user.getUsername().trim().isEmpty()) {
                errors.put("username", "Username is required.");
            } else if (user.getUsername().length() > USERNAME_MAX_LENGTH) {
                errors.put("username", "Username must not exceed " + USERNAME_MAX_LENGTH + " characters.");
            } else if (!user.getUsername().matches("^[a-z0-9_.-]+$")) {
                errors.put("username", "Username must not contain uppercase letters.");
            } else if (userDAO.isUsernameExist(user.getUsername())) {
                errors.put("username", "Username already exists.");
            }
        }

        if (user.getPassword() != null && user.getPassword().trim().isEmpty()) {
            errors.put("password", "Password is required.");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!Pattern.matches(EMAIL_REGEX, user.getEmail())) {
            errors.put("email", "Invalid email format.");
        } else if (user.getEmail().length() > EMAIL_MAX_LENGTH) {
            errors.put("email", "Email must not exceed " + EMAIL_MAX_LENGTH + " characters.");
        }

        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            errors.put("phoneNumber", "Phone number is required.");
        } else if (!Pattern.matches(PHONE_REGEX, user.getPhone())) {
            errors.put("phoneNumber", "Phone number must be 1-20 digits, optionally starting with '+'.");
        } else if (user.getPhone().length() > PHONE_MAX_LENGTH) {
            errors.put("phoneNumber", "Phone number must not exceed " + PHONE_MAX_LENGTH + " characters.");
        }

        if (user.getRoleId() != 0 && user.getRoleId() < 0) {
            errors.put("roleId", "Role is required.");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("fullName", "Full name is required.");
        } else if (fullName.length() > FULLNAME_MAX_LENGTH) {
            errors.put("fullName", "Full name must not exceed " + FULLNAME_MAX_LENGTH + " characters.");
        } else if (fullName.matches(".*\\d.*")) {
            errors.put("fullName", "Full name must not contain numbers.");
        }

        if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) {
            try {
                LocalDate dob = LocalDate.parse(dateOfBirth);
                if (dob.isAfter(LocalDate.now())) {
                    errors.put("dateOfBirth", "Date of birth cannot be in the future.");
                } else if (Period.between(dob, LocalDate.now()).getYears() < 18) {
                    errors.put("dateOfBirth", "User must be at least 18 years old.");
                }
            } catch (Exception e) {
                errors.put("dateOfBirth", "Invalid date of birth format.");
            }
        }

        if (gender != null && !gender.trim().isEmpty()) {
            if (!"male".equalsIgnoreCase(gender) && !"female".equalsIgnoreCase(gender) && !"other".equalsIgnoreCase(gender)) {
                errors.put("gender", "Invalid gender.");
            }
        }

        return errors;
    }

    public static Map<String, String> validateForProfile(User user, String fullName,
                                                        String dateOfBirth, String gender) {
        Map<String, String> errors = new HashMap<>();

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!Pattern.matches(EMAIL_REGEX, user.getEmail())) {
            errors.put("email", "Invalid email format.");
        } else if (user.getEmail().length() > EMAIL_MAX_LENGTH) {
            errors.put("email", "Email must not exceed " + EMAIL_MAX_LENGTH + " characters.");
        }

        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            errors.put("phoneNumber", "Phone number is required.");
        } else if (!Pattern.matches(PHONE_REGEX, user.getPhone())) {
            errors.put("phoneNumber", "Phone number must be 1-20 digits, optionally starting with '+'.");
        } else if (user.getPhone().length() > PHONE_MAX_LENGTH) {
            errors.put("phoneNumber", "Phone number must not exceed " + PHONE_MAX_LENGTH + " characters.");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("fullName", "Full name is required.");
        } else if (fullName.length() > FULLNAME_MAX_LENGTH) {
            errors.put("fullName", "Full name must not exceed " + FULLNAME_MAX_LENGTH + " characters.");
        } else if (fullName.matches(".*\\d.*")) {
            errors.put("fullName", "Full name must not contain numbers.");
        }

        if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) {
            try {
                LocalDate dob = LocalDate.parse(dateOfBirth);
                if (dob.isAfter(LocalDate.now())) {
                    errors.put("dateOfBirth", "Date of birth cannot be in the future.");
                } else if (Period.between(dob, LocalDate.now()).getYears() < 18) {
                    errors.put("dateOfBirth", "User must be at least 18 years old.");
                }
            } catch (Exception e) {
                errors.put("dateOfBirth", "Invalid date of birth format.");
            }
        }

        if (gender != null && !gender.trim().isEmpty()) {
            if (!"male".equalsIgnoreCase(gender) && !"female".equalsIgnoreCase(gender) && !"other".equalsIgnoreCase(gender)) {
                errors.put("gender", "Invalid gender.");
            }
        }

        return errors;
    }

    public static Map<String, String> validateForUpdateUser(User user, UserDAO userDAO, String fullName,
                                                           String dateOfBirth, String gender, String departmentId, String userPicture) {
        Map<String, String> errors = new HashMap<>();

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!Pattern.matches(EMAIL_REGEX, user.getEmail())) {
            errors.put("email", "Invalid email format.");
        } else if (user.getEmail().length() > EMAIL_MAX_LENGTH) {
            errors.put("email", "Email must not exceed " + EMAIL_MAX_LENGTH + " characters.");
        }

        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            errors.put("phoneNumber", "Phone number is required.");
        } else if (!Pattern.matches(PHONE_REGEX, user.getPhone())) {
            errors.put("phoneNumber", "Phone number must be 1-20 digits, optionally starting with '+'.");
        } else if (user.getPhone().length() > PHONE_MAX_LENGTH) {
            errors.put("phoneNumber", "Phone number must not exceed " + PHONE_MAX_LENGTH + " characters.");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("fullName", "Full name is required.");
        } else if (fullName.length() > FULLNAME_MAX_LENGTH) {
            errors.put("fullName", "Full name must not exceed " + FULLNAME_MAX_LENGTH + " characters.");
        } else if (fullName.matches(".*\\d.*")) {
            errors.put("fullName", "Full name must not contain numbers.");
        }

        if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) {
            try {
                LocalDate dob = LocalDate.parse(dateOfBirth);
                if (dob.isAfter(LocalDate.now())) {
                    errors.put("dateOfBirth", "Date of birth cannot be in the future.");
                } else if (Period.between(dob, LocalDate.now()).getYears() < 18) {
                    errors.put("dateOfBirth", "User must be at least 18 years old.");
                }
            } catch (Exception e) {
                errors.put("dateOfBirth", "Invalid date of birth format.");
            }
        }

        if (gender != null && !gender.trim().isEmpty()) {
            if (!"male".equalsIgnoreCase(gender) && !"female".equalsIgnoreCase(gender) && !"other".equalsIgnoreCase(gender)) {
                errors.put("gender", "Invalid gender.");
            }
        }

        return errors;
    }
}