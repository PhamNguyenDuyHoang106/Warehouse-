package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import dal.UserDAO;

public class EmailUtils {
    public static void sendEmail(String to, String subject, String content) throws MessagingException {
        final String username = "nguyenvu150203@gmail.com";
        final String password = "dcyr itbq jwpf eicr";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage message = new MimeMessage(session); 
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject, "UTF-8"); 
        message.setContent(content, "text/html; charset=UTF-8");

        Transport.send(message);
    }

}
