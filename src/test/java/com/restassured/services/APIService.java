package com.restassured.services;

import com.configurations.GlobalData;
import com.utilities.ConfigFilesUtility;
import com.utilities.MyApp;
import com.utilities.OAuthUtilities;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class APIService {

	private static JSONArray jsonArray;
	private static ConfigFilesUtility configFileObj;
	private static boolean isXMLData = false;

	@SuppressWarnings({ "unused", "static-access" })
	public static String callRequest(ConfigFilesUtility con, String apiName, String urlParams, String headers, int requestType, int bodyType, String inputBody,
			String datatsetHeader, String dataResources, String authenticationData,String formurlEncodedData,
			String formData,String savedParameters, String statusParameters) {

		configFileObj= new ConfigFilesUtility();
		isXMLData = false;

		String[] bodyTpes = new String[] { "", "form-data", "x-www-form-urlencoded", "raw" };
		String[] types = new String[] { "", "GET", "POST", "PUT", "DELETE" };
		String contentType = "";
		JSONObject jsonoBj = new JSONObject();
		jsonArray = new JSONArray();

		try {

			String data  = con.getProperty("PrimaryInfo");
			JSONObject jsonObject = new JSONObject(data);
			String testCaseName = jsonObject.getString("testcase_name");
			String projectName = jsonObject.optString("project_name");
			String projectId = jsonObject.optString("project_id");
			String reportsPath = "reportsPath";
			try {
				configFileObj.loadPropertyFile(  projectName +".properties");
			} catch (Exception e) {
				//e.printStackTrace();
			}

			String returnString = jsonObject.optString("returnString");
			String packageFolder = jsonObject.optString("moduleName");
			String type = types[requestType];
			jsonoBj.put("testcase_name",testCaseName + "-" + datatsetHeader);
			jsonoBj.put("datasets", jsonArray);
			new GlobalData().reportData(testCaseName, jsonoBj);
			new GlobalData().primaryInfoData(con);
			String Url = getFinalData(jsonObject.optString("project_url") + dataResources);

			reportCreation("info","API Name : " + apiName);
			reportCreation("info",Url);

			RequestSpecification requestSpec = given();
			requestSpec.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")));
			if(authenticationData != null && !authenticationData.isEmpty()) {
				JSONObject authData = new JSONObject(authenticationData);
				String authType = authData.optString("authtype");

				if(authType.equalsIgnoreCase("oauth2")) {
					String response = "";
					try {
						JSONObject obj = authData.optJSONObject("oauth2"); 
						String tokenURL = obj.optString("tokenurl");
						String clientId = obj.optString("clientid");
						String clientSecret = obj.optString("clientsecret");
						reportCreation("info", "Token URL: " + tokenURL);
						reportCreation("info", "Client Id: " + clientId);
						reportCreation("info", "Client Secret: " + clientSecret);
						response=  OAuthUtilities.getAccessTokenFromOAuthData(tokenURL, clientId, clientSecret);
						MyApp.data(projectName, response, savedParameters, "[]");
						reportCreation("info", "Response: " + response);
					} catch (Exception e) {
						reportCreation("fail", "Invalid response : " + e.getLocalizedMessage());
					}

					return response;
				}

				if (authType.equalsIgnoreCase("bearertoken")) {
					JSONObject bearerTokenObj = authData.optJSONObject("bearertoken");
					String token = getFinalData(bearerTokenObj.optString("token"));

					requestSpec.header("Authorization", "bearer " + token);
					reportCreation("info", "Authorization : bearer " + token);
				} else if (authType.equalsIgnoreCase("basicauth")) {
					JSONObject basicauthObj = authData.optJSONObject("basicauth");
					String username = getFinalData(basicauthObj.optString("username"));
					String password = getFinalData(basicauthObj.optString("password"));

					reportCreation("info", "Username : " + username);
					reportCreation("info", "Password : "  + password);
					requestSpec.auth().basic(username, password);
				} else if (authType.equalsIgnoreCase("apikey")) {

					JSONObject apiKeyObj = authData.optJSONObject("apikey");
					String apikey = getFinalData(apiKeyObj.optString("key"));
					String value = getFinalData(apiKeyObj.optString("value"));

					reportCreation("info", "apikey : " + apikey + "\n value : "  + value);
					String headerOrQuery = apiKeyObj.optString("value");
					if (headerOrQuery.equalsIgnoreCase("header")) {
						requestSpec.header(apikey, value);
					} else {
						requestSpec.queryParam(apikey, value);
					}
				}
			}


			JSONArray headersJsonArray = new JSONArray(headers);
			JSONArray parameters = new JSONArray(urlParams);
			JSONObject body = new JSONObject(inputBody);
			int raw_id = body.optInt("raw_id");	 //content type
			reportCreation("info","Request Type :  " + type);
			contentType = (raw_id == 5 ? "" : "application/json; charset=UTF-8");
			if(!contentType.isEmpty())
				reportCreation("info", "Content Type :  " + contentType);
			requestSpec.header("Content-Type",contentType);
			System.out.println("Request Type :  " + type);

			if (headersJsonArray.length() > 0) {
				extentHeaderLog( "Headers");
				System.out.println("Headers :  " + StringEscapeUtils.unescapeJava(headersJsonArray.toString()));
			} 

			for (int i = 0; i < headersJsonArray.length(); i++) {
				JSONObject headerObj = headersJsonArray.getJSONObject(i);
				String headerkey = getFinalData(headerObj.getString("header_key"));
				String headerValue = getFinalData(headerObj.getString("header_value"));
				reportCreation("info", headerkey + " : "+ headerValue);
				requestSpec.header(headerkey, headerValue);
			}

			if (parameters.length() > 0) {
				extentHeaderLog( "Input Parameters");
				System.out.println("Parameters :  " + StringEscapeUtils.unescapeJava(parameters.toString()));
			} 

			for (int i = 0; i < parameters.length(); i++) {
				JSONObject parametersObj = parameters.getJSONObject(i);
				if (requestType > 1) {
					String key = getFinalData(parametersObj.getString("param_key")).replaceAll("\n", "");
					String value = getFinalData(parametersObj.getString("param_value")).replaceAll("\n", "");
					reportCreation("info", key + " : "+ value);
					requestSpec.queryParam(key, value);
				} else {
					String key = getFinalData(parametersObj.getString("param_key")).replaceAll("\n", "");
					String value = getFinalData(parametersObj.getString("param_value")).replaceAll("\n", "");
					reportCreation("info", key + " : "+ value);
					requestSpec.param(key, value);
				}
			}

			if (body.length() > 0) {
				extentHeaderLog( "Input Body");
				reportCreation("info", "body  :  " + body.toString());
				System.out.println("body :  " + StringEscapeUtils.escapeHtml(body.toString()));
			}

			Response response = null;
			if (requestType == 1) { // GET
				response = requestSpec.when().get(Url).then().extract().response();
			} else if (requestType > 1) { // POST,PUT,DELETE
				String rawBody = "";
				JSONArray bodyArray = null;
				if (bodyType == 1 || bodyType == 2) { // form-data or x-www-form-urlencoded
					if(bodyType == 2) {
						contentType =  "application/x-www-form-urlencoded; charset=UTF-8";
						bodyArray = new JSONArray(formurlEncodedData);
					}
					if(bodyType == 1) {
						bodyArray = body.optJSONArray(formData);
					}
					for (int i = 0; i < bodyArray.length(); i++) {
						JSONObject bodyObj = bodyArray.getJSONObject(i);

						String key = getFinalData(bodyObj.optString("key")).replaceAll("\n", "");
						String value =getFinalData(bodyObj.optString("value")).replaceAll("\n", "");
						reportCreation("info", key + " : " + value);
						requestSpec.formParam(key, value);
					}
				} else if (bodyType == 3) { // raw data
					rawBody = getFinalData(body.optString("raw_text"));
					System.out.println("raw body :  " + StringEscapeUtils.unescapeJava(rawBody));
				}


				//requestSpec;
				if(bodyType > 2) {
					requestSpec.body(rawBody);
				}
				if (requestType == 2) {
					response = requestSpec.when().post(Url);
				} else if (requestType == 3) {
					response =  requestSpec.when().put(Url);
				} else if (requestType == 4) {
					response =  requestSpec.when().delete(Url);
				} else if (requestType == 5) {
					response =  requestSpec.when().patch(Url);
				} 
			}

			extentHeaderLog( "Output");
			if (response != null) {	

				ResponseBody<?> responseBody = response.getBody();
				int statusCode = response.getStatusCode();
				String responseString = response.asString();
				System.out.println(responseString);
				String finalResponse = responseString;
				try {
					String cType = response.getContentType().toLowerCase();
					if(cType.contains("xml")) {
						isXMLData = true;
						reportCreation("info", "Response Content-Type: " + cType);
						JSONObject js = XML.toJSONObject(response.asString());
						finalResponse = js.toString();
						
					}
				} catch (Exception e) {
					finalResponse =responseString;
				}

				boolean status = MyApp.checkStatusCode("" + statusCode, statusParameters);
				if(!status) {
					if (statusCode == 200 || statusCode == 201) {
						MyApp.data(projectName, finalResponse, savedParameters, "[]");

						String validatedResponse = MyApp.data(projectName, finalResponse, "[]", statusParameters );
						if(validatedResponse.isEmpty()) {
							reportCreation("pass", "Response: " + responseString);
						} else {
							reportCreation("fail", "Response: " + responseString);
							reportCreation("fail",  validatedResponse);
						}

						System.out.println(testCaseName + " API status code is :" + statusCode + " : " + responseString);
						System.out.println(responseString);
						return responseString;
					} else if(statusCode == 400) {	
						reportCreation("fail", responseString);
						System.out.println("response :  " + responseString +"status :" + statusCode);
						return responseString;				
					} else if(statusCode == 404) {
						reportCreation("fail", "Invalid response : HTTP Status 404  Not Found ");
						System.out.println("Invalid response body returned as :  " + responseString);
					} 
				} else {
					reportCreation("info", "Response: " + responseString);
				}

				System.out.println(responseString);
			}

		} catch (Exception e) {
			String exception = e.getClass().getSimpleName() + "-" + e.getLocalizedMessage();
			reportCreation("fail", "Invalid response : " + exception);

		}
		return "";

	}

	public static void extentReportLog(String data) {
		reportCreation("info", StringEscapeUtils.unescapeJava(data));
	}

	public static void extentHeaderLog(String data) {
		reportCreation("info", "<b>" + StringEscapeUtils.unescapeJava(data) + "</b>");
	}


	public static boolean isJSONValid(String json) {
		try {
			new JSONObject(json);
		} catch (JSONException ex) {
			try {
				new JSONArray(json);
			} catch (JSONException exception) {
				return false;
			}
		}
		return true;
	}


	public static boolean isXMLLike(String inXMLStr) {

		boolean retBool = false;
		Pattern pattern;
		Matcher matcher;
		// REGULAR EXPRESSION TO SEE IF IT AT LEAST STARTS AND ENDS
		// WITH THE SAME ELEMENT
		final String XML_PATTERN_STR = "<(\\S+?)(.*?)>(.*?)</\\1>";
		// IF WE HAVE A STRING
		if (inXMLStr != null && inXMLStr.trim().length() > 0) {
			// IF WE EVEN RESEMBLE XML
			if (inXMLStr.trim().startsWith("<")) {

				pattern = Pattern.compile(XML_PATTERN_STR,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

				// RETURN TRUE IF IT HAS PASSED BOTH TESTS
				matcher = pattern.matcher(inXMLStr);
				retBool = matcher.matches();
			}
			// ELSE WE ARE FALSE
		}
		return retBool;
	}

	public static void reportCreation(String result, String data) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result_type", result);
		jsonObject.put("text", isXMLData ? StringEscapeUtils.unescapeXml(data) :  StringEscapeUtils.unescapeJava(data));

		if(result.equalsIgnoreCase("fail")) {
			System.err.println(StringEscapeUtils.unescapeJava(data));
		} else
			System.out.println((StringEscapeUtils.unescapeJava(data)));

		jsonArray.put(jsonObject);
	}




	private static String getFinalData(String splitData) {
		String returnData = splitData;
		try {

			if (splitData.contains("$") && splitData.contains("#")) {

				String[] data = splitData.split("\\$");
				for (int i = 0; i < data.length; i++) {
					if (data[i].startsWith("#") && data[i].endsWith("#")) {
						System.out.println(data[i]);
						String replacement = data[i].replaceAll("#", "");
						if(configFileObj == null) 
							return "";
						String finalConfigData = configFileObj.getProperty(replacement);
						returnData = returnData.replaceAll("\\$", "").replaceAll("#", "").replace(replacement,
								finalConfigData);
					}
				}
			}
		}catch (Exception e) {
			return splitData + "<br><br>Please check ordering the testcases and given parameters is valid or not";
		}
		return returnData;
	}

}
