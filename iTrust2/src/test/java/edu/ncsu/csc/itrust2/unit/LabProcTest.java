package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.LabCodeForm;
import edu.ncsu.csc.itrust2.forms.hcp.LabProcForm;
import edu.ncsu.csc.itrust2.models.persistent.LabCode;
import edu.ncsu.csc.itrust2.models.persistent.LabProc;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * This class tests creating lab procedures and lab procedure forms
 *
 * @author nmtrimbl
 *
 */
public class LabProcTest {

    /** Lab code attached to the procedure */
    private static final String CODE      = "22222-6";

    /** Lab tech assigned to the lab procedure */
    private static final String LABTECH   = "labtech";

    /** Priority of the lab procedure */
    private static final int    PRIORITY  = 1;

    /** Date the lab procedure was created */
    private static final String DATE      = "04/30/2018";

    /** Comments on the procedure */
    private static final String COMMENTS  = "None";

    /** Status of the lab procedure */
    private static final String STATUS    = "Not Started";

    /** Patient attached to the lab procedure */
    private static final String PATIENT   = "patient";

    /** Long name of the lab code */
    private static final String LONG_NAME = "Onchocerca sp IgG2 Ab [Presence] in Serum by Immunoassay";

    /** Component of the lab code */
    private static final String COMPONENT = "Onchocerca sp Ab.IgG2";

    /** Property of the lab code */
    private static final String PROPERTY  = "PrThr";

    /** Method of the lab code */
    private static final String METHOD    = "IA";

    /**
     * Test creating lab procedure
     */
    @Test
    public void testCreatingLabProc () {
        // Create a new lab code to use
        final LabCodeForm lpc = new LabCodeForm();
        lpc.setCode( CODE );
        lpc.setComponent( COMPONENT );
        lpc.setLongName( LONG_NAME );
        lpc.setMethod( METHOD );
        lpc.setProperty( PROPERTY );
        final LabCode lc = new LabCode( lpc );
        lc.save();

        // Create a new lab procedure form
        LabProcForm lpf = new LabProcForm();
        lpf.setCode( CODE );
        lpf.setComments( COMMENTS );
        lpf.setDate( DATE );
        lpf.setPriority( PRIORITY );
        lpf.setLabtech( LABTECH );
        lpf.setStatus( STATUS );
        lpf.setPatient( PATIENT );

        // Make a lab procedure from the form and save it
        final LabProc lp = new LabProc( lpf );
        lp.save();

        // Make sure all fields are there
        assertEquals( LabCode.getByCode( CODE ).getCode(), lp.getCode().getCode() );
        assertEquals( COMMENTS, lp.getComments() );
        assertEquals( PRIORITY, lp.getPriority().getPriorityNumber() );
        assertEquals( STATUS, lp.getStatus().getName() );

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        Date parsedDate = null;
        try {
            parsedDate = sdf.parse( DATE );
        }
        catch ( final ParseException e ) {
            // Ignore, Hibernate will catch the null date
        }
        final Calendar c = Calendar.getInstance();
        c.setTime( parsedDate );

        assertEquals( c, lp.getDate() );
        assertEquals( User.getByName( LABTECH ), lp.getLabtech() );
        assertEquals( User.getByName( PATIENT ), lp.getPatient() );

        // Clear lab procedure form and make a new one using the newly created
        // lab procedure
        lpf = null;
        lpf = new LabProcForm( lp );
        assertEquals( CODE, lpf.getCode() );
        assertEquals( COMMENTS, lpf.getComments() );
        assertEquals( DATE, lpf.getDate() );
        assertEquals( PRIORITY, lpf.getPriority() );
        assertEquals( LABTECH, lpf.getLabtech() );
        assertEquals( STATUS, lpf.getStatus() );
        assertEquals( PATIENT, lpf.getPatient() );

        // Try getting lab procedures from the database using get methods
        assertEquals( lp.getPatient(), LabProc.getForPatient( PATIENT ).get( 0 ).getPatient() );
        assertEquals( lp.getLabtech(), LabProc.getByLabtech( User.getByName( LABTECH ) ).getLabtech() );
        assertEquals( lp.getLabtech(), LabProc.getAllForLabtech( User.getByName( LABTECH ) ).get( 0 ).getLabtech() );
        final long id = lp.getId();
        assertEquals( lp.getId(), LabProc.getById( id ).getId() );
        assertTrue( LabProc.getAll().size() > 0 );

        assertNull( LabProc.getById( (long) -1 ) );
        assertNull( LabProc.getByLabtech( User.getByName( "patient" ) ) );
    }

}
