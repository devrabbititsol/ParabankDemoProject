package com.parallel;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.configurations.GlobalData;

public class ExtTest {  
	static Map<Integer, ExtentTest> extentTestMap = new HashMap<>();
	static Map<String, JSONObject> jsonObjMap = new HashMap<>();
	static ExtentReports extent = ExtentManager.createExtentReports();
	static JSONArray jsonArray = ExtentManager.addData();

	public static synchronized ExtentTest getTest(String testName,String status, String data) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result_type", status);
		jsonObject.put("text", data);
		JSONArray array = jsonObjMap.get((int) Thread.currentThread().getId() + testName).getJSONArray("datasets");
		array.put(jsonObject);
		return extentTestMap.get((int) Thread.currentThread().getId());
	}
	public static synchronized ExtentTest getTest() {
		return extentTestMap.get((int) Thread.currentThread().getId());
	}


	public static synchronized void endTest() {
		//extent.removeTest(extentTestMap.get((int) (long) (Thread.currentThread().getId())));
		System.err.println("flushed");
		System.err.println(jsonObjMap.toString());
		GlobalData.setReportData(new JSONArray().put(new JSONObject(jsonObjMap.toString().replaceAll("=", ":"))));
		extent.flush();
	}

	public static synchronized ExtentTest startTest(String testName, String browserName) {
		ExtentTest test = extent.createTest(testName, "");
		JSONArray array = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("browser_name", browserName);
		obj.put("testcase_name", testName);
		obj.put("datasets", array);
		extentTestMap.put((int) Thread.currentThread().getId(), test);
		jsonObjMap.put((int) Thread.currentThread().getId() + testName, obj);
		return test;
	}


}
