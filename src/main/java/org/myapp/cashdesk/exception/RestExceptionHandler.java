package org.myapp.cashdesk.exception;

import org.myapp.cashdesk.dto.response.CashDeskErrorValidationResponseDTO;
import org.myapp.cashdesk.dto.CashDeskErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static java.util.Objects.nonNull;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CashDeskErrorValidationResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        List<String> validationErrorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> nonNull(error.getDefaultMessage()) ? error.getDefaultMessage() : "")
                .toList();

        return ResponseEntity.badRequest()
                .body(new CashDeskErrorValidationResponseDTO(HttpStatus.BAD_REQUEST.value(), validationErrorMessages));
    }

    @ExceptionHandler(CashDeskValidationException.class)
    public ResponseEntity<CashDeskErrorDTO> handleCashDeskValidation(CashDeskValidationException ex) {
        return ResponseEntity.badRequest().body(new CashDeskErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler({CashDeskParseException.class, CashDeskSerializationException.class})
    public ResponseEntity<CashDeskErrorDTO> handleParsAndSerialization(Exception ex) {
        return ResponseEntity.internalServerError().body(new CashDeskErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CashDeskErrorDTO> handleEntityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(new CashDeskErrorDTO(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<CashDeskErrorDTO> handleInsufficientFunds(InsufficientFundsException ex) {
        return ResponseEntity.badRequest().body(new CashDeskErrorDTO(ex.getMessage()));
    }
}
