package testnglisteners;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class ExtentManager {

	private static ExtentReports extent;

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
}