/*
 * 
 * 
 * @Author : Deepak Mahapatra
 * 
 */

package utilities;

import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.WebDriver;


public class Constants {
	
	private WebDriver driver;
	static Properties prop;
	private static ConfigReader configReader;
	static String password; 
	static byte[] encodedBytes;
	public Constants(WebDriver driver){
		this.driver = driver;
	
	}
 public static String deCodeString(String pw){
	 
	 byte[] decodedBytes = Base64.decodeBase64(pw);
	 System.out.println("decodedBytes "+ new String(decodedBytes));
	 return (new String (decodedBytes));
 }
 
 public static String enCodeString(String pw) throws Exception{
	 encodedBytes = Base64.encodeBase64(pw.getBytes());
	 System.out.println("encodedBytes "+ new String(encodedBytes));
	 return (new String (encodedBytes));
	 
 }
 public static void sendAutomationReportMail() {
	 
 }
 public static void main(String[] args)throws Exception {
	 //String pw =prop.getProperty("password_personal");
	 configReader= new ConfigReader();
	 prop= configReader.init_prop();

	 //password ="Deepak";
	enCodeString("Password-3");
	deCodeString(new String(encodedBytes));
	 //enCodeString(password);
}
}
