/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author bartk
 */
@WebServlet(name = "RenderedImagesServlet", urlPatterns = {"/images/*"})
public class RenderedImagesServlet extends AbstractImagesServlet {

    @Override
    protected BufferedImage getImage(final String fileName) throws IOException {
        return ImageIO.read(new File(configurationProvider.getRenderedPicturesDirectory() + fileName));
    }

    @Override
    protected BufferedImage getDefaultImage() {
        return null;
    }
}
