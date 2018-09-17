package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.hcp.LabProcForm;
import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.StatusLP;

/**
 * Represents a lab procedure in the system.
 *
 * @author Noah Trimble (nmtrimbl)
 *
 */
@Entity
@Table ( name = "LabProcedures" )
public class LabProc extends DomainObject<LabProc> {

    /** ID for the lab procedure */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long     id;

    /** Lab code attached to the procedure */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "labcode_id" )
    private LabCode  code;

    /** Lab tech assigned to the lab procedure */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "labtech_id" )
    private User     labtech;

    /** Priority of the lab procedure */
    @NotNull
    @JoinColumn ( name = "priority_id" )
    private Priority priority;

    /** Date the lab procedure was created */
    @NotNull
    @JoinColumn ( name = "date_id" )
    private Calendar date;

    /** Comments on the procedure */
    @NotNull
    @JoinColumn ( name = "comments_id" )
    private String   comments;

    /** Status of the lab procedure */
    @NotNull
    @JoinColumn ( name = "status_id" )
    private StatusLP status;

    /** Patient attached to the lab procedure */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private User     patient;

    /**
     * Empty constructor for Hibernate.
     */
    public LabProc () {
    }

    /**
     * Construct a new LabProc using the details in the given form.
     *
     * @param form
     *            the lab procedure form
     */
    public LabProc ( final LabProcForm form ) {
        setCode( LabCode.getByCode( form.getCode() ) );
        setLabtech( User.getByName( form.getLabtech() ) );
        setPatient( User.getByName( form.getPatient() ) );
        setPriority( Priority.parse( form.getPriority() ) );
        setComments( form.getComments() );
        setStatus( StatusLP.parse( form.getStatus() ) );

        if ( form.getId() != null ) {
            setId( form.getId() );
        }

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        Date parsedDate = null;
        try {
            parsedDate = sdf.parse( form.getDate() );
        }
        catch ( final ParseException e ) {
            // Ignore, Hibernate will catch the null date
        }
        final Calendar c = Calendar.getInstance();
        c.setTime( parsedDate );
        setDate( c );
    }

    /**
     * Gets the id of the lab procedure
     *
     * @return the id
     */
    @Override
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
    public LabCode getCode () {
        return code;
    }

    /**
     * Sets the lab code for the procedure
     *
     * @param code
     *            the code to set
     */
    public void setCode ( final LabCode code ) {
        this.code = code;
    }

    /**
     * Gets the assigned lab tech
     *
     * @return the labtech
     */
    public User getLabtech () {
        return labtech;
    }

    /**
     * Assigns a lab tech
     *
     * @param labtech
     *            the labtech to set
     */
    public void setLabtech ( final User labtech ) {
        this.labtech = labtech;
    }

    /**
     * Gets the priority
     *
     * @return the priority
     */
    public Priority getPriority () {
        return priority;
    }

    /**
     * Sets the priority
     *
     * @param priority
     *            the priority to set
     */
    public void setPriority ( final Priority priority ) {
        this.priority = priority;
    }

    /**
     * Gets the date of creation
     *
     * @return the date
     */
    public Calendar getDate () {
        return date;
    }

    /**
     * Sets the creation date
     *
     * @param date
     *            the date to set
     */
    public void setDate ( final Calendar date ) {
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
    public StatusLP getStatus () {
        return status;
    }

    /**
     * Sets the status of the procedure
     *
     * @param status
     *            the status to set
     */
    public void setStatus ( final StatusLP status ) {
        this.status = status;
    }

    /**
     * Returns the user associated with this lab procedure.
     *
     * @return the patient
     */
    public User getPatient () {
        return patient;
    }

    /**
     * Sets the lab procedure's patient to the given user
     *
     * @param user
     *            the user
     */
    public void setPatient ( final User user ) {
        this.patient = user;
    }

    /**
     * Returns a collection of lab procedures that meet the "where" query
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return a collection of matching lab procedures
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LabProc> getWhere ( final List<Criterion> where ) {
        return (List<LabProc>) getWhere( LabProc.class, where );
    }

    /**
     * Gets the LabProc with the given id, or null if none exists.
     *
     * @param id
     *            the id to query for
     * @return the matching lab procedure
     */
    public static LabProc getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Gets the LabProc with the given lab tech, or null if none exists.
     *
     * @param labtech
     *            the labtech to query for
     * @return the matching lab procedure
     */
    public static LabProc getByLabtech ( final User labtech ) {
        try {
            return getWhere( createCriterionAsList( "labtech", labtech ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Gets all of the LabProcs with the given lab tech, or null if none exists.
     *
     * @param labtech
     *            the labtech to query for
     * @return the matching lab procedures
     */
    public static List<LabProc> getAllForLabtech ( final User labtech ) {
        return getWhere( createCriterionList( createCriterion( "labtech", labtech ) ) );
    }

    /**
     * Rerieve all LabProcs for the patient provided
     *
     * @param patient
     *            The Patient to find LabProcs for
     * @return The List of records that was found
     */
    public static List<LabProc> getForPatient ( final String patient ) {
        return getWhere( createCriterionAsList( "patient", User.getByNameAndRole( patient, Role.ROLE_PATIENT ) ) );
    }

    /**
     * Gets a collection of all the lab procedures in the system.
     *
     * @return the system's lab procedures
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LabProc> getAll () {
        return (List<LabProc>) DomainObject.getAll( LabProc.class );
    }
}
