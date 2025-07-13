package com.bohemian.intellect.service;

import com.bohemian.intellect.dto.EmailSenderDto;
import com.bohemian.intellect.model.User;
import com.bohemian.intellect.repository.RedisRepository;
import com.bohemian.intellect.repository.UserRepository;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;


    public EmailService(JavaMailSender mailSender, ResourceLoader resourceLoader, UserRepository userRepository, RedisRepository redisRepository) {
        this.mailSender = mailSender;
        this.resourceLoader = resourceLoader;
        this.userRepository = userRepository;
        this.redisRepository = redisRepository;
    }

    public void sendMail(EmailSenderDto emailSenderDto) {

        String body = "";
        try {
            Resource resource = resourceLoader.getResource("classpath:template/email/" + emailSenderDto.type() + ".html");
            body = Files.readString(resource.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template: " + emailSenderDto.type(), e);
        }
        for (Map.Entry<String, String> entry : emailSenderDto.data().entrySet()) {
            body = body.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        MimeMessage mimeMessage = null;
        try {
            mimeMessage = mailSender.createMimeMessage();
            mimeMessage.setRecipients(Message.RecipientType.TO, emailSenderDto.to());
            mimeMessage.setSubject(emailSenderDto.subject());
            mimeMessage.setContent(body, "text/html; charset=utf-8");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(mimeMessage);
    }

    public ResponseEntity<?> generateOtp() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if(user.isVerified()) return new ResponseEntity<>("Already Verified", HttpStatus.BAD_GATEWAY);

        if(redisRepository.validForOps(user.getId())) {
            return new ResponseEntity<>("Timeout of "+redisRepository.getTimeoutTime(user.getId()), HttpStatus.BAD_REQUEST);
        }

        SecureRandom random = new SecureRandom();

        StringBuilder otp = new StringBuilder();

        for(int i=0; i<6; i++) {
            otp.append(random.nextInt(10));
        }
        redisRepository.saveOtp(user.getId(), otp.toString());
        sendMail(new EmailSenderDto(
                user.getEmail(),
                "otp",
                "Verify your email to Intellect",
                Map.of("otp", otp.toString())
        ));
        return new ResponseEntity<>("Sent", HttpStatus.OK);
    }

    public ResponseEntity<?> validateOtp(String otp) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if(redisRepository.getOtp(user.getId()) == null) return new ResponseEntity<>("Generate an otp", HttpStatus.BAD_REQUEST);

        if(otp.equals(redisRepository.getOtp(user.getId()))) {
            user.setVerified(true);
            userRepository.save(user);
            redisRepository.clear(user.getId());
            return new ResponseEntity<>("Verified", HttpStatus.OK);
        } else {
            int x = redisRepository.retryCount(user.getId());
            if(x > 5) {
                redisRepository.addCooldown(user.getId(), 3600);
                redisRepository.resetRetryCount(user.getId());
            }
        }
        return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
    }

}
