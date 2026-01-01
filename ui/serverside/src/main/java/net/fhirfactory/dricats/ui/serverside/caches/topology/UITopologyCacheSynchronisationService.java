/*
 * Copyright (c) 2025 Mark A. Hunter
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
package net.fhirfactory.dricats.ui.serverside.caches.topology;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.datagrid.common.topologygrid.IApplicationComponentCacheClient;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class UITopologyCacheSynchronisationService {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(UITopologyCacheSynchronisationService.class);

    //
    // Constants
    //
    private static final Long DEFAULT_TOPOLOGY_UPDATE_CHECK_INTERVAL = 10000L;
    private static final Long DEFAULT_TOPOLOGY_UPDATE_COUNT = 100L;

    //
    // Attributes
    //
    private boolean initialised = false;
    private Long initialCheckDelayPeriod = DEFAULT_TOPOLOGY_UPDATE_CHECK_INTERVAL;
    private Long synchronizationCheckPeriod = DEFAULT_TOPOLOGY_UPDATE_CHECK_INTERVAL;
    private boolean backgroundCheckInitiated = false;
    private LocalDateTime lastTopologyUpdateCheck;
    private Long lastTopologyCacheVersion;


    @Inject
    private UITopologyCacheService topologyCacheService;

    @Inject
    private IApplicationComponentCacheClient topologyGridCacheClient;

    //
    // Constructor(s)
    //

    public UITopologyCacheSynchronisationService() {
        super();
        lastTopologyUpdateCheck = LocalDateTime.MIN;
        lastTopologyCacheVersion = 0L;
    }

    //
    // Lifecycle Methods
    //
    @PostConstruct
    public void initialise() {
        getLogger().debug(".initialise(): Entry");
        if (!initialised) {
            getLogger().debug(".initialise(): Initialising...");
            initialised = true;
            scheduleTopologyDetailsBackgroundSynchronisationTask();
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Business Methods
    //

    public void scheduleTopologyDetailsBackgroundSynchronisationTask() {
        getLogger().debug(".scheduleTopologyDetailsBackgroundSynchronisationTask(): Entry");
        if(isBackgroundCheckInitiated()){
            // do nothing
        } else {
            TimerTask UITopologyCacheSynchronisationCheck = new TimerTask() {
                public void run() {
                    getLogger().debug(".UITopologyCacheSynchronisationCheck(): Entry");
                    doTopologyUpdateCheck();
                    getLogger().debug(".UITopologyCacheSynchronisationCheck(): Exit");
                }
            };
            String timerName = "UITopologyCacheSynchronisationCheck";
            Timer timer = new Timer(timerName);
            timer.schedule(UITopologyCacheSynchronisationCheck, getInitialCheckDelayPeriod(), getSynchronizationCheckPeriod());
            setBackgroundCheckInitiated(true);
        }
        getLogger().debug(".scheduleITOpsBackgroundSynchronisationTask(): Exit");
    }

    private void doTopologyUpdateCheck(){
        getLogger().debug("doTopologyUpdateCheck(): Entry");
        touchLastTopologyUpdateCheck();
        if(!getTopologyGridCacheClient().hasChangesSince(getLastTopologyCacheVersion())){
            getLogger().debug("doTopologyUpdateCheck(): No topology changes since last check");
            getLogger().debug("doTopologyUpdateCheck(): Exit");
            return;
        }
        long currentChangeVersion = getTopologyGridCacheClient().getChangeVersion();
        do {
            List<ApplicationComponent> changedApplicationComponents = getTopologyGridCacheClient().getChangedApplicationComponents(getLastTopologyCacheVersion(), DEFAULT_TOPOLOGY_UPDATE_COUNT);
            for (ApplicationComponent applicationComponent : changedApplicationComponents) {
                getLogger().debug("doTopologyUpdateCheck(): Processing application component: {}", applicationComponent.getObjectId().getFullyDistinguishedName().getCommonName().getValue());
                String key = applicationComponent.resolveKey();
                getTopologyCacheService().getComponents().put(key, applicationComponent);
            }
            long currentChangeVersionIteration = getLastTopologyCacheVersion() + changedApplicationComponents.size();
            setLastTopologyCacheVersion(currentChangeVersionIteration);
        } while (getLastTopologyCacheVersion() <= currentChangeVersion);
        setLastTopologyCacheVersion(currentChangeVersion);
        getLogger().debug("doTopologyUpdateCheck(): Topology cache updated to version {}", getLastTopologyCacheVersion());
        touchLastTopologyUpdateCheck();
        getLogger().debug("doTopologyUpdateCheck(): Exit");
    }



    //
    // Accessor Methods
    //
    protected Logger getLogger() {
        return LOG;
    }

    protected UITopologyCacheService getTopologyCacheService() {
        return topologyCacheService;
    }

    protected IApplicationComponentCacheClient getTopologyGridCacheClient() {
        return topologyGridCacheClient;
    }

    public LocalDateTime getLastTopologyUpdateCheck() {
        return lastTopologyUpdateCheck;
    }

    public void setLastTopologyUpdateCheck(LocalDateTime lastTopologyUpdateCheck) {
        this.lastTopologyUpdateCheck = lastTopologyUpdateCheck;
    }

    public void touchLastTopologyUpdateCheck() {
        setLastTopologyUpdateCheck(LocalDateTime.now());
    }

    public Long getLastTopologyCacheVersion() {
        return lastTopologyCacheVersion;
    }

    public void setLastTopologyCacheVersion(Long lastTopologyCacheVersion) {
        this.lastTopologyCacheVersion = lastTopologyCacheVersion;
    }

    protected Long getInitialCheckDelayPeriod() {
        return initialCheckDelayPeriod;
    }

    protected boolean isBackgroundCheckInitiated() {
        return backgroundCheckInitiated;
    }

    protected void setBackgroundCheckInitiated(boolean initiated){
        this.backgroundCheckInitiated = initiated;
    }

    protected Long getSynchronizationCheckPeriod() {
        return synchronizationCheckPeriod;
    }

    protected void setSynchronizationCheckPeriod(Long synchronizationCheckPeriod) {
        this.synchronizationCheckPeriod = synchronizationCheckPeriod;
    }
}
