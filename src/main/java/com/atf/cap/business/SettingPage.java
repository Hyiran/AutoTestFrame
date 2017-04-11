package com.atf.cap.business;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.SelendroidFindBy;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.atf.cap.appium.WaitUtils;
import com.atf.cap.page.CapPage;

public class SettingPage extends CapPage<SettingPage>{

	// IP/PORT配置
	@SelendroidFindBy(id = "show_serveripset_set")
	@AndroidFindBy(id = "ctrip.android.view:id/show_serveripset_set")
	private WebElement showServeripset;
	
	// FAT环境
	@SelendroidFindBy(id = "show_develop_Button")
	@AndroidFindBy(id = "ctrip.android.view:id/show_develop_Button")
	private WebElement fatBtn;
	
	// UAT环境
	@SelendroidFindBy(id = "show_test_Button")
	@AndroidFindBy(id = "ctrip.android.view:id/show_test_Button")
	private WebElement uatBtn;
	
	// 生产环境
	@SelendroidFindBy(id = "show_product_Button")
	@AndroidFindBy(id = "ctrip.android.view:id/show_product_Button")
	private WebElement productBtn;
	
	// 堡垒
	@SelendroidFindBy(id = "show_fortress_button")
	@AndroidFindBy(id = "ctrip.android.view:id/show_fortress_button")
	private WebElement baoleiBtn;
	
	// 个性化
	@SelendroidFindBy(id = "show_recproduct_Button")
	@AndroidFindBy(id = "ctrip.android.view:id/show_recproduct_Button")
	private WebElement recproductBtn;
	
	// 提交设置
	@SelendroidFindBy(id = "save_serverIPAndPort")
	@AndroidFindBy(id = "ctrip.android.view:id/save_serverIPAndPort")
	private WebElement saveServerIPAndPort;
	
	// Set Server IP
	@SelendroidFindBy(id = "show_testIP_content")
	@AndroidFindBy(id = "ctrip.android.view:id/show_testIP_content")
	private WebElement serverIP;
	
	// Set Server port
	@SelendroidFindBy(id = "show_testport_content")
	@AndroidFindBy(id = "ctrip.android.view:id/show_testport_content")
	private WebElement serverPort;
	
	// Set payment Server IP
	@SelendroidFindBy(id = "show_payment_testIP_content")
	@AndroidFindBy(id = "ctrip.android.view:id/show_payment_testIP_content")
	private WebElement paymentServerIP;
	
	// Set payment Server port
	@SelendroidFindBy(id = "show_payment_testport_content")
	@AndroidFindBy(id = "ctrip.android.view:id/show_payment_testport_content")
	private WebElement paymentServerPort;
	
	// Set payment Server IP
	@SelendroidFindBy(id = "show_baffle_testIP_content")
	@AndroidFindBy(id = "ctrip.android.view:id/show_baffle_testIP_content")
	private WebElement baffleServerIP;

	// Set payment Server port
	@SelendroidFindBy(id = "show_baffle_testport_content")
	@AndroidFindBy(id = "ctrip.android.view:id/show_baffle_testport_content")
	private WebElement baffleServerPort;
	
	public SettingPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @Summary: Click IP/PORT配置 按钮
	 */
	public void clickShowServeripset(){
		showServeripset.click();
	}
	
	
	/**
	 * @Summary: Click FAT 按钮
	 */
	public void clickFATBtn(){
		fatBtn.click();
	}
	
	/**
	 * @Summary: 设置 FAT 环境 , 不能更改Server IP， 端口等值
	 */
	public void setFATEnv(){
		clickFATBtn();
		saveServerIPAndPort.click();
		WaitUtils.untilPageElementVisible(showServeripset, 5);;
	}
	
	/**
	 * @Summary: Click  Product 按钮 
	 */
	public void clickProductBtn(){
		productBtn.click();
	}
	
	/**
	 * @Summary: 设置 Product 环境 , 不能更改Server IP， 端口等值
	 */
	public void setProductEnv(){
		clickProductBtn();
		saveServerIPAndPort.click();
		WaitUtils.untilPageElementVisible(showServeripset, 5);;
	}
	
	/**
	 * @Summary: Click  UAT 按钮 
	 */
	public void clickUATBtn(){
		uatBtn.click();
	}
	
	/**
	 * @Summary: 设置 UAT 环境 , 不能更改Server IP， 端口等值
	 */
	public void setUATEnv(){
		clickUATBtn();
		saveServerIPAndPort.click();
		WaitUtils.untilPageElementVisible(showServeripset, 5);;
	}
	
	/**
	 * @Summary: Click  堡垒 按钮 
	 */
	public void clickbaoleiBtn(){
		baoleiBtn.click();
	}
	
	/**
	 * @Summary: 设置 堡垒 环境 , 不能更改Server IP， 端口等值
	 */
	public void setbaoleiEnv(){
		clickbaoleiBtn();
		saveServerIPAndPort.click();
		WaitUtils.untilPageElementVisible(showServeripset, 5);;
	}
	
	/**
	 * @Summary: Click 个性化 按钮 
	 */
	public void clickrecproductBtn(){
		recproductBtn.click();
	}
	
	/**
	 * @Summary: 设置个性化, 不能更改Server IP， 端口等值
	 */
	public void setRecproductEnv(){
		clickbaoleiBtn();
		saveServerIPAndPort.click();
		WaitUtils.untilPageElementVisible(showServeripset, 5);;
	}
	
	/**
	 * @Summary: Click 提交设置 按钮 
	 */
	public void clickSaveServerIPAndPortSetting(){
		saveServerIPAndPort.click();
	}
	
	/**
	 * 
	 * @param settingInfo: Server IP, Server 端口 等信息
	 * @Summary: 更改Server IP, Server 端口 等信息
	 */
	public void inputIPAndPortAddress(SettingInfo settingInfo){
		if(settingInfo.getServerIP() != null){
			serverIP.clear();
			serverIP.sendKeys(settingInfo.getServerIP());
		}
		
		if(settingInfo.getServerPort() != null){
			serverPort.clear();
			serverPort.sendKeys(settingInfo.getServerPort());
		}
		
		if(settingInfo.getPaymentServerIP() != null){
			paymentServerIP.clear();
			paymentServerIP.sendKeys(settingInfo.getPaymentServerIP());
		}
		
		if(settingInfo.getPaymentServerPort() != null){
			paymentServerPort.clear();
			paymentServerPort.sendKeys(settingInfo.getPaymentServerPort());
		}
		
		if(settingInfo.getBaffleServerIP() != null){
			baffleServerIP.clear();
			baffleServerIP.sendKeys(settingInfo.getBaffleServerIP());
		}
		
		if(settingInfo.getBaffleServerPort() != null){
			baffleServerPort.clear();
			baffleServerPort.sendKeys(settingInfo.getBaffleServerPort());
		}
		
		clickSaveServerIPAndPortSetting();
	}
	
	/**
	 * @Summary Go to Setting page form my ctrip
	 * 我的携程->更多-> 设置->设置IP地址 ->IP/PORT配置->我的携程环境设置
	 */
	public void goToSettingPageFromMyCtrip(){
		BusinessUtils businessUtils = new BusinessUtils(driver);
		businessUtils.goToMyCtripIPPORTSettingFromMyCtrip();
		clickShowServeripset();
	}
}
