package dbd.perks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    private String receiverMail = "hyde69ciel@naver.com";

    private String mailSubject = "hyde69ciel@naver.com";

    private String mailBody = "예시 메일 내용";

    public void sendEmail(String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiverMail);
        message.setSubject(mailSubject);
        message.setText(mailBody);
        mailSender.send(message);
    }
}
