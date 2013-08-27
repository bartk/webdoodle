/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author bartk
 */
@WebServlet(name = "ThumbnailServlet", urlPatterns = {"/thumbnails/*"})
public class ThumbnailServlet extends AbstractImagesServlet {
    private static BufferedImage defaultImage;
    
    @Override
    protected BufferedImage getImage(final String fileName) throws IOException {
        return ImageIO.read(new File(configurationProvider.getThumbnailsDirectory() + fileName));
    }

    @Override
    protected BufferedImage getDefaultImage() {
        try {
            if (defaultImage == null) {
                defaultImage = ImageIO.read(getClass().getResourceAsStream("./default_Thumbnail.jpg"));
            }
            return defaultImage;
        } catch (IOException ex) {
            Logger.getLogger(ThumbnailServlet.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
}
