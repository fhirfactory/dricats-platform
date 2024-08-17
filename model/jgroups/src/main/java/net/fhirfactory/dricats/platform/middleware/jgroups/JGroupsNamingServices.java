/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.platform.middleware.jgroups;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.jgroups.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JGroupsNamingServices {
	//
	// Housekeeping
	//
	private static final Logger LOG = LoggerFactory.getLogger(JGroupsNamingServices.class);
	
	//
	// Attributes
	//
	
	private String systemUniqueId;
	
    private static String CHANNEL_NAME_SEPARATOR = "::";
	
    private static int SITE_POSITION_IN_CHANNEL_NAME = 0;
    private static int ZONE_POSITION_IN_CHANNEL_NAME = 1;
    private static int APPLICATION_NAME_POSITION_IN_CHANNEL_NAME = 2;
    private static int FUNCTION_POSITION_IN_CHANNEL_NAME = 3;
    private static int UNIQUE_ID_POSITION_IN_CHANNEL_NAME = 4;
    
    private static int APPLICATION_NAME_POSITION_IN_ENDPOINT_NAME = 0;
    private static int ENDPOINT_FUNCTION_POSITION_IN_ENDPOINT_NAME = 1;
    private static int UNIQUE_ID_POSITION_IN_ENDPOINT_NAME = 2;
    
    private static String DEFAULT_APPLICATION_NAME = "ApplicationNameUnknown";
    private static String DEFAULT_ENDPOINT_FUNCTION_NAME = "EndpointFunctionUnknown";
    private static String DEFAULT_UNIQUE_ID = "0000-0000-0000-0000";
    
    //
    // Constructor(s)
    //
    
    public JGroupsNamingServices() {
    	systemUniqueId = UUID.randomUUID().toString();
    }
    
    //
    // Business Methods
    //
    
    
    public String getChannelNameSeparator() {
        return CHANNEL_NAME_SEPARATOR;
    }
    
    public String getSystemUniqueID() {
    	return(systemUniqueId);
    }
    
    public String buildEndpointName(String applicationName, String endpointFunction, String uniqueID) {
	    StringBuilder endpointNameBuilder = new StringBuilder();
	    // APPLICATION_NAME_POSITION_IN_ENDPOINT_NAME
	    endpointNameBuilder.append(applicationName).append(getChannelNameSeparator());
	    // ENDPOINT_FUNCTION_POSITION_IN_ENDPOINT_NAME
	    endpointNameBuilder.append(endpointFunction).append(getChannelNameSeparator());
	    // UNIQUE_ID_POSITION_IN_ENDPOINT_NAME
	    endpointNameBuilder.append(uniqueID);
	    String endpointName = endpointNameBuilder.toString();
	    return(endpointName);
    }

	public String buildChannelName(String siteName, String zoneName, String systemName, String endpointTypeName, String uniqueId ) {
	    StringBuilder channelNameBuilder = new StringBuilder();
	    // SITE_POSITION_IN_CHANNEL_NAME
	    channelNameBuilder.append(siteName).append(getChannelNameSeparator());
	    // ZONE_POSITION_IN_CHANNEL_NAME
	    channelNameBuilder.append(zoneName).append(getChannelNameSeparator());
	    // APPLICATION_NAME_POSITION_IN_CHANNEL_NAME
	    channelNameBuilder.append(systemName).append(getChannelNameSeparator());
	    // FUNCTION_POSITION_IN_CHANNEL_NAME
	    channelNameBuilder.append(endpointTypeName).append(getChannelNameSeparator());
	    // UNIQUE_ID_POSITION_IN_CHANNEL_NAME
	    channelNameBuilder.append(uniqueId);
		String channelName = channelNameBuilder.toString();
	    return(channelName);
	}
	
	public String getEndpointNameFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String applicationName = getApplicationNameFromChannelName(channelName);
        String functionName = getEndpointFunctionNameFromChannelName(channelName);
        String endpointUniqueID = getEndpointUniqueIDFromChannelName(channelName);
        if(StringUtils.isEmpty(applicationName)) {
        	applicationName = DEFAULT_APPLICATION_NAME;
        }
        if(StringUtils.isEmpty(functionName)) {
        	functionName = DEFAULT_ENDPOINT_FUNCTION_NAME;
        }
        if(StringUtils.isEmpty(endpointUniqueID)) {
        	endpointUniqueID = DEFAULT_UNIQUE_ID;
        }
        String endpointName = buildEndpointName(applicationName, functionName, endpointUniqueID);
        return(endpointName);
    }

    public String getEndpointSiteFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        if(nameSplit.length < SITE_POSITION_IN_CHANNEL_NAME){
            return(null);
        }
        String endpointSite = nameSplit[SITE_POSITION_IN_CHANNEL_NAME];
        return(endpointSite);
    }

    public boolean isEndpointsInSameSiteBasedOnChannelNames(String endpointChannelName1, String endpointChannelName2){
        if(StringUtils.isEmpty(endpointChannelName1) || StringUtils.isEmpty(endpointChannelName2)){
            return(false);
        }
        String endpointSite1 = getEndpointSiteFromChannelName(endpointChannelName1);
        String endpointSite2 = getEndpointSiteFromChannelName(endpointChannelName2);
        if(StringUtils.isEmpty(endpointSite1) || StringUtils.isEmpty(endpointSite2)){
            return(false);
        }
        boolean areSameSite = endpointSite1.contentEquals(endpointSite2);
        return(areSameSite);
    }

    public String getZoneFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        if(nameSplit.length < ZONE_POSITION_IN_CHANNEL_NAME){
            return(null);
        }
        String endpointZone = nameSplit[ZONE_POSITION_IN_CHANNEL_NAME];
        return(endpointZone);
    }

    public boolean isEndpointsInSameZoneBasedOnChannelNames(String endpointChannelName1, String endpointChannelName2){
        if(StringUtils.isEmpty(endpointChannelName1) || StringUtils.isEmpty(endpointChannelName2)){
            return(false);
        }
        String endpointZone1 = getZoneFromChannelName(endpointChannelName1);
        String endpointZone2 = getZoneFromChannelName(endpointChannelName2);
        if(StringUtils.isEmpty(endpointZone1) || StringUtils.isEmpty(endpointZone2)){
            return(false);
        }
        boolean areSameZone = endpointZone1.contentEquals(endpointZone2);
        return(areSameZone);
    }

    public String getApplicationNameFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        if(nameSplit.length < APPLICATION_NAME_POSITION_IN_CHANNEL_NAME){
            return(null);
        }
        String endpointServiceName = nameSplit[APPLICATION_NAME_POSITION_IN_CHANNEL_NAME];
        return(endpointServiceName);
    }

    public boolean isEndpointsDeliveringSameServiceBasedOnChannelNames(String endpointChannelName1, String endpointChannelName2){
        if(StringUtils.isEmpty(endpointChannelName1) || StringUtils.isEmpty(endpointChannelName2)){
            return(false);
        }
        String endpointServiceName1 = getApplicationNameFromChannelName(endpointChannelName1);
        String endpointServiceName2 = getApplicationNameFromChannelName(endpointChannelName2);
        if(StringUtils.isEmpty(endpointServiceName1) || StringUtils.isEmpty(endpointServiceName2)){
            return(false);
        }
        boolean areSameService = endpointServiceName1.contentEquals(endpointServiceName2);
        return(areSameService);
    }

    public String getEndpointFunctionNameFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        if(nameSplit.length < FUNCTION_POSITION_IN_CHANNEL_NAME){
            getLogger().warn(".getEndpointFunctionFromChannelName(): channelName is not properly formed, value->{}", channelName);
            return(null);
        }
        String endpointFunctionName = nameSplit[FUNCTION_POSITION_IN_CHANNEL_NAME];
        return(endpointFunctionName);
    }

    public boolean isEndpointsSupportingSameFunctionBaseOnChannelNames(String endpointChannelName1, String endpointChannelName2){
        if(StringUtils.isEmpty(endpointChannelName1) || StringUtils.isEmpty(endpointChannelName2)){
            return(false);
        }
        String endpointFunctionName1 = getEndpointFunctionNameFromChannelName(endpointChannelName1);
        String endpointFunctionName2 = getEndpointFunctionNameFromChannelName(endpointChannelName2);
        if(StringUtils.isEmpty(endpointFunctionName1) || StringUtils.isEmpty(endpointFunctionName2)){
            return(false);
        }
        boolean areSameFunction = endpointFunctionName1.contentEquals(endpointFunctionName2);
        return(areSameFunction);
    }

    public String getEndpointUniqueIDFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        if(nameSplit.length < UNIQUE_ID_POSITION_IN_CHANNEL_NAME){
            return(null);
        }
        String endpointUniqueID = nameSplit[UNIQUE_ID_POSITION_IN_CHANNEL_NAME];
        return(endpointUniqueID);
    }

    public String getApplicationNameFromEndpointName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        if(nameSplit.length < APPLICATION_NAME_POSITION_IN_ENDPOINT_NAME){
            return(null);
        }
        String endpointFunctionName = nameSplit[APPLICATION_NAME_POSITION_IN_ENDPOINT_NAME];
        return(endpointFunctionName);
    }

    public boolean isEndpointsSupportingSameServiceBasedOnEndpointNames(String endpointName1, String endpointName2) {
        if (StringUtils.isEmpty(endpointName1) || StringUtils.isEmpty(endpointName2)) {
            return (false);
        }
        String endpointServiceName1 = getApplicationNameFromEndpointName(endpointName1);
        String endpointServiceName2 = getApplicationNameFromEndpointName(endpointName2);
        if (StringUtils.isEmpty(endpointServiceName1) || StringUtils.isEmpty(endpointServiceName2)) {
            return (false);
        }
        boolean areSameService = endpointServiceName1.contentEquals(endpointServiceName2);
        return (areSameService);
    }

    public String getEndpointUniqueIDFromEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String[] nameSplit = endpointName.split(getChannelNameSeparator());
        if(nameSplit.length < UNIQUE_ID_POSITION_IN_ENDPOINT_NAME){
            return(null);
        }
        String endpointScopeName = nameSplit[UNIQUE_ID_POSITION_IN_ENDPOINT_NAME];
        return(endpointScopeName);
    }


	//
	// Utility Methods
	//
	
	protected Logger getLogger() {
		return(LOG);
	}
}
