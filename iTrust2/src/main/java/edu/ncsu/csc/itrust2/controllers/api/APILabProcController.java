package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.hcp.LabProcForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LabProc;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with lab procedures. Exposes functionality
 * to add, edit, fetch, and delete lab procedures.
 *
 * @author Noah Trimble (nmtrimbl)
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APILabProcController extends APIController {

    /**
     * Adds a new lab procedure to the system. Requires HCP permissions.
     *
     * @param form
     *            details of the new lab procedure
     * @return the created lab procedure
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @PostMapping ( BASE_PATH + "/labProcs" )
    public ResponseEntity addLabProc ( @RequestBody final LabProcForm form ) {
        try {
            final LabProc p = new LabProc( form );
            p.save();
            LoggerUtil.log( TransactionType.ADD_LAB_PROC, LoggerUtil.currentUser(), p.getLabtech().getUsername(),
                    "Created lab procedure with id " + p.getId() );
            return new ResponseEntity( p, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.ADD_LAB_PROC, LoggerUtil.currentUser(), "Failed to create lab procedure" );
            return new ResponseEntity( errorResponse( "Could not save the lab procedure: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits an existing lab procedure in the system. Matches lab procedures by
     * ids. Requires lab tech permissions.
     *
     * @param form
     *            the form containing the details of the new lab procedure
     * @return the edited lab procedure
     */
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    @PutMapping ( BASE_PATH + "/labProcs" )
    public ResponseEntity editLabProc ( @RequestBody final LabProcForm form ) {
        try {
            final LabProc p = new LabProc( form );
            final LabProc saved = LabProc.getById( p.getId() );
            if ( saved == null ) {
                LoggerUtil.log( TransactionType.EDIT_LAB_PROC, LoggerUtil.currentUser(),
                        "No lab procedure found with id " + p.getId() );
                return new ResponseEntity( errorResponse( "No lab procedure found with id " + p.getId() ),
                        HttpStatus.NOT_FOUND );
            }
            p.save(); /* Overwrite existing */
            LoggerUtil.log( TransactionType.EDIT_LAB_PROC, LoggerUtil.currentUser(),
                    "Edited lab procedure with id " + p.getId() );
            return new ResponseEntity( p, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.EDIT_LAB_PROC, LoggerUtil.currentUser(), "Failed to edit lab procedure" );
            return new ResponseEntity( errorResponse( "Failed to update lab procedure: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the lab procedure with the given id.
     *
     * @param id
     *            the id
     * @return the id of the deleted lab procedure
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @DeleteMapping ( BASE_PATH + "/labProcs/{id}" )
    public ResponseEntity deleteLabProc ( @PathVariable final Long id ) {
        final LabProc p = LabProc.getById( id );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "No lab procedure found with id " + id ), HttpStatus.NOT_FOUND );
        }
        try {
            p.delete();
            LoggerUtil.log( TransactionType.DELETE_LAB_PROC, LoggerUtil.currentUser(), p.getLabtech().getUsername(),
                    "Deleted lab procedure with id " + p.getId() );
            return new ResponseEntity( p.getId(), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.DELETE_LAB_PROC, LoggerUtil.currentUser(), p.getLabtech().getUsername(),
                    "Failed to delete lab procedure" );
            return new ResponseEntity( errorResponse( "Failed to delete lab procedure: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns a collection of all the lab procedures in the system.
     *
     * @return all saved lab procedures
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT', 'ROLE_LABTECH')" )
    @GetMapping ( BASE_PATH + "/labProcs" )
    public List<LabProc> getLabProcs () {
        final boolean isHCP = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains( new SimpleGrantedAuthority( "ROLE_HCP" ) );
        final boolean isLabtech = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains( new SimpleGrantedAuthority( "ROLE_LABTECH" ) );

        if ( isHCP ) {
            // Return all lab procedures in the system
            return LabProc.getAll();
        }
        else if ( isLabtech ) {
            // Retrieve only assigned lab procedures to lab tech
            LoggerUtil.log( TransactionType.VIEW_LAB_PROCS, LoggerUtil.currentUser(),
                    "User viewed a list of all assigned lab procedures" );
            return LabProc.getAllForLabtech( User.getByName( LoggerUtil.currentUser() ) );
        }
        else {
            // Return only lab procedures assigned to the patient
            return LabProc.getForPatient( LoggerUtil.currentUser() );
        }
    }

    /**
     * Returns a single lab procedure using the given id.
     *
     * @param id
     *            the id of the desired lab procedure
     * @return the requested lab procedure
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @GetMapping ( BASE_PATH + "/labProcs/{id}" )
    public ResponseEntity getLabProc ( @PathVariable final Long id ) {
        final LabProc p = LabProc.getById( id );
        if ( p == null ) {
            LoggerUtil.log( TransactionType.VIEW_LAB_PROCS, LoggerUtil.currentUser(),
                    "Failed to find lab procedure with id " + id );
            return new ResponseEntity( errorResponse( "No lab procedure found for " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.VIEW_LAB_PROCS, LoggerUtil.currentUser(), "Viewed lab procedure  " + id );
            return new ResponseEntity( p, HttpStatus.OK );
        }
    }

}
