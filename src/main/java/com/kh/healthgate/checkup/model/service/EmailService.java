package com.kh.healthgate.checkup.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 건강검진 이메일 알림을 실제로 발송하는 Service
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * application.properties의 spring.mail.username 값을 받는다.
     *
     * 실제 값은 MAIL_USERNAME 환경변수로 설정한다.
     */
    @Value("${spring.mail.username:}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 건강검진 안내 이메일을 발송한다.
     *
     * @param recipientEmail 수신할 직원 이메일
     * @param employeeName 수신할 직원 이름
     * @param content 알림 메시지 내용
     */
    public void sendCheckupReminder(
            String recipientEmail,
            String employeeName,
            String content) {

        validateMailInformation(
                recipientEmail,
                content
        );

        SimpleMailMessage mailMessage =
                new SimpleMailMessage();

        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(
                "[HealthGate] 건강검진 안내"
        );

        String greeting =
                employeeName == null || employeeName.isBlank()
                        ? "안녕하세요."
                        : employeeName + "님, 안녕하세요.";

        mailMessage.setText(
                greeting
                + System.lineSeparator()
                + System.lineSeparator()
                + content
                + System.lineSeparator()
                + System.lineSeparator()
                + "HealthGate"
        );

        /*
         * Gmail SMTP에 실제 발송을 요청한다.
         *
         * 발송에 실패하면 예외가 발생하며,
         * 호출한 Service에서 FAILED 이력으로 저장한다.
         */
        mailSender.send(mailMessage);
    }

    /**
     * 이메일 발송에 필요한 정보를 검증한다.
     */
    private void validateMailInformation(
            String recipientEmail,
            String content) {

        if (senderEmail == null || senderEmail.isBlank()) {
            throw new IllegalStateException(
                    "발송용 이메일 환경변수 MAIL_USERNAME이 설정되지 않았습니다."
            );
        }

        if (recipientEmail == null
                || recipientEmail.isBlank()) {

            throw new IllegalArgumentException(
                    "알림을 받을 직원의 이메일이 등록되지 않았습니다."
            );
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "발송할 알림 메시지를 입력해 주세요."
            );
        }
    }
}