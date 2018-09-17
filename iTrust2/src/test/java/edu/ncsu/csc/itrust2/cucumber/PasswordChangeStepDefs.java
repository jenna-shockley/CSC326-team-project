package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.PasswordResetToken;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * System tests for changing passwords
 *
 * @author Noah Trimble (nmtrimbl)
 *
 */
public class PasswordChangeStepDefs {

    static {
        java.util.logging.Logger.getLogger( "com.gargoylesoftware" ).setLevel( Level.OFF );
    }

    private WebDriver          driver;
    private final String       baseUrl = "http://localhost:8080/iTrust2";

    // Token for testing
    private PasswordResetToken token   = null;
    WebDriverWait              wait;

    @Before
    public void setup () {

        driver = new HtmlUnitDriver( true );
        wait = new WebDriverWait( driver, 5 );

        HibernateDataGenerator.generateUsers();
    }

    @After
    public void tearDown () {
        driver.close();
    }

    private void setTextField ( final By byval, final Object value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    /**
     * Make sure testTech is in the system
     */
    @Given ( "testTech exists in the system" )
    public void startingUser () {
        final User testTech = new User( "testTech", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_LABTECH, 1 );
        testTech.save();
    }

    @Given ( "I can log in to iTrust as (.+) with password (.+)" )
    public void login ( final String username, final String password ) {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), username );
        setTextField( By.name( "password" ), password );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        wait.until( ExpectedConditions.not( ExpectedConditions.titleIs( "iTrust2 :: Login" ) ) );
    }

    /**
     * Login as testTech
     */
    @When ( "I login as testTech" )
    public void loginLabtech () {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "testTech" );
        setTextField( By.name( "password" ), "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        wait.until( ExpectedConditions.not( ExpectedConditions.titleIs( "iTrust2 :: Login" ) ) );
    }

    /**
     * Click on Change Password
     */
    @When ( "I navigate to the change password page" )
    public void navigateChange () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('changePassword').click();" );
    }

    /**
     * Fills out Change Password form
     *
     * @param password
     *            Current password
     * @param newPassword
     *            New password
     */
    @When ( "I fill out the form with current password (.+) and new password (.+)" )
    public void fillChangeForm ( final String password, final String newPassword ) {
        // Wait until page loads
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "currentPW" ) ) );

        setTextField( By.name( "currentPW" ), password );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @When ( "I fill out the form with current password (.+), new password (.+), and re-entry (.+)" )
    public void fillChangeForm ( final String currentPassword, final String newPassword, final String newPassword2 ) {
        // Wait until page loads
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "currentPW" ) ) );

        setTextField( By.name( "currentPW" ), currentPassword );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword2 );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    /**
     * Make sure password was updated
     */
    @Then ( "My password is updated sucessfully" )
    public void verifyUpdate () {
        try {
            Thread.sleep( 5000 );
            wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "message" ),
                    "Password changed successfully" ) );
        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "message" ) ).getText() + "\n" + token.getId() + "\n"
                    + token.getTempPasswordPlaintext() );
        }
        // driver.findElement( By.id( "logout" ) ).click();
    }

    /**
     * Make sure you can login with new password
     */
    @Then ( "I can login with new password" )
    public void loginAfterChange () {
        driver.findElement( By.id( "logout" ) ).click();

        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "testTech" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123457" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        /**
         * Not an assert statement in the typical sense, but we know that we can
         * log in if we can find the "iTrust" button in the top-left after
         * attempting to do so.
         */
        try {
            driver.findElement( By.linkText( "iTrust2" ) );
        }
        catch ( final Exception e ) {
            Assert.assertNull( e );
        }
    }

    @Then ( "My password is not updated because (.*)" )
    public void verifyNoUpdate ( final String message ) {
        try {
            wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "message" ), message ) );
        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "message" ) ).getText() + "\n"
                    + ( null == token ? "" : ( token.getId() + "\n" + token.getTempPasswordPlaintext() ) ) );
        }
    }

    @Given ( "The user (.+) exists with email (.+)" )
    public void userExistsWithEmail ( final String username, final String email ) throws InterruptedException {

        final User user = User.getByName( username );
        switch ( user.getRole() ) {
            case ROLE_PATIENT:
                Patient dbPatient = Patient.getByName( username );
                if ( null == dbPatient ) {
                    dbPatient = new Patient();
                }
                dbPatient.setSelf( user );
                dbPatient.setFirstName( "Test" );
                dbPatient.setLastName( "User" );
                dbPatient.setEmail( email );
                dbPatient.setAddress1( "1234 Street Dr." );
                dbPatient.setCity( "city" );
                dbPatient.setZip( "12345" );
                dbPatient.setPhone( "123-456-7890" );
                dbPatient.save();
                break;

            default:
                Personnel dbPersonnel = Personnel.getByName( username );
                if ( null == dbPersonnel ) {
                    dbPersonnel = new Personnel();
                }
                dbPersonnel.setSelf( user );
                dbPersonnel.setFirstName( "Test" );
                dbPersonnel.setLastName( "User" );
                dbPersonnel.setEmail( email );
                dbPersonnel.setAddress1( "1234 Street Dr." );
                dbPersonnel.setCity( "city" );
                dbPersonnel.setZip( "12345" );
                dbPersonnel.setPhone( "123-456-7890" );
                dbPersonnel.setEnabled( 1 );
                dbPersonnel.save();
                break;
        }

    }

    @When ( "I navigate to the Forgot Password page" )
    public void navigateForgot () {
        driver.get( baseUrl );
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('passwordResetRequest').click();" );
    }

    @When ( "I enter the temporary password wrong, new password (.+) and reentry (.+)" )
    public void wrongTemp ( final String newPassword, final String newPassword2 ) {
        // Wait until page loads
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "tempPW" ) ) );

        setTextField( By.name( "tempPW" ), "this is wrong" );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword2 );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @When ( "I fill out the request for with the username (.+)" )
    public void fillResetRequest ( final String username ) throws InterruptedException {
        Thread.sleep( 100 );
        final WebElement un = driver.findElement( By.name( "username" ) );
        un.clear();
        un.sendKeys( username );
        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @When ( "I receive an email with a link and temporary password" )
    public void getEmail () throws InterruptedException {
        // wait for the email to be sent
        try {
            new WebDriverWait( driver, 30 ).until( ExpectedConditions.textToBePresentInElementLocated(
                    By.name( "message" ), "Password reset request successfully sent" ) );
        }
        catch ( final Exception e ) {
            fail( e.getMessage() + "\n" + driver.findElement( By.name( "message" ) ).getText() );
        }

        // wait for the email to be delivered
        Thread.sleep( 5 * 1000 );
        token = getTokenFromEmail();
        if ( token == null ) {
            fail( "Failed to receive email" );
        }
    }

    @When ( "I follow the link to the password reset page" )
    public void followLink () {
        // NOTE: can host be localhost always?
        // Token should not be null at this point
        final String link = "http://localhost:8080/iTrust2/resetPassword?tkid=" + token.getId();
        driver.get( link );
    }

    @When ( "I enter the temporary password and new password (.+)" )
    public void fillResetForm ( final String newPassword ) {
        // Wait until page loads
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "tempPW" ) ) );

        setTextField( By.name( "tempPW" ), token.getTempPasswordPlaintext() );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @Given ( "The user (.+) does not exist in the system" )
    public void noUser ( final String username ) {
        final User user = User.getByName( username );
        if ( null != user ) {
            try {
                user.delete();
            }
            catch ( final Exception e ) {
                ;
            }
        }
    }

    @Then ( "I see an error message on the password page" )
    public void resetError () {
        try {
            wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "message" ),
                    "Password reset request could not be sent" ) );
        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "message" ) ).getText() );
        }
    }

    @When ( "I enter the temporary password, new password (.+) and reentry (.+)" )
    public void fillResetForm ( final String newPassword, final String newPassword2 ) {
        // Wait until page loads
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "tempPW" ) ) );

        setTextField( By.name( "tempPW" ), token.getTempPasswordPlaintext() );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword2 );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    /*
     * Credit for checking email:
     * https://www.tutorialspoint.com/javamail_api/javamail_api_checking_emails.
     * htm
     */
    private PasswordResetToken getTokenFromEmail () {
        final String username = "csc326.201.1@gmail.com";
        final String password = "iTrust2Admin123456";
        final String host = "pop.gmail.com";
        PasswordResetToken token = null;
        try {
            // create properties field
            final Properties properties = new Properties();
            properties.put( "mail.store.protocol", "pop3" );
            properties.put( "mail.pop3.host", host );
            properties.put( "mail.pop3.port", "995" );
            properties.put( "mail.pop3.starttls.enable", "true" );
            final Session emailSession = Session.getDefaultInstance( properties );
            // emailSession.setDebug(true);

            // create the POP3 store object and connect with the pop server
            final Store store = emailSession.getStore( "pop3s" );

            store.connect( host, username, password );

            // create the folder object and open it
            final Folder emailFolder = store.getFolder( "INBOX" );
            emailFolder.open( Folder.READ_ONLY );

            // retrieve the messages from the folder in an array and print it
            final Message[] messages = emailFolder.getMessages();
            Arrays.sort( messages, ( x, y ) -> {
                try {
                    return y.getSentDate().compareTo( x.getSentDate() );
                }
                catch ( final MessagingException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return 0;
            } );
            for ( final Message message : messages ) {
                // SUBJECT
                if ( message.getSubject() != null && message.getSubject().contains( "iTrust2 Password Reset" ) ) {
                    String content = (String) message.getContent();
                    content = content.replaceAll( "\r", "" ); // Windows
                    content = content.substring( content.indexOf( "?tkid=" ) );

                    final Scanner scan = new Scanner( content.substring( 6, content.indexOf( '\n' ) ) );
                    System.err.println( "token(" + content.substring( 6, content.indexOf( '\n' ) ) + ")end" );
                    final long tokenId = scan.nextLong();
                    scan.close();

                    content = content.substring( content.indexOf( "temporary password: " ) );
                    content = content.substring( 20, content.indexOf( "\n" ) );
                    content.trim();

                    if ( content.endsWith( "\n" ) ) {
                        content = content.substring( content.length() - 1 );
                    }

                    token = new PasswordResetToken();
                    token.setId( tokenId );
                    token.setTempPasswordPlaintext( content );
                    break;
                }
            }

            // close the store and folder objects
            emailFolder.close( false );
            store.close();
            return token;
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
}
