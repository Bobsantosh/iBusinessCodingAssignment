package testnglisteners;

import java.io.PrintStream;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class OverridePrintStream extends PrintStream {
	public OverridePrintStream(PrintStream original) {
		super(original);
	}

	@Override
	public void println(String line) {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		StackTraceElement caller = stack[2];
		String prettyPrint = line.replace("\n", "<br>");
		try {
			ExtentTestNGITestListener.Logger.get().log(Status.INFO, MarkupHelper.createCodeBlock(prettyPrint));
		} catch (Exception e) {
			System.out.println("Log Exception");
		}
		super.println(caller.getClassName() + ": " + line);
	}
}
