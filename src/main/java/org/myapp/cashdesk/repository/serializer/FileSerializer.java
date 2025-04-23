package org.myapp.cashdesk.repository.serializer;

public interface FileSerializer<T> {
    String serialize(T entity);
    T parse(String line);
}