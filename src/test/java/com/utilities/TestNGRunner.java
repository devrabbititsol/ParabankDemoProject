package com.utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;

public class TestNGRunner {

	public static void main(String[] args) {

		// Create object of TestNG Class
		TestNG runner=new TestNG();

		// Create a list of String 
		List<String> suitefiles=new ArrayList<String>();

		// Add xml file which you have to execute
		suitefiles.add(System.getenv("AUTOMATION_PATH") + File.separator + args[0]);

		// now set xml file for execution
		runner.setTestSuites(suitefiles);

		// finally execute the runner using run method
		runner.run();
	}

}