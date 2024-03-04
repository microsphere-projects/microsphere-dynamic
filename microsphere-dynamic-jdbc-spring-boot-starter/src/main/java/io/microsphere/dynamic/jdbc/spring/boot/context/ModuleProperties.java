package io.microsphere.dynamic.jdbc.spring.boot.context;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Dynamic JDBC Module {@link ConfigurationProperties}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class ModuleProperties {

    private Dynamic dynamic = new Dynamic();

    private final AutoConfiguration autoConfiguration = new AutoConfiguration();

    private Set<String> bannedModules = new LinkedHashSet<>();

    public Dynamic getDynamic() {
        return dynamic;
    }

    public void setDynamic(Dynamic dynamic) {
        this.dynamic = dynamic;
    }

    public Set<String> getBannedModules() {
        return bannedModules;
    }

    public void setBannedModules(Set<String> bannedModules) {
        this.bannedModules = bannedModules;
    }

    public static class Dynamic {

        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class AutoConfiguration {

        private Set<String> basePackages = new LinkedHashSet<>();

        public Set<String> getBasePackages() {
            return basePackages;
        }

        public void setBasePackages(Set<String> basePackages) {
            this.basePackages = basePackages;
        }
    }

}
