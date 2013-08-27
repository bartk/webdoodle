package pl.scubed.webdoodle.rest;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import pl.scubed.webdoodle.model.CanvasRegistry;
import pl.scubed.webdoodle.model.PixelRegistry;
import pl.scubed.webdoodle.xmlschema.ObjectFactory;
import pl.scubed.webdoodle.xmlschema.TCanvas;
import pl.scubed.webdoodle.xmlschema.TPalette;
import pl.scubed.webdoodle.xmlschema.TPixel;

@Model
@Path("/canvas")
public class CanvasService {

    private final ObjectFactory objectFactory = new ObjectFactory();
    
    @EJB
    private CanvasRegistry canvasRegistry;
    
    @EJB
    private PixelRegistry pixelRegistry;
    
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{id}/pixels")
    public List<JAXBElement<TPixel>> getAllPixelsForCanvas(@PathParam("id") long canvasId) {
        final List<JAXBElement<TPixel>> result = new ArrayList<>();
        
        for (final TPixel pixel : pixelRegistry.getAllPixelsForCanvasById(canvasId)) {
            result.add(objectFactory.createPixel(pixel));
        }
        
        return result;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<JAXBElement<TCanvas>> getCanvases() {
        final List<JAXBElement<TCanvas>> result = new ArrayList<>();

        for (final TCanvas canvas : canvasRegistry.all()) {
            result.add(objectFactory.createCanvas(canvas));
        }

        return result;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JAXBElement<TCanvas> create(TCanvas newCanvas) {
        return objectFactory.createCanvas(canvasRegistry.createNewCanvas(newCanvas.getName(),
                newCanvas.getWidth(),
                newCanvas.getHeight()));
    }
    
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JAXBElement<TCanvas> getById(@PathParam("id") long id) {
        return objectFactory.createCanvas(canvasRegistry.getById(id));
    }
    
    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JAXBElement<TCanvas> close(@PathParam("id") long canvasId) {
        canvasRegistry.closeCanvasById(canvasId);
        return objectFactory.createCanvas(canvasRegistry.getById(canvasId));
    }
    
    @GET
    @Path("/palette")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JAXBElement<TPalette> getPalette() {
        return objectFactory.createPalette(canvasRegistry.palette());
    }
}
