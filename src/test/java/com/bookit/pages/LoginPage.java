package com.bookit.pages;

import com.bookit.utilities.Driver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BasePage{

    @FindBy(name = "email")
    public WebElement emailField;

    @FindBy(name = "password")
    public WebElement passwordField;
    @FindBy(xpath = "//button[.='sign in']")
    public WebElement signInBtn;



    public void logIn(String email,String password){
        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        signInBtn.click();
    }

}
