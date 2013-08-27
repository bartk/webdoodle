/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.model;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import pl.scubed.webdoodle.xmlschema.TCanvas;

/**
 *
 * @author bartk
 */
@Singleton
@LocalBean
@Startup
public class ClosedCanvasCleaner implements ClosedCanvaseCleanerMXBean {
    @EJB
    private CanvasRegistry canvasRegistry;
    
    @EJB
    private ConfigurationProvider configurationProvider;
    
    private boolean automaticDeletionEnabled;
    
    private MBeanServer platformMBeanServer;
    
    private ObjectName objectName;
    
    @Schedule(minute = "*/15", hour = "*", persistent = false)
    private void onTimeout() {
        if (automaticDeletionEnabled) {
            deleteClosedCanvases();
        }
    }
    
    @PostConstruct
    private void setup() {
        platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            objectName = new ObjectName(configurationProvider.getApplicationName() + ":type=" + this.getClass().getName());
            platformMBeanServer.registerMBean(this, objectName);
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException ex) {
            Logger.getLogger(ClosedCanvasCleaner.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @PreDestroy 
    private void cleanup() {
        try {
            platformMBeanServer.unregisterMBean(objectName);
        } catch (InstanceNotFoundException | MBeanRegistrationException ex) {
            Logger.getLogger(ClosedCanvasCleaner.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private void automaticDeletion(boolean enabled) {
        this.automaticDeletionEnabled = enabled;
    }

    @Override
    public void turnAutomaticDeletionOn() {
        automaticDeletion(true);
    }

    @Override
    public void turnAutomaticDeletionOff() {
        automaticDeletion(false);
    }

    @Override
    public void deleteClosedCanvases() {
        List<TCanvas> canvases = canvasRegistry.all();
        List<Long> delete = new ArrayList<>();
        for (TCanvas c: canvases) {
            if (c.isClosed()) {
                delete.add(c.getId());
            }
        }
        for (Long id : delete) {
            canvasRegistry.deleteById(id);
        }
    }
}
