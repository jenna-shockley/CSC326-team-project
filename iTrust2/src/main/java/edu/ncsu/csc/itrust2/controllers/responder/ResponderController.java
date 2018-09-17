package edu.ncsu.csc.itrust2.controllers.responder;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to manage basic abilities for emergency responder roles
 *
 * @author Noah Trimble
 *
 */

@Controller
public class ResponderController {

    /**
     * Returns the responder for the given model
     *
     * @param model
     *            model to check
     * @return role
     */
    @RequestMapping ( value = "responder/index" )
    @PreAuthorize ( "hasRole('ROLE_RESPONDER')" )
    public String index ( final Model model ) {
        return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_RESPONDER.getLanding();
    }

    /**
     * Returns the form page for an Emergency Responder to view
     * EmergencyHealthRecords
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/responder/viewEmergencyRecords" )
    @PreAuthorize ( "hasRole('ROLE_RESPONDER')" )
    public String viewEmergencyRecords ( final Model model ) {
        return "/personnel/viewEmergencyRecords";
    }

}
