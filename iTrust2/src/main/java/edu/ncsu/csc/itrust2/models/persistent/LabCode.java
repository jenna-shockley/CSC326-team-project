package edu.ncsu.csc.itrust2.models.persistent;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.hibernate.criterion.Criterion;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.forms.admin.LabCodeForm;

/**
 * Represents a lab code in the LOINC format.
 *
 * @author Cody Nesbitt
 */
@Entity
@Table ( name = "LabCodes" )
public class LabCode extends DomainObject<LabCode> {
    /** For Hibernate/Thymeleaf _must_ be an empty constructor */
    public LabCode () {
    }

    /**
     * Constructs a new form from the details in the given form
     *
     * @param form
     *            the form to base the new lab code on
     */
    public LabCode ( final LabCodeForm form ) {
        setId( form.getId() );
        setCode( form.getCode() );
        setLongName( form.getLongName() );
        setComponent( form.getComponent() );
        setProperty( form.getProperty() );
        setMethod( form.getMethod() );
    }

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long   id;

    @NotEmpty
    @Pattern ( regexp = "^\\d{5}-\\d{1}$" )
    private String code;

    @NotEmpty
    @Length ( max = 100 )
    private String longName;

    @NotEmpty
    @Length ( max = 50 )
    private String component;

    @NotEmpty
    @Length ( max = 10 )
    private String property;

    @NotEmpty
    @Length ( max = 50 )
    private String method;

    /**
     * Get's the Lab Code's id.
     *
     * @return id
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the Lab Code's id to the given value. All saved LabCodes must have
     * unique ids.
     *
     * @param id
     *            the new id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the Lab Code's LOINC.
     *
     * @return the LOINC
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the LOINC to the given string. Must be in the format "#####-#".
     *
     * @param code
     *            the LOINC
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Gets the Lab Code's long name.
     *
     * @return the long name
     */
    public String getLongName () {
        return longName;
    }

    /**
     * Sets the long name to the given String.
     *
     * @param longName
     *            the long name
     */
    public void setLongName ( final String longName ) {
        this.longName = longName;
    }

    /**
     * Gets the Lab Code's component.
     *
     * @return the component
     */
    public String getComponent () {
        return component;
    }

    /**
     * Sets the component to the given String.
     *
     * @param component
     *            the component
     */
    public void setComponent ( final String component ) {
        this.component = component;
    }

    /**
     * Gets the Lab Code's property.
     *
     * @return the property
     */
    public String getProperty () {
        return property;
    }

    /**
     * Sets the property to the given String.
     *
     * @param property
     *            the property
     */
    public void setProperty ( final String property ) {
        this.property = property;
    }

    /**
     * Gets the Lab Code's method.
     *
     * @return the method
     */
    public String getMethod () {
        return method;
    }

    /**
     * Sets the method to the given String.
     *
     * @param method
     *            the method
     */
    public void setMethod ( final String method ) {
        this.method = method;
    }

    /**
     * Gets a list of lab codes that match the given query.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return the collection of matching lab codes
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LabCode> getWhere ( final List<Criterion> where ) {
        return (List<LabCode>) getWhere( LabCode.class, where );
    }

    /**
     * Returns the lab code whose id matches the given value.
     *
     * @param id
     *            the id to search for
     * @return the matching lab code or null if none is found
     */
    public static LabCode getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Gets the lab code with the code matching the given value. Returns null if
     * none found.
     *
     * @param code
     *            the code to search for
     * @return the matching lab code
     */
    public static LabCode getByCode ( final String code ) {
        try {
            return getWhere( createCriterionAsList( "code", code ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Collects and returns all lab codes in the system
     *
     * @return all saved lab codes
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LabCode> getAll () {
        return (List<LabCode>) DomainObject.getAll( LabCode.class );
    }

}
