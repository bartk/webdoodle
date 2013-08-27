/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.model;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import pl.scubed.webdoodle.xmlschema.TColorRow;
import pl.scubed.webdoodle.xmlschema.TPalette;

/**
 *
 * @author bartk
 */
@Singleton
@LocalBean
@Startup
public class ConfigurationProvider implements ConfigurationProviderMXBean {

    private static final String FORMAT_JPG = "jpg";
    private MBeanServer platformMBeanServer;
    private ObjectName objectName;

    @PostConstruct
    public void init() {
        try {
            platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            objectName = new ObjectName(getApplicationName() + ":type=" + this.getClass().getName());
            platformMBeanServer.registerMBean(this, objectName);
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException ex) {
            Logger.getLogger(ConfigurationProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            platformMBeanServer.unregisterMBean(objectName);
        } catch (InstanceNotFoundException | MBeanRegistrationException ex) {
            Logger.getLogger(ConfigurationProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getApplicationName() {
        return "WebDoodle";
    }

    @Override
    public String getStateKeepingDirectory() {
        return System.getProperty("java.io.tmpdir") + File.separator;
    }

    @Override
    public String getRenderedPicturesDirectory() {
        return System.getProperty("java.io.tmpdir") + File.separator;
    }

    @Override
    public String getThumbnailsDirectory() {
        return System.getProperty("java.io.tmpdir") + File.separator;
    }

    public int getThumbnailMaxWidth() {
        return 50;
    }

    public int getThumbnailMaxHeight() {
        return 40;
    }

    public String getFileFormat() {
        return FORMAT_JPG;
    }

    private TPalette palette;
    
    public TPalette getPalette() {
        if (palette == null) {
            palette = PaletteBuiler.palette(3).
                    addColor("#000000").
                    addColor("#ffffff").
                    addColor("#ff0000").
                    addColor("#00ff00").
                    addColor("#0000ff").
                    addColor("#ffff00").
                    addColor("#00ffff").
                    addColor("#ff00ff").
                    build();
        }
        
        return palette;
    }

    private static class PaletteBuiler {
        private final TPalette palette;
        private final int columns;
        private int currentRow;
        
        private static PaletteBuiler palette(int i) {
            return new PaletteBuiler(i);
        }
        
        public PaletteBuiler(int columns) {
            this.columns = columns;
            this.palette = new TPalette();
            this.palette.getColorRow().add(new TColorRow());
        }

        private synchronized PaletteBuiler addColor(String color) {            
            TColorRow row = palette.getColorRow().get(currentRow);
            if (row.getColor().size() < columns) {
                row.getColor().add(color);
            }
            else {
                currentRow++;
                palette.getColorRow().add(new TColorRow());
                row = palette.getColorRow().get(currentRow);
                row.getColor().add(color);
            }
            return this;
        }

        private TPalette build() {
            return palette;
        }
    }
}
