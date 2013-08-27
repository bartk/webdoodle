/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.model;

import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import pl.scubed.webdoodle.utils.NameUtils;
import pl.scubed.webdoodle.xmlschema.TCanvas;
import pl.scubed.webdoodle.xmlschema.TPalette;

/**
 *
 * @author bartk
 */
@Singleton
@LocalBean
@Startup
public class CanvasRegistry {
    private  TPalette palette;
    
    private long currentId = 0;
    private List<TCanvas> canvases;
    
    @EJB
    private ConfigurationProvider configurationProvider;
    
    @EJB
    private PersistentState persistentState;
    
    @Inject
    private Event<CanvasCloseEvent> closeListeners;
    
    @Inject
    private Event<CanvasDeleteEvent> deleteListeners;
    
    @PostConstruct
    private void init() {
        palette = configurationProvider.getPalette();
        canvases = persistentState.restoreCanvases();
        for (TCanvas c : canvases) {
            currentId = (c.getId() >= currentId) ? c.getId() + 1: currentId;
        }
    }
    
    @PreDestroy
    private void cleanup() {
        persistentState.backupCanvases(canvases);
    }
    
    public TCanvas createNewCanvas(final String name, final int width, final int height) {
        if (name == null || name.trim().isEmpty() || width < 1 || height < 1) {
            throw new IllegalArgumentException();
        }
        
        final TCanvas canvas = new TCanvas();
        canvas.setName(name);
        canvas.setClosed(false);
        canvas.setWidth(width);
        canvas.setHeight(height);
        canvas.setId(generateId());
        canvases.add(canvas);
        return  canvas;
    }

    private long generateId() {
        return currentId++;
    }  
    
    public List<TCanvas> all() {
        return Collections.unmodifiableList(canvases);
    }

    public TCanvas getById(long id) {
        for (TCanvas canvas : canvases) {
            if (canvas.getId() == id) {
                return canvas;
            }
        }
        return null;
    }

    public void closeCanvasById(long id) {
        TCanvas canvas = getById(id);
        canvas.setClosed(true);
        canvas.setCanvasImgSrc("/images/" + NameUtils.makeImageFileName(canvas, configurationProvider.getFileFormat()));
        canvas.setThumbnailImgSrc("/thumbnails/" + NameUtils.makeThumbnailFileName(canvas, configurationProvider.getFileFormat()));
        closeListeners.fire(new CanvasCloseEvent(canvas));
    }

    public void deleteById(long id) {
        TCanvas canvas = getById(id);
        deleteListeners.fire(new CanvasDeleteEvent(canvas));
        canvases.remove(canvas);
    }
    
    public TPalette palette() {
        return palette;
    }
}
