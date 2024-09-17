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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
    private LinkedList<JGroupsNetworkAddress> unprocessedMembershipAdditions;
    private LinkedList<JGroupsNetworkAddress> unprocessedMembershipRemovals;
    private Object membershipLock;

    //
    // Constructor(s)
    //
    public JGroupsMembership() {
        this.previousMembership = new ArrayList<>();
        this.currentMembership = new ArrayList<>();
        unprocessedMembershipAdditions = new LinkedList<>();
        unprocessedMembershipRemovals = new LinkedList<>();
        this.membershipLock = new Object();
    }

    //
    // Business Methods
    //
    public void updateUnprocessedMembershipAdditionsList() {
        List<JGroupsNetworkAddress> currentAdditionsList = getMembershipAdditions();
        if (!currentAdditionsList.isEmpty()) {
            synchronized (getMembershipLock()) {
                for (JGroupsNetworkAddress addr : currentAdditionsList) {
                    boolean isInList = false;
                    for (JGroupsNetworkAddress currentUnprocessedAddr : unprocessedMembershipAdditions) {
                        if (StringUtils.equalsIgnoreCase(addr.getAddressName(), currentUnprocessedAddr.getAddressName())) {
                            isInList = true;
                            break;
                        }
                    }
                    if (!isInList) {
                        unprocessedMembershipAdditions.addLast(addr);
                    }
                }
            }
        }
    }

    public void updateUnprocessedMembershipRemovalsList() {
        List<JGroupsNetworkAddress> currentRemovalsList = getMembershipRemovals();
        if (!currentRemovalsList.isEmpty()) {
            synchronized (getMembershipLock()) {
                for (JGroupsNetworkAddress addr : currentRemovalsList) {
                    boolean isInList = false;
                    for (JGroupsNetworkAddress currentUnprocessedAddr : unprocessedMembershipRemovals) {
                        if (StringUtils.equalsIgnoreCase(addr.getAddressName(), currentUnprocessedAddr.getAddressName())) {
                            isInList = true;
                            break;
                        }
                    }
                    if (!isInList) {
                        unprocessedMembershipRemovals.addLast(addr);
                    }
                }
            }
        }
    }

    public List<JGroupsNetworkAddress> getMembershipAdditions() {
        List<JGroupsNetworkAddress> additions = new ArrayList<>();
        if (getCurrentMembership() == null) {
            return (additions);
        }
        synchronized (getMembershipLock()) {
            for (Address newListElement : getCurrentMembership()) {
                if (getPreviousMembership().contains(newListElement)) {
                    // do nothing
                } else {
                    JGroupsNetworkAddress currentAdapterAddress = new JGroupsNetworkAddress();
                    currentAdapterAddress.setAddressName(newListElement.toString());
                    currentAdapterAddress.setJGroupsAddress(newListElement);
                    additions.add(currentAdapterAddress);
                }
            }
        }
        return (additions);
    }

    public List<JGroupsNetworkAddress> getMembershipRemovals() {
        List<JGroupsNetworkAddress> removals = new ArrayList<>();
        synchronized (getMembershipLock()) {
            for (Address oldListElement : getPreviousMembership()) {
                if (getCurrentMembership().contains(oldListElement)) {
                    // no nothing
                } else {
                    JGroupsNetworkAddress currentAdapterAddress = new JGroupsNetworkAddress();
                    currentAdapterAddress.setAddressName(oldListElement.toString());
                    currentAdapterAddress.setJGroupsAddress(oldListElement);
                    removals.add(currentAdapterAddress);
                }
            }
        }
        return (removals);
    }

    public void updateCurrentMembership(List<Address> newCurrentMembershipList) {
        synchronized (getMembershipLock()) {
            getPreviousMembership().clear();
            getPreviousMembership().addAll(getCurrentMembership());
            getCurrentMembership().clear();
            if (newCurrentMembershipList != null) {
                getCurrentMembership().addAll(newCurrentMembershipList);
            }
        }
    }

    public Address getTargetMemberAddress(String name) {
        getLogger().debug(".getTargetMemberAddress(): Entry, name->{}", name);
        Address foundAddress = null;
        getLogger().trace(".getTargetMemberAddress(): Got the Address set via view, now iterate through and see if one is suitable");
        synchronized (getMembershipLock()) {
            for (Address currentAddress : getCurrentMembership()) {
                getLogger().trace(".getTargetMemberAddress(): Iterating through Address list, current element->{}", currentAddress);
                if (currentAddress.toString().contentEquals(name)) {
                    getLogger().trace(".getTargetMemberAddress(): Exit, A match!");
                    foundAddress = currentAddress;
                    break;
                }
            }
        }
        getLogger().debug(".getTargetMemberAddress(): Exit, address->{}", foundAddress);
        return (foundAddress);
    }

    public boolean isTargetAddressActive(String addressName) {
        getLogger().debug(".isTargetAddressActive(): Entry, addressName->{}", addressName);
        if (StringUtils.isEmpty(addressName)) {
            getLogger().debug(".isTargetAddressActive(): addressName is empty, exit returning -false-");
            return (false);
        }
        getLogger().trace(".isTargetAddressActive(): IPCChannel is NOT null, get updated Address set via view");
        boolean addressIsActive = false;
        getLogger().trace(".isTargetAddressActive(): Got the Address set via view, now iterate through and see our address is there");
        synchronized (getMembershipLock()) {
            for (Address currentAddress : getCurrentMembership()) {
                getLogger().trace(".isTargetAddressActive(): Iterating through Address list, current element->{}", currentAddress);
                if (currentAddress.toString().contentEquals(addressName)) {
                    getLogger().trace(".isTargetAddressActive(): Exit, A match");
                    addressIsActive = true;
                    break;
                }
            }
        }
        getLogger().debug(".isTargetAddressActive(): Exit, addressIsActive->{}", addressIsActive);
        return (addressIsActive);
    }

    public List<JGroupsNetworkAddress> getCurrentMembershipJGroupsAddresses() {
        getLogger().debug(".getAllClusterTargets(): Entry");
        List<JGroupsNetworkAddress> adapterAddresses = new ArrayList<>();
        synchronized (getCurrentMembership()) {
            for (Address currentAddress : getCurrentMembership()) {
                getLogger().debug(".getAllTargets(): Iterating through Address list, current element->{}", currentAddress);
                JGroupsNetworkAddress currentAdapterAddress = new JGroupsNetworkAddress();
                currentAdapterAddress.setJGroupsAddress(currentAddress);
                currentAdapterAddress.setAddressName(currentAddress.toString());
                adapterAddresses.add(currentAdapterAddress);
            }
        }
        getLogger().debug(".getAllClusterTargets(): Exit, adapterAddresses->{}", adapterAddresses);
        return (adapterAddresses);
    }

    //
    // Bean Methods
    //
    /**
     * @return the Membership Lock
     */
    public Object getMembershipLock() {
        return (this.membershipLock);
    }

    /**
     * @return the previousMembership
     */
    public ArrayList<Address> getPreviousMembership() {
        return previousMembership;
    }

    /**
     * @param previousScannedMembership the previousMembership to set
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
     * @param currentScannedMembership the currentMembership to set
     */
    public void setCurrentMembership(ArrayList<Address> currentScannedMembership) {
        this.currentMembership = currentScannedMembership;
    }

    public LinkedList<JGroupsNetworkAddress> getUnprocessedMembershipAdditions() {
        return unprocessedMembershipAdditions;
    }

    public void setUnprocessedMembershipAdditions(LinkedList<JGroupsNetworkAddress> unprocessedMembershipAdditions) {
        this.unprocessedMembershipAdditions = unprocessedMembershipAdditions;
    }

    public LinkedList<JGroupsNetworkAddress> getUnprocessedMembershipRemovals() {
        return unprocessedMembershipRemovals;
    }

    public void setUnprocessedMembershipRemovals(LinkedList<JGroupsNetworkAddress> unprocessedMembershipRemovals) {
        this.unprocessedMembershipRemovals = unprocessedMembershipRemovals;
    }

//
    // Utility Methods
    //
    protected Logger getLogger() {
        return (LOG);
    }

}
