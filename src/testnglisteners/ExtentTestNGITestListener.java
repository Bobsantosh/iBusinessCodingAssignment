package testnglisteners;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class ExtentTestNGITestListener implements ITestListener {

	private static ExtentReports extent = createInstance(
			"Reports/iBussiness_Solutions" + new SimpleDateFormat("MM_dd_yyyy_HHmmss").format(new Date()) + ".html");
	public static ExtentTest parent;
	public static ThreadLocal<ExtentTest> Logger = new ThreadLocal<>();

	public static ExtentReports getInstance() {
		if (extent == null)
			createInstance(
					"Reports/iBussiness_Solutions" + new SimpleDateFormat("MM_dd_yyyy_HHmmss").format(new Date()) + ".html");
		return extent;
	}

	public static ExtentReports createInstance(String fileName) {
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(fileName);
		htmlReporter.config().setDocumentTitle("iBussiness Solutions Test Execution Report");
		htmlReporter.config().setReportName("iBussiness Solutions Test Execution Report");

		extent = new ExtentReports();
		extent.setAnalysisStrategy(AnalysisStrategy.CLASS);
		extent.attachReporter(htmlReporter);
		String tzId = TimeZone.getDefault().getID();
		DateTime dt = DateTime.now(DateTimeZone.forID(tzId));
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd_hh_mm-a");
		extent.setSystemInfo("LoggedInUser", System.getProperty("user.name").toUpperCase());
		extent.setSystemInfo("OS", System.getProperty("os.name").toUpperCase());
		extent.setSystemInfo("JRE Version", System.getProperty("java.version"));
		extent.setSystemInfo("Executed at:", fmt.print(dt) + " (" + tzId + ")");
		return extent;
	}
	
	@Override
	public synchronized void onStart(ITestContext context) {
		System.out.println("TestCase Name:: ********* " + context.getName() + " *********");
		if (!(context.getName().equalsIgnoreCase("TestSetup"))) {
			parent = extent.createTest(context.getName());
		}
	}

	@Override
	public synchronized void onFinish(ITestContext context) {
		extent.flush();
	}

	@Override
	public synchronized void onTestStart(ITestResult result) {
		System.out.println("Executing Test:: " + result.getMethod().getMethodName());
		ExtentTest extentTest = parent.createNode(result.getMethod().getMethodName(),
				result.getMethod().getDescription());
		for (String group : result.getMethod().getGroups()) {
			extentTest.assignCategory(group);
		}
		Logger.set(extentTest);
		Logger.get().log(Status.PASS, "Executing Test ->> " + result.getMethod().getMethodName());
	}

	@Override
	public synchronized void onTestSuccess(ITestResult result) {
		Logger.get().log(Status.PASS, "Test passed ->> " + result.getMethod().getMethodName() + ", Time taken :: "
				+ (result.getEndMillis() - result.getStartMillis()) / 1000.0);
		System.out.println("Test :: " + result.getMethod().getMethodName() + " PASSED!!");
		System.out.println("Time taken :: " + (result.getEndMillis() - result.getStartMillis()) / 1000.0 + "\n");
	}

	@Override
	public synchronized void onTestFailure(ITestResult result) {
		System.out.println("Test ->> " + result.getMethod().getMethodName() + ", FAILED!!");
		Logger.get().log(Status.FAIL, "Test ->> " + result.getMethod().getMethodName() + ", FAILED!!");
		Logger.get().log(Status.FAIL, result.getThrowable());
	}

	@Override
	public synchronized void onTestSkipped(ITestResult result) {
		System.out.println("Test :: " + result.getMethod().getMethodName() + " SKIPPED!!");
		ExtentTest extentTest = parent.createNode(result.getMethod().getMethodName());
		for (String group : result.getMethod().getGroups()) {
			extentTest.assignCategory(group);
		}
		Logger.get().skip(result.getThrowable().getMessage());
	}

	@Override
	public synchronized void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		System.out.println("Test :: " + result.getMethod().getMethodName() + " FAILED!!");
		Logger.get().log(Status.FAIL, "Test ->> " + result.getMethod().getMethodName() + ", FAILED!!");
		Logger.get().log(Status.FAIL, result.getThrowable());
	}

}
