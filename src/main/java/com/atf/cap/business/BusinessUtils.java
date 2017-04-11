package com.atf.cap.business;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.SelendroidFindBy;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.atf.cap.appium.WaitUtils;
import com.atf.cap.page.CapPage;

public class BusinessUtils  extends CapPage<MemberLoginPage>{

	public BusinessUtils(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	// 设置IP地址
	@SelendroidFindBy(id = "set_ip_btn")
	@AndroidFindBy(id = "ctrip.android.view:id/set_ip_btn")
	private WebElement setIPBtn;

	// IP/PORT配置
	@SelendroidFindBy(id = "show_serveripset_set")
	@AndroidFindBy(id = "ctrip.android.view:id/show_serveripset_set")
	private WebElement showServeripset;

	// 我的携程
	@SelendroidFindBy(id = "myctrip")
	@FindBy(id = "ctrip.android.view:id/myctrip")
	private WebElement myCtrip;
	
	// 我的携程中登录/注册
	@SelendroidFindBy(id = "myctrip_login_btn")
	@FindBy(id = "ctrip.android.view:id/myctrip_login_btn")
	private WebElement myCtripLoginBtn;

	// 我的携程->更多
	@SelendroidFindBy(id = "infobar_more")
	@FindBy(id = "ctrip.android.view:id/infobar_more")
	private WebElement myCtripMore;

	// 我的携程->更多->设置
	@SelendroidFindBy(id = "infobar_setting")
	@FindBy(id = "ctrip.android.view:id/infobar_setting")
	private WebElement myCtripMoreSetting;
		
	@SelendroidFindBy(id = "nonmember_msginfobar")
	@FindBy(id = "ctrip.android.view:id/nonmember_msginfobar")
	private WebElement order;	
	
	/**
	 * @Summary: Click 设置IP地址 按钮
	 */
	public void clickSetIPBtn(){
		setIPBtn.click();
	}

	/**
	 * @Summary: 我的携程->更多-> 设置->设置IP地址 ->IP/PORT配置
	 */
	public void goToMyCtripIPPORTSettingFromMyCtrip(){
		myCtrip.click();
		
		Point location = order.getLocation();
		driver.swipe(location.getX(), location.getY(), location.getX(), 100, 100);
		WaitUtils.sleepTightInSeconds(1);
		if(!WaitUtils.isPageElementExist(myCtripMoreSetting)){
			myCtripMore.click();
		}
		
		WaitUtils.untilPageElementExist(myCtripMoreSetting);
		myCtripMoreSetting.click();
		
		WaitUtils.untilPageElementExist(setIPBtn);
		setIPBtn.click();
	}
	
	/**
	 * @Summary: 我的携程->登录/注册->会员登录
	 */
	public void goTologinDialogFromMyCtrip(){
		myCtrip.click();
		WaitUtils.untilPageElementVisible(myCtripLoginBtn, 10);
		myCtripLoginBtn.click();
	}

}
