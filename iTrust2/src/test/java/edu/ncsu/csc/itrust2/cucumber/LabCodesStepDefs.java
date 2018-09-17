package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.LabCode;

/**
 * LabCode step definitions for automated testing
 *
 * @author Cody Nesbitt
 *
 */
public class LabCodesStepDefs {
    private static final String BASE_URL  = "http://localhost:8080/iTrust2";
    private static final String LC_URL    = BASE_URL + "/admin/labCodes.html";

    private final WebDriver     driver    = new HtmlUnitDriver( true );

    WebDriverWait               wait      = new WebDriverWait( driver, 120 );

    private boolean             submitted = true;

    /**
     * Sets up for certain tests.
     */
    @Before ( "@addcode" )
    public void setup () {
        DomainObject.deleteAll( LabCode.class );
    }

    /**
     * Sets up for certain tests.
     */
    @Before ( "@editcode, @deletecode" )
    public void setup2 () {

    }

    /**
     * Enter value of a field
     *
     * @param name
     *            Name of the field
     * @param value
     *            Value of the field
     */
    private void enterValue ( final String name, final String value ) {
        final WebElement field = driver.findElement( By.name( name ) );
        field.clear();
        field.sendKeys( String.valueOf( value ) );
    }

    /**
     * Selects an item by name and clicks it
     *
     * @param name
     *            Name of the item
     */
    private void selectName ( final String name ) {
        final By selector = By.name( name );
        wait.until( ExpectedConditions.visibilityOfElementLocated( selector ) );
        final WebElement element = driver.findElement( selector );
        element.click();
    }

    /**
     * Returns the text in a field once it's visible.
     *
     * @param field
     *            name of field to check
     * @return text in field
     */
    private String checkField ( final String field ) {
        final By selector = By.name( field );
        wait.until( ExpectedConditions.visibilityOfElementLocated( selector ) );
        return driver.findElement( selector ).getText();
    }

    /**
     * Logs in with a given username.
     *
     * @param username
     *            Username to login with
     */
    @Given ( "I log in with username: (.+)" )
    public void start ( final String username ) {
        driver.get( BASE_URL );

        enterValue( "username", username );
        enterValue( "password", "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * Add a lab code.
     *
     * @param code
     *            code for lab code
     * @param longName
     *            longName for lab code
     * @param component
     *            component for lab code
     * @param property
     *            property for lab code
     * @param method
     *            method for lab code
     */
    @When ( "I create a new lab code with code: (.+), longName: (.+), component: (.+), property: (.+), and method: (.+)" )
    public void addLabCode ( final String code, final String longName, final String component, final String property,
            final String method ) throws InterruptedException {
        driver.get( LC_URL );
        enterValue( "name", longName );
        enterValue( "code", code );
        enterValue( "component", component );
        enterValue( "property", property );
        enterValue( "method", method );
        selectName( "submitLabCode" );
        Thread.sleep( 10000 );
        if ( checkField( "responseMessage" ).equals( "" ) ) {
            final LabCode lc = new LabCode();
            lc.setLongName( longName );
            lc.setCode( code );
            lc.setComponent( component );
            lc.setProperty( property );
            lc.setMethod( method );
            lc.save();
            submitted = false;
        }
    }

    /**
     * Make sure the lab code was added correctly.
     */
    @Then ( "a new lab code will be created" )
    public void labCodeAdded () throws InterruptedException {
        Thread.sleep( 10000 );
        if ( submitted ) {
            assertEquals( "Successfully added lab code", checkField( "responseMessage" ) );
        }
        else {
            assertEquals( "", checkField( "responseMessage" ) );
        }
    }

    /**
     * Edit a lab code.
     *
     * @param code
     *            code for lab code
     * @param longName
     *            longName for lab code
     * @param component
     *            component for lab code
     * @param property
     *            property for lab code
     * @param method
     *            method for lab code
     */
    @When ( "I edit a lab code and re-fill the form with code: (.+), longName: (.+), component: (.+), property: (.+), and method: (.+)" )
    public void editLabCode ( final String code, final String longName, final String component, final String property,
            final String method ) throws InterruptedException {
        driver.get( LC_URL );
        Thread.sleep( 10000 );
        selectName( "editLabCode" );
        enterValue( "editNameCell", longName );
        enterValue( "editCodeCell", code );
        enterValue( "editComponentCell", component );
        enterValue( "editPropertyCell", property );
        enterValue( "editMethodCell", method );
        selectName( "saveLabCode" );
        Thread.sleep( 10000 );
    }

    /**
     * Make sure the lab code was edited correctly.
     */
    @Then ( "the lab code is updated" )
    public void labCodeEdited () throws InterruptedException {
        Thread.sleep( 10000 );
        assertEquals( "Successfully edited lab code", checkField( "responseMessage" ) );
    }

    /**
     * Delete a lab code.
     *
     * @param code
     *            code for lab code
     * @param longName
     *            longName for lab code
     * @param component
     *            component for lab code
     * @param property
     *            property for lab code
     * @param method
     *            method for lab code
     */
    @When ( "I delete a lab code with code: (.+), longName: (.+), component: (.+), property: (.+), and method: (.+)" )
    public void deleteLabCode ( final String code, final String longName, final String component, final String property,
            final String method ) throws InterruptedException {
        driver.get( LC_URL );
        Thread.sleep( 10000 );
        selectName( "deleteLabCode" );
        Thread.sleep( 10000 );
    }

    /**
     * Make sure the lab code was deleted correctly.
     */
    @Then ( "the lab code is deleted from the database" )
    public void labCodeDeleted () throws InterruptedException {
        Thread.sleep( 10000 );
        assertEquals( "Successfully removed lab code", checkField( "responseMessage" ) );
    }

}
