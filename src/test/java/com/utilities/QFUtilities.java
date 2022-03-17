package com.utilities;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class QFUtilities {


	// InputText method -- you can customize by using Actions
	public void sendKeys(WebDriver driver, String xpath, String inputValue) {
		//scrollElement( driver, xpath);
		By locator = getLocator(xpath);
		WebElement element = waitForElement(driver,locator);
		try {
			element.click();
		} catch (Exception e) {
		}
		inputClear(element);
		element.sendKeys(inputValue);
	}

	public void clickAction(WebDriver driver, String xpath) {	
		By locator = getLocator(xpath);
		try {
			waitForElementClickable(driver, locator);
			click(driver, locator);
		} catch(org.openqa.selenium.NoSuchElementException ex) {
			wait(driver, xpath);
			javascriptClick(driver, locator);

		} catch(org.openqa.selenium.StaleElementReferenceException ex) {
			wait(driver, xpath);
			javascriptClick(driver, locator);
		} catch (org.openqa.selenium.ElementNotInteractableException ex) {
			wait(driver, xpath);
			javascriptClick(driver, locator);
		} 
	}

	public void clickAction(WebDriver driver, String xpath, boolean isClick) {	

		if(isClick) {
			By locator = getLocator(xpath);
			try {
				click(driver, locator);
			} catch(org.openqa.selenium.StaleElementReferenceException ex) {
				wait(driver, xpath);
				javascriptClick(driver, locator);
			} catch (org.openqa.selenium.ElementNotInteractableException ex) {
				wait(driver, xpath);
				javascriptClick(driver, locator);
			} catch (Exception e) {
				wait(driver, xpath);
				javascriptClick(driver, locator);
			}
		}
	}

	private void click(WebDriver driver, By locator) {

		waitForElement(driver,locator);	
		Actions actions = new Actions(driver);
		WebElement hoverOption = driver.findElement(locator);
		actions.moveToElement(hoverOption).build().perform();
		hoverOption.click();
	}

	private void javascriptClick(WebDriver driver, By locator) {
		WebElement element = waitForElement(driver, locator);
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		//	executor.executeScript("arguments[0].scrollIntoView(true);", element);
		executor.executeScript("arguments[0].click();", element);
	}
	
	public String selectOption(WebDriver driver, String xpath, String optionValue) {
		By locator = getLocator(xpath);
		waitForElement(driver,locator);	
		driver.findElement(locator).click();
		new Select(driver.findElement(locator)).selectByVisibleText(optionValue.trim());
		driver.findElement(locator).click();
		String actualText = new Select(driver.findElement(locator)).getFirstSelectedOption().getText();
		return actualText;
	}


	public String validateSelectOption(WebDriver driver, String xpath, String expectedText) {
		By locator = getLocator(xpath);
		waitForElement(driver,locator);	
		Select select = new Select(driver.findElement(locator));  
		String actualText = select.getFirstSelectedOption().getText();
		String actualTxt  = "";
		if(expectedText.contains(actualText) || actualText.contains(expectedText)) {
			actualTxt =  "Pass: Validated selected option is: " + expectedText;
		} else {
			actualTxt =  "Fail: Expected: " + expectedText + "<br/>";
			actualTxt = actualTxt + "Actual: " + actualText + "<br/>";
			actualTxt = actualTxt +  "Actual and Expected text are not equal. Expected is: " + expectedText;
		}

		return actualTxt;

	}


	public void mouseHover(WebDriver driver, String xpath, boolean isClick) {
		By locator = getLocator(xpath);
		waitForElement(driver,locator);	
		Actions actions = new Actions(driver);
		WebElement hoverOption = driver.findElement(locator);
		actions.moveToElement(hoverOption).build().perform();
		if(isClick) hoverOption.click();

	}


	//Validate InputText method -- you can customize by using Actions
	public String validateInput(WebDriver driver, String xpath, String expectedText) {
		By locator = getLocator(xpath);
		waitForElement(driver,locator);	
		WebElement element = driver.findElement(locator);
		String actualText = element.getAttribute("value");
		String actualTxt  = "";
		if(expectedText.contains(actualText) || actualText.contains(expectedText)) {
			actualTxt =  "Pass: Validated Text is: " + expectedText;
		} else {
			actualTxt =  "Fail: Expected: " + expectedText + "<br/>";
			actualTxt = actualTxt + "Actual: " + actualText + "<br/>";
			actualTxt = actualTxt +  "Actual and Expected text are not equal. Expected is: " + expectedText;
		}

		return actualTxt;
	}

	//Validate InputText method -- you can customize by using Actions
	public String validateLabel(WebDriver driver, String xpath, String validateMsg) {
		By locator = getLocator(xpath);
		waitForElement(driver,locator);	
		WebElement element = driver.findElement(locator);
		return element.getText();
	}

	//Validate InputText method -- you can customize by using Actions
	public String validateAndClickAction(WebDriver driver, String xpath, String expectedText, boolean isClick) {
		By locator = getLocator(xpath);
		waitForElement(driver,locator);	
		WebElement element = driver.findElement(locator);
		String actualText = element.getText();
		String actualTxt  = "";
		if(expectedText.contains(actualText) || actualText.contains(expectedText)) {
			actualTxt = "Pass: Validated Text is: " + expectedText;
		} else {
			actualTxt = "Fail: Expected: " + expectedText + "<br/>";
			actualTxt = actualTxt +  "Actual: " + actualText + "<br/>";
		}

		if(isClick) element.click();

		return actualTxt;
	}

	//Validate InputText method -- you can customize by using Actions
	public String validateInputButton(WebDriver driver, String xpath, String expectedText, boolean isClick) {
		By locator = getLocator(xpath);
		waitForElement(driver,locator);	
		WebElement element = driver.findElement(locator);
		String actualText = element.getAttribute("value");

		String actualTxt  = "";
		if(expectedText.equalsIgnoreCase(actualText)) {
			actualTxt =  "Pass: Validated Text is: " + expectedText;
		} else {
			actualTxt =  "Fail: Expected: " + expectedText + "<br/>";
			actualTxt = actualTxt + "Actual: " + actualText + "<br/>";
			actualTxt = actualTxt +  "Actual and Expected text are not equal. Expected is: " + expectedText;
		}

		if(isClick) element.click();

		return actualTxt;
	}

	//Validate InputText method -- you can customize by using Actions
	public String isElementDisplayed(WebDriver driver, String xpath, String fieldName, boolean isClick) {
		By locator = getLocator(xpath);
		try {
			waitForElement(driver,locator);	
			WebElement element = driver.findElement(locator);


			if(fieldName.isEmpty()) {
				try {

					fieldName = element.getText();	
					if(fieldName  == null || fieldName.isEmpty()) {
						fieldName = element.getAttribute("value");
					}

					if(fieldName  == null || fieldName.isEmpty()) {
						fieldName = element.getAttribute("title");
					}
				} catch (Exception e) {
					fieldName = element.getAttribute("value");
				}
			}


			if(element.isDisplayed()) {
				String text = "Pass: " + fieldName +  " is displayed";
				if(isClick) {
					element.click();
					text = text + "<br/>" + fieldName + "is Clicked";
				}
				return text;
			} else 
				return "Fail: Need Investigation - " + fieldName + " Expected element is not displayed.  Please check " + xpath;
		} catch (Exception e) {
			return "Fail: Need Investigation - " + fieldName + "Expected element is not displayed.  Please check " + xpath;
		}
	}

	// window
	String parentHandle = "";
	public void windowHandle(WebDriver webDriver) {
		parentHandle = webDriver.getWindowHandle();
		Set<String> handles = webDriver.getWindowHandles();
		for (String windowHandles : handles) {
			System.out.println(windowHandles);
			webDriver.switchTo().window(windowHandles);
		}
	}



	public void switchToParentWindow(WebDriver webDriver) {
		if (parentHandle != null && !parentHandle.isEmpty()) {
			webDriver.switchTo().window(parentHandle);
		}
	}

	public void switchToIframe(WebDriver driver, String xpath) {
		try {

			boolean isElementFound = false;
			for(int j = 0; j < 5; j++) {
				Thread.sleep(2000);
				for (int framePosition = 0; framePosition <= 5; framePosition++) {
					try {
						driver.switchTo().defaultContent();
						driver.switchTo().frame(framePosition);
						try {
							WebElement ele = driver.findElement(getLocator(xpath));
							if (ele.isDisplayed()) {
								waitForElement(driver, getLocator(xpath));
								isElementFound = true;
								System.out.println(framePosition);
								return;
							}
						} catch (Exception e) {

							try {
								List<WebElement> elements1 = driver.findElements(By.tagName("frame"));


								if (elements1 != null && elements1.size() > 0) {
									for (int m = 0; m <= elements1.size(); m++) {
										try {
											driver.switchTo().frame(m);
											WebElement ele = driver.findElement(getLocator(xpath));
											if (ele.isDisplayed()) {
												waitForElement(driver, getLocator(xpath));
												isElementFound = true;
												System.out.println(framePosition);
												return;
											}
										} catch (Exception e1) {
											driver.switchTo().parentFrame();                                    }

									}

									if(isElementFound) {
										break;
									}

								}
							} catch (Exception e2) {
								driver.switchTo().parentFrame();  
							}


							try {
								List<WebElement> elements1 = driver.findElements(By.tagName("iframe"));


								if (elements1 != null && elements1.size() > 0) {
									for (int m = 0; m <= elements1.size(); m++) {
										try {
											driver.switchTo().frame(m);
											WebElement ele = driver.findElement(getLocator(xpath));
											if (ele.isDisplayed()) {
												waitForElement(driver, getLocator(xpath));
												isElementFound = true;
												System.out.println(framePosition);
												break;
											}
										} catch (Exception e1) {
											driver.switchTo().parentFrame();
										}
										//    driver.switchTo().defaultContent();
									}

									if(isElementFound) {
										break;
									}

								}
							} catch (Exception e3) {
								// TODO: handle exception
							}
						}
					} catch (Exception e) {
						driver.switchTo().defaultContent();
					}
				}



				if(isElementFound || j == 4) {
					break;
				}
			}
		} catch (Exception e) {
			driver.switchTo().defaultContent();
		}
		//driver.switchTo().defaultContent();
	}


	public void selectiOption(WebDriver driver, String xpath, String optionValue) {
		By locator = getLocator(xpath);
		waitForElement(driver,locator);	
		driver.findElement(locator).click();
		{
			WebElement dropdown = driver.findElement(locator);
			dropdown.findElement(By.xpath("//option[. = '" + optionValue + "']")).click();
		}
		driver.findElement(locator).click();
	}


	public void alertHandle(WebDriver driver, boolean isAlertAccept) {
		try {
			Alert alert = driver.switchTo().alert();
			Thread.sleep(5000);
			if (isAlertAccept) {
				alert.accept();
			} else {
				alert.dismiss();
			}
		} catch (Exception e) {

		}
	}

	public boolean isElementDisplayed(WebDriver driver, String xpath) {
		try {
			By locator = getLocator(xpath);
			WebElement element = waitForElement(driver,locator);	
			return element.isDisplayed();
		} catch(Exception e) {
			return false;
		}
	}


	private By getLocator(String xpath) {
		if(xpath.startsWith("/")) {
			return By.xpath(xpath);
		} else {
			return By.cssSelector(xpath);
		}
	}

	public void scrollUp(WebDriver driver, String xpath) {

		boolean isDisplayed = false;
		for(int i = 0; i < 10; i++) {
			try {
				JavascriptExecutor js = ((JavascriptExecutor) driver);
				js.executeScript("window.scrollTo(0,-100"); 
				WebElement element = driver.findElement(getLocator(xpath));
				if(element.isDisplayed()) {
					isDisplayed = true;
					break;
				}
			} catch (Exception e) {

			}
		}

		if(!isDisplayed) {
			Actions a = new Actions(driver);
			a.sendKeys(Keys.PAGE_UP).build().perform();
		}
	}

	public void scrollDown(WebDriver driver, String xpath) {

		boolean isDisplayed = false;
		for(int i=0; i<10; i++) {
			try {
				JavascriptExecutor js = ((JavascriptExecutor) driver);
				js.executeScript("window.scrollTo(0,300"); 
				WebElement element = driver.findElement(getLocator(xpath));
				if(element.isDisplayed()) {
					isDisplayed = true;
					break;
				}
			} catch (Exception e) {
				Actions a = new Actions(driver);
				a.sendKeys(Keys.PAGE_DOWN).build().perform();
			}
		}

		if(!isDisplayed) {
			Actions a = new Actions(driver);
			a.sendKeys(Keys.PAGE_DOWN).build().perform();
		}

	}


	public void wait(WebDriver driver, String xpath) {
		try {
			for(int i=0; i<5; i++) {
				try {
					WebElement element1 = driver.findElement(getLocator(xpath));
					if(element1.isDisplayed()) {
						return;
					} else {
						Thread.sleep(5000);
					}
				} catch (Exception e) {
					Thread.sleep(5000);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void enter(WebDriver driver, String xpath) {
		driver.findElement(getLocator(xpath)).sendKeys(Keys.ENTER);
	}

	public void tab(WebDriver driver, String xpath) {
		driver.findElement(getLocator(xpath)).sendKeys(Keys.TAB);
	}



	public WebElement waitForElement(WebDriver driver, WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, 40);
		return wait.until(ExpectedConditions.visibilityOf(element));
	}

	public WebElement waitForElement(WebDriver driver, By locator) {
		WebDriverWait wait = new WebDriverWait(driver, 40);
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public WebElement waitForElementClickable(WebDriver driver, By locator) {
		WebDriverWait wait = new WebDriverWait(driver, 40);
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	public void inputClear(WebElement element) {
		element.clear();
		try {
			while (!element.getAttribute("value").equals("") && element.getAttribute("type").equals("text")) {
				element.sendKeys(Keys.BACK_SPACE);
			}

		} catch (Exception e) {
			System.out.println("Not editable input" + e.getLocalizedMessage());
		}

	}
}
