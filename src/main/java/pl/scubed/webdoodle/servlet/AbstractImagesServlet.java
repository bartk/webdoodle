/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.ejb.EJB;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import pl.scubed.webdoodle.model.ConfigurationProvider;

/**
 *
 * @author bartk
 */
public abstract class AbstractImagesServlet extends HttpServlet {
    @EJB
    protected ConfigurationProvider configurationProvider;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("image/jpeg");
        if ("true".equalsIgnoreCase(request.getParameter("download"))) {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;");
        }
        
        try (ServletOutputStream out = response.getOutputStream()) {
            String fileName = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
            BufferedImage imageFile;
            try {
                imageFile = getImage(fileName);
            }
            catch (IOException ex) {
                imageFile = getDefaultImage();
            }
            
            if (imageFile != null) {
                ImageIO.write(imageFile, "jpg", out);
            }
            else { 
                System.err.println("Image not found. Sendig 404");
                response.sendError(404);}
        }
    }

    protected abstract BufferedImage getImage(final String fileName) throws IOException;
    
    protected abstract BufferedImage getDefaultImage();
}
