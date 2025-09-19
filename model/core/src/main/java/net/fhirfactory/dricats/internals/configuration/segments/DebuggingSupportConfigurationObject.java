/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.configuration.segments;

import java.io.Serial;
import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class DebuggingSupportConfigurationObject implements Serializable{
	//
	// Housekeeping
	//
	@Serial
	private static final long serialVersionUID = -7896252672561141923L;

	//
	// Attributes
	//	
    private String wildflyLogLevel;
    private String wildflyLogEnable;
    private String pegacornLogLevel;
    private String pegacornLogCategory;
    private String aetherLogLevel;
    private String aetherLogCategory;
    private String javaxNetDebug;

    //
    // Constructor(s)
    //
    public DebuggingSupportConfigurationObject(){
        super();
    }
    
    //
    // Bean Methods
    //
    public String getWildflyLogLevel() {
        return wildflyLogLevel;
    }

    public void setWildflyLogLevel(String wildflyLogLevel) {
        this.wildflyLogLevel = wildflyLogLevel;
    }

    public String getWildflyLogEnable() {
        return wildflyLogEnable;
    }

    public void setWildflyLogEnable(String wildflyLogEnable) {
        this.wildflyLogEnable = wildflyLogEnable;
    }

    public String getPegacornLogLevel() {
        return pegacornLogLevel;
    }

    public void setPegacornLogLevel(String pegacornLogLevel) {
        this.pegacornLogLevel = pegacornLogLevel;
    }

    public String getPegacornLogCategory() {
        return pegacornLogCategory;
    }

    public void setPegacornLogCategory(String pegacornLogCategory) {
        this.pegacornLogCategory = pegacornLogCategory;
    }

    public String getAetherLogLevel() {
        return aetherLogLevel;
    }

    public void setAetherLogLevel(String aetherLogLevel) {
        this.aetherLogLevel = aetherLogLevel;
    }

    public String getAetherLogCategory() {
        return aetherLogCategory;
    }

    public void setAetherLogCategory(String aetherLogCategory) {
        this.aetherLogCategory = aetherLogCategory;
    }

    public String getJavaxNetDebug() {
        return javaxNetDebug;
    }

    public void setJavaxNetDebug(String javaxNetDebug) {
        this.javaxNetDebug = javaxNetDebug;
    }
    
    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return "DebuggingSupportConfigurationObject{" +
                "wildflyLogLevel=" + wildflyLogLevel +
                ", wildflyLogEnable=" + wildflyLogEnable +
                ", pegacornLogLevel=" + pegacornLogLevel +
                ", pegacornLogCategory=" + pegacornLogCategory +
                ", aetherLogLevel=" + aetherLogLevel +
                ", aetherLogCategory=" + aetherLogCategory +
                ", javaxNetDebug=" + javaxNetDebug +
                '}';
    }

    //
    // Has Methods
    //
    
    public boolean hasWildflyLogLevel(){
        boolean has = !(StringUtils.isEmpty(this.wildflyLogLevel));
        return(has);
    }

    public boolean hasWildflyLogEnable(){
        boolean has = !(StringUtils.isEmpty(this.wildflyLogEnable));
        return(has);
    }

    public boolean hasPegacornLogLevel(){
        boolean has = !(StringUtils.isEmpty(this.pegacornLogLevel));
        return(has);
    }

    public boolean hasPegacornLogCategory(){
        boolean has = !(StringUtils.isEmpty(this.pegacornLogCategory));
        return(has);
    }

    public boolean hasAetherLogLevel(){
        boolean has = !(StringUtils.isEmpty(this.aetherLogLevel));
        return(has);
    }

    public boolean hasAetherLogCategory(){
        boolean has = !(StringUtils.isEmpty(this.aetherLogCategory));
        return(has);
    }

    public boolean hasJavaxNetDebug(){
        boolean has = !(StringUtils.isEmpty(this.javaxNetDebug));
        return(has);
    }
    
    //
    // Merge Methods
    //

    public void mergeOverrides( DebuggingSupportConfigurationObject override ){
        if(override.hasWildflyLogLevel()){
            this.setWildflyLogLevel(override.getWildflyLogLevel());
        }
        if(override.hasWildflyLogEnable()){
            this.setWildflyLogEnable(override.getWildflyLogEnable());
        }
        if(override.hasPegacornLogLevel()){
            this.setPegacornLogCategory(override.getPegacornLogLevel());
        }
        if(override.hasPegacornLogCategory()){
            this.setPegacornLogCategory(override.getPegacornLogCategory());
        }
        if(override.hasAetherLogLevel()){
            this.setAetherLogLevel(override.getWildflyLogLevel());
        }
        if (override.hasAetherLogCategory()) {
            this.setAetherLogCategory(override.getAetherLogCategory());
        }
        if(override.hasJavaxNetDebug()){
            this.setJavaxNetDebug(override.getJavaxNetDebug());
        }
    }

}
