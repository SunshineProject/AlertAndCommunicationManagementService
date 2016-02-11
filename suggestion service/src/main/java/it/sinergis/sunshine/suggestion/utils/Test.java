package it.sinergis.sunshine.suggestion.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class Test {
	
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	
	public static void main(String args[]) throws AddressException, MessagingException {
		//		generateAndSendEmail();
		//		send();
		//		generateAndSendEmailDeda();
		
		//		double[] tetai = { 3.6252924429201454, 3.3184217542005077, 3.0221256322667807, 2.760954657780122,
		//				2.358658337910081, 2.1520724904576363, 1.951919402030844, 1.9486336504201187, 4.259852560569408,
		//				5.730253307636718, 5.254257679193625, 5.3249420642395, 5.275511449238636, 5.097800826313328,
		//				4.658348836373643, 4.065183705533578, 3.4338934900750484, 3.26973432302872, 4.399003514820974,
		//				5.302341376043872, 6.252524561238544, 7.053549905655351, 7.69275751835481, 8.053957362440379 };
		//		int[] modality = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 };
		//		Email.sendEmail("oscar.benedetti@sinergis.it", "posta.oscar@gmail.com", "posta.oscar@gmail.com",
		//				"email di test con tabella", Functions.getStringFromOutputSuggestion(tetai, "05:43", modality,
		//						"ferrara", 49146, 3.0, 2.0, 4.0, 3.0, 2.5, 2.5, "Scuola Poledrelli", "7.5", "19", 20));
		//		System.out.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
		dd();
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
	
	private static void dd() {
		DateFormat outputFormatterObseravtion = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat outputFormatterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Calendar cal = java.util.Calendar.getInstance();
		//		TimeZone tz = TimeZone.getTimeZone("GMT+0");
		//		cal.setTimeZone(tz);
		String phentime = outputFormatterObseravtion.format(new Date());
		try {
			cal.setTime(outputFormatterObseravtion.parse(phentime));
		}
		catch (ParseException e) {
			System.out.println("Error in parsing format date [profile]");
		}
		cal.add(java.util.Calendar.DATE, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 5);
		
		System.out.println(cal.getTime());
	}
	
}
