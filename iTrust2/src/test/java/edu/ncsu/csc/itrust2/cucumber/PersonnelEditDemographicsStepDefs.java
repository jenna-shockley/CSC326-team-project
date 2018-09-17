package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * System tests pertaining to editing personnel demographics.
 *
 * @author Noah Trimble (nmtrimbl)
 *
 */
public class PersonnelEditDemographicsStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";

    /**
     * Check for sample admin
     */
    @Given ( "An admin exists in the system" )
    public void personnelExists () {
        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users
    }

    /**
     * Check for sample responder
     */
    @Given ( "A sample ER exists in the system" )
    public void responderExists () {
        // responder user will already exist because of HibernateDataGenerator
    }

    /**
     * Login as sample admin
     */
    @When ( "I log in as a personnel admin" )
    public void loginAsAdmin () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "admin" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Login as sample responder
     */
    @When ( "I log in as the sample ER" )
    public void loginAsER () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "responder" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Go to the Edit Demographics page
     */
    @When ( "I navigate to the Personnel Edit My Demographics page" )
    public void editDemographicsP () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics').click();" );
    }

    /**
     * Fill in new demographics
     */
    @When ( "I fill in new, updated personnel demographics" )
    public void fillDemographicsP () {
        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        firstName.clear();
        firstName.sendKeys( "Gregor" );

        final WebElement lastName = driver.findElement( By.id( "lastName" ) );
        lastName.clear();
        lastName.sendKeys( "Gysi" );

        final WebElement email = driver.findElement( By.id( "email" ) );
        email.clear();
        email.sendKeys( "gysi@bundestag.de" );

        final WebElement address1 = driver.findElement( By.id( "address1" ) );
        address1.clear();
        address1.sendKeys( "Platz der Republik 1" );

        final WebElement city = driver.findElement( By.id( "city" ) );
        city.clear();
        city.sendKeys( "Berlin" );

        final WebElement state = driver.findElement( By.id( "state" ) );
        final Select dropdown = new Select( state );
        dropdown.selectByVisibleText( "CT" );

        final WebElement zip = driver.findElement( By.id( "zip" ) );
        zip.clear();
        zip.sendKeys( "11011" );

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        phone.clear();
        phone.sendKeys( "123-456-7890" );

        final WebElement specialty = driver.findElement( By.id( "specialty" ) );
        specialty.clear();
        specialty.sendKeys( "Politiker" );

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

    }

    /**
     * The demographics were successfully updated.
     */
    @Then ( "The personnel demographics are updated" )
    public void updatedSuccessfullyP () {
        assertTrue( driver.getPageSource().contains( "Your demographics were updated successfully" ) );
    }

    /**
     * The new demographics are able to be viewed
     */
    @Then ( "The personnel can view new demographics" )
    public void viewDemographicsP () {
        driver.get( baseUrl );
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics').click();" );
        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        assertEquals( firstName.getAttribute( "value" ), "Gregor" );

        final WebElement address = driver.findElement( By.id( "address1" ) );
        assertEquals( address.getAttribute( "value" ), "Platz der Republik 1" );
    }

    /**
     * The new demographics are able to be viewed
     */
    @Then ( "The admin can view new demographics" )
    public void viewDemographicsAdmin () {
        driver.get( baseUrl );
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics').click();" );
        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        assertEquals( firstName.getAttribute( "value" ), "Gregor" );

        final WebElement address = driver.findElement( By.id( "address1" ) );
        assertEquals( address.getAttribute( "value" ), "Platz der Republik 1" );
    }
}
