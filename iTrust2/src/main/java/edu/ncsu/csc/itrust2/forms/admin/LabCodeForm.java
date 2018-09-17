package edu.ncsu.csc.itrust2.forms.admin;

import edu.ncsu.csc.itrust2.models.persistent.LabCode;

/**
 * A form for REST API communication. Contains fields for constructing LabCode
 * objects.
 *
 * @author Cody Nesbitt
 */
public class LabCodeForm {

    private Long   id;
    private String code;
    private String longName;
    private String component;
    private String property;
    private String method;

    /**
     * Empty constructor for filling in fields without a LabCode object.
     */
    public LabCodeForm () {
    }

    /**
     * Constructs a new form with information from the given lab code.
     *
     * @param labCode
     *            the labCode object
     */
    public LabCodeForm ( final LabCode labCode ) {
        setId( labCode.getId() );
        setCode( labCode.getCode() );
        setLongName( labCode.getLongName() );
        setComponent( labCode.getComponent() );
        setProperty( labCode.getProperty() );
        setMethod( labCode.getMethod() );
    }

    /**
     * Gets the Lab Code's id.
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the id to the given Long.
     *
     * @param id
     *            the id
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
}
