package com.github.imdmk.spenttime.user;

/**
 * Represents the possible outcomes of a user deletion operation.
 */
public enum UserDeleteStatus {

    /**
     * The user was found and successfully deleted.
     */
    DELETED,

    /**
     * The user was not found in the data source.
     */
    NOT_FOUND,

    /**
     * The deletion attempt failed due to an unexpected error or database issue.
     */
    FAILED
}
