package edu.ncsu.csc.itrust2.controllers.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * EmergencyHealthRecord model.
 *
 * @author Cody Nesbitt
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIEHRController extends APIController {

    /** gson variable */
    private final Gson gson = new Gson();

    /**
     * Retrieves and returns the EmergencyHealthRecords with the MID provided
     *
     * @param mid
     *            the MID of the EmergencyHealthRecord to be retrieved
     * @return response
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_RESPONDER')" )
    @GetMapping ( BASE_PATH + "/records/name/{MID}" )
    public ResponseEntity<String> getPatientName ( @PathVariable ( "MID" ) final String mid ) {
        final Patient p = Patient.getByName( mid );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "Could not find EmergencyHealthRecord for MID " + mid ),
                    HttpStatus.NOT_FOUND );
        }
        final String name = p.getFirstName() + " " + p.getLastName();

        LoggerUtil.log( TransactionType.VIEW_EHR, LoggerUtil.currentUser(), p.getSelf().getUsername(),
                LoggerUtil.currentUser() + " viewed the EHR of " + p.getSelf().getUsername() );
        return ResponseEntity.ok( gson.toJson( name ) );
    }

    /**
     * Retrieves and returns the EmergencyHealthRecords with the MID provided
     *
     * @param mid
     *            the MID of the EmergencyHealthRecord to be retrieved
     * @return response
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_RESPONDER')" )
    @GetMapping ( BASE_PATH + "/records/age/{MID}" )
    public ResponseEntity getPatientAge ( @PathVariable ( "MID" ) final String mid ) {
        final Patient p = Patient.getByName( mid );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "Could not find EmergencyHealthRecord for MID " + mid ),
                    HttpStatus.NOT_FOUND );
        }
        if ( p.getDateOfBirth() == null ) {
            return new ResponseEntity( "", HttpStatus.OK );
        }
        final Calendar now = Calendar.getInstance();
        now.setTimeInMillis( System.currentTimeMillis() );
        int years = now.get( Calendar.YEAR ) - p.getDateOfBirth().get( Calendar.YEAR );
        final int currMonth = now.get( Calendar.MONTH ) + 1;
        final int birthMonth = p.getDateOfBirth().get( Calendar.MONTH ) + 1;
        if ( currMonth < birthMonth ) {
            years--;
        }
        return new ResponseEntity( years, HttpStatus.OK );
    }

    /**
     * Retrieves and returns the EmergencyHealthRecords with the MID provided
     *
     * @param mid
     *            the MID of the EmergencyHealthRecord to be retrieved
     * @return response
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_RESPONDER')" )
    @GetMapping ( BASE_PATH + "/records/birth/{MID}" )
    public ResponseEntity<String> getPatientBirth ( @PathVariable ( "MID" ) final String mid ) {
        final Patient p = Patient.getByName( mid );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "Could not find EmergencyHealthRecord for MID " + mid ),
                    HttpStatus.NOT_FOUND );
        }
        if ( p.getDateOfBirth() == null ) {
            return new ResponseEntity( "", HttpStatus.OK );
        }
        final SimpleDateFormat formatter = new SimpleDateFormat( "MM-dd-yyyy" );
        final String date = formatter.format( p.getDateOfBirth().getTime() );

        return ResponseEntity.ok( gson.toJson( date ) );
    }

    /**
     * Retrieves and returns the EmergencyHealthRecords with the MID provided
     *
     * @param mid
     *            the MID of the EmergencyHealthRecord to be retrieved
     * @return response
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_RESPONDER')" )
    @GetMapping ( BASE_PATH + "/records/gender/{MID}" )
    public ResponseEntity<String> getPatientGender ( @PathVariable ( "MID" ) final String mid ) {
        final Patient p = Patient.getByName( mid );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "Could not find EmergencyHealthRecord for MID " + mid ),
                    HttpStatus.NOT_FOUND );
        }
        if ( p.getGender() == null ) {
            return new ResponseEntity( "", HttpStatus.OK );
        }

        return ResponseEntity.ok( gson.toJson( p.getGender().toString() ) );
    }

    /**
     * Retrieves and returns the EmergencyHealthRecords with the MID provided
     *
     * @param mid
     *            the MID of the EmergencyHealthRecord to be retrieved
     * @return response
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_RESPONDER')" )
    @GetMapping ( BASE_PATH + "/records/blood/{MID}" )
    public ResponseEntity<String> getPatientBloodType ( @PathVariable ( "MID" ) final String mid ) {
        final Patient p = Patient.getByName( mid );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "Could not find EmergencyHealthRecord for MID " + mid ),
                    HttpStatus.NOT_FOUND );
        }
        if ( p.getBloodType() == null ) {
            return new ResponseEntity( "", HttpStatus.OK );
        }

        return ResponseEntity.ok( gson.toJson( p.getBloodType().getName() ) );
    }

    /**
     * Retrieves and returns the EmergencyHealthRecords with the MID provided
     *
     * @param mid
     *            the MID of the EmergencyHealthRecord to be retrieved
     * @return response
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_RESPONDER')" )
    @GetMapping ( BASE_PATH + "/records/prescriptions/{MID}" )
    public ResponseEntity getPrescriptionsForRecord ( @PathVariable ( "MID" ) final String mid ) {
        final Patient p = Patient.getByName( mid );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "Could not find EmergencyHealthRecord for MID " + mid ),
                    HttpStatus.NOT_FOUND );
        }
        final List<Prescription> prescriptions = Prescription.getForPatient( mid );
        return new ResponseEntity( prescriptions, HttpStatus.OK );
    }

    /**
     * Retrieves and returns the EmergencyHealthRecords with the MID provided
     *
     * @param mid
     *            the MID of the EmergencyHealthRecord to be retrieved
     * @return response
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_RESPONDER')" )
    @GetMapping ( BASE_PATH + "/records/diagnoses/{MID}" )
    public ResponseEntity getDiagnosesForRecord ( @PathVariable ( "MID" ) final String mid ) {
        final Patient p = Patient.getByName( mid );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "Could not find EmergencyHealthRecord for MID " + mid ),
                    HttpStatus.NOT_FOUND );
        }
        final List<Diagnosis> diagnoses = Diagnosis.getForPatient( p.getSelf() );
        return new ResponseEntity( diagnoses, HttpStatus.OK );
    }

}
