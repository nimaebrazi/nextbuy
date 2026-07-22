package com.nextbuy.adhub.ad.domain.exception;

import com.nextbuy.adhub.shared.exception.DomainException;

public class AdDomainException extends DomainException {
    private AdDomainException(String message) {
        super(message);
    }

    public static class IdIsAlreadyAssigned extends AdDomainException {
        public IdIsAlreadyAssigned() {
            super("Id is already assigned");
        }
    }

    public static class AssignedIdMustBeAssigned extends AdDomainException {
        public AssignedIdMustBeAssigned() {
            super("Assigned id must be assigned");
        }
    }

    public static class InvalidStatus extends AdDomainException {
        public InvalidStatus(String oldStatus, String newStatus) {
            super("Ad status: %s is not valid for status: %s!".formatted(oldStatus, newStatus));
        }
    }

    public static class AdIsNotInCorrectStateForInitialization extends AdDomainException {
        public AdIsNotInCorrectStateForInitialization() {
            super("Ad is not in correct state for initialization!");
        }
    }

    public static class IdMustBeAssigned extends AdDomainException {
        public IdMustBeAssigned(String field) {
            super("%s must be assigned".formatted(field));
        }
    }

    public static class FieldIsRequired extends AdDomainException {
        public FieldIsRequired(String field) {
            super("Ad '%s' field is required!".formatted(field));
        }
    }

    public static class ExceededLimitLength extends AdDomainException {
        public ExceededLimitLength(String field, String limit) {
            super("Field %s exceeds maximum length. Limit is %s!".formatted(field, limit));
        }
    }

    public static class DeletedAdModificationNotAllowed extends AdDomainException {
        public DeletedAdModificationNotAllowed() {
            super("Deleted ad cannot be modified.");
        }
    }
}
