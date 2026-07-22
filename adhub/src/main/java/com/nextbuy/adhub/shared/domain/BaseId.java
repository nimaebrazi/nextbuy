package com.nextbuy.adhub.shared.domain;

import java.util.Objects;

/**
 * Type-safe identifier for a domain entity or external reference.
 *
 * @param <T> phantom type marker — use the aggregate class (e.g. {@code Id<Ad>})
 *            or an empty marker for cross-context references (e.g. {@code Id<Seller>})
 */
public abstract class BaseId<T> {

    private final T value;

    protected BaseId() {
        this.value = null;
    }

    protected BaseId(T id) {
        this.value = id;
    }

    public T value() {
        return value;
    }

    /**
     * True when the entity has been persisted and received a database ID.
     */
    public boolean isAssigned() {
        return value != null;
    }

    /**
     * Returns the ID or throws if this is an unassigned (pre-persist) ID.
     */
    public T valueOrThrow() {
        if (value == null) {
            throw new IllegalStateException("Id is not yet assigned");
        }
        return value;
    }

    /**
     * Returns the raw Long, which may be null for unassigned or optional references.
     */
    public T valueOrNull() {
        return value;
    }

    @Override
    /**
     * Unassigned ids (null value) are never equal to anything but themselves —
     * two entities awaiting persistence must not compare as the same identity.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseId<?> baseId = (BaseId<?>) o;
        return value != null && value.equals(baseId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return isAssigned() ? String.valueOf(value) : "Id[unassigned]";
    }

}
