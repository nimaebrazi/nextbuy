@ApplicationModule(
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {"shared::SharedDomain", "shared::SharedExceptions"},
        displayName = "Location"
)
package com.nextbuy.adhub.location;

import org.springframework.modulith.ApplicationModule;
