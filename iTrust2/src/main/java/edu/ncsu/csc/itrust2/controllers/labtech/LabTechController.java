package edu.ncsu.csc.itrust2.controllers.labtech;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to manage basic abilities for lab tech roles
 *
 * @author Noah Trimble
 *
 */

@Controller
public class LabTechController {

    /**
     * Returns the lab tech for the given model
     *
     * @param model
     *            model to check
     * @return role
     */
    @RequestMapping ( value = "labtech/index" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public String index ( final Model model ) {
        return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_LABTECH.getLanding();
    }

    /**
     * Returns the page that allows the lab tech to view and edit their assigned
     * lab procedures
     *
     * @param model
     *            the data for the front end
     * @return page to display to the user
     */
    @RequestMapping ( value = "labtech/labProc" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public String labProc ( final Model model ) {
        return "labtech/labProc";
    }
}
