package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.hamcrest.Matchers;
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

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.LabCodeForm;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.LabCode;
import edu.ncsu.csc.itrust2.models.persistent.LabProc;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Class for testing lab code API.
 *
 * @author Cody Nesbitt
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APILabCodeTest {
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        DomainObject.deleteAll( LabProc.class );
        DomainObject.deleteAll( LabCode.class );
    }

    /**
     * Tests basic lab code API functionality.
     *
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void testLabCodeAPI () throws UnsupportedEncodingException, Exception {
        // Create lab codes for testing
        final LabCodeForm form1 = new LabCodeForm();
        form1.setCode( "00000-0" );
        form1.setLongName( "code1" );
        form1.setComponent( "comp1" );
        form1.setProperty( "prop1" );
        form1.setMethod( "meth1" );

        final LabCodeForm form2 = new LabCodeForm();
        form2.setCode( "00000-1" );
        form2.setLongName( "code2" );
        form2.setComponent( "comp2" );
        form2.setProperty( "prop2" );
        form2.setMethod( "meth2" );

        // Add code1 to system
        final String content1 = mvc
                .perform( post( "/api/v1/labcodes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as LabCode object
        final Gson gson = new GsonBuilder().create();
        final LabCode code1 = gson.fromJson( content1, LabCode.class );
        assertEquals( form1.getCode(), code1.getCode() );
        assertEquals( form1.getLongName(), code1.getLongName() );
        assertEquals( form1.getComponent(), code1.getComponent() );
        assertEquals( form1.getProperty(), code1.getProperty() );
        assertEquals( form1.getMethod(), code1.getMethod() );

        // Attempt to add same lab code twice
        mvc.perform( post( "/api/v1/labcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form1 ) ) ).andExpect( status().isConflict() );

        // Add code2 to system
        final String content2 = mvc
                .perform( post( "/api/v1/labcodes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final LabCode code2 = gson.fromJson( content2, LabCode.class );
        assertEquals( form2.getCode(), code2.getCode() );
        assertEquals( form2.getLongName(), code2.getLongName() );
        assertEquals( form2.getComponent(), code2.getComponent() );
        assertEquals( form2.getProperty(), code2.getProperty() );
        assertEquals( form2.getMethod(), code2.getMethod() );

        // Verify lab codes have been added
        mvc.perform( get( "/api/v1/labcodes" ) ).andExpect( status().isOk() )
                .andExpect( content().string( Matchers.containsString( form1.getCode() ) ) )
                .andExpect( content().string( Matchers.containsString( form2.getCode() ) ) );

        // Edit first lab code's long name
        code1.setLongName( "this is a new long name" );
        final String editContent = mvc
                .perform( put( "/api/v1/labcodes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( code1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final LabCode editedCode = gson.fromJson( editContent, LabCode.class );
        assertEquals( code1.getId(), editedCode.getId() );
        assertEquals( code1.getCode(), editedCode.getCode() );
        assertEquals( "this is a new long name", editedCode.getLongName() );
        assertEquals( code1.getComponent(), editedCode.getComponent() );
        assertEquals( code1.getProperty(), editedCode.getProperty() );
        assertEquals( code1.getMethod(), editedCode.getMethod() );

        // Attempt invalid edit
        code2.setCode( "00000-0" );
        mvc.perform( put( "/api/v1/labcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( code2 ) ) ).andExpect( status().isConflict() );

        // Follow up with valid edit
        code2.setCode( "00000-3" );
        mvc.perform( put( "/api/v1/labcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( code2 ) ) ).andExpect( status().isOk() );

        // Delete test codes
        mvc.perform( delete( "/api/v1/labcodes/" + code1.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( code1.getId().toString() ) );
        mvc.perform( delete( "/api/v1/labcodes/" + code2.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( code2.getId().toString() ) );
    }
}
