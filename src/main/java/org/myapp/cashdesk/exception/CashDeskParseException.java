package org.myapp.cashdesk.exception;

public class CashDeskParseException extends RuntimeException {
  public CashDeskParseException(String message) {
        super(message);
    }

  public CashDeskParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
