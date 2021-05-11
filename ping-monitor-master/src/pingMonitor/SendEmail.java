package pingMonitor;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail
{
	Session mailSession;
	public void setMailServerProperties(int smtpPort)
	{
		Properties emailProperties = System.getProperties();
		emailProperties.put("mail.smtp.port", smtpPort);
		emailProperties.put("mail.smtp.auth", "true");
		emailProperties.put("mail.smtp.starttls.enable", "true");
		mailSession = Session.getDefaultInstance(emailProperties, null);
	}
	public MimeMessage draftEmailMessage(String sendTo, int pingValue, int avgvalue, Address sEND_FROM) throws AddressException, MessagingException
	{
		String[] toEmails = { sendTo };
		String emailSubject = "Given pingvalue "+pingValue +" is grater than average ping value "+avgvalue;
		String msgtext = "This is an email sent by ping monitor because average ping"+avgvalue+" value is more than given ping value."+pingValue;
		MimeMessage emailMessage = new MimeMessage(mailSession);
		/**
		 * Set the mail recipients
		 * */
		for (int i = 0; i < toEmails.length; i++)
		{
			emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails[i]));
		}
		
		/**
		 * If sending HTML mail
		 * */
		emailMessage.setSentDate(new Date());
        emailMessage.setFrom(sEND_FROM);
        emailMessage.setSubject(emailSubject);
        String msg = (new StringBuilder("Message Date Time: ")).append((new Date(System.currentTimeMillis())).toString()).append("\n\n").append(msgtext).toString();
        emailMessage.setContent(msg, "text/html");
        /*Transport.send(message);
		emailMessage.setContent(emailBody, "text/html");
		*//**
		 * If sending only text mail
		 * */
		//emailMessage.setText(emailBody);// for a text email
		return emailMessage;
	}

	public void sendEmail(String sEND_TO, String sendFrom, String usrPwd, String smtpHost, int pING_VALUE, int avgvalue, Address sEND_FROM) throws AddressException, MessagingException
	{
		/**
		 * Sender's credentials		 * */
		String fromUser = sendFrom;
		String fromUserEmailPassword = usrPwd;

		String emailHost = smtpHost;
		Transport transport = mailSession.getTransport("smtp");
		transport.connect(emailHost, fromUser, fromUserEmailPassword);
		/**
		 * Draft the message
		 * */
		MimeMessage emailMessage = draftEmailMessage(sEND_TO,pING_VALUE,avgvalue,sEND_FROM);
		/**
		 * Send the mail
		 * */
		transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
		transport.close();
		System.out.println("Email sent successfully.");
	}
}
