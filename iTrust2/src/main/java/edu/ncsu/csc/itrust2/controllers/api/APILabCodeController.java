package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.admin.LabCodeForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LabCode;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with lab codes. Exposes functionality to
 * add, edit, fetch, and delete lab code.
 *
 * @author Cody Nesbitt
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APILabCodeController extends APIController {
    /**
     * Adds a new lab code to the system. Requires admin permissions. Returns an
     * error message if something goes wrong.
     *
     * @param form
     *            the lab code form
     * @return the created lab code
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PostMapping ( BASE_PATH + "/labcodes" )
    public ResponseEntity addLabCode ( @RequestBody final LabCodeForm form ) {
        try {
            final LabCode lc = new LabCode( form );

            // Make sure code does not conflict with existing lab codes
            if ( LabCode.getByCode( lc.getCode() ) != null ) {
                LoggerUtil.log( TransactionType.ADD_LAB_CODE, LoggerUtil.currentUser(),
                        "Conflict: lab code with code " + lc.getCode() + " already exists" );
                return new ResponseEntity( errorResponse( "Lab code with code " + lc.getCode() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            lc.save();
            LoggerUtil.log( TransactionType.ADD_LAB_CODE, LoggerUtil.currentUser(),
                    "Lab code " + lc.getCode() + " created" );
            return new ResponseEntity( lc, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.ADD_LAB_CODE, LoggerUtil.currentUser(), "Failed to create lab code" );
            return new ResponseEntity( errorResponse( "Could not add lab code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits a lab code in the system. The id stored in the form must match an
     * existing lab code. Requires admin permissions.
     *
     * @param form
     *            the edited lab code form
     * @return the edited lab code or an error message
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PutMapping ( BASE_PATH + "/labcodes" )
    public ResponseEntity editLabCode ( @RequestBody final LabCodeForm form ) {
        try {
            final LabCode lc = new LabCode( form );

            // Check for existing lab code in database
            final LabCode savedLabCode = LabCode.getById( lc.getId() );
            if ( savedLabCode == null ) {
                return new ResponseEntity( errorResponse( "No lab code found with id " + lc.getId() ),
                        HttpStatus.NOT_FOUND );
            }

            // If the code was changed, make sure it is unique
            final LabCode sameCode = LabCode.getByCode( lc.getCode() );
            if ( sameCode != null && !sameCode.getId().equals( savedLabCode.getId() ) ) {
                return new ResponseEntity( errorResponse( "Lab Code with code " + lc.getCode() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            lc.save(); /* Overwrite existing lab code */

            LoggerUtil.log( TransactionType.EDIT_LAB_CODE, LoggerUtil.currentUser(),
                    "Lab code with id " + lc.getId() + " edited" );
            return new ResponseEntity( lc, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.EDIT_LAB_CODE, LoggerUtil.currentUser(), "Failed to edit lab code" );
            return new ResponseEntity( errorResponse( "Could not update lab code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the lab code with the id matching the given code. Requires admin
     * permissions.
     *
     * @param id
     *            the id of the lab code to delete
     * @return the id of the deleted lab code
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @DeleteMapping ( BASE_PATH + "/labcodes/{id}" )
    public ResponseEntity deleteLabCode ( @PathVariable final String id ) {
        try {
            final LabCode lc = LabCode.getById( Long.parseLong( id ) );
            if ( lc == null ) {
                LoggerUtil.log( TransactionType.DELETE_LAB_CODE, LoggerUtil.currentUser(),
                        "Could not find lab code with id " + id );
                return new ResponseEntity( errorResponse( "No lab code found with id " + id ), HttpStatus.NOT_FOUND );
            }
            lc.delete();
            LoggerUtil.log( TransactionType.DELETE_LAB_CODE, LoggerUtil.currentUser(),
                    "Deleted lab code with id " + lc.getId() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.DELETE_LAB_CODE, LoggerUtil.currentUser(), "Failed to delete lab code" );
            return new ResponseEntity( errorResponse( "Could not delete lab code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Gets a list of all the lab codes in the system.
     *
     * @return a list of lab codes
     */
    @GetMapping ( BASE_PATH + "/labcodes" )
    public List<LabCode> getLabCode () {
        LoggerUtil.log( TransactionType.VIEW_LAB_CODES, LoggerUtil.currentUser(), "Fetched list of lab codes" );
        return LabCode.getAll();
    }
}
