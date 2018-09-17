package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import edu.ncsu.csc.itrust2.forms.admin.LabCodeForm;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.hcp.LabProcForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.LabCode;
import edu.ncsu.csc.itrust2.models.persistent.LabProc;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Class for testing lab procedure API.
 *
 * @author Noah Trimble (nmtrimbl)
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APILabProcTest {
    /** Lab code attached to the procedure */
    private static final String   CODE      = "22222-6";

    /** Lab tech assigned to the lab procedure */
    private static final String   LABTECH   = "labtech";

    /** Priority of the lab procedure */
    private static final int      PRIORITY  = 1;

    /** Date the lab procedure was created */
    private static final String   DATE      = "04/30/2018";

    /** Comments on the procedure */
    private static final String   COMMENTS  = "None";

    /** Status of the lab procedure */
    private static final String   STATUS    = "Not Started";

    /** Patient attached to the lab procedure */
    private static final String   PATIENT   = "patient";

    /** Long name of the lab code */
    private static final String   LONG_NAME = "Onchocerca sp IgG2 Ab [Presence] in Serum by Immunoassay";

    /** Component of the lab code */
    private static final String   COMPONENT = "Onchocerca sp Ab.IgG2";

    /** Property of the lab code */
    private static final String   PROPERTY  = "PrThr";

    /** Method of the lab code */
    private static final String   METHOD    = "IA";

    private MockMvc               mvc;

    private Gson                  gson;
    // LabCodeForm labCodeForm;

    @Autowired
    private WebApplicationContext context;

    /**
     * Performs setup operations for the tests.
     *
     * @throws Exception
     */
    @Before
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void setup () throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        gson = new GsonBuilder().create();
        final UserForm patientForm = new UserForm( "api_test_patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patientForm ) ) );

        // Create a new lab code to use
        final LabCodeForm lpc = new LabCodeForm();
        lpc.setCode( CODE );
        lpc.setComponent( COMPONENT );
        lpc.setLongName( LONG_NAME );
        lpc.setMethod( METHOD );
        lpc.setProperty( PROPERTY );
        final LabCode lc = new LabCode( lpc );
        lc.save();
    }

    /**
     * Tests basic prescription APIs as an HCP.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "USER", "HCP", "ADMIN" } )
    public void testLabProcAPIHCP () throws Exception {

        // Create two lab procedure forms for testing
        final LabProcForm lpf1 = new LabProcForm();
        lpf1.setCode( CODE );
        lpf1.setComments( COMMENTS );
        lpf1.setDate( DATE );
        lpf1.setPriority( PRIORITY );
        lpf1.setLabtech( "datBoi" );
        lpf1.setStatus( STATUS );
        lpf1.setPatient( PATIENT );

        final LabProcForm lpf2 = new LabProcForm();
        lpf2.setCode( CODE );
        lpf2.setComments( COMMENTS );
        lpf2.setDate( DATE );
        lpf2.setPriority( PRIORITY );
        lpf2.setLabtech( LABTECH );
        lpf2.setStatus( STATUS );
        lpf2.setPatient( "api_test_patient" );

        // Attempt to add invalid lab procedure
        mvc.perform( post( "/api/v1/labProcs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( lpf1 ) ) ).andExpect( status().isBadRequest() );

        // Fix problem
        lpf1.setLabtech( LABTECH );

        // Add first lab procedure to system
        final String content1 = mvc
                .perform( post( "/api/v1/labProcs" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( lpf1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as LabProc
        final LabProc lp1 = gson.fromJson( content1, LabProc.class );
        final LabProcForm lp1Form = new LabProcForm( lp1 );
        // assertEquals( CODE, lp1Form.getCode() );
        assertEquals( COMMENTS, lp1Form.getComments() );
        assertEquals( DATE, lp1Form.getDate() );
        assertEquals( PRIORITY, lp1Form.getPriority() );
        assertEquals( LABTECH, lp1Form.getLabtech() );
        assertEquals( STATUS, lp1Form.getStatus() );
        assertEquals( PATIENT, lp1Form.getPatient() );

        // Add second lab procedure to system
        final String content2 = mvc
                .perform( post( "/api/v1/labProcs" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( lpf2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final LabProc lp2 = gson.fromJson( content2, LabProc.class );
        final LabProcForm lp2Form = new LabProcForm( lp2 );
        // assertEquals( CODE, lp2Form.getCode() );
        assertEquals( COMMENTS, lp2Form.getComments() );
        assertEquals( DATE, lp2Form.getDate() );
        assertEquals( PRIORITY, lp2Form.getPriority() );
        assertEquals( LABTECH, lp2Form.getLabtech() );
        assertEquals( STATUS, lp2Form.getStatus() );
        assertEquals( lpf2.getPatient(), lp2Form.getPatient() );

        // Verify lab procedures have been added
        final String allLabProcContent = mvc.perform( get( "/api/v1/labProcs" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final List<LabProc> allLabProcs = gson.fromJson( allLabProcContent, new TypeToken<List<LabProc>>() {
        }.getType() );
        assertTrue( allLabProcs.size() >= 2 );

        // Get an invalid lab procedure
        mvc.perform( get( "/api/v1/labProcs/-1" ) ).andExpect( status().isNotFound() );

        // Get a valid lab procedure
        final String content3 = mvc.perform( get( "/api/v1/labProcs/" + lp1.getId() ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final LabProc fetched = gson.fromJson( content3, LabProc.class );
        assertEquals( lp1.getId(), fetched.getId() );

        // Delete invalid lab procedure
        mvc.perform( delete( "/api/v1/labProcs/-1" ) ).andExpect( status().isNotFound() );

        // Delete test objects
        mvc.perform( delete( "/api/v1/labProcs/" + lp1.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( lp1.getId().toString() ) );
        mvc.perform( delete( "/api/v1/labProcs/" + lp2.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( lp2.getId().toString() ) );
    }

    /**
     * Tests basic prescription APIs as a lab tech.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "labtech", roles = { "USER", "LABTECH" } )
    public void testLabProcAPILabtech () throws Exception {
        // Create lab procedure form for testing
        final LabProcForm lpf1 = new LabProcForm();
        lpf1.setCode( CODE );
        lpf1.setComments( COMMENTS );
        lpf1.setDate( DATE );
        lpf1.setPriority( PRIORITY );
        lpf1.setLabtech( LABTECH );
        lpf1.setStatus( STATUS );
        lpf1.setPatient( PATIENT );
        final LabProc lp1 = new LabProc( lpf1 );

        // Try to edit lab proc before it is in the system
        mvc.perform( put( "/api/v1/labProcs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new LabProcForm( lp1 ) ) ) ).andExpect( status().isNotFound() );

        lp1.save();

        // Edit the lab procedure
        lp1.setComments( "Went well" );
        final String editContent = mvc
                .perform( put( "/api/v1/labProcs" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new LabProcForm( lp1 ) ) ) )
                .andReturn().getResponse().getContentAsString();
        final LabProc edited = gson.fromJson( editContent, LabProc.class );
        assertEquals( lp1.getId(), edited.getId() );
        assertEquals( lp1.getComments(), edited.getComments() );

        // Attempt invalid edit
        lp1.setComments( null );
        mvc.perform( put( "/api/v1/labProcs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new LabProcForm( lp1 ) ) ) ).andExpect( status().isBadRequest() );

        lp1.setComments( COMMENTS );
        // Get lab procedures as lab tech
        final String allLabProcContent = mvc.perform( get( "/api/v1/labProcs" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final List<LabProc> allLabProcs = gson.fromJson( allLabProcContent, new TypeToken<List<LabProc>>() {
        }.getType() );
        assertEquals( LABTECH, allLabProcs.get( 0 ).getLabtech().getUsername() );
    }
}
