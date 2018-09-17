package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for API functionality for interacting with Personal Representatives
 *
 * @author Cody Nesbitt
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
@FixMethodOrder ( MethodSorters.NAME_ASCENDING )
public class APIPersonalRepTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    final String                  resultEmpty = "[]";
    final String                  resultP1    = "[{\"self\":{\"username\":\"patient1\",\"password\":\"$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.\",\"enabled\":1,\"role\":\"ROLE_PATIENT\"},\"firstName\":\"Cody\",\"lastName\":\"Nesbitt\",\"personalReps\":[],\"patientsRepresented\":[]}]";
    final String                  resultP2    = "[{\"self\":{\"username\":\"patient2\",\"password\":\"$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.\",\"enabled\":1,\"role\":\"ROLE_PATIENT\"},\"firstName\":\"Bob\",\"lastName\":\"Human\",\"personalReps\":[],\"patientsRepresented\":[]}]";

    /**
     * Sets up test
     *
     * @throws Exception
     */
    @Before
    public void setup () throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        DomainObject.deleteAll( Patient.class );

        final Patient patient1 = new Patient();
        final User user1 = new User( "patient1", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        user1.save();
        patient1.setSelf( user1 );
        patient1.setFirstName( "Cody" );
        patient1.setLastName( "Nesbitt" );
        patient1.save();

        final Patient patient2 = new Patient();
        final User user2 = new User( "patient2", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        user2.save();
        patient2.setSelf( user2 );
        patient2.setFirstName( "Bob" );
        patient2.setLastName( "Human" );
        patient2.save();
    }

    /**
     * Test the Personal Rep as Patient functionality in APIPatientController
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient1", roles = { "PATIENT" } )
    public void testPersonalRepsAsPatient () throws Exception {
        assertEquals( resultEmpty, mvc.perform( get( "/api/v1/representatives" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

        assertEquals( resultEmpty, mvc.perform( get( "/api/v1/patientsrepresented" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

        assertEquals( "true", mvc.perform( put( "/api/v1/addrep/Bob/Human" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString() );

        assertEquals( resultP2, mvc.perform( get( "/api/v1/representatives" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString() );

        assertEquals( resultEmpty, mvc.perform( get( "/api/v1/patientsrepresented" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

        assertEquals( "true", mvc.perform( put( "/api/v1/removerep/patient2" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

        assertEquals( resultEmpty, mvc.perform( get( "/api/v1/representatives" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

        assertEquals( resultEmpty, mvc.perform( get( "/api/v1/patientsrepresented" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );
    }

    /**
     * Test the Personal Rep as HCP functionality in APIPatientController
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testPersonalRepsAsHCP () throws Exception {
        assertEquals( resultEmpty, mvc.perform( get( "/api/v1/representatives/patient1" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

        assertEquals( resultEmpty, mvc.perform( get( "/api/v1/patientsrepresented/patient1" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString() );

        assertEquals( "true", mvc.perform( put( "/api/v1/addrep/patient1/Bob/Human" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

        assertEquals( resultP2, mvc.perform( get( "/api/v1/representatives/patient1" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

        assertEquals( resultEmpty, mvc.perform( get( "/api/v1/patientsrepresented/patient1" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString() );
    }
}
