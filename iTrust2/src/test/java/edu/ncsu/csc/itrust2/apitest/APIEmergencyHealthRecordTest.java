package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Class for testing emergency health record API.
 *
 * @author Jenna Shockley
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIEmergencyHealthRecordTest {
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    private Gson                  gson;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        gson = new GsonBuilder().create();
    }

    /**
     * Tests basic emergency health record API functionality.
     *
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testEmergencyHealthRecordAPI () throws UnsupportedEncodingException, Exception {

        // no user
        mvc.perform( get( "/api/v1/records/name/cooljoe" ) ).andExpect( status().isNotFound() );
        mvc.perform( get( "/api/v1/records/age/cooljoe" ) ).andExpect( status().isNotFound() );
        mvc.perform( get( "/api/v1/records/birth/cooljoe" ) ).andExpect( status().isNotFound() );
        mvc.perform( get( "/api/v1/records/gender/cooljoe" ) ).andExpect( status().isNotFound() );
        mvc.perform( get( "/api/v1/records/blood/cooljoe" ) ).andExpect( status().isNotFound() );
        mvc.perform( get( "/api/v1/records/prescriptions/cooljoe" ) ).andExpect( status().isNotFound() );
        mvc.perform( get( "/api/v1/records/diagnoses/cooljoe" ) ).andExpect( status().isNotFound() );

        // create miscellaneous objects for testing
        final User pat1 = new User( "cooljoe96", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        pat1.save();
        final User hcp1 = new User( "hcp1", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_HCP, 1 );
        hcp1.save();
        final Patient joePatient = new Patient( pat1 );
        joePatient.setFirstName( "Joe" );
        joePatient.setLastName( "Smith" );
        final Calendar dateOfBirth = GregorianCalendar.getInstance();
        dateOfBirth.set( Calendar.YEAR, 1998 );
        dateOfBirth.set( Calendar.MONTH, 5 );
        dateOfBirth.set( Calendar.DATE, 30 );
        joePatient.setDateOfBirth( dateOfBirth );
        joePatient.setGender( Gender.Male );
        joePatient.setBloodType( BloodType.APos );
        joePatient.save();

        // no prescrips/diagnoses
        final String prescriptions1 = mvc.perform( get( "/api/v1/records/prescriptions/cooljoe96" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final List<Prescription> allPrescriptions = gson.fromJson( prescriptions1, new TypeToken<List<Prescription>>() {
        }.getType() );
        assertEquals( allPrescriptions.size(), 0 );

        final String diagnoses1 = mvc.perform( get( "/api/v1/records/diagnoses/cooljoe96" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final List<Prescription> allDiagnoses = gson.fromJson( diagnoses1, new TypeToken<List<Diagnosis>>() {
        }.getType() );
        assertEquals( allDiagnoses.size(), 0 );

        Prescription p1 = new Prescription();
        final Drug drug1 = new Drug();
        drug1.setName( "drug1" );
        drug1.setDescription( "a drug with powerful effects" );
        drug1.save();
        p1.setDrug( drug1 );
        p1.setDosage( 1 );
        p1.setStartDate( Calendar.getInstance() );
        p1.setEndDate( Calendar.getInstance() );
        p1.setRenewals( 0 );
        p1.setPatient( pat1 );
        p1.save();
        p1 = Prescription.getForPatient( "cooljoe96" ).get( 0 );
        Diagnosis d1 = new Diagnosis();
        final OfficeVisit o1 = new OfficeVisit();
        o1.setPatient( pat1 );
        o1.setHcp( hcp1 );
        o1.setDate( Calendar.getInstance() );
        o1.setType( AppointmentType.GENERAL_CHECKUP );
        final Hospital h1 = new Hospital( "hosp1", "32 cool street", "12345", "" );
        h1.save();
        o1.setHospital( h1 );
        final OfficeVisitForm f1 = new OfficeVisitForm();
        f1.setPatient( "cooljoe96" );
        f1.setHcp( "hcp1" );
        f1.setDate( "01-01-2001" );
        f1.setTime( "12:00" );
        f1.setType( "GENERAL_CHECKUP" );
        f1.setHospital( "hosp1" );
        final BasicHealthMetrics b1 = new BasicHealthMetrics( f1 );
        b1.save();
        o1.setBasicHealthMetrics( b1 );
        o1.save();
        final ICDCode c1 = new ICDCode();
        c1.setCode( "C69" );
        c1.setDescription( "description" );
        c1.save();
        d1.setVisit( o1 );
        d1.setCode( c1 );
        d1.save();
        d1 = Diagnosis.getForPatient( pat1 ).get( 0 );

        // get record
        final String name1 = mvc.perform( get( "/api/v1/records/name/cooljoe96" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertEquals( name1, "\"Joe Smith\"" );
        final String age1 = mvc.perform( get( "/api/v1/records/age/cooljoe96" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertEquals( age1, "19" );
        final String birthDate1 = mvc.perform( get( "/api/v1/records/birth/cooljoe96" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertEquals( birthDate1, "\"06-30-1998\"" );
        final String gender1 = mvc.perform( get( "/api/v1/records/gender/cooljoe96" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertEquals( gender1, "\"Male\"" );
        final String bloodType1 = mvc.perform( get( "/api/v1/records/blood/cooljoe96" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertEquals( bloodType1, "\"A+\"" );

        final String prescriptions2 = mvc.perform( get( "/api/v1/records/prescriptions/cooljoe96" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final List<Prescription> allPrescriptions2 = gson.fromJson( prescriptions2,
                new TypeToken<List<Prescription>>() {
                }.getType() );
        assertEquals( allPrescriptions2.size(), 1 );

        final String diagnoses2 = mvc.perform( get( "/api/v1/records/diagnoses/cooljoe96" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final List<Prescription> allDiagnoses2 = gson.fromJson( diagnoses2, new TypeToken<List<Diagnosis>>() {
        }.getType() );
        assertEquals( allDiagnoses2.size(), 1 );

        // get rid of test data
        mvc.perform( delete( "/api/v1/patients" ) );
        mvc.perform( delete( "/api/v1/prescriptions/" + p1.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( p1.getId().toString() ) );
        d1.delete();
        mvc.perform( delete( "/api/v1/officevisits/" + o1.getId() ) );

    }
}
