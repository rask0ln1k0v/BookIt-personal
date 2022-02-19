package com.bookit.step_definitions;

import com.bookit.pages.HomePage;
import com.bookit.pages.SelfPage;
import com.bookit.pages.SpotsPage;
import com.bookit.utilities.*;
import com.sun.javafx.collections.MappingChange;
import cucumber.runtime.Env;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiStepDefs {
    String accessToken;
    String accesTokenTeamLead;
    Response response;
    Map<String,String> newRecordMap;
    List<String> roomsAPI;


    @Given("User logged in Bookit api as teacher role")
    public void user_logged_in_Bookit_api_as_teacher_role() {

      accessToken =  BookItApiUtil.getAccessToken(Environment.TEACHER_EMAIL,Environment.TEACHER_PASSWORD);
        System.out.println("Teacher email:" + Environment.TEACHER_EMAIL);
        System.out.println("Teacher Password:" + Environment.TEACHER_PASSWORD);
        System.out.println(accessToken);

    }

    @And("User sends GET request to {string}")
    public void userSendsGETRequestTo(String path) {

     response =  given().accept(ContentType.JSON)
                .and().header("Authorization",accessToken)
                .when().get(Environment.BASE_URL+path);
        System.out.println("API Endpoint = " + Environment.BASE_URL + path);
        response.prettyPrint();


    }

    @Then("status code should be {int}")
    public void statusCodeShouldBe(int expStatusCode) {
        Assert.assertEquals(expStatusCode,response.statusCode());

    }


    @And("content type is {string}")
    public void contentTypeIs(String expectedContentType) {
    assertThat(expectedContentType,is(response.contentType()));
    }


    @And("role is {string}")
    public void roleIs(String expectedRole) {
        assertThat(response.path("role"),is(expectedRole));

    }

    @Then("User should see same info on UI and API")
    public void user_should_see_same_info_on_UI_and_API() {

        //read values into a map from api
        Map<String,Object> apiUserMap = response.body().as(Map.class);

        //read values from UI using POM
        SelfPage selfPage = new SelfPage();
        String apiFullName = apiUserMap.get("firstName") + " " + apiUserMap.get("lastName");
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(),10);
        wait.until(ExpectedConditions.visibilityOf(selfPage.fullName));
        String uiFullName = selfPage.fullName.getText();
        String uiRole = selfPage.role.getText();
        assertThat(uiFullName,is(apiFullName));
        assertThat(uiRole, is(apiUserMap.get("role")));

    }

    @When("Users sends POST request to {string} with following info:")
    public void users_sends_POST_request_to_with_following_info(String path, Map<String,String> newEntryInfo) {
        newRecordMap = newEntryInfo;
     response =   given().accept(ContentType.JSON)
                .and().contentType(ContentType.JSON)
                .and().header("Authorization",accessToken)
                .and().queryParams(newEntryInfo)
                .when().post(Environment.BASE_URL+path);
        System.out.println(response.statusCode());
        System.out.println(response.asString());


    }

    @Then("User deletes previously created student")
    public void user_deletes_previously_created_student() {
        int studentID = response.path("entryiId");
            given().accept(ContentType.JSON)
                    .and().header("Authorization",accessToken)
                .when().delete(Environment.BASE_URL+"/api/students/" +studentID )
                .then().assertThat().statusCode(is(204));

    }


    @And("User sends GET request to {string} with {string}")
    public void userSendsGETRequestToWith(String endpoint, String teamID) {
        response = given().accept(ContentType.JSON)
                .and().header("Authorization",accessToken)
                .and().pathParam("id",teamID).log().all()
                .when().get(Environment.BASE_URL + endpoint);
    }

    @And("Team name should be {string} in response")
    public void teamNameShouldBeInResponse(String teamName) {
        Assert.assertEquals(teamName,response.path("name"));

    }

    @And("Database query should have same {string} and {string}")
    public void databaseQueryShouldHaveSameAnd(String teamID, String teamName) {
        String sql = "select name,id from team where id ="+teamID;
        Map<String,Object> dbTeamInfo = DBUtils.getRowMap(sql);

     Assert.assertEquals(dbTeamInfo.get("name"),teamName);
     Assert.assertEquals(""+dbTeamInfo.get("id"),teamID);
    }

    @And("Database should persist same team info")
    public void databaseShouldPersistSameTeamInfo() {
       int newTeamID = response.path("entryiId");
   String sql = "select * from team where id = "+newTeamID;

   Map<String,Object> dbNewTeamMap = DBUtils.getRowMap(sql);
   Assert.assertEquals(""+dbNewTeamMap.get("id"),""+newTeamID);
   assertThat(dbNewTeamMap.get("name"),equalTo(newRecordMap.get("team-name")));
   assertThat(dbNewTeamMap.get("batch_number").toString(),equalTo(newRecordMap.get("batch-number")));
   assertThat(dbNewTeamMap.get("id"), equalTo((long)newTeamID));


    }
        @And("User deletes previously created team")
    public void userDeletesPreviouslyCreatedTeam() {
            int teamId = response.path("entryiId");
            given().accept(ContentType.JSON).log().all()
                    .and().header("Authorization", accessToken)
                    .when().delete(Environment.BASE_URL+"/api/teams/"+teamId)
                    .then().assertThat().statusCode(200);

    }


    @Given("User logged in Bookit api as team lead role")
    public void userLoggedInBookitApiAsTeamLeadRole() {
        accessToken= BookItApiUtil.getAccessToken(Environment.LEADER_EMAIL,Environment.LEADER_PASSWORD);

    }

    @When("User sends GET request to {string} with following informations:")
    public void userSendsGETRequestToWithFollowingInformations(String endpoint,Map<String,String> roomInformations) {
        response= given().accept(ContentType.JSON)
                .and().auth().oauth2(accessToken)
                .and().queryParams(roomInformations)
                .and().get(Environment.BASE_URL+endpoint);
        assertThat(response.statusCode(),is(200));
    }

    @Then("Json response names must match the UI result room names.")
    public void jsonResponseNamesMustMatchTheUIResultRoomNames() {
        SpotsPage spotsPage = new SpotsPage();
        roomsAPI = response.jsonPath().getList("name");
        System.out.println("api rooms " + roomsAPI);
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(),20);
        wait.until(ExpectedConditions.visibilityOfAllElements(spotsPage.roomNames));
        UIStepDefs.availableRooms = BrowserUtils.getElementsText(spotsPage.roomNames);
        System.out.println("UI rooms " + UIStepDefs.availableRooms);
        assertThat(UIStepDefs.availableRooms,equalTo(roomsAPI));

    }


    @And("available rooms in database should match UI and API results")
    public void availableRoomsInDatabaseShouldMatchUIAndAPIResults() {
    String sql ="select room.name from room\n" +
            "inner join cluster c on room.cluster_id = c.id\n" +
            "where c.name='light-side';";
        List<Object> dbAvailableRooms = DBUtils.getColumnData(sql, "name");
        BrowserUtils.waitFor(4);
        assertThat(dbAvailableRooms,equalTo(UIStepDefs.availableRooms));
        assertThat(dbAvailableRooms,equalTo(roomsAPI));

    }
}
