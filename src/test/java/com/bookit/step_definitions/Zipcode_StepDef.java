package com.bookit.step_definitions;

import com.bookit.pages.USPSZipcodePage;
import com.bookit.utilities.Driver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static io.restassured.RestAssured.*;

public class Zipcode_StepDef {

    USPSZipcodePage zipcodePage = new USPSZipcodePage();
    Response response;
    @Given("User sends GET request to {string} with {string} zipcode")
    public void user_sends_GET_request_to_with(String endpoint, String zipCode) {
        response = given().accept(ContentType.JSON)
                .and().pathParam("postal-code", zipCode)
                .when().get(endpoint);


    }

    @Then("city name should be {string} in response")
    public void city_name_should_be_in_response(String cityName) {
        assertThat(response.statusCode(),is(HttpStatus.SC_OK));
        String responseCityName = response.path("places[0].'place name'");
        assertThat(responseCityName,equalTo(cityName));

    }

    @Then("User searches for {string} on USPS website")
    public void user_searches_for_on_USPS_website(String zipCode) {
        Driver.getDriver().get("https://tools.usps.com/zip-code-lookup.htm?citybyzipcode");
        zipcodePage.searchZipcode(zipCode);

    }

    @Then("city name should be {string} in result")
    public void city_name_should_be_in_result(String expectedCityName) {
        System.out.println(zipcodePage.cityName.getText());
        assertThat(zipcodePage.cityName.getText(),equalToIgnoringCase(expectedCityName));

    }


}
