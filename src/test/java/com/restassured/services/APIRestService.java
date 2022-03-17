/*
 * package com.restassured.services;
 * 
 * import java.util.HashMap; import java.util.Map;
 * 
 * import org.apache.log4j.Logger; import org.json.JSONArray; import
 * org.json.JSONObject; import org.json.XML;
 * 
 * import com.fasterxml.jackson.databind.ObjectMapper; import
 * com.google.gson.JsonArray; import com.utilities.ConfigFilesUtility; import
 * com.utilities.MyApp; import com.utilities.OAuthUtilities; import
 * com.utilities.ReportPortalBaseClass; import com.utilities.Utilities;
 * 
 * import io.restassured.RestAssured; import
 * io.restassured.builder.RequestSpecBuilder; import
 * io.restassured.config.EncoderConfig; import
 * io.restassured.config.RestAssuredConfig; import
 * io.restassured.http.ContentType; import io.restassured.http.Cookie; import
 * io.restassured.http.Cookies; import io.restassured.response.Response; import
 * io.restassured.response.ResponseBody; import
 * io.restassured.specification.RequestSpecification; import
 * io.restassured.specification.ResponseSpecification;
 * 
 * public class APIRestService extends ReportPortalBaseClass {
 * 
 * 
 * public static ResponseSpecification getResponseSpecification(String
 * jsonHeaders) {
 * 
 * try { RequestSpecBuilder reqSpecBuilder = new RequestSpecBuilder(); JSONArray
 * headersJsonArray = new JSONArray(jsonHeaders); if(headersJsonArray.length() >
 * 0) {
 * 
 * @SuppressWarnings("unchecked") Map<String,String> headers = new
 * ObjectMapper().readValue(jsonHeaders, HashMap.class);
 * reqSpecBuilder.addHeaders(headers); }
 * 
 * reqSpecBuilder.setRelaxedHTTPSValidation();
 * reqSpecBuilder.setContentType("application/json; charset=UTF-8");
 * RequestSpecification requestSpecification =
 * RestAssured.given(reqSpecBuilder.build()); return
 * requestSpecification.expect(); } catch (Exception e) { return null; } }
 * 
 * 
 * private Cookies addCookie(Map<String,String> cookiesMap) { Cookie
 * userNameCookie = new Cookie.Builder("username", "some_value")
 * .setSecured(true) .setComment("some comment") .build(); Cookies cookies = new
 * Cookies(userNameCookie); return cookies; }
 * 
 * public static Response doRequest(int requestType, String url,String
 * inputBody, String headers, int bodyType,String formData, String
 * formurlEncodedData, ConfigFilesUtility configFileObj, String
 * authenticationData) { JSONArray bodyArray = null; //JSONArray parameters =
 * new JSONArray(urlParams); JSONObject body = new JSONObject(inputBody);
 * RequestSpecification requestSpecification =
 * getResponseSpecification(headers).given(); if(bodyType == 1 || bodyType == 2)
 * {
 * 
 * if(bodyType == 1) { bodyArray = body.optJSONArray(formData); } else
 * if(bodyType == 2) { bodyArray = new JSONArray(formurlEncodedData); }
 * 
 * for (int i = 0; i < bodyArray.length(); i++) { JSONObject bodyObj =
 * bodyArray.getJSONObject(i);
 * 
 * String key = Utilities.getFinalData(bodyObj.optString("key"),
 * configFileObj).replaceAll("\n", ""); String value =
 * Utilities.getFinalData(bodyObj.optString("value"),
 * configFileObj).replaceAll("\n", ""); requestSpecification.formParam(key,
 * value); } } else if(bodyType == 3) { String rawBody =
 * Utilities.getFinalData(body.optString("raw_text"), configFileObj);
 * requestSpecification.body(rawBody); } Response response = null;
 * if(requestType == 2) { response = requestSpecification.when().post(url); }
 * else if (requestType == 3) { response = requestSpecification.when().put(url);
 * } else if ( requestType == 4) { response =
 * requestSpecification.when().delete(url); }
 * 
 * return response;
 * 
 * }
 * 
 * 
 * public static Response getRequest(String url, String headers, String
 * authenticationData) { Response response =
 * getResponseSpecification(headers).given() .contentType(ContentType.JSON)
 * .when() .get(url); return response; }
 * 
 * public static String getRequestWithBasicAuth(RequestSpecification
 * requestSpecification, String basicAUthData) { RequestSpecification
 * requestSpec = requestSpecification.expect().given() .auth() .preemptive()
 * .basic(userName, accessKey);
 * 
 * return requestSpec; }
 * 
 * 
 * 
 * public static void putRequest(String url, String query, String body, String
 * headers) {
 * 
 * Response response = getResponseSpecification(headers).given()
 * .contentType(ContentType.JSON) .body(body) .when() .put(url);
 * System.out.println(response.getStatusCode());
 * System.out.println(response.asString()); }
 * 
 * public static void deleteRequest(String url, String headers) { Response
 * response = getResponseSpecification(headers).given()
 * .contentType(ContentType.JSON) .when() .delete(url);
 * System.out.println(response.getStatusCode());
 * System.out.println(response.asString()); }
 * 
 * public static String callRequest( ConfigFilesUtility con, String apiName,
 * String urlParams, String headers, int requestType, int bodyType, String
 * inputBody, String datatsetHeader, String dataResources, String
 * authenticationData, String formurlEncodedData, String formData, String
 * savedParameters, String statusParameters, Logger logger) {
 * 
 * try { String data = con.getProperty("PrimaryInfo"); JSONObject jsonObject =
 * new JSONObject(data); String testCaseName =
 * jsonObject.getString("testcase_name"); String projectName =
 * jsonObject.optString("project_name"); ConfigFilesUtility configFileObj= new
 * ConfigFilesUtility(); configFileObj.loadPropertyFile(projectName
 * +".properties"); String Url =
 * Utilities.getFinalData(jsonObject.optString("project_url") + dataResources,
 * configFileObj); Response response = null; if (requestType == 1) { response =
 * getRequest(Url, headers, authenticationData); } else if (requestType > 1) {
 * response = doRequest(requestType, Url, inputBody, headers, bodyType,
 * formData, formurlEncodedData, configFileObj, authenticationData);
 * 
 * }
 * 
 * String finalResponse = response.asString(); int statusCode =
 * response.getStatusCode();
 * 
 * validateResponse(response, projectName, testCaseName, savedParameters,
 * statusParameters);
 * 
 * } catch (Exception e) { // TODO: handle exception }
 * 
 * return statusParameters;
 * 
 * 
 * }
 * 
 * 
 * private static void validateResponse(Response response, String projectName,
 * String testCaseName, String savedParameters, String statusParameters) { if
 * (response != null) {
 * 
 * ResponseBody<?> responseBody = response.getBody(); int statusCode =
 * response.getStatusCode(); String responseString = response.asString();
 * System.out.println(responseString); try { String cType =
 * response.getContentType().toLowerCase(); if(cType.contains("xml")) {
 * JSONObject js = XML.toJSONObject(response.asString()); responseString =
 * js.toString(); System.out.println("======="+js); } } catch (Exception e) { }
 * 
 * APIService.reportCreation("info", testCaseName + " API status code is : " +
 * statusCode); MyApp.checkStatusCode("" + statusCode, statusParameters);
 * APIService.reportCreation("info", "Response: " + responseString);
 * 
 * MyApp.data(projectName, responseString, savedParameters, "[]"); // to save
 * the specific key from reponse
 * 
 * String validatedResponse = MyApp.data(projectName, responseString, "[]",
 * statusParameters); // to validate the response with provided key
 * if(!validatedResponse.isEmpty()) { APIService.reportCreation("fail",
 * validatedResponse); }
 * 
 * System.out.println(responseString); } }
 * 
 * 
 * private void authentication(RequestSpecification requestSpec, String
 * authenticationData) {
 * requestSpec.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.
 * encoderConfig().defaultContentCharset("UTF-8"))); if(authenticationData !=
 * null && !authenticationData.isEmpty()) { JSONObject authData = new
 * JSONObject(authenticationData); String authType =
 * authData.optString("authtype");
 * 
 * if(authType.equalsIgnoreCase("oauth2")) { String response = ""; try {
 * JSONObject obj = authData.optJSONObject("oauth2"); String tokenURL =
 * obj.optString("tokenurl"); String clientId = obj.optString("clientid");
 * String clientSecret = obj.optString("clientsecret");
 * APIService.reportCreation("info", "Token URL: " + tokenURL);
 * APIService.reportCreation("info", "Client Id: " + clientId);
 * APIService.reportCreation("info", "Client Secret: " + clientSecret);
 * response= OAuthUtilities.getAccessTokenFromOAuthData(tokenURL, clientId,
 * clientSecret); MyApp.data(projectName, response, savedParameters, "[]");
 * APIService.reportCreation("info", "Response: " + response); } catch
 * (Exception e) { reportCreation("fail", "Invalid response : " +
 * e.getLocalizedMessage()); }
 * 
 * return response; }
 * 
 * if (authType.equalsIgnoreCase("bearertoken")) { JSONObject bearerTokenObj =
 * authData.optJSONObject("bearertoken"); String token =
 * getFinalData(bearerTokenObj.optString("token"));
 * 
 * requestSpec.header("Authorization", "bearer " + token);
 * reportCreation("info", "Authorization : bearer " + token); } else if
 * (authType.equalsIgnoreCase("basicauth")) { JSONObject basicauthObj =
 * authData.optJSONObject("basicauth"); String username =
 * getFinalData(basicauthObj.optString("username")); String password =
 * getFinalData(basicauthObj.optString("password"));
 * 
 * reportCreation("info", "Username : " + username); reportCreation("info",
 * "Password : " + password); requestSpec.auth().basic(username, password); }
 * else if (authType.equalsIgnoreCase("apikey")) {
 * 
 * JSONObject apiKeyObj = authData.optJSONObject("apikey"); String apikey =
 * getFinalData(apiKeyObj.optString("key")); String value =
 * getFinalData(apiKeyObj.optString("value"));
 * 
 * reportCreation("info", "apikey : " + apikey + "\n value : " + value); String
 * headerOrQuery = apiKeyObj.optString("value"); if
 * (headerOrQuery.equalsIgnoreCase("header")) { requestSpec.header(apikey,
 * value); } else { requestSpec.queryParam(apikey, value); } }
 * 
 * }
 * 
 * 
 * }
 */