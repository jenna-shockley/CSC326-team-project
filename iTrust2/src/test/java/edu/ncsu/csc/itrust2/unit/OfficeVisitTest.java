package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.LabCodeForm;
import edu.ncsu.csc.itrust2.forms.hcp.LabProcForm;
import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.LabCode;
import edu.ncsu.csc.itrust2.models.persistent.LabProc;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Tests working with office visits
 *
 * @author Noah Trimble (nmtrimbl)
 *
 */
public class OfficeVisitTest {

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
     * Tests office visits
     *
     * @throws ParseException
     * @throws NumberFormatException
     */
    @Test
    public void testOfficeVisit () throws NumberFormatException, ParseException {

        final Hospital hosp = new Hospital( "Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC" );
        hosp.save();

        final OfficeVisit visit = new OfficeVisit();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( User.getByName( "hcp" ) );
        bhm.setPatient( User.getByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setLdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );
        bhm.setPatientSmokingStatus( PatientSmokingStatus.NEVER );
        bhm.setWeight( 75f );
        bhm.setTri( 300 );
        bhm.setSystolic( 50 );
        bhm.setHeadCircumference( 10f );

        bhm.save();

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_CHECKUP );
        visit.setHospital( hosp );
        visit.setPatient( User.getByName( "AliceThirteen" ) );
        visit.setHcp( User.getByName( "hcp" ) );
        visit.setDate( Calendar.getInstance() );
        visit.save();

        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();

        final ICDCode code = new ICDCode();
        code.setCode( "A21" );
        code.setDescription( "Top Quality" );

        code.save();

        final Diagnosis diagnosis = new Diagnosis();

        diagnosis.setCode( code );
        diagnosis.setNote( "This is bad" );
        diagnosis.setVisit( visit );

        diagnoses.add( diagnosis );

        visit.setDiagnoses( diagnoses );

        visit.save();

        // Drug
        final Drug drug = new Drug();

        drug.setCode( "1234-4321-89" );
        drug.setDescription( "Lithium Compounds" );
        drug.setName( "Li2O8" );
        drug.save();

        // Prescription
        final Prescription pres = new Prescription();
        pres.setDosage( 3 );
        pres.setDrug( drug );

        final Calendar end = Calendar.getInstance();
        end.add( Calendar.DAY_OF_WEEK, 10 );
        pres.setEndDate( end );
        pres.setPatient( User.getByName( "AliceThirteen" ) );
        pres.setStartDate( Calendar.getInstance() );
        pres.setRenewals( 5 );

        pres.save();

        visit.setPrescriptions( Collections.singletonList( pres ) );

        visit.save();

        visit.setPrescriptions( Collections.emptyList() );

        visit.save();

        // Lab Procedure

        // Create a new lab code to use
        final LabCodeForm lpc = new LabCodeForm();
        lpc.setCode( CODE );
        lpc.setComponent( COMPONENT );
        lpc.setLongName( LONG_NAME );
        lpc.setMethod( METHOD );
        lpc.setProperty( PROPERTY );
        final LabCode lc = new LabCode( lpc );
        lc.save();

        final LabProcForm lpf = new LabProcForm();
        lpf.setCode( CODE );
        lpf.setComments( COMMENTS );
        lpf.setDate( DATE );
        lpf.setPriority( PRIORITY );
        lpf.setLabtech( LABTECH );
        lpf.setStatus( STATUS );
        lpf.setPatient( PATIENT );
        final LabProc lp = new LabProc( lpf );
        lp.save();

        visit.setLabProcs( Collections.singletonList( lp ) );
        visit.save();
        assertEquals( lp, visit.getLabProcs().get( 0 ) );

        // Try creating a form
        final OfficeVisitForm ovf = new OfficeVisitForm( visit );
        assertEquals( lpf.getLabtech(), ovf.getLabProcs().get( 0 ).getLabtech() );
        ovf.setPreScheduled( null );
        ovf.setId( null );

        // Create another visit with same information
        final OfficeVisit visitEq = new OfficeVisit( ovf );
        visitEq.save();
        assertEquals( visit.getHcp(), visitEq.getHcp() );

        visit.setLabProcs( Collections.emptyList() );
        visit.save();

        visit.delete();
    }

}
