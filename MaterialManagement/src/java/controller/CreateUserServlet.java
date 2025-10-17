package controller;

import dal.UserDAO;
import entity.Department;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import utils.UserValidator;
import utils.EmailUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Random;

@WebServlet(name = "CreateUserServlet", value = "/CreateUser")
@MultipartConfig
public class CreateUserServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User admin = (User) session.getAttribute("user");
        if (admin.getRoleId() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        List<Department> departments = userDAO.getActiveDepartments();
        request.setAttribute("departments", departments);

        request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User admin = (User) session.getAttribute("user");
        if (admin.getRoleId() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            String username = request.getParameter("username");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String address = request.getParameter("address");
            String dateOfBirthStr = request.getParameter("dateOfBirth");
            String gender = request.getParameter("gender");
            String roleIdStr = request.getParameter("roleId");
            String departmentIdStr = request.getParameter("departmentId");
            String departmentName = request.getParameter("departmentName");

            int roleId = 0;
            Integer departmentId = null;
            String randomPassword = generateRandomPassword(12);

            String modifiedUsername = username + generateRandomLetters(5);

            try {
                roleId = Integer.parseInt(roleIdStr);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid role ID.");
                List<Department> departments = userDAO.getActiveDepartments();
                request.setAttribute("departments", departments);
                request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
                return;
            }

            if (roleId != 2) {
                try {
                    if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
                        departmentId = Integer.parseInt(departmentIdStr);
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            User newUser = new User();
            newUser.setUsername(modifiedUsername);
            newUser.setPassword(hashPasswordWithMD5(randomPassword));
            newUser.setEmail(email);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setRoleId(roleId);
            newUser.setDepartmentId(departmentId);
            newUser.setVerificationToken(UUID.randomUUID().toString());
            newUser.setVerificationStatus("pending");
            newUser.setVerificationExpiry(LocalDateTime.now().plusHours(24));
            newUser.setStatus(User.Status.inactive);

            Map<String, String> errors = new HashMap<>();

            if (userDAO.isEmailExist(email, 0)) {
                errors.put("email", "This email is already in use.");
            }

            String userPicture = null;
            Part filePart = request.getPart("userPicture");
            if (filePart != null && filePart.getSize() > 0) {
                if (filePart.getSize() > 2 * 1024 * 1024) {
                    errors.put("userPicture", "Image file size must not exceed 2MB.");
                } else {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                    userPicture = fileName;

                    String buildPath = getServletContext().getRealPath("/");
                    Path projectRoot = Paths.get(buildPath).getParent().getParent();
                    Path uploadDir = projectRoot.resolve("web").resolve("images").resolve("profiles");

                    try {
                        if (!Files.exists(uploadDir)) {
                            Files.createDirectories(uploadDir);
                        }
                        Path savePath = uploadDir.resolve(fileName);
                        filePart.write(savePath.toString());
                        newUser.setUserPicture(fileName);
                    } catch (IOException e) {
                        errors.put("userPicture", "Error saving image file: " + e.getMessage());
                    }
                }
            }

            errors.putAll(UserValidator.validateForCreateUser(newUser, userDAO, fullName, address, dateOfBirthStr, gender, departmentIdStr, userPicture));

            if (roleId != 2) {
                if (departmentId == null || departmentIdStr == null || departmentIdStr.isEmpty()) {
                    errors.put("departmentId", "Department is required for non-Director roles.");
                } else {
                    final Integer finalDepartmentId = departmentId;
                    List<Department> departments = userDAO.getActiveDepartments();
                    boolean departmentExists = departments.stream().anyMatch(dept -> dept.getDepartmentId() == finalDepartmentId);
                    if (!departmentExists) {
                        errors.put("departmentId", "Selected department does not exist or is not active.");
                    }
                }
            } else {
                newUser.setDepartmentId(null);
            }

            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                try {
                    LocalDate dob = LocalDate.parse(dateOfBirthStr);
                    if (dob.isAfter(LocalDate.now())) {
                        errors.put("dateOfBirth", "Date of birth cannot be in the future.");
                    } else {
                        newUser.setDateOfBirth(dob);
                    }
                } catch (Exception e) {
                    errors.put("dateOfBirth", "Invalid date of birth.");
                }
            }

            if (gender != null && !gender.isEmpty()) {
                if ("male".equalsIgnoreCase(gender) || "female".equalsIgnoreCase(gender) || "other".equalsIgnoreCase(gender)) {
                    newUser.setGender(User.Gender.valueOf(gender.toLowerCase()));
                } else {
                    errors.put("gender", "Invalid gender.");
                }
            }

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("enteredUser", newUser);
                request.setAttribute("enteredDateOfBirth", dateOfBirthStr);
                request.setAttribute("enteredGender", gender);
                request.setAttribute("enteredRoleId", roleIdStr);
                request.setAttribute("enteredDepartmentId", departmentIdStr);
                request.setAttribute("enteredDepartmentName", departmentName);
                List<Department> departments = userDAO.getActiveDepartments();
                request.setAttribute("departments", departments);
                request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
                return;
            }

            newUser.setFullName(fullName);
            newUser.setAddress(address);

            boolean created = userDAO.createUser(newUser);
            if (created) {
                try {
                    String baseUrl = request.getScheme() + "://"
                            + request.getServerName() + ":"
                            + request.getServerPort()
                            + request.getContextPath();

                    String verificationLink = baseUrl + "/VerifyUser?token=" + newUser.getVerificationToken();

                    String subject = "Verify Your Account";
                    String content = "<html><body>"
                            + "<p>Hello " + newUser.getFullName() + ",</p>"
                            + "<p>Your account has been successfully created. Please verify your email by clicking the link below:</p>"
                            + "<p><a href=\"" + verificationLink + "\">Verify Your Account</a></p>"
                            + "<p><strong>Username:</strong> " + newUser.getUsername() + "</p>"
                            + "<p><strong>Password:</strong> " + randomPassword + "</p>"
                            + "<p>This link will expire in 24 hours.</p>"
                            + "<p>Best regards,<br>The Support Team</p>"
                            + "</body></html>";

                    EmailUtils.sendEmail(newUser.getEmail(), subject, content);
                    session.setAttribute("successMessage", "Account created successfully! Verification email sent.");
                    response.sendRedirect(request.getContextPath() + "/UserList");
                } catch (Exception e) {
                    request.setAttribute("error", "Account created, but email failed: " + e.getMessage());
                    List<Department> departments = userDAO.getActiveDepartments();
                    request.setAttribute("departments", departments);
                    request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Failed to create account. Please try again.");
                List<Department> departments = userDAO.getActiveDepartments();
                request.setAttribute("departments", departments);
                request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "System error: " + e.getMessage());
            e.printStackTrace();
            List<Department> departments = userDAO.getActiveDepartments();
            request.setAttribute("departments", departments);
            request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
        }
    }

    private String hashPasswordWithMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private String generateRandomLetters(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    private String generateRandomPassword(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}