/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.ws;

import java.util.Map;
import javax.inject.Inject;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import pl.scubed.webdoodle.model.CanvasRegistry;
import pl.scubed.webdoodle.model.PixelRegistry;
import pl.scubed.webdoodle.xmlschema.TPixel;

/**
 *
 * @author bartk
 */
@ServerEndpoint(value = "/canvas/{id}", decoders = {PixelCoder.class}, encoders = {PixelCoder.class})
public class CanvasEndpoint {

    private static final String CANVAS_ID = "CANVAS_ID";
    @Inject
    private PixelRegistry pixelRegistry;
    @Inject
    private CanvasRegistry canvasRegistry;

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig, @PathParam("id") Long canvasId) {
        session.getUserProperties().put(CANVAS_ID, canvasId);
    }

    @OnMessage
    public void onMessage(final Session session, final TPixel pixel) {
        if (!canvasRegistry.getById(pixel.getCanvasId()).isClosed()) {
            pixelRegistry.putPixel(pixel);

            Map<String, Object> sessionProperties = session.getUserProperties();
            if (!sessionProperties.containsKey(CANVAS_ID)) {
                sessionProperties.put(CANVAS_ID, pixel.getCanvasId());
            }

            for (Session s : session.getOpenSessions()) {
                if (s.isOpen() && !s.equals(session) && s.getUserProperties().get(CANVAS_ID) == pixel.getCanvasId()) {
                    s.getAsyncRemote().sendObject(pixel);
                }
            }
        }
    }
}
