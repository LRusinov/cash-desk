package org.myapp.cashdesk.model.denomination;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DenominationRegistry {
    private static final Map<Currency, Class<? extends Denomination>> REGISTRY = new ConcurrentHashMap<>();

    public static void register(final Currency currency, final Class<? extends Denomination> denominationClass) {
        if (isNull(currency) || isNull(denominationClass)) {
            throw new IllegalArgumentException("Currency and denomination class cannot be null");
        }
        REGISTRY.put(currency, denominationClass);
    }


    public static Class<? extends Denomination> getDenominationClass(final Currency currency) {
        Class<? extends Denomination> cls = REGISTRY.get(currency);
        if (isNull(cls)) {
            throw new IllegalArgumentException("No denomination registered for currency: " + currency);
        }
        return cls;
    }

    public static boolean supportsCurrency(final Currency currency) {
        return REGISTRY.containsKey(currency);
    }
}