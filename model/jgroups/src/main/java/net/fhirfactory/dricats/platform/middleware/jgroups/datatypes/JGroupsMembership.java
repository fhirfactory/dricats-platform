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
package net.fhirfactory.dricats.platform.middleware.jgroups.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.jgroups.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGroupsMembership {
	//
	// Housekeeping
	//
	private static final Logger LOG = LoggerFactory.getLogger(JGroupsMembership.class);
	
	//
	// Attributes
	//
    private ArrayList<Address> previousMembership;
    private ArrayList<Address> currentMembership;
    private Object membershipLock;
    
    //
    // Constructor(s)
    //
    
    public JGroupsMembership() {
    	this.previousMembership = new ArrayList<>();
    	this.currentMembership = new ArrayList<>();
    	this.membershipLock = new Object();
    }
    
    //
    // Business Methods
    //
    
    public List<JGroupsNetworkAddress> getMembershipAdditions(){
        List<JGroupsNetworkAddress> additions = new ArrayList<>();
        if(getCurrentMembership() == null) {
        	return(additions);
        }
        synchronized(getMembershipLock()){
	        for(Address newListElement: getCurrentMembership()){
	            if(getPreviousMembership().contains(newListElement)){
	                // do nothing
	            } else {
	            	JGroupsNetworkAddress currentAdapterAddress = new JGroupsNetworkAddress();
	            	currentAdapterAddress.setAddressName(newListElement.toString());
	            	currentAdapterAddress.setJGroupsAddress(newListElement);
	                additions.add(currentAdapterAddress);
	            }
	        }
        }
        return(additions);
    }
    
    public List<JGroupsNetworkAddress> getMembershipRemovals(){
        List<JGroupsNetworkAddress> removals = new ArrayList<>();
        synchronized(getMembershipLock()) {
	        for(Address oldListElement: getPreviousMembership()){
	            if(getCurrentMembership().contains(oldListElement)){
	                // no nothing
	            } else {
	            	JGroupsNetworkAddress currentAdapterAddress = new JGroupsNetworkAddress();
	                currentAdapterAddress.setAddressName(oldListElement.toString());
	                currentAdapterAddress.setJGroupsAddress(oldListElement);
	                removals.add(currentAdapterAddress);
	            }
	        }
        }
        return(removals);
    }
    
    public void updateCurrentMembership(List<Address> newCurrentMembershipList) {
    	synchronized(getMembershipLock()){
	    	getPreviousMembership().clear();
	    	getPreviousMembership().addAll(getCurrentMembership());
			getCurrentMembership().clear();
	    	if(newCurrentMembershipList != null) {
	    		getCurrentMembership().addAll(newCurrentMembershipList);
	    	}
    	}
    }
    
    //
    // Bean Methods
    //

    /**
     * @return the Membership Lock
     */
    
    public Object getMembershipLock() {
    	return(this.membershipLock);
    }
    
	/**
	 * @return the previousMembership
	 */
	public ArrayList<Address> getPreviousMembership() {
		return previousMembership;
	}



	/**
	 * @param previousMembership the previousMembership to set
	 */
	public void setPreviousMembership(ArrayList<Address> previousScannedMembership) {
		this.previousMembership = previousScannedMembership;
	}



	/**
	 * @return the currentMembership
	 */
	public ArrayList<Address> getCurrentMembership() {
		return currentMembership;
	}



	/**
	 * @param currentMembership the currentMembership to set
	 */
	public void setCurrentMembership(ArrayList<Address> currentScannedMembership) {
		this.currentMembership = currentScannedMembership;
	}
    
	//
	// Utility Methods
	//
	
	protected Logger getLogger() {
		return(LOG);
	}



}
