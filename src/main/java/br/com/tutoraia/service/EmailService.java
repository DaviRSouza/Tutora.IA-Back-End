package br.com.tutoraia.service;

import br.com.tutoraia.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Slf4j
public class EmailService {

    public void sendEmail(User user, String recoverLink) {
        final String htmlContent =
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Recuperação de Senha</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h2>Recuperação de Senha</h2>\n" +
                "    <p>Olá, " + user.getName() + "</p>\n" +
                "    <p>Clique no link abaixo para recuperar sua senha:</p>\n" +
                "    <a href=\"" + recoverLink + "\">Redefinir Senha</a>\n" +
                "    <p>Se você não solicitou a redefinição, ignore este email.</p>\n" +
                "</body>\n" +
                "</html>";
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("@gmail.com", ""); // tutoraia@gmail.com || hsdighwqau8dgwayu -> senha criptografada pelo google
                    }
                });
        session.setDebug(true);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("oficialstylesync@gmail.com")); // tutoraia@gmail.com

            Address[] toUser = InternetAddress
                    .parse(user.getEmail());

            message.setRecipients(Message.RecipientType.TO, toUser);
            message.setSubject("Redefinição de Senha - não funcionando");
            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
