package com.nextbuy.adhub.ad.domain.exception;

import com.nextbuy.adhub.shared.exception.DomainException;

public class AdDomainException extends DomainException {
    private AdDomainException(String message, String messageKey, Object... args) {
        super(message, messageKey, args);
    }

    public static class IdIsAlreadyAssigned extends AdDomainException {
        public IdIsAlreadyAssigned() {
            super("Id is already assigned", "ad.error.id_already_assigned");
        }
    }

    public static class AssignedIdMustBeAssigned extends AdDomainException {
        public AssignedIdMustBeAssigned() {
            super("Assigned id must be assigned", "ad.error.assigned_id_must_be_assigned");
        }
    }

    public static class InvalidStatus extends AdDomainException {
        public InvalidStatus(String oldStatus, String newStatus) {
            super(
                    "Ad status: %s is not valid for status: %s!".formatted(oldStatus, newStatus),
                    "ad.error.invalid_status"
            );
        }
    }

    public static class IdMustBeAssigned extends AdDomainException {
        public IdMustBeAssigned(String field) {
            super("%s must be assigned".formatted(field), "ad.error.id_must_be_assigned", field);
        }
    }

    public static class FieldIsRequired extends AdDomainException {
        public FieldIsRequired(String field) {
            super("Ad '%s' field is required!".formatted(field), "ad.error.field_required", field);
        }
    }

    public static class ExceededLimitLength extends AdDomainException {
        public ExceededLimitLength(String field, String limit) {
            super(
                    "Field %s exceeds maximum length. Limit is %s!".formatted(field, limit),
                    "ad.error.exceeded_limit",
                    field,
                    limit
            );
        }
    }

    public static class DeletedAdModificationNotAllowed extends AdDomainException {
        public DeletedAdModificationNotAllowed() {
            super("Deleted ad cannot be modified.", "ad.error.deleted");
        }
    }
}
