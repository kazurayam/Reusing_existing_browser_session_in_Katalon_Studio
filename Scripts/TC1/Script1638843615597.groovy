import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.chrome.ChromeDriver
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.remote.HttpCommandExecutor
import org.openqa.selenium.remote.CommandExecutor

import org.openqa.selenium.remote.SessionId
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.Response
import org.openqa.selenium.remote.Command
import java.lang.reflect.Field
import org.openqa.selenium.remote.http.W3CHttpCommandCodec
import org.openqa.selenium.remote.http.W3CHttpResponseCodec

/**
 * https://tarunlalwani.com/post/reusing-existing-browser-session-selenium-java/
 */

public static RemoteWebDriver createDriverFromSession(final SessionId sessionId, URL command_executor) {
	CommandExecutor executor = new HttpCommandExecutor(command_executor) {
		@Override
		public Response execute(Command command) throws IOException {
			Response response = null;
			if (command.getName() == "newSession") {
				response = new Response();
				response.setSessionId(sessionId.toString());
				response.setStatus(0);
				response.setValue(Collections.<String, String>emptyMap());
				
				try {
					Field commandCodec = null;
					commandCodec = this.getClass().getSuperclass().getDeclaredField("commandCodec");
					commandCodec.setAccessible(true);
					commandCodec.set(this, new W3CHttpCommandCodec());
	
					Field responseCodec = null;
					responseCodec = this.getClass().getSuperclass().getDeclaredField("responseCodec");
					responseCodec.setAccessible(true);
					responseCodec.set(this, new W3CHttpResponseCodec());
					
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
				
			} else {
				response = super.execute(command);
			}
			return response;
		}
	};
	return new RemoteWebDriver(executor, new DesiredCapabilities());
}

System.setProperty("webdriver.chrome.driver", DriverFactory.getChromeDriverPath())
ChromeDriver driver = new ChromeDriver()
DriverFactory.changeWebDriver(driver)
WebUI.navigateToUrl("http://demoaut.katalon.com")

HttpCommandExecutor executor = (HttpCommandExecutor)driver.getCommandExecutor();
URL url = executor.getAddressOfRemoteServer();
println "url is " + url
SessionId session_id = driver.getSessionId()
println "session_is is " + session_id

RemoteWebDriver driver2 = createDriverFromSession(session_id, url)
driver2.get("http://tarunlalwani.com");

WebUI.delay(3)
WebUI.closeBrowser()