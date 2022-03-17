package com.parallel;

import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONArray;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
	public static Calendar cal = Calendar.getInstance(TimeZone .getTimeZone("GMT")); 
	public static long time = cal.getTimeInMillis();   
	static ExtentReports extentReports;
	static JSONArray jsonArray;
	public synchronized static ExtentReports createExtentReports() {

		if(extentReports == null) {
			extentReports = new ExtentReports();
			jsonArray = new JSONArray();
			ExtentSparkReporter reporter = new ExtentSparkReporter("./extent-reports/extent-report.html");
			reporter.config().setReportName("Sample Extent Report");
			extentReports.attachReporter(reporter);
			
		}

		return extentReports;
	}     
	
	public static JSONArray addData() {
		return jsonArray;
	}
}

