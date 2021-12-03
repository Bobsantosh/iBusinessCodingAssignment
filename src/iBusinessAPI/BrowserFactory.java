package iBusinessAPI;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BrowserFactory {

	static WebDriver driver;

	public static WebDriver chromeBrowser() {
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless");
		options.addArguments("--disable-notifications");
		options.setAcceptInsecureCerts(true);
		driver = new ChromeDriver(options);
		return driver;
	}

	public static WebDriver firefoxBroswer() {
		WebDriverManager.firefoxdriver().setup();
		FirefoxProfile profile = new FirefoxProfile();
		FirefoxOptions options = new FirefoxOptions();
		options.setHeadless(true);
		profile.setAcceptUntrustedCertificates(true);
		profile.setAssumeUntrustedCertificateIssuer(true);
		driver = new FirefoxDriver(options);
		Dimension d = new Dimension(1920, 1080);
		driver.manage().window().setSize(d);
		return driver;
	}

}
