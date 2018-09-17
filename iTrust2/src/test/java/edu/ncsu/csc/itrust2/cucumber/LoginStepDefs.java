package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * System tests for logging in
 *
 * @author Noah Trimble (nmtrimbl)
 *
 */
public class LoginStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";
    WebDriverWait           wait    = new WebDriverWait( driver, 2 );

    /**
     * Check for sample labtech
     */
    @Given ( "A sample labtech exists in the system" )
    public void labtechExists () {
        // labtech user will already exist because of HibernateDataGenerator
    }

    /**
     * Login as sample labtech
     */
    @When ( "I login as the sample labtech" )
    public void loginAsLabtech () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "labtech" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Make sure log entries are visible
     */
    @Then ( "A list of log entries appears" )
    public void checkLogs () {
        // Wait until page loads
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "logout" ) ) );

        // Check that log entries are there
        assertTrue( driver.getPageSource().contains( "Log History" ) );
        assertTrue( driver.getPageSource().contains( "Primary User" ) );
    }

    /**
     * Make sure Edit menu is visible
     */
    @Then ( "There is an edit drop down menu" )
    public void checkEdit () {
        assertTrue( driver.getPageSource().contains( "Edit" ) );
    }

    /**
     * Make sure View Source tab is visible
     */
    @Then ( "There is a View Source tab" )
    public void checkViewSource () {
        assertTrue( driver.findElement( By.linkText( "View Source" ) ).isDisplayed() );
    }

    /**
     * Make sure About tab is visible
     */
    @Then ( "There is an About tab" )
    public void checkAbout () {
        assertTrue( driver.findElement( By.linkText( "About" ) ).isDisplayed() );
    }

    /**
     * Make sure Change Password option visible
     */
    @Then ( "There is a Change Password option" )
    public void checkChangePassword () {
        assertTrue( driver.getPageSource().contains( "Change Password" ) );
    }

    /**
     * Make sure log out is visible
     */
    @Then ( "There is a Log Out button" )
    public void checkLogOut () {
        assertTrue( driver.getPageSource().contains( "Log out" ) );
    }

    /**
     * Make sure username is visible
     */
    @Then ( "My username, labtech, is displayed" )
    public void checkUsername () {
        assertTrue( driver.getPageSource().contains( "labtech" ) );
    }
}
