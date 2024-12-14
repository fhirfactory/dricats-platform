/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.platform.configuration;

import net.fhirfactory.dricats.internals.model.core.configuration.ConfigurationMapEntry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class LocalConfigurationServer {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(LocalConfigurationServer.class);

    //
    // Attributes
    //
    private Map<String, ConfigurationMapEntry> configurationMap;

    //
    // Constructor(s)
    //

    public LocalConfigurationServer(){
        getLogger().debug(".constructor(): Entry");
        this.configurationMap = new HashMap<>();
        getLogger().debug(".constructor(): Exit");
    }

    //
    // Getters and Setters
    //

    protected Map<String, ConfigurationMapEntry> getConfigurationMap() {
        return configurationMap;
    }

    public Logger getLogger() {
        return LOG;
    }

    //
    // Business Methods
    //

    public void registerConfigurationObject(String configurationObjectName, Object configurationObject) {
        getLogger().debug(".registryConfigurationObject(): Entry, configurationObjectName->{}, configurationObject->{}", configurationObjectName, configurationObject);
        if(StringUtils.isEmpty(configurationObjectName)) {
            getLogger().debug(".registryConfigurationObject(): Exit, configurationObjectName is empty");
            return;
        }
        if(configurationObject == null) {
            getLogger().debug(".registryConfigurationObject(): Exit, configurationObject is null");
            return;
        }
        if(!getConfigurationMap().containsKey(configurationObjectName)) {
            getLogger().trace(".registryConfigurationObject(): Replacing Entry -> {}", configurationObjectName);
            ConfigurationMapEntry configurationMapEntry = getConfigurationMap().get(configurationObjectName);
            configurationMapEntry.setConfigurationObject(configurationObject);
            configurationMapEntry.setConfigurationObjectType(configurationObject.getClass());
            configurationMapEntry.setRegistrationChangeInstant(LocalDateTime.now());
        } else {
            getLogger().trace(".registryConfigurationObject(): Inserting Entry -> {}", configurationObjectName);
            ConfigurationMapEntry configurationMapEntry = new ConfigurationMapEntry();
            configurationMapEntry.setConfigurationObject(configurationObject);
            configurationMapEntry.setConfigurationObjectType(configurationObject.getClass());
            configurationMapEntry.setRegistrationChangeInstant(LocalDateTime.now());
            configurationMapEntry.setRegistrationInstant(LocalDateTime.now());
            configurationMapEntry.setConfigurationName(configurationObjectName);
            getConfigurationMap().put(configurationObjectName, configurationMapEntry);
        }
        getLogger().debug(".registryConfigurationObject(): Exit, Entry inserted/updated into Map");
    }

    public Object getConfigurationObject(String configurationObjectName) {
        getLogger().debug(".registryConfigurationObject(): Entry, configurationObjectName->{}", configurationObjectName);
        if(StringUtils.isEmpty(configurationObjectName)) {
            getLogger().debug(".registryConfigurationObject(): Exit, configurationObjectName is empty");
            return null;
        }
        ConfigurationMapEntry entry = getConfigurationMap().get(configurationObjectName);
        if(entry == null) {
            getLogger().debug(".registryConfigurationObject(): Exit, configurationObjectName not found");
            return null;
        }
        getLogger().debug(".registryConfigurationObject(): Exit, configurationObject found!");
        return(entry.getConfigurationObject());
    }
}
