package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;

import java.util.List;

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
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.LabCode;
import edu.ncsu.csc.itrust2.models.persistent.LabProc;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Lab Procedure step definitions for automated testing
 *
 * @author Noah Trimble (nmtrimbl)
 *
 */
public class LabProcsStepDefs {

    private static final String BASE_URL  = "http://localhost:8080/iTrust2/";
    private static final String VISIT_URL = BASE_URL + "hcp/documentOfficeVisit.html";
    private static final String EDIT_URL  = BASE_URL + "hcp/editOfficeVisit.html";
    private static final String VIEW_URL  = BASE_URL + "patient/viewOfficeVisits.html";

    private final WebDriver     driver    = new HtmlUnitDriver( true );
    private final String        baseUrl   = "http://localhost:8080/iTrust2";

    WebDriverWait               wait      = new WebDriverWait( driver, 5 );

    /**
     * Create a hospital and lab code in the system
     */
    @Before ( "@addproc, @viewproc, @deleteproc" )
    public void setup () {
        final Hospital hosp = new Hospital( "General Hospital", "123 Main St", "12345", "NC" );
        hosp.save();

        final LabCode lc = new LabCode();
        lc.setCode( "22222-6" );
        lc.setComponent( "comp" );
        lc.setLongName( "name" );
        lc.setMethod( "method" );
        lc.setProperty( "prop" );
        lc.save();
    }

