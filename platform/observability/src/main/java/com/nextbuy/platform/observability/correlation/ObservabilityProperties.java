package com.nextbuy.platform.observability.correlation;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nextbuy.observability")
public class ObservabilityProperties {

    private final Correlation correlation = new Correlation();

    public Correlation getCorrelation() {
        return correlation;
    }

    public static class Correlation {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
