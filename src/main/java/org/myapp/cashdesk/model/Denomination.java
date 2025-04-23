package org.myapp.cashdesk.model;

import java.math.BigDecimal;

public interface Denomination {
    BigDecimal getValue();
    Currency getCurrency();
}