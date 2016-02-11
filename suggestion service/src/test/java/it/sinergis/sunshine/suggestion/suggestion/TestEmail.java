package it.sinergis.sunshine.suggestion.suggestion;

import it.sinergis.sunshine.suggestion.utils.Email;
import it.sinergis.sunshine.suggestion.utils.Functions;
import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class TestEmail {
	
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	
	public static void main(String args[]) throws AddressException, MessagingException {
		//		generateAndSendEmail();
		//		send();
		//		generateAndSendEmailDeda();
		double[] tetai = { 13.5, 18.4, 19.6, 21.4, 18.4, 19.6, 21.4, 18.4, 19.6, 21.4, 18.4, 19.6, 21.4, 18.4, 19.6,
				21.4, 18.4, 19.6, 21.4, 18.4, 19.6, 21.4, 21.4, 19.6 };
		int[] modality = { 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 };
		Email.sendEmail("oscar.benedetti@sinergis.it", "posta.oscar@gmail.com", "luca.giovannini@sinergis.it",
				"email di test con tabella", Functions.getStringFromOutputSuggestion(tetai, "05:43", modality,
						"ferrara", 49146, 3.0, 2.0, 4.0, 3.0, 2.5, 2.5, "Scuola Poledrelli", "7:45", "18:00", 20));
		System.out.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
	}
	
	public static void generateAndSendEmail() throws AddressException, MessagingException {
		
		// Step1
		System.out.println("\n 1st ===> setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		System.out.println("Mail Server Properties have been setup successfully..");
		
		// Step2
		System.out.println("\n\n 2nd ===> get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("posta.oscar@gmail.com"));
		generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("posta.oscar@gmail.com"));
		generateMailMessage.setSubject("Greetings from Crunchify..");
		String emailBody = "Test email by Crunchify.com JavaMail API example. "
				+ "<br><br> Regards, <br>Crunchify Admin";
		generateMailMessage.setContent(emailBody, "text/html");
		System.out.println("Mail Session has been created successfully..");
		
		// Step3
		System.out.println("\n\n 3rd ===> Get Session and Send mail");
		Transport transport = getMailSession.getTransport("smtp");
		
		// Enter your correct gmail UserID and Password
		// if you have 2FA enabled then provide App Specific Password
		transport.connect("smtp.gmail.com", "posta.oscar@gmail.com", "$@nguinel@");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}
	
	public static void generateAndSendEmailDeda() throws AddressException, MessagingException {
		
		// Step1
		System.out.println("\n 1st ===> setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "false");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		System.out.println("Mail Server Properties have been setup successfully..");
		
		// Step2
		System.out.println("\n\n 2nd ===> get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("posta.oscar@gmail.com"));
		generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("posta.oscar@gmail.com"));
		generateMailMessage.setSubject("Greetings from Crunchify..");
		String emailBody = "Test email by Crunchify.com JavaMail API example. "
				+ "<br><br> Regards, <br>Crunchify Admin";
		generateMailMessage.setContent(emailBody, "text/html");
		System.out.println("Mail Session has been created successfully..");
		
		// Step3
		System.out.println("\n\n 3rd ===> Get Session and Send mail");
		Transport transport = getMailSession.getTransport("smtp");
		
		// Enter your correct gmail UserID and Password
		// if you have 2FA enabled then provide App Specific Password
		transport.connect("smtp.dedagroup.it", null, null);
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}
	
	public static void send() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		String from = "oscar.benedetti@sinergis.it";
		String to = "oscar.benedetti@sinergis.it";
		String subject = "email deda";
		
		Properties props = new Properties();
		props.put("mail.smtp.host", ReadFromConfig.loadByName("smtpServer"));
		props.put("mail.smtp.port", "25");
		
		Session mailSession = Session.getDefaultInstance(props);
		Message simpleMessage = new MimeMessage(mailSession);
		
		InternetAddress fromAddress = null;
		InternetAddress toAddress = null;
		try {
			fromAddress = new InternetAddress(from);
			toAddress = new InternetAddress(to);
		}
		catch (AddressException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
		
		try {
			simpleMessage.setFrom(fromAddress);
			simpleMessage.setRecipient(RecipientType.TO, toAddress);
			simpleMessage.setSubject(subject);
			simpleMessage.setText("ciao");
			
			Transport.send(simpleMessage);
			System.out.println("Email inviata");
		}
		catch (MessagingException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
		
	}
	
}
