package org.myapp.cashdesk.repository.serializer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myapp.cashdesk.model.Cashier;
import org.myapp.cashdesk.model.Transaction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializerFactory {
    @Getter
    private static final FileSerializer<Cashier> cashierSerializer = new CashierSerializer();
    @Getter
    private static final FileSerializer<Transaction> transactionSerializer = new TransactionSerializer();
}