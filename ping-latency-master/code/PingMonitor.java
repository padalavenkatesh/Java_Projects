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
		String ip = "";
		String pingResult = "";
		int PING_VALUE = 0;
		int SMTP_PORT = 0 ;
		String SEND_TO= null;
		String SEND_FROM = null;
		String USR_PWD = null;
		String SMTP_HOST = null;
		FileInputStream is = null;
		int avgvalue=0;
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
		ip=configFile.getProperty("PING_IP");
		String loc = configFile.getProperty("LOC");
		System.out.println(ip);
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
			int Lost=Integer.parseInt(pingResult.substring(pingResult.lastIndexOf("Lost")+7,pingResult.lastIndexOf(" (")));
			//System.out.println(Lost);
			if(Lost==0){
				
			System.out.println("hello");
			String avg=pingResult.substring(pingResult.lastIndexOf("Average")+10 );
			avgvalue=Integer.parseInt(avg.substring(0,avg.indexOf("ms")));
			System.out.println(avgvalue);
			if(PING_VALUE<=avgvalue)
			{
				System.out.println("Ping Average value is More than given value");
				SendEmail javaEmail = new SendEmail();
				javaEmail.setMailServerProperties(SMTP_PORT);
				javaEmail.draftEmailMessage(SEND_TO,PING_VALUE,avgvalue,sender,ip,loc);
				javaEmail.sendEmail(SEND_TO,SEND_FROM,USR_PWD,SMTP_HOST,PING_VALUE,avgvalue,sender,ip,loc);
			}       
		}
			else
			{
				System.out.println("Ip Not Pining");
				NotPing notpingEmail = new NotPing();
				notpingEmail.setMailServerProperties(SMTP_PORT);
				notpingEmail.draftEmailMessage(SEND_TO,PING_VALUE,avgvalue,sender,ip,loc);
				notpingEmail.sendEmail(SEND_TO,SEND_FROM,USR_PWD,SMTP_HOST,PING_VALUE,avgvalue,sender,ip,loc);
			}
			
		
			in.close(); 

			
			
		} catch (IOException e) {
			System.out.println(e);

		}

	}
}


