@ApplicationModule(
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {
                "shared::SharedDomain",
                "shared::SharedExceptions",
                "ad::AdExceptions",
                "location::LocationApi"
        },
        displayName = "Bootstrap"
)
package com.nextbuy.adhub.bootstrap;

import org.springframework.modulith.ApplicationModule;
