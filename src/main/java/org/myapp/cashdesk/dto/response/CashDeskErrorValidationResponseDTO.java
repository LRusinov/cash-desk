package org.myapp.cashdesk.dto.response;

import java.util.List;

/**
 * Contains information for the failed validations
 *
 * @param httpStatus response status
 * @param validationErrorMessages list of the error validation messages
 */
public record CashDeskErrorValidationResponseDTO(int httpStatus, List<String> validationErrorMessages){}
