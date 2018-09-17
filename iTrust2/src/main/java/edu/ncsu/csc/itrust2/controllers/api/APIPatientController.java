package edu.ncsu.csc.itrust2.controllers.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * Patient model.
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPatientController extends APIController {

    /**
     * Retrieves and returns a list of all Patients stored in the system
     *
     * @return list of patients
     */
    @GetMapping ( BASE_PATH + "/patients" )
    public List<Patient> getPatients () {
        final List<Patient> pats = Patient.getPatients();
        for ( final Patient p : pats ) {
            for ( final Patient rep : p.getPersonalReps() ) {
                rep.setPersonalReps( new HashSet<Patient>() );
                rep.setRepresentees( new HashSet<Patient>() );
            }
        }
        return pats;
    }

    /**
     * If you are logged in as a patient, then you can use this convenience
     * lookup to find your own information without remembering your id. This
     * allows you the shorthand of not having to look up the id in between.
     *
     * @return The patient object for the currently authenticated user.
     */
    @GetMapping ( BASE_PATH + "/patient" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity getPatient () {
        final User self = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
        final Patient patient = Patient.getPatient( self );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for you, " + self.getUsername() ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, LoggerUtil.currentUser(), self.getUsername(),
                    "Retrieved demographics for user " + self.getUsername() );
            for ( final Patient rep : patient.getPersonalReps() ) {
                rep.setPersonalReps( new HashSet<Patient>() );
                rep.setRepresentees( new HashSet<Patient>() );
            }
            return new ResponseEntity( patient, HttpStatus.OK );
        }
    }

    /**
     * Retrieves and returns the Patient with the username provided
     *
     * @param username
     *            The username of the Patient to be retrieved, as stored in the
     *            Users table
     * @return response
     */
    @GetMapping ( BASE_PATH + "/patients/{username}" )
    public ResponseEntity getPatient ( @PathVariable ( "username" ) final String username ) {
        final Patient patient = Patient.getByName( username );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "No Patient found for username " + username ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.PATIENT_DEMOGRAPHICS_VIEW, LoggerUtil.currentUser(), username,
                    "HCP retrieved demographics for patient with username " + username );
            for ( final Patient rep : patient.getPersonalReps() ) {
                rep.setPersonalReps( new HashSet<Patient>() );
                rep.setRepresentees( new HashSet<Patient>() );
            }
            for ( final Patient rep : patient.getPatientsRepresented() ) {
                rep.setPersonalReps( new HashSet<Patient>() );
                rep.setRepresentees( new HashSet<Patient>() );
            }
            return new ResponseEntity( patient, HttpStatus.OK );
        }
    }

    /**
     * Gets the list of personal representatives for a given patient as an HCP
     *
     * @param username
     *            patient for whom to retrieve list
     * @return list of PRs
     */
    @GetMapping ( BASE_PATH + "/representatives/{username}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public ResponseEntity getPersonalRepsAsHCP ( @PathVariable ( "username" ) final String username ) {
        final Patient p = Patient.getByName( username );
        final Set<Patient> list;
        try {
            list = p.getPersonalReps();
        }
        catch ( final NullPointerException e ) {
            return new ResponseEntity( errorResponse( "No PR list found for username " + username ),
                    HttpStatus.NOT_FOUND );
        }
        LoggerUtil.log( TransactionType.VIEW_PR_BY_HCP, LoggerUtil.currentUser(), username, "" );
        for ( final Patient rep : p.getPersonalReps() ) {
            rep.setPersonalReps( new HashSet<Patient>() );
            rep.setRepresentees( new HashSet<Patient>() );
        }
        return new ResponseEntity( list, HttpStatus.OK );
    }

    /**
     * Gets the list of personal representatives for the user as a Patient
     *
     * @return list of PRs
     */
    @GetMapping ( BASE_PATH + "/representatives" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity getPersonalRepsAsPatient () {
        final String username = LoggerUtil.currentUser();
        final Patient p = Patient.getByName( username );
        final Set<Patient> list;
        try {
            list = p.getPersonalReps();
        }
        catch ( final NullPointerException e ) {
            return new ResponseEntity( errorResponse( "No PR list found for username " + username ),
                    HttpStatus.NOT_FOUND );
        }
        LoggerUtil.log( TransactionType.VIEW_PR_BY_PATIENT, LoggerUtil.currentUser(), username, "" );
        for ( final Patient rep : p.getPersonalReps() ) {
            rep.setPersonalReps( new HashSet<Patient>() );
            rep.setRepresentees( new HashSet<Patient>() );
        }
        return new ResponseEntity( list, HttpStatus.OK );
    }

    /**
     * Gets the list of patients represented by a given patient as an HCP
     *
     * @param username
     *            patient for whom to retrieve list
     * @return list of patients represented
     */
    @GetMapping ( BASE_PATH + "/patientsrepresented/{username}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public ResponseEntity getPatientsRepresentedAsHCP ( @PathVariable ( "username" ) final String username ) {
        final Patient p = Patient.getByName( username );
        final Set<Patient> list;
        try {
            list = p.getPatientsRepresented();
        }
        catch ( final NullPointerException e ) {
            return new ResponseEntity( errorResponse( "No PR list found for username " + username ),
                    HttpStatus.NOT_FOUND );
        }
        for ( final Patient rep : p.getPatientsRepresented() ) {
            rep.setPersonalReps( new HashSet<Patient>() );
            rep.setRepresentees( new HashSet<Patient>() );
        }
        return new ResponseEntity( list, HttpStatus.OK );
    }

    /**
     * Gets the list of patients represented by the user as a Patient
     *
     * @return list of patients represented
     */
    @GetMapping ( BASE_PATH + "/patientsrepresented" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity getPatientsRepresentedAsPatient () {
        final String username = LoggerUtil.currentUser();
        final Patient p = Patient.getByName( username );
        final Set<Patient> list;
        try {
            list = p.getPatientsRepresented();
        }
        catch ( final NullPointerException e ) {
            return new ResponseEntity( errorResponse( "No PR list found for username " + username ),
                    HttpStatus.NOT_FOUND );
        }
        for ( final Patient rep : p.getPatientsRepresented() ) {
            rep.setPersonalReps( new HashSet<Patient>() );
            rep.setRepresentees( new HashSet<Patient>() );
        }
        return new ResponseEntity( list, HttpStatus.OK );
    }

    /**
     * Add a patient as a personal representative for another patient by their
     * first and last name as an HCP
     *
     * @param username
     *            patient whose list to add to
     * @param repFirst
     *            first name of rep to add
     * @param repLast
     *            last name of rep to add
     * @return success boolean
     */
    @PutMapping ( BASE_PATH + "/addrep/{username}/{repFirst}/{repLast}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public ResponseEntity addPersonalRepAsHCP ( @PathVariable ( "username" ) final String username,
            @PathVariable ( "repFirst" ) final String repFirst, @PathVariable ( "repLast" ) final String repLast ) {
        final Patient p = Patient.getByName( username );
        final Patient rep = Patient.getByFirstAndLast( repFirst, repLast );
        if ( p.addPersonalRep( rep ) && rep.addRepresentee( p ) ) {
            p.save();
            rep.save();
            LoggerUtil.log( TransactionType.ADD_PR_BY_HCP, LoggerUtil.currentUser(), username, "" );
            return new ResponseEntity( true, HttpStatus.OK );
        }
        else {
            return new ResponseEntity(
                    errorResponse(
                            "Unable to add user by name " + repFirst + " " + repLast + " as PR for " + username ),
                    HttpStatus.NOT_FOUND );
        }
    }

    /**
     * Add a patient as a personal representative for the user by their first
     * and last name as a patient
     *
     * @param repFirst
     *            first name of rep to add
     * @param repLast
     *            last name of rep to add
     * @return success boolean
     */
    @PutMapping ( BASE_PATH + "/addrep/{repFirst}/{repLast}" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity addPersonalRepAsPatient ( @PathVariable ( "repFirst" ) final String repFirst,
            @PathVariable ( "repLast" ) final String repLast ) {
        final String username = LoggerUtil.currentUser();
        final Patient p = Patient.getByName( username );
        final Patient rep = Patient.getByFirstAndLast( repFirst, repLast );
        if ( p.addPersonalRep( rep ) && rep.addRepresentee( p ) ) {
            p.save();
            rep.save();
            LoggerUtil.log( TransactionType.ADD_PR_BY_PATIENT, LoggerUtil.currentUser(), username, "" );
            return new ResponseEntity( true, HttpStatus.OK );
        }
        else {
            return new ResponseEntity(
                    errorResponse(
                            "Unable to add user by name " + repFirst + " " + repLast + " as PR for " + username ),
                    HttpStatus.NOT_FOUND );
        }
    }

    /**
     * Remove the given patient as a personal representative for the user
     *
     * @param repMID
     *            username of rep to remove
     * @return success boolean
     */
    @PutMapping ( BASE_PATH + "/removerep/{repMID}" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity removePersonalRep ( @PathVariable ( "repMID" ) final String repMID ) {
        final String username = LoggerUtil.currentUser();
        final Patient p = Patient.getByName( username );
        final Patient rep = Patient.getByName( repMID );
        if ( p.deletePersonalRep( rep ) && rep.deleteRepresentee( p ) ) {
            p.save();
            rep.save();
            LoggerUtil.log( TransactionType.REMOVE_PR_BY_PATIENT, username, repMID, "" );
            return new ResponseEntity( true, HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Unable to remove user by username " + repMID + " as PR" ),
                    HttpStatus.NOT_FOUND );
        }
    }

    /**
     * Remove the user as a personal representative for the given patient
     *
     * @param repMID
     *            username of patient for whom to remove self as personal rep
     * @return success boolean
     */
    @PutMapping ( BASE_PATH + "/removeselfasrep/{repMID}" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity removeSelfAsPersonalRep ( @PathVariable ( "repMID" ) final String repMID ) {
        final String username = LoggerUtil.currentUser();
        final Patient p = Patient.getByName( username );
        final Patient rep = Patient.getByName( repMID );
        if ( rep.deletePersonalRep( p ) && p.deleteRepresentee( rep ) ) {
            p.save();
            rep.save();
            LoggerUtil.log( TransactionType.REMOVE_PR_AS_PR, username, repMID, "" );
            return new ResponseEntity( true, HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Unable to remove self as PR for user by username " + repMID ),
                    HttpStatus.NOT_FOUND );
        }
    }

    /**
     * Creates a new Patient record for a User from the RequestBody provided.
     *
     * @param patientF
     *            the Patient to be validated and saved to the database
     * @return response
     */
    @PostMapping ( BASE_PATH + "/patients" )
    public ResponseEntity createPatient ( @RequestBody final PatientForm patientF ) {
        try {
            final Patient patient = new Patient( patientF );
            if ( null != Patient.getPatient( patient.getSelf() ) ) {
                return new ResponseEntity(
                        errorResponse( "Patient with the id " + patient.getSelf().getUsername() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            patient.save();
            LoggerUtil.log( TransactionType.CREATE_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not create " + patientF.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Updates the Patient with the id provided by overwriting it with the new
     * Patient record that is provided. If the ID provided does not match the ID
     * set in the Patient provided, the update will not take place
     *
     * @param id
     *            The username of the Patient to be updated
     * @param patientF
     *            The updated Patient to save
     * @return response
     */
    @PutMapping ( BASE_PATH + "/patients/{id}" )
    public ResponseEntity updatePatient ( @PathVariable final String id, @RequestBody final PatientForm patientF ) {
        // check that the user is an HCP or a patient with username equal to id
        boolean userEdit = false; // true if user edits his or her own
                                  // demographics, false if hcp edits them
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            if ( !auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_HCP" ) )
                    && ( !auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_PATIENT" ) )
                            || !auth.getName().equals( id ) ) ) {
                return new ResponseEntity( errorResponse( "You do not have permission to edit this record" ),
                        HttpStatus.UNAUTHORIZED );
            }

            userEdit = auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_HCP" ) ) ? true : false;
        }
        catch ( final Exception e ) {
            return new ResponseEntity( HttpStatus.UNAUTHORIZED );
        }

        try {
            final Patient patient = new Patient( patientF );
            if ( null != patient.getSelf().getUsername() && !id.equals( patient.getSelf().getUsername() ) ) {
                return new ResponseEntity(
                        errorResponse( "The ID provided does not match the ID of the Patient provided" ),
                        HttpStatus.CONFLICT );
            }
            final Patient dbPatient = Patient.getByName( id );
            if ( null == dbPatient ) {
                return new ResponseEntity( errorResponse( "No Patient found for id " + id ), HttpStatus.NOT_FOUND );
            }
            patient.save();

            // Log based on whether user or hcp edited demographics
            if ( userEdit ) {
                LoggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser(),
                        "User with username " + patient.getSelf().getUsername() + "updated their demographics" );
            }
            else {
                LoggerUtil.log( TransactionType.PATIENT_DEMOGRAPHICS_EDIT, LoggerUtil.currentUser(),
                        patient.getSelf().getUsername(),
                        "HCP edited demographics for patient with username " + patient.getSelf().getUsername() );
            }
            return new ResponseEntity( patient, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not update " + patientF.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
