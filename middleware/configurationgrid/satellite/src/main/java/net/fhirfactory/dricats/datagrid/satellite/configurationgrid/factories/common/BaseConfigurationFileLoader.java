/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.datagrid.satellite.configurationgrid.factories.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.fhirfactory.dricats.model.configuration.configurationfile.archetypes.BaseSubsystemConfigurationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract public class BaseConfigurationFileLoader {
    //
     // Housekeeping
    //
    private final Logger LOG = LoggerFactory.getLogger(BaseConfigurationFileLoader.class);



    //
     // Abstract Methods
    //

    abstract protected String specifyPropertyFileName();
    abstract protected Class specifyPropertyFileClass();

    //
     // Getters and Setters
    //

    protected Logger getLogger(){
        return LOG;
    }


    //
     // Business Methods
    //

    public BaseSubsystemConfigurationObject readPropertyFile(){
        String propertyFileName = specifyPropertyFileName();
        Class propertyFileClass = specifyPropertyFileClass();
        getLogger().warn(".readPropertyFile(): Entry, propertyFileName->{}", propertyFileName);
        try {
            getLogger().trace(".readPropertyFile(): Establish YAML ObjectMapper");
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            yamlMapper.findAndRegisterModules();
            yamlMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
            getLogger().warn(".readPropertyFile(): [Openning Configuration File] Start");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//            InputStream propertyYAMLFile = classLoader.getResourceAsStream("./"+propertyFileName);
            Path path = Paths.get(propertyFileName);
            File file = path.toFile();
            getLogger().warn(".readPropertyFile(): [Openning Configuration File] End");
            getLogger().warn(".readPropertyFile(): [Importing Configuration File] Start");
            BaseSubsystemConfigurationObject propertyFile = (BaseSubsystemConfigurationObject) yamlMapper.readValue(file, propertyFileClass);
            getLogger().warn(".readPropertyFile(): [Read YAML Configuration File] Finish");
            getLogger().debug(".loadPropertyFile(): Exit, file loaded, propertyFile->{}", propertyFile);
            return(propertyFile);
        } catch(FileNotFoundException noFile){
            getLogger().error(".loadPropertyFile(): Configuration File->{} is not found, error->{}", propertyFileName, noFile.getMessage());
        } catch(IOException ioError){
            getLogger().error(".loadPropertyFile(): Configuration File->{} could not be loaded, error->{}", propertyFileName, ioError.getMessage());
        }
        getLogger().debug(".loadPropertyFile(): failed to load file");
        return(null);
    }
}
