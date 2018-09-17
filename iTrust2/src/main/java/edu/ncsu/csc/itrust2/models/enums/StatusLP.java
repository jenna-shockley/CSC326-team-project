package edu.ncsu.csc.itrust2.models.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing possible statuses of lab procedures
 *
 * @author Noah Trimble (nmtrimbl)
 *
 */
public enum StatusLP {

    /**
     * Not started
     */
    NotStarted ( "Not Started" ),
    /**
     * In progress
     */
    InProgress ( "In Progress" ),
    /**
     * Completed
     */
    Completed ( "Completed" );

    /**
     * Name of this status
     */
    private String name;

    /**
     * Constructor for the Status
     *
     * @param name
     *            Name of the Status to create
     */
    private StatusLP ( final String name ) {
        this.name = name;
    }

    /**
     * Get the Name of the Status
     *
     * @return Name of the Status
     */
    public String getName () {
        return this.name;
    }

    /**
     * Returns a map from field name to value, which is more easily serialized
     * for sending to front-end.
     *
     * @return map from field name to value for each of the fields in this enum
     */
    public Map<String, Object> getInfo () {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put( "id", name() );
        map.put( "name", getName() );
        return map;
    }

    /**
     * Convert the Status to a String
     */
    @Override
    public String toString () {
        return getName();
    }

    /**
     * Parse the String provided into an actual Status enum.
     *
     * @param statusStr
     *            String representation of the Status
     * @return Status enum corresponding to the string, or null if nothing could
     *         be matched
     */
    public static StatusLP parse ( final String statusStr ) {
        for ( final StatusLP status : values() ) {
            if ( status.getName().equals( statusStr ) ) {
                return status;
            }
        }

        return null;
    }

}
