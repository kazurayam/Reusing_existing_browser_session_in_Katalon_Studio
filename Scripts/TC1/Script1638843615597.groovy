import java.lang.reflect.Field

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.Command
import org.openqa.selenium.remote.CommandExecutor
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.HttpCommandExecutor
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.Response
import org.openqa.selenium.remote.SessionId
import org.openqa.selenium.remote.http.W3CHttpCommandCodec
import org.openqa.selenium.remote.http.W3CHttpResponseCodec

import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

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