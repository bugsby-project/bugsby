package com.bugsby.datalayer.service.email;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.PrefilledIssue;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.service.exceptions.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final String MAIL_PROPERTIES_PATH = "mail/mail.properties";
    private static final String MAIL_ACCOUNT_ADDRESS_PROPERTY = "mail.account.address";
    private static final String MAIL_ACCOUNT_PASSWORD_PROPERTY = "mail.account.password";
    private static final String EMAIL_TITLE = "Build failure";

    private final Properties properties;
    private final Session session;
    private final BuildFailureEmailBodyBuilder buildFailureEmailBodyBuilder;

    @Autowired
    public EmailServiceImpl(BuildFailureEmailBodyBuilder buildFailureEmailBodyBuilder) {
        this.buildFailureEmailBodyBuilder = buildFailureEmailBodyBuilder;
        this.properties = this.getProperties();
        this.session = this.getSession(this.properties);
    }


    private Properties getProperties() {
        Properties props = new Properties();

        try (InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(MAIL_PROPERTIES_PATH)) {
            props.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Error on loading mail.properties file");
            e.printStackTrace();
        }

        return props;
    }

    private Session getSession(Properties properties) {
        return Session.getDefaultInstance(
                properties,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(properties.getProperty(MAIL_ACCOUNT_ADDRESS_PROPERTY), properties.getProperty(MAIL_ACCOUNT_PASSWORD_PROPERTY));
                    }
                }
        );
    }

    @Override
    public void sendBuildFailureEmail(PrefilledIssue prefilledIssue) throws EmailException {
        prefilledIssue.getProject().getInvolvements().stream()
                .map(Involvement::getUser)
                .forEach(user -> {
                    try {
                        this.sendEmailToUser(prefilledIssue, user);
                    } catch (MessagingException e) {
                        throw new EmailException(e.getMessage());
                    }
                });
    }

    private void sendEmailToUser(PrefilledIssue prefilledIssue, User user) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(properties.getProperty(MAIL_ACCOUNT_ADDRESS_PROPERTY)));
        message.setReplyTo(null);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
        message.setSubject(EMAIL_TITLE);
        message.setContent(this.buildFailureEmailBodyBuilder.build(prefilledIssue), "text/html; charset=utf-8");

        Transport.send(message);
    }
}
