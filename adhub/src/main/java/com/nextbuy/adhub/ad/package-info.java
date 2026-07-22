/**
 * Ad bounded context — listing lifecycle, persistence, and API.
 *
 * <p>Status transitions and domain operations are documented in
 * {@code LIFECYCLE.md} in this package.
 */
@ApplicationModule(
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared::SharedDomain",
                "shared::SharedExceptions",
                "category::CategoryApi",
                "location::LocationApi"
        },
        displayName = "Ad"
)
package com.nextbuy.adhub.ad;

import org.springframework.modulith.ApplicationModule;
