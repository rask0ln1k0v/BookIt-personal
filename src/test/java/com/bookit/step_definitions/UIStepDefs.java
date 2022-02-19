package com.bookit.step_definitions;

import com.bookit.pages.HomePage;
import com.bookit.pages.HuntPage;
import com.bookit.pages.LoginPage;
import com.bookit.pages.SpotsPage;
import com.bookit.utilities.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import cucumber.runtime.Env;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.withArgs;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.*;

public class UIStepDefs {

    LoginPage loginPage = new LoginPage();
    HomePage homePage = new HomePage();
    HuntPage huntPage = new HuntPage();
    SpotsPage spotsPage = new SpotsPage();

    static List<String> availableRooms;



    @Given("User logged in to Bookit app as teacher role")
    public void user_logged_in_to_Bookit_app_as_teacher_role() {
        Driver.getDriver().get(Environment.URL);
        loginPage.logIn(Environment.TEACHER_EMAIL,Environment.TEACHER_PASSWORD);
    }

    @Given("User is on self page")
    public void user_is_on_self_page() {
        homePage.gotoSelf();
    }


    @Given("User logged in Bookit app as team lead role")
    public void userLoggedInBookitAppAsTeamLeadRole() {
        Driver.getDriver().get(Environment.URL);
        loginPage.logIn(Environment.LEADER_EMAIL,Environment.LEADER_PASSWORD);
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(),10);
        wait.until(ExpectedConditions.urlContains("map"));
        assertTrue(Driver.getDriver().getCurrentUrl().endsWith("map"));
    }

    @When("User goes to room hunt page")
    public void userGoesToRoomHuntPage() {
        homePage.hunt.click();
    }

    @And("User searches for room with date:")
    public void userSearchesForRoomWithDate(Map<String,String> dateInfo)  {
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(),15);
        wait.until(ExpectedConditions.visibilityOf(huntPage.from));
        huntPage.dateField.sendKeys(dateInfo.get("date"));
        huntPage.selectStartTime(dateInfo.get("from"));
        huntPage.selectFinishTime(dateInfo.get("to"));
        huntPage.submitBtn.click();

    }

    @Then("User should see available rooms:")
    public void userShouldSeeAvailableRooms() {
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(),10);
        wait.until(ExpectedConditions.visibilityOfAllElements(spotsPage.roomNames));
        BrowserUtils.waitFor(4);
        availableRooms= BrowserUtils.getElementsText(spotsPage.roomNames);
       assertEquals(7,availableRooms.size());
        System.out.println(availableRooms);

    }






}
