package org.myapp.cashdesk.repository.impl.serializer;

public interface FileSerializer<T> {
    String serialize(T entity);

    T parse(String line);
}