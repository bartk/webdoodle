/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.rest;

import javax.enterprise.inject.Model;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import pl.scubed.webdoodle.xmlschema.ObjectFactory;
import pl.scubed.webdoodle.xmlschema.TConfig;

/**
 *
 * @author bartk
 */
@Model
@Path("/config")
public class ApplicationConfigService {
    private final ObjectFactory objectFactory = new ObjectFactory();

    @Context
    private ServletContext servletContext;
    
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JAXBElement<TConfig> getConfig() {
        TConfig config = new TConfig();
        config.setContextPath(servletContext.getContextPath());
        return objectFactory.createConfig(config);
    }
}
