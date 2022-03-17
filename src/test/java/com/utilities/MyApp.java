package com.utilities;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.restassured.services.APIService;

public class MyApp {


	public static boolean checkStatusCode(String statusCode, String apiStatusParameters) {

		try {
			JSONArray apiStatusParametersArray = new JSONArray(apiStatusParameters);
			for (int i = 0; i < apiStatusParametersArray.length(); i++) {

				JSONObject obj =  apiStatusParametersArray.optJSONObject(i);
				if(obj != null) {

					String value = obj.optString("value");
					String key = obj.optString("key");
					if(key.equalsIgnoreCase("statuscode")) {
						APIService.reportCreation("info", "Actual Status Code: " + statusCode);
						if(!statusCode.equalsIgnoreCase(value)) {
							APIService.reportCreation("info", "Expected Status Code: " + value);
							APIService.reportCreation("fail", " Actual and Expected Status code not equal");
						} else {
							APIService.reportCreation("pass", "Actual and Expected status codes are equal");
						}
						
						return true;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}

		return  false;
	}


	public static String data(String projectName, String json, String  savedParamters, String apiStatusParameters) {

		JSONArray savedParametersArray = new JSONArray(savedParamters);
		JSONArray apiStatusParametersArray = new JSONArray(apiStatusParameters);

		for (int i = 0; i < apiStatusParametersArray.length(); i++) {

			list = new ArrayList<String>();
			JSONObject obj =  apiStatusParametersArray.optJSONObject(i);
			if(obj != null) {
				String value = obj.optString("value");
				String propertiesKey = obj.optString("key");
				String desc = obj.optString("description");

				check(propertiesKey, JsonParser.parseString(json));
				System.out.println("list size: " + list.size());

				if(list.size() >0 ) {
					int index = 0;
					String finalData = "";
					try {
						index = desc.isEmpty() ? 0 : Integer.parseInt(desc);
						finalData = list.get(index);
					} catch (Exception e) {
						index = 0;
						finalData = list.get(index);
					}

					String property = finalData;
					if (property.startsWith("\"")) {
						property = property.substring(1);
					}
					if (property.endsWith("\"")) {
						property = property.substring(0, property.length()-1);
					}



					if(StringEscapeUtils.unescapeJava(value).equalsIgnoreCase(property)) {

						String actual = property;
						String expected = value;
						try {
							APIService.reportCreation("Actual", "Actual " +  propertiesKey + ": " + actual);
							APIService.reportCreation("Expected", "Expected " +  propertiesKey + ": " + expected);			
							APIService.reportCreation("pass", propertiesKey + " Validated as expected");
						} catch (Exception e) {
							// value is not expected
						}
						continue;
					} else {
						return "Expected : " + value + ", Actual : " + property ;
					}
				}
			}
		}

		for (int i = 0; i < savedParametersArray.length(); i++) {
			list = new ArrayList<String>();
			System.out.println("JSON: " + json);
			JSONObject obj =  savedParametersArray.optJSONObject(i);
			if(obj != null) {
				String jsonkey = obj.optString("value");
				String propertiesKey = obj.optString("key");
				String desc = obj.optString("description");

				if(desc != null && desc.equalsIgnoreCase("save")) {
					writepropertiesFile(projectName, jsonkey + " = " + propertiesKey , jsonkey);
					continue;
				}

				check(jsonkey, JsonParser.parseString(json));
				System.out.println("list size: " + list.size());
				if(list.size() >0 ) {
					int index = 0;
					String finalData = "";
					try {
						index = desc.isEmpty() ? 0 : Integer.parseInt(desc);
						finalData = list.get(index);
					} catch (Exception e) {
						index = 0;
						finalData = list.get(index);
					}

					String property = finalData;
					if (property.startsWith("\"")) {
						property = property.substring(1);
					}
					if (property.endsWith("\"")) {
						property = property.substring(0, property.length()-1);
					}

					writepropertiesFile(projectName, propertiesKey + " = " + property , propertiesKey);
					System.out.println(list);
				}
			}

		}
		return "";

	}


	private static void writepropertiesFile(String projectName, String content, String propKey) {
		String path = "." + File.separator + "ConfigFiles" + File.separator + projectName + ".properties";
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Checking If The File Exists At The Specified Location Or Not
		Path filePathObj = Paths.get(path);
		boolean fileExists = Files.exists(filePathObj);
		if(fileExists) {
			try {
				List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
				boolean isOverwrited = false;
				for (int i = 0; i < fileContent.size(); i++) {
					if (fileContent.get(i).contains(propKey + " = ")) {
						fileContent.set(i, content);
						isOverwrited = true;
						break;
					}
				}
				if(isOverwrited) {
					Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
				} else {
					content = "\n" + content;
					Files.write(filePathObj, content.getBytes(), StandardOpenOption.APPEND);
				}

			} catch (IOException ioExceptionObj) {
				System.out.println("Problem Occured While Writing To The File= " + ioExceptionObj.getMessage());
			}
		} else {
			System.out.println("File Not Present! Please Check!");
		}     
	}


	static List<String> list = new ArrayList<String>();
	private static void check(String key, JsonElement jsonElement) {

		if (jsonElement.isJsonArray()) {
			for (JsonElement jsonElement1 : jsonElement.getAsJsonArray()) {
				check(key, jsonElement1);
			}
		} else {
			if (jsonElement.isJsonObject()) {
				Set<Map.Entry<String, JsonElement>> entrySet = jsonElement
						.getAsJsonObject().entrySet();
				for (Map.Entry<String, JsonElement> entry : entrySet) {
					String key1 = entry.getKey();
					if (key1.equalsIgnoreCase(key)) {
						list.add(entry.getValue().toString());
					}
					check(key, entry.getValue());
				}
			} else {
				if (jsonElement.toString().equals(key)) {
					list.add(jsonElement.toString());
				}
			}
		}
	}

}