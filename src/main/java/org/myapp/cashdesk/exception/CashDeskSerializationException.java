package org.myapp.cashdesk.exception;

public class CashDeskSerializationException extends RuntimeException {
    public CashDeskSerializationException(String message) {
        super(message);
    }
  public CashDeskSerializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
