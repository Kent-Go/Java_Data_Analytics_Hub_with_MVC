/*

 * InvalidNegativeIntegerException.java
 * 
 * Version: 1.0
 *
 * Date: 01/10/2023
 * 
 * © 2023 Go Chee Kin.
 * 
 * All rights reserved.
 */

package analytics.model.exceptions;

/**
 * 
 * The InvalidNegativeIntegerException class is a user-defined exception for
 * negative integer.
 */
public class InvalidNegativeIntegerException extends Exception {
    public InvalidNegativeIntegerException(int integer, String inputField) {
	super(String.format("Invalid %s input. %d is negative. Response cannot be negative. Please try again!", inputField, integer));
    }
}
