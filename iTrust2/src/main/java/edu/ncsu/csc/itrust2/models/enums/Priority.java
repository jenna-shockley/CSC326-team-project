package edu.ncsu.csc.itrust2.models.enums;

/**
 * Enum representing possible priorities of lab procedures
 *
 * @author Noah Trimble (nmtrimbl)
 *
 */
public enum Priority {

    /**
     * Highest priority
     */
    VeryHigh ( 1 ),
    /**
     * High Priority
     */
    High ( 2 ),
    /**
     * Medium Priority
     */
    Medium ( 3 ),
    /**
     * Low Priority
     */
    Low ( 4 )

    ;

    /**
     * Priority
     */
    private int priorityNumber;

    /**
     * Create a Priority from the priority number.
     *
     * @param n
     *            Priority number
     */
    private Priority ( final int n ) {
        this.priorityNumber = n;
    }

    /**
     * Gets the priority number
     *
     * @return priority number
     */
    public int getPriorityNumber () {
        return priorityNumber;
    }

    /**
     * Parse the integer provided into an actual Priority enum.
     *
     * @param priorityNumber
     *            int representation of the priority
     * @return Priority enum corresponding to the integer, or null if nothing
     *         could be matched
     */
    public static Priority parse ( final int priorityNumber ) {
        for ( final Priority priority : values() ) {
            if ( priority.getPriorityNumber() == priorityNumber ) {
                return priority;
            }
        }

        return null;
    }

}
