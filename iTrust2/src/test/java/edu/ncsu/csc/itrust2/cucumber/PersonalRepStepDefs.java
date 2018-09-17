package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * Step definitions for AddPersonalRep feature
 */
public class PersonalRepStepDefs {

    private WebDriver    driver;
    private final String baseUrl        = "http://localhost:8080/iTrust2";
    private final String patientViewRep = baseUrl + "/patient/viewPersonalReps";
    private final String hcpViewRep     = baseUrl + "/hcp/viewPersonalReps";

    // Token for testing
    WebDriverWait        wait;

    /**
     * Sets up the driver
     */
    @Before
    public void setup () {

        driver = new HtmlUnitDriver( true );
        wait = new WebDriverWait( driver, 5 );

        HibernateDataGenerator.generateUsers();
    }

    /**
     * Closes the driver
     */
    @After
    public void tearDown () {
        driver.close();
    }

    /**
     * Patient login
     */
    @Given ( "the user is a patient" )
    public void loginPatientH () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "AliceThirteen" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Patient login
     */
    @Given ( "the user is a PR" )
    public void loginPatientPR () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "BobTheFourYearOld" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * HCP login
     *
     * @throws ParseException
     */
    @Given ( "the user is an HCP" )
    public void loginHCP () throws ParseException {

        // login as hcp
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "hcp" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Add personnel representative to the patient
     */
    @When ( "I add a patient as their PR" )
    public void addPersonalRep () {
        // add first pr
        driver.get( patientViewRep );

        final By selector1 = By.cssSelector( "input[name='" + "patient" + "'][value='" + "BobTheFourYearOld" + "']" );
        wait.until( ExpectedConditions.visibilityOfElementLocated( selector1 ) );
        final WebElement element = driver.findElement( selector1 );
        element.click();
        driver.findElement( By.name( "submit" ) ).click();

        // add second pr
        driver.get( patientViewRep );
        final By selector2 = By.cssSelector( "input[name='" + "patient" + "'][value='" + "TimTheOneYearOld" + "']" );
        wait.until( ExpectedConditions.visibilityOfElementLocated( selector2 ) );
        final WebElement element2 = driver.findElement( selector2 );
        element2.click();
        driver.findElement( By.name( "submit" ) ).click();
    }

    /**
     * Remove pr
     */
    @When ( "the user goes to remove a PR and selects one of their current PRs" )
    public void removePersonalRep () {
        driver.get( patientViewRep );
        driver.findElement( By.name( "removeRep" ) ).click();
    }

    /**
     * Remove self as pr
     */
    @When ( "the user goes to remove themself as a PR and selects someone they represent" )
    public void removeSelfAsPersonalRep () {
        driver.get( patientViewRep );
        driver.findElement( By.name( "removeSelf" ) ).click();
    }

    /**
     * Add personnel representative by hcp
     */
    @When ( "the HCP selects a patient, goes to add a PR for that patient, and enters the name of another patient" )
    public void hcpAddsPR () {
        driver.get( hcpViewRep );
        // select patient
        final By selector = By.cssSelector( "input[name='" + "patient" + "'][value='" + "AliceThirteen" + "']" );
        wait.until( ExpectedConditions.visibilityOfElementLocated( selector ) );
        final WebElement element = driver.findElement( selector );
        element.click();

        // add pr
        final By selector2 = By.cssSelector( "input[name='" + "patient" + "'][value='" + "TimTheOneYearOld" + "']" );
        wait.until( ExpectedConditions.visibilityOfElementLocated( selector2 ) );
        final WebElement element2 = driver.findElement( selector2 );
        element2.click();
        driver.findElement( By.name( "submit" ) ).click();

    }

    /**
     * Create personal rep successfully
     */
    @Then ( "the specified patient is added to the user's list of PRs" )
    public void addedPRSuccessfully () {
        assertTrue( driver.getPageSource().contains( "TimTheOneYearOld Smith" ) );
        assertTrue( driver.getPageSource().contains( "BobTheFourYearOld Smith" ) );

    }

    /**
     * Remove pr successfully
     */
    @Then ( "that patient is removed from the user's list of PRs" )
    public void prRemovedSuccessfully () {

        assertTrue( !driver.getPageSource().contains( "TimTheOneYearOld Smith" ) );
    }

    /**
     * Remove self as pr successfully
     */
    @Then ( "that person is removed from the user's list of patients they represent" )
    public void prSelfRemovedSuccessfully () {

        assertTrue( !driver.getPageSource().contains( "AliceThirteen Smith" ) );
    }

    /**
     * Create pr by hcp successful
     */
    @Then ( "the second patient is added to the list of PRs for the first patient" )
    public void hcpAddedPRSuccessfully () {
        assertTrue( driver.getPageSource().contains( "TimTheOneYearOld Smith" ) );
    }

}