    /**
     * Gets the username of a user when given first and last name
     *
     * @param first
     *            First name of user
     * @param last
     *            Last name of user
     * @return username
     */
    private String getUserName ( final String first, final String last ) {
        return first.substring( 0, 1 ).toLowerCase() + last.toLowerCase();
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
     * Selects an item and clicks it
     *
     * @param name
     *            Name of the item
     * @param value
     *            Value of the item
     */
    private void selectItem ( final String name, final String value ) {
        final By selector = By.cssSelector( "input[name='" + name + "'][value='" + value + "']" );
        wait.until( ExpectedConditions.visibilityOfElementLocated( selector ) );
        final WebElement element = driver.findElement( selector );
        element.click();
    }

    /**
     * Selects an item by name and clicks it
     *
     * @param name
     *            Name of the item
     */
    private void selectName ( final String name ) {
        final WebElement element = driver.findElement( By.cssSelector( "input[name='" + name + "']" ) );
        element.click();
    }

    /**
     * Selects an item by value and clicks it
     *
     * @param value
     *            value of the item
     */
    private void selectValue ( final String value ) {
        final WebElement element = driver.findElement( By.cssSelector( "[value='" + value + "']" ) );
        element.click();
    }

    /**
     * Logs in with a given username
     *
     * @param username
     *            Username to login with
     */
    @Given ( "I have logged in with this username: (.+)" )
    public void login ( final String username ) {
        driver.get( baseUrl );

        enterValue( "username", username );
        enterValue( "password", "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * Begins documenting an office visit
     *
     * @param firstName
     *            First name of the patient
     * @param lastName
     *            Last name of the patient
     * @param dob
     *            Date of birth of the patient
     */
    @When ( "I start documenting an office visit for patient with name: (.+) (.+) and date of birth: (.+)" )
    public void startOfficeVisit ( final String firstName, final String lastName, final String dob ) {
        driver.get( VISIT_URL );
        final String patient = getUserName( firstName, lastName );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.cssSelector( "[value='" + patient + "']" ) ) );
        selectItem( "name", patient );
    }

    /**
     * Fill in basic office visit information
     *
     * @param date
     *            Date of the office visit
     * @param hospital
     *            Hospital where the office visit is located
     * @param notes
     *            Notes associated with the office visit
     * @param weight
     *            Weight of the patient
     * @param height
     *            height of the patient
     * @param bloodPressure
     *            Blood pressure of the patient
     * @param hss
     *            Hss of the patient
     * @param pss
     *            Pss of the patient
     * @param hdl
     *            hdl for the patient
     * @param ldl
     *            Ldl for the patient
     * @param triglycerides
     *            Triglycerides of the patient
     */
    @When ( "fill in office visit with date: (.+), hospital: (.+), notes: (.*), weight: (.+), height: (.+), blood pressure: (.+), household smoking status: (.+), patient smoking status: (.+), hdl: (.+), ldl: (.+), and triglycerides: (.+)" )
    public void fillOfficeVisitForm ( final String date, final String hospital, final String notes, final String weight,
            final String height, final String bloodPressure, final String hss, final String pss, final String hdl,
            final String ldl, final String triglycerides ) {
        enterValue( "date", date );
        enterValue( "time", "10:10 AM" );
        selectItem( "hospital", hospital );
        enterValue( "notes", notes );
        enterValue( "weight", weight );
        enterValue( "height", height );
        enterValue( "systolic", bloodPressure.split( "/" )[0] );
        enterValue( "diastolic", bloodPressure.split( "/" )[1] );
        selectItem( "houseSmokingStatus", hss );
        selectItem( "patientSmokingStatus", pss );
        enterValue( "hdl", hdl );
        enterValue( "ldl", ldl );
        enterValue( "tri", triglycerides );
    }

    /**
     * Add a lab procedure
     *
     * @param labcode
     *            Lab code for the procedure
     * @param labtech
     *            Labtech working on the procedure
     * @param priority
     *            Priority of the procedure
     * @param comments
     *            Comments on the procedure
     */
    @When ( "add a lab procedure with a lab code of (.+), lab tech of (.+), priority of (.+) with (.+) comments" )
    public void addLabProc ( final String labcode, final String labtech, final String priority,
            final String comments ) {
        enterValue( "commentsEntry", comments );
        selectName( labcode );

        // Extract labtech string to select
        int size = 0;
        try {
            size = LabProc.getAllForLabtech( User.getByName( labtech ) ).size();
        }
        catch ( final NullPointerException e ) {
            size = 0;
        }
        final StringBuilder line = new StringBuilder();
        line.append( labtech );
        line.append( " currently has " );
        line.append( size );
        line.append( " lab procedures." );
        selectName( line.toString() );

        selectValue( priority );

        driver.findElement( By.name( "fillLabProcs" ) ).click();
        assertEquals( "", driver.findElement( By.name( "errorMsg" ) ).getText() );
    }

    /**
     * Submits the office visit
     */
    @When ( "submit office visit" )
    public void submitOfficeVisit () {
        driver.findElement( By.name( "submit" ) ).click();
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "success" ), " " ) );
    }

    /**
     * Checks that the office visit was submitted successfully
     */
    @Then ( "A message indicates visit was submitted successfully" )
    public void officeVisitSuccessful () {
        final WebElement msg = driver.findElement( By.name( "success" ) );
        assertEquals( "Office visit created successfully", msg.getText() );
    }

    /**
     * Views office visit
     *
     * @throws InterruptedException
     */
    @When ( "I choose to view my office visit" )
    public void viewLabProcs () throws InterruptedException {
        driver.get( VIEW_URL );
        final List<OfficeVisit> visits = OfficeVisit.getForHCPAndPatient( "svang", "jbean" );

        // Get visit id to select
        long id = 0;
        for ( int i = 0; i < visits.size(); i++ ) {
            if ( !visits.get( i ).getLabProcs().isEmpty() ) {
                id = visits.get( i ).getId();
                break;
            }
        }

        // Wait an additional 2 seconds for safety
        Thread.sleep( 2000 );
        selectName( Long.toString( id ) );
    }

    /**
     * View a lab procedure
     *
     * @param labcode
     *            Lab code for the procedure
     * @param date
     *            Date of the procedure
     * @param priority
     *            Priority of the procedure
     * @param comments
     *            Comments on the procedure
     * @throws InterruptedException
     */
    @Then ( "I see a lab procedure with a lab code of (.+), date of (.+), priority of (.+) with (.+) comments" )
    public void labprocVisible ( final String labcode, final String date, final String priority, final String comments )
            throws InterruptedException {
        Thread.sleep( 2000 );
        assertEquals( labcode, driver.findElement( By.name( "code" ) ).getText() );
        assertEquals( date, driver.findElement( By.name( "date" ) ).getText() );
        assertEquals( priority, driver.findElement( By.name( "priority" ) ).getText() );
        assertEquals( comments, driver.findElement( By.name( "comments" ) ).getText() );
        assertEquals( "NotStarted", driver.findElement( By.name( "status" ) ).getText() );
    }

    /**
     * Navigates to edit office visit
     *
     * @throws InterruptedException
     */
    @When ( "I choose to edit an office visit with a lab procedure attached to it" )
    public void viewLabProcsEditWindow () throws InterruptedException {
        driver.get( EDIT_URL );
        final List<OfficeVisit> visits = OfficeVisit.getForHCPAndPatient( "svang", "jbean" );

        // Get visit id to select
        long id = 0;
        for ( int i = 0; i < visits.size(); i++ ) {
            if ( !visits.get( i ).getLabProcs().isEmpty() ) {
                id = visits.get( i ).getId();
                break;
            }
        }

        // Wait an additional 2 seconds for safety
        Thread.sleep( 2000 );
        selectName( Long.toString( id ) );
    }

    /**
     * Remove a lab procedure
     *
     * @param labcode
     *            Lab code for the procedure
     * @param date
     *            Date of the procedure
     * @param priority
     *            Priority of the procedure
     * @param comments
     *            Comments on the procedure
     * @throws InterruptedException
     */
    @Then ( "I remove a lab procedure with a lab code of (.+), date of (.+), priority of (.+) with (.+) comments" )
    public void removeLabProc ( final String labcode, final String date, final String priority, final String comments )
            throws InterruptedException {
        driver.findElement( By.name( "removeLabProc" ) ).click();
    }

    /**
     * Checks that the office visit was edited successfully
     */
    @Then ( "A message indicates visit was edited successfully" )
    public void officeVisitEditSuccessful () {
        final WebElement msg = driver.findElement( By.name( "success" ) );
        assertEquals( "Office visit edited successfully", msg.getText() );
    }
}
