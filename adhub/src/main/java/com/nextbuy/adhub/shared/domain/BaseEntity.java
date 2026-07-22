package com.nextbuy.adhub.shared.domain;

import java.util.Objects;

public abstract class BaseEntity<ID> {
    private ID id;

    public ID getId() {
        return id;
    }

    protected void assignIdentity(ID id) {
        if (this.id != null) {
            throw new IllegalStateException("Id is already assigned");
        }
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) obj;
        return id != null && id.equals(that.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
