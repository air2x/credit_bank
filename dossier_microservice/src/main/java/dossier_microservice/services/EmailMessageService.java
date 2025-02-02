package dossier_microservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class EmailMessageService {

    private JavaMailSender mailSender;
    private static final String TXT = ".txt";
    private static final String FOR = " для ";

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sharafieff.ai@yandex.ru");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendEmailWithFile(String to, String subject, String body) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String filePath = to + TXT;
        createDocuments(filePath, body + FOR + to);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        helper.setFrom("sharafieff.ai@yandex.ru");

        FileSystemResource file = new FileSystemResource(new File(filePath));
        helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

        mailSender.send(message);
    }

    public static void createDocuments(String filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }
}
