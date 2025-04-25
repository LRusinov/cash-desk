package org.myapp.cashdesk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myapp.cashdesk.dto.request.CashOperationRequestDTO;
import org.myapp.cashdesk.dto.response.TransactionDTO;
import org.myapp.cashdesk.service.CashDeskOperationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CashOperationController {

    private final CashDeskOperationService cashDeskOperationService;


    @Operation(
            summary = "Executes Cash Desk operation and creates transaction history for it.",
            description = "Executes the given transaction and modifies the balance of hte cashier who process the operation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns either object containing the newly created transaction information or error object.",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @PostMapping("/cash-operation")
    public TransactionDTO cashOperation(@RequestBody @Valid final CashOperationRequestDTO request) {
        return cashDeskOperationService.processOperation(request);
    }
}
