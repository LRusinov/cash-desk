package org.myapp.cashdesk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class Cashier {
    private long id;
    private String name;
    private Map<Currency, Balance> balance;
}
