package iBusinessAPI;

import static java.util.Arrays.asList;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import testnglisteners.ExtentTestNGITestListener;

@SuppressWarnings("deprecation")
public class CodingAssignment extends ExtentTestNGITestListener {
	public static double rand = Math.random() * (200 - 100 + 1) + 100;
	public static String EMAIL = rand + "bobba.vigna@qa.in";
	private static String CREATE_ACCOUNT_PAYLOAD = "{\n" + "  \"FirstName\": \"Bobba\",\n" + "  \"Email\": \"" + EMAIL
			+ "\",\n" + "  \"Phone\": 1234567890,\n" + "  \"LastName\": \"Vigna\",\n" + "  \"Role__c\": \"QA\"\n"
			+ "}\n";
	private static String GENERATE_TOKEN_URL = "https://login.salesforce.com/services/oauth2/token";
	private static String CREATE_ACCOUNT_URL = "https://empathetic-shark-a7palw-dev-ed.my.salesforce.com/services/data/v48.0/sobjects/Contact";
	private static String LOGIN_URL = "https://login.salesforce.com";
	private static String NAVIGATE_URL = "https://empathetic-shark-a7palw-dev-ed.lightning.force.com";
	private static String USERNAME = "testforibusiness@ibusiness.in";
	private static String PASSWORD = "Test@123";
	private static String SCHEMA_PATH = "./Schemas/createAccount.json";
	private static String CLIENT_ID = "3MVG9fe4g9fhX0E5qgtfiN3DaF2W..oTppSVm3aQseMPrMG1rR0ie5_Q.pOx7TuLFvaG1dJN1cFTQvboyZobT";
	private static String CLIENT_SECRET = "AA94756377125E3FA965C0C2B91290A55E852B53E401CFBD46BBCEBDABB1B3F3";

	private static WebDriver driver;

	@Test(priority = 1, enabled = true)
	public static void codeAssignment() {
		String accessToken = getAccessToken();
		String createdId = createAccount(accessToken);
		login();
		verifyEmail(createdId);
		driver.close();
	}

	public static String getAccessToken() {
		ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
		Logger.get().log(Status.PASS, "Generating Access Token using Password GrantType and Client_Id: " + CLIENT_ID);
		resourceDetails.setUsername(USERNAME);
		resourceDetails.setPassword(PASSWORD);
		resourceDetails.setAccessTokenUri(GENERATE_TOKEN_URL);
		resourceDetails.setClientId(CLIENT_ID);
		resourceDetails.setClientSecret(CLIENT_SECRET);
		resourceDetails.setGrantType("password");
		resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.form);
		DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();

		OAuth2RestTemplate auth2RestTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
		auth2RestTemplate.setMessageConverters(asList(new MappingJackson2HttpMessageConverter()));
		Logger.get().log(Status.PASS, "Access Token: " + auth2RestTemplate.getAccessToken());
		Assert.assertNotNull(auth2RestTemplate.getAccessToken());
		return auth2RestTemplate.getAccessToken().toString();
	}

	public static String createAccount(String accessToken) {
		Logger.get().log(Status.PASS, "Creating Account using Payload");
		Logger.get().log(Status.PASS, MarkupHelper.createCodeBlock(CREATE_ACCOUNT_PAYLOAD, CodeLanguage.JSON));
		Response response = RestAssured.given().header("Authorization", "Bearer " + accessToken)
				.header("Content-Type", "application/json").header("Sforce-Auto-Assign", "TRUE").log().all()
				.body(CREATE_ACCOUNT_PAYLOAD).post(CREATE_ACCOUNT_URL);
		int status = response.getStatusCode();
		Assert.assertEquals(status, 201);
		verifySchema(response, SCHEMA_PATH);
		String createdId = response.jsonPath().getString("id");
		Logger.get().log(Status.PASS, "Created ID: " + createdId);
		return createdId;
	}

	public static void verifySchema(Response response, String SchemaPath) {
		try {
			ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
			File jsonFile = new File(SchemaPath);
			Logger.get().log(Status.PASS, "Validating Schema from Path ->> " + SchemaPath);
			Logger.get().log(Status.PASS, "Validating Schema using Respose ->> " + response.asString());
			responseSpecBuilder.expectBody(JsonSchemaValidator.matchesJsonSchema(jsonFile));
			ResponseSpecification respSpec = responseSpecBuilder.build();
			response.then().spec(respSpec);
			Logger.get().log(Status.PASS, "Schema Validation PASSED");
		} catch (AssertionError assertionError) {
			Assert.fail("Schema Validation FAILED" + "<br><br>" + assertionError);
		} catch (Exception e) {
			Assert.fail("Schema Validation FAILED" + "<br><br>" + e.getMessage());
		}
	}

	public static void login() {
		driver = BrowserFactory.chromeBrowser();
		driver.manage().window().maximize();
		driver.get(LOGIN_URL);
		WebDriverWait wait1 = new WebDriverWait(driver, 120);
		wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(USERNAME);
		wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("password"))).sendKeys(PASSWORD);
		wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("Login"))).click();
		wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1//span[text()='Home']")));
		Logger.get().log(Status.PASS, "Logged into Saleforce application successfully : " + driver.getCurrentUrl());
	}

	public static void verifyEmail(String createdId) {
		driver.get(NAVIGATE_URL + "/" + createdId);
		WebDriverWait wait1 = new WebDriverWait(driver, 120);
		wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[text()='Email']")));
		Logger.get().log(Status.PASS, "Navigated to URL : " + driver.getCurrentUrl());
		Assert.assertEquals(driver.findElement(By.xpath("//a[@class='emailuiFormattedEmail']")).getText(), EMAIL);
		Logger.get().log(Status.PASS, "Verified Email : " + EMAIL);
	}

}
