package pingMonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.internet.InternetAddress;

public class PingMonitor {
	private static Object getProperty;

	public static void main(String[] args) throws Exception {
		String ip = "mail3.imedx.com";
		String pingResult = "";
		int PING_VALUE = 0;
		int SMTP_PORT = 0 ;
		String SEND_TO= null;
		String SEND_FROM = null;
		
		String USR_PWD = null;
		String SMTP_HOST = null;
		String pingCmd = "ping " + ip;
		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(pingCmd);
			BufferedReader in = new BufferedReader(new
					InputStreamReader(p.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				pingResult += inputLine;
			}
			String avg=pingResult.substring(pingResult.lastIndexOf("Average")+10 );
			int avgvalue=Integer.parseInt(avg.substring(0,avg.indexOf("ms")));
			System.out.println(avgvalue);
			in.close(); 
			FileInputStream is = null;
			File mailPropsFile = new File("./properties/ping.properties");
			if(mailPropsFile.exists())
			{
				System.out.println("The mail properties file exists....");
				is = new FileInputStream(mailPropsFile);
			}
			Properties configFile = new Properties();
			configFile.load(is);
			SEND_TO = configFile.getProperty("SEND_TO_SUPPORT");
			SEND_FROM = configFile.getProperty("SMTP_USER");
			USR_PWD = configFile.getProperty("SMTP_PWD");
			SMTP_HOST  = configFile.getProperty("SMTP_HOST");
			SMTP_PORT = Integer.parseInt(configFile.getProperty("SMTP_PORT"));
			PING_VALUE = Integer.parseInt(configFile.getProperty("PING_VALUE"));
			javax.mail.Address sender = new InternetAddress(configFile.getProperty("SMTP_USER"));
			System.out.println(sender);
			System.out.println(SEND_TO);
			System.out.println(SEND_FROM);
			System.out.println(USR_PWD);
			System.out.println(SMTP_HOST);
			System.out.println(SMTP_PORT); 
			if(PING_VALUE<=avgvalue)
			{
				System.out.println("ping value is higher");
				SendEmail javaEmail = new SendEmail();
				javaEmail.setMailServerProperties(SMTP_PORT);
				javaEmail.draftEmailMessage(SEND_TO,PING_VALUE,avgvalue,sender);
				javaEmail.sendEmail(SEND_TO,SEND_FROM,USR_PWD,SMTP_HOST,PING_VALUE,avgvalue,sender);
			}       
			in.close(); 
		} catch (IOException e) {
			System.out.println(e);

		}

	}
}


