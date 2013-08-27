/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.ws;

import java.io.IOException;
import java.io.StringWriter;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import org.codehaus.jackson.map.ObjectMapper;
import pl.scubed.webdoodle.xmlschema.TPixel;

/**
 *
 * @author bartk
 */
public class PixelCoder implements Decoder.Text<TPixel>, Encoder.Text<TPixel> {
    private ObjectMapper objectMapper;

    
    private TPixel tryDecode(String json) throws IOException {
        return objectMapper.readValue(json, TPixel.class);
    }
    
    @Override
    public TPixel decode(String arg0) throws DecodeException {
        try {
            return tryDecode(arg0);
        } catch (IOException ex) {
            throw new DecodeException(arg0, ex.getMessage());
        }
    }

    @Override
    public boolean willDecode(String arg0) {
        try {
            tryDecode(arg0);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    @Override
    public void init(EndpointConfig config) {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void destroy() {
    }

    @Override
    public String encode(TPixel object) throws EncodeException {
        try {
            StringWriter writer = new StringWriter();
            objectMapper.writeValue(writer, object);
            return writer.toString();
        } catch (Exception ex) {
            throw new EncodeException(object, ex.getMessage());
        }
    }
}
