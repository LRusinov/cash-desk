package org.myapp.cashdesk.dto;

import java.util.List;



public record CashDeskErrorValidationResponseDTO(int httpStatus, List<String> validationErrorMessages){}
