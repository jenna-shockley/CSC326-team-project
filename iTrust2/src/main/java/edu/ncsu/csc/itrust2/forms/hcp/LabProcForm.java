package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.ncsu.csc.itrust2.models.persistent.LabProc;

/**
 * A form for REST API communication. Contains fields for constructing LabProc
 * objects.
 *
 * @author Noah Trimble (nmtrimbl)
 */
public class LabProcForm implements Serializable {

    /** Serial ID */
    private static final long serialVersionUID = 1L;

    /** ID for the lab procedure */
    private Long              id;

    /** Lab code attached to the procedure */
    private String            code;

    /** Lab tech assigned to the lab procedure */
    private String            labtech;

    /** Priority of the lab procedure */
    private int               priority;

    /** Date the lab procedure was created */
    private String            date;

    /** Comments on the procedure */
    private String            comments;

    /** Status of the lab procedure */
    private String            status;

    /** Patient attached to the lab procedure */
    private String            patient;

    /**
     * Empty constructor for filling in fields without a LabProc object.
     */
    public LabProcForm () {
    }

    /**
     * Constructs a new form with information from the given lab procedure.
     *
     * @param proc
     *            the LabProc object
     */
    public LabProcForm ( final LabProc proc ) {
        setId( proc.getId() );
        setCode( proc.getCode().getCode() );
        setLabtech( proc.getLabtech().getUsername() );
        final SimpleDateFormat tempDate = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        setDate( tempDate.format( proc.getDate().getTime() ) );
        setPriority( proc.getPriority().getPriorityNumber() );
        setComments( proc.getComments() );
        setStatus( proc.getStatus().getName() );
        setPatient( proc.getPatient().getUsername() );
    }

    /**
     * Gets the id of the lab procedure
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of the lab procedure
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the lab code for the procedure
     *
     * @return the code
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the lab code for the procedure
     *
     * @param code
     *            the code to set
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Gets the assigned lab tech
     *
     * @return the labtech
     */
    public String getLabtech () {
        return labtech;
    }

    /**
     * Assigns a lab tech
     *
     * @param labtech
     *            the labtech to set
     */
    public void setLabtech ( final String labtech ) {
        this.labtech = labtech;
    }

    /**
     * Gets the priority
     *
     * @return the priority
     */
    public int getPriority () {
        return priority;
    }

    /**
     * Sets the priority
     *
     * @param priority
     *            the priority to set
     */
    public void setPriority ( final int priority ) {
        this.priority = priority;
    }

    /**
     * Gets the date of creation
     *
     * @return the date
     */
    public String getDate () {
        return date;
    }

    /**
     * Sets the creation date
     *
     * @param date
     *            the date to set
     */
    public void setDate ( final String date ) {
        this.date = date;
    }

    /**
     * Gets the comments on the procedure
     *
     * @return the comments
     */
    public String getComments () {
        return comments;
    }

    /**
     * Adds comments to the procedure
     *
     * @param comments
     *            the comments to set
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Gets the status of the procedure
     *
     * @return the status
     */
    public String getStatus () {
        return status;
    }

    /**
     * Sets the status of the procedure
     *
     * @param status
     *            the status to set
     */
    public void setStatus ( final String status ) {
        this.status = status;
    }

    /**
     * Returns the user associated with this lab procedure.
     *
     * @return the patient
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Sets the lab procedure's patient to the given user
     *
     * @param patient
     *            the patient
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

}
