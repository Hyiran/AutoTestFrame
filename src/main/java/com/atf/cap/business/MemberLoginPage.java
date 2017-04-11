package com.atf.cap.business;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.SelendroidFindBy;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.atf.cap.CapLogger;
import com.atf.cap.appium.WaitUtils;
import com.atf.cap.page.CapPage;

public class MemberLoginPage extends CapPage<MemberLoginPage>{
	
	// 用户名，密码
	@AndroidFindBy(className = "android.widget.EditText")
	private List<WebElement> userNamePassword;

	// 登录按钮
	@SelendroidFindBy(id = "button_login_update")
	@FindBy(id = "ctrip.android.view:id/button_login_update")
	private WebElement login;
	
	// 我的携程
	@SelendroidFindBy(id = "myctrip")
	@FindBy(id = "ctrip.android.view:id/myctrip")
	private WebElement myCtrip;
	
	// 我的携程中登录/注册
	@SelendroidFindBy(id = "myctrip_login_btn")
	@FindBy(id = "ctrip.android.view:id/myctrip_login_btn")
	private WebElement myCtripLoginBtn;
	
	@SelendroidFindBy(id = "content_text")
	@FindBy(id = "ctrip.android.view:id/content_text")
	private List<WebElement> failedLoginDialog;
	
	@SelendroidFindBy(id = "button_login_update")
	@FindBy(id = "ctrip.android.view:id/button_login_update")
	private List<WebElement> loginbuttons;
	
	public MemberLoginPage(WebDriver driver) {
		super(driver);
	}
	
	public WebElement getUserNameElement() {
		return userNamePassword.get(0);
	}

	public WebElement getPasswordElement() {		
		return userNamePassword.get(1);
	}

	public WebElement getLoginButton() {
		return this.login;
	}

	
	/**
	 * 
	 * @param user
	 * @param password
	 * @return true: login success, false: login failed please try again
	 * @Summary this method UserName element, password element and Login button are hard code
	 */
	public boolean loginFromMyCtrip(String user, String password){
		BusinessUtils businessUtils = new BusinessUtils(driver);
		businessUtils.goTologinDialogFromMyCtrip();
		boolean isloginSuccess = login(user, password);
		driver.navigate().back();
		return isloginSuccess;
	}

	/**
	 * 
	 * @param user
	 * @param password
	 * @return true: login success, false: login failed please try again
	 */
	public boolean login(String user, String password){
		boolean isLoginSuccess = true;
		
		// Switch to Native app
		String current = driver.getContext();		
		if(current.toLowerCase().contains("webview")){
			driver.context("Native_app");
		}
		
		// input user name
		WebElement userName = getUserNameElement();
		userName.clear();
		userName.sendKeys(user);

		// input password 
		WebElement passwordElement = getPasswordElement();
		passwordElement.clear();
		passwordElement.sendKeys(password);

		// click login button
		WebElement loginElement = getLoginButton();
		loginElement.click();
		
		// login success, switch back the preview context
		isLoginSuccess = isLoginSuccess();
		if(isLoginSuccess){
			driver.context(current);
		}

		return isLoginSuccess;
	}
	
	public boolean isLoginSuccess(){
		boolean isLoginSuccess = true;
		
		// login success or not
		List<WebElement> errorDialog = this.failedLoginDialog;
		for(WebElement e:errorDialog){
			String text = e.getAttribute("text");
			if(text.contains("输入有误") || text.contains("未连接") || text.contains("互联网")){
				isLoginSuccess = false;
				break;
			}
		}
		
		List<WebElement> loginButton = this.loginbuttons;
		if(loginButton.size() > 0){
			isLoginSuccess = false;
		}
		
		return isLoginSuccess;
	}
	
	@Override
	protected void isLoaded() {

		WaitUtils.untilPageElementExist(userNamePassword.get(0), 10);

		CapLogger.step("******Loaded MyCtrip Page!******");

	}


}
