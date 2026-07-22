@ApplicationModule(
        type = ApplicationModule.Type.CLOSED,
        allowedDependencies = {"shared::SharedDomain", "shared::SharedExceptions"},
        displayName = "Category"
)
package com.nextbuy.adhub.category;

import org.springframework.modulith.ApplicationModule;
