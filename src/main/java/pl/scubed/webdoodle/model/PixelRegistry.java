/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import pl.scubed.webdoodle.xmlschema.TPixel;

/**
 *
 * @author bartk
 */
@Singleton
@LocalBean
@Startup
public class PixelRegistry {

    private Map<Long, List<TPixel>> canvasPixelsMap;
    @EJB
    private Renderer renderer;
    @EJB
    private PersistentState persistentState;

    @PostConstruct
    private void init() {
        canvasPixelsMap = persistentState.restorePixels();
    }

    @PreDestroy
    private void cleanup() {
        persistentState.backupPixels(canvasPixelsMap);
    }

    @Asynchronous
    public void putPixel(final TPixel pixel) {
        List<TPixel> pixelList = getPixelList(pixel.getCanvasId());
        pixelList.add(pixel);
    }

    private synchronized List<TPixel> getPixelList(final long canvasId) {
        List<TPixel> pixelList = canvasPixelsMap.get(canvasId);
        if (pixelList == null) {
            pixelList = Collections.synchronizedList(new ArrayList<TPixel>());
            canvasPixelsMap.put(canvasId, pixelList);
        }
        return pixelList;
    }

    public List<TPixel> getAllPixelsForCanvasById(final long canvasId) {
        return Collections.unmodifiableList(getPixelList(canvasId));
    }

    public void onCanvasClosed(@Observes CanvasCloseEvent canvasCloseEvent) {
        final List<TPixel> pixels = canvasPixelsMap.get(canvasCloseEvent.getCanvas().getId());
        renderer.render(canvasCloseEvent.getCanvas(), pixels);
        canvasPixelsMap.remove(canvasCloseEvent.getCanvas().getId());
    }

    public void onCanvasDeleted(@Observes CanvasDeleteEvent canvasDeleteEvent) {
        renderer.deleteRendered(canvasDeleteEvent.getCanvas());
    }
}
