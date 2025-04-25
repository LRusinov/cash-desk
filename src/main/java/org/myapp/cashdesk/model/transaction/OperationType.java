package org.myapp.cashdesk.model.transaction;

/**
 * Contains all Cash Desk possible operation types
 */
public enum OperationType {
    DEPOSIT, WITHDRAWAL;

    public static OperationType fromStringAlternative(String value) {
        for (OperationType operationType : OperationType.values()) {
            if (operationType.name().equalsIgnoreCase(value)) {
                return operationType;
            }
        }
        throw new IllegalArgumentException("Invalid operation type: " + value);
    }
}
