package ru.largusshop.internal_orders.service;

import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static java.util.Objects.nonNull;

@Service
public class EmailService {

    public void sendEmail(String text, String mailTo) {
        sendEmails(text, Collections.singletonList(mailTo));
    }

    public void sendEmailWithAttachment(String text, String mailTo, ByteArrayOutputStream baos, String filename) {
        sendEmailsWithAttachment(text, Collections.singletonList(mailTo), baos, filename);
    }

    public void sendEmails(String text, List<String> mailsTo) {
        sendEmailsWithAttachment(text, mailsTo, null, null);
    }

    public void sendEmailsWithAttachment(String text, List<String> mailsTo, ByteArrayOutputStream baos, String filename) {

        final String username = "clearbox204@gmail.com";
        final String password = "753159456258aR";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                                              new javax.mail.Authenticator() {
                                                  protected PasswordAuthentication getPasswordAuthentication() {
                                                      return new PasswordAuthentication(username, password);
                                                  }
                                              });

        for (String mailTo : mailsTo) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("clearbox204@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
                message.setSubject("Внутренний заказ");
                message.setText(text);
                if(nonNull(baos)) {
                    addAttachment(message, baos, filename);
                }
                Transport.send(message);
                System.out.println("Done");
            } catch (MessagingException e) {
                System.err.println(LocalDateTime.now() + " Messaging failed to: " + mailTo);
                e.printStackTrace();
            }
        }
    }

    private void addAttachment(Message message, ByteArrayOutputStream baos, String attachmentFileName) throws MessagingException {
        DataSource ds = null;
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        byte[] bytes = baos.toByteArray();
        ds = new ByteArrayDataSource(bytes, "application/excel");
        DataHandler dh = new DataHandler(ds);
        mimeBodyPart.setHeader("Content-Disposition", "attachment;filename="+attachmentFileName+".xls");
        mimeBodyPart.setDataHandler(dh);
        mimeBodyPart.setFileName(attachmentFileName);
        Multipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(mimeBodyPart);
        message.setContent(multiPart);
    }
}
