package org.myapp.cashdesk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.myapp.cashdesk.dto.response.CashierHistoryDTO;
import org.myapp.cashdesk.service.CashDeskBalanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CashDeskController {

    private final CashDeskBalanceService cashDeskBalanceService;

    @Operation(
            summary = "Retrieves all balances by optionally given cashier name and date range period.",
            description = "Expects some or none of the following filter options: cashierName, fromDate, toDate." +
                    " Depending on the given filter options, all cashier balances are returned."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns either empty list if there are no matching results for the giver filter, " +
                            "list of the found balances or Error object if some of the validations fails.",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    })
    @GetMapping("/cash-balance")
    public ResponseEntity<List<CashierHistoryDTO>> getCashierBalanceByNameAndPeriod(@RequestParam(required = false)
                                                               @Size(min = 2, message = "Cashier name must be at least 2 characters long") final String cashierName,

                                                                                    @RequestParam(required = false)
                                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateFrom,

                                                                                    @RequestParam(required = false)
                                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateTo) {
        return new ResponseEntity<>(cashDeskBalanceService.getCashierBalanceByNameAndPeriod(cashierName, dateFrom, dateTo), HttpStatus.OK);
    }
}
