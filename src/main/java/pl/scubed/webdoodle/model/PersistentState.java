/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import pl.scubed.webdoodle.xmlschema.TCanvas;
import pl.scubed.webdoodle.xmlschema.TPixel;

/**
 *
 * @author bartk
 */
@Singleton
@LocalBean
public class PersistentState {
    private final static String CANVASES_FILENAME = "Canvases";
    private final static String PIXELS_FILENAME = "Pixels";
    
    @EJB
    private ConfigurationProvider configurationProvider;

    public List<TCanvas> restoreCanvases() {
        File file = getFileForName(getCanvasesFilename());
        return readFile(file, List.class, new ArrayList<TCanvas>());
    }

    public void backupCanvases(final List<TCanvas> canvases) {
        writeFile(canvases, getCanvasesFilename());
    }
    
    public Map<Long, List<TPixel>> restorePixels() {
        File file = getFileForName(getPixelsFilename());
        return readFile(file, HashMap.class, new HashMap<Long, List<TPixel>>());
    }

    public void backupPixels(final Map<Long, List<TPixel>> canvasPixelsMap) {
        writeFile(canvasPixelsMap, getPixelsFilename());
    }
    
    private void writeFile(final Object object, final String filenName) {
        File file = getFileForName(filenName);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(object);
        } catch (IOException ex) {
            System.err.println("error while doing backup: " + ex.getMessage());
        }
    }

    private File getFileForName(final String filenName) {
        String directory = configurationProvider.getStateKeepingDirectory();
        File file = new File(directory + filenName);
        return file;
    }

    private <T> T readFile(File file, Class<T> clazz, T defaultObject) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (T) ois.readObject();
        }
        catch (IOException | ClassNotFoundException ex) {
            System.err.println("error while restoring " + ex.getMessage());
            return defaultObject;
        }
    }

    private String getCanvasesFilename() {
        return configurationProvider.getApplicationName() + CANVASES_FILENAME;
    }

    private String getPixelsFilename() {
        return configurationProvider.getApplicationName() + PIXELS_FILENAME;
    }
}
