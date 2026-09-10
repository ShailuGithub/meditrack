package com.airtribe.meditrack.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Generic in-memory store keyed by each item's id, backing every service
 * (doctors, patients, appointments, bills) instead of each one hand-rolling
 * its own {@code Map}. {@code idExtractor} tells the store how to pull the
 * key out of {@code T} without {@code DataStore} needing to know anything
 * about {@code T} itself.
 *
 * <p>{@code save}/{@code deleteById} are {@code synchronized} so concurrent
 * writers (e.g. two threads booking appointments) can't interleave and
 * corrupt the backing map.
 */
public class DataStore<T> {

    private final Map<String, T> store = new LinkedHashMap<>();
    private final Function<T, String> idExtractor;

    public DataStore(Function<T, String> idExtractor) {
        this.idExtractor = idExtractor;
    }

    public synchronized void save(T item) {
        store.put(idExtractor.apply(item), item);
    }

    public Optional<T> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public synchronized boolean deleteById(String id) {
        return store.remove(id) != null;
    }

    public int count() {
        return store.size();
    }
}
