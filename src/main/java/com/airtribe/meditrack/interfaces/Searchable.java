package com.airtribe.meditrack.interfaces;

import java.util.List;

/**
 * Implemented by services that expose a free-text search over the entities
 * they manage. {@code T} is the entity type being searched.
 */
public interface Searchable<T> {

    List<T> search(String query);

    /**
     * Default method: services get "does anything match?" for free from
     * {@link #search(String)} unless they choose to override it.
     */
    default boolean exists(String query) {
        return !search(query).isEmpty();
    }
}
