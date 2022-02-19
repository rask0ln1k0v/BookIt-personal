package com.bookit.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class USPSZipcodePage extends BasePage{

    @FindBy (id = "tZip")
    private WebElement zipcodeField;

    @FindBy(id = "cities-by-zip-code")
    private WebElement submitBtn;

    @FindBy(xpath = "//p[.='RECOMMENDED CITY NAME']/following-sibling::p")
    public WebElement cityName;

    public void  searchZipcode(String zipcode){
        zipcodeField.sendKeys(zipcode);
        submitBtn.click();
    }

}
