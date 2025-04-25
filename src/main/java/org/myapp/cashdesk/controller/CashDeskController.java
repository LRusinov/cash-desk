package org.myapp.cashdesk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.myapp.cashdesk.dto.response.CashierHistoryDTO;
import org.myapp.cashdesk.service.CashDeskBalanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
