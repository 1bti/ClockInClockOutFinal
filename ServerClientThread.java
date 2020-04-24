package multithreadedserver;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class ServerClientThread extends Thread {
	
	
	//Method that sends keys in script using dynamic wait times
	public static void sendKeys(WebDriver driver, WebElement element, int timeout, String value){
		new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOf(element));
		element.sendKeys(value);
	}
	
	//Method that click elements in script using dynamic wait times
	public static void clickOn(WebDriver driver, WebElement element, int timeout){
		new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOf(element));
		element.click();
	}
	
	//clock in method needs to be in ServerClient Thread due to I/O
	public static void clockin(String url, String username, String password, DataInputStream inStream, DataOutputStream outStream) throws InterruptedException, IOException {
		//Driver initialization
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		//Log in to OKTA
		driver.get(url);
		WebElement user = driver.findElement(By.xpath("//*[@id='okta-signin-username']"));
		sendKeys(driver, user, 10, username);
		WebElement pass = driver.findElement(By.xpath("//*[@id=\'okta-signin-password\']"));
		sendKeys(driver,pass,10,password);
		WebElement signIn = driver.findElement(By.xpath("//*[@id=\"okta-signin-submit\"]"));
		clickOn(driver,signIn,100);
		
		//Clock in
		WebElement clockIn = driver.findElement(By.id("ClockIn"));
		clickOn(driver,clockIn,500);
		Thread.sleep(2000);
		List<WebElement> elements = driver.findElements(By.cssSelector("td[ng-bind='objJobCode.strDescription']"));
		Boolean missedClockOut = driver.findElements(By.xpath("//*[@id=\'featureForm\']/div[2]/div/div/div/div[2]/div[1]")).size() > 0;
		String serverMessage = "";
		String clientMessage = "";
		System.out.println(missedClockOut);
		//If client has multiple jobs
		if(!elements.isEmpty()){
			//Display Jobs
			serverMessage="Which Job?";
	        outStream.writeUTF(serverMessage);
	        outStream.flush();
	        serverMessage = elements.size() + "";
	        outStream.writeUTF(serverMessage);
	        outStream.flush();
			int i = 0;
	        while (i < elements.size()) {
	            int choice = i + 1;
	        	serverMessage = choice + " " + elements.get(i).getText();
	        	outStream.writeUTF(serverMessage);
		        outStream.flush();
	            i++;
	        }
	        
	        //Select Jobs
	        clientMessage=inStream.readUTF();
	        int selection = Integer.parseInt(clientMessage);
	        String xpathJob = "//*[@id=\'GatherJobCodeList\']/tbody/tr[" + Integer.toString(selection) + "]/td[3]";
	        WebElement selectJob = driver.findElement(By.xpath(xpathJob));
	        clickOn(driver,selectJob,10);
			WebElement submit = driver.findElement(By.xpath("//*[@id=\'featureForm\']/div[2]/div/div/div/div[3]/input[3]"));
			clickOn(driver,submit,10);
			Thread.sleep(1000);
			serverMessage = "Done.";
	        outStream.writeUTF(serverMessage);
	        outStream.flush();
			driver.close();
		}
		//If client missed a clock out
		else if(missedClockOut) {
			serverMessage = "Missed Clock Out.";
	        outStream.writeUTF(serverMessage);
	        outStream.flush();
			driver.close();
		}
		//If client successfully clocked in
		else {
			serverMessage = "Done.";
	        outStream.writeUTF(serverMessage);
	        outStream.flush();
			driver.close();
		}
	}
	
	
	public static void clockout(String url, String username, String password, DataInputStream inStream, DataOutputStream outStream) throws InterruptedException, IOException {
		//Driver intialization
		String serverMessage = "";
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(url);
		
		//Log in to OKTA
		WebElement user = driver.findElement(By.xpath("//*[@id='okta-signin-username']"));
		sendKeys(driver, user, 10, username);
		WebElement pass = driver.findElement(By.xpath("//*[@id=\'okta-signin-password\']"));
		sendKeys(driver,pass,10,password);
		WebElement signIn = driver.findElement(By.xpath("//*[@id=\"okta-signin-submit\"]"));
		clickOn(driver,signIn,100);
		
		//Clock out
		WebElement clockIn = driver.findElement(By.id("ClockOut"));
		clickOn(driver,clockIn,500);
		Boolean missedClockIn = driver.findElements(By.xpath("//*[@id=\'featureForm\']/div[2]/div/div/div/div[2]/div[1]")).size() > 0;
		if(missedClockIn){
			serverMessage = "Missed Clock In.";
	        outStream.writeUTF(serverMessage);
	        outStream.flush();
		}
		driver.close();
		
	}
	
	//local variables and constructor
	  Socket serverClient;
	  int clientNo;
	  int squre;
	  String url = "https://rollins.okta.com/login/login.htm?fromURI=%2Fhome%2Frollinscollege_timeclockplus_1%2F0oa1t9fmp5CiaW4J9357%2Faln1t9kgat2oTrfaq357";
	  String username = "";
	  String password = "";
	  String action = "";
	  ServerClientThread(Socket inSocket,int counter){
	    serverClient = inSocket;
	    clientNo=counter;
	  }
	  
	  //Thread "main" method
	  public void run(){
		
		//Uses a try-catch function to not disrupt multi-thread server
	    try{
	      //I/O	
	      DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
	      DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());
	      String clientMessage="", serverMessage="";
	        
	      clientMessage=inStream.readUTF(); // Username input
	      System.out.println("From Client-" +clientNo+ ": Username is :"+clientMessage);
	      username = clientMessage;
	      clientMessage=inStream.readUTF(); // Password input
	      System.out.println("From Client-" +clientNo+ ": Password is :"+clientMessage);
	      password = clientMessage;
	      clientMessage=inStream.readUTF(); //Clock in or Clock out input
	      serverMessage="Info recieved.";  //Confirmation to Client info
	      outStream.writeUTF(serverMessage);
	      outStream.flush();
	      if(clientMessage.equals("1")) {
	    	  System.out.println("From Client-" +clientNo+ ": Action is :Clock In");
		      action = clientMessage;
		      clockin(url,username,password, inStream, outStream);
	      }
	      else {
	    	  System.out.println("From Client-" +clientNo+ ": Action is :Clock Out");
		      action = clientMessage;
		      clockout(url,username, password, inStream, outStream);
	      }
	      serverMessage="Action Completed."; //Confirmation of Action
	      outStream.writeUTF(serverMessage);
	      outStream.flush();
	      
	      inStream.close();
	      outStream.close();
	      serverClient.close();
	    }catch(Exception ex){
	      System.out.println(ex);
	    }finally{
	      System.out.println("Client -" + clientNo + " exit!! ");
	    }
	  }
	}