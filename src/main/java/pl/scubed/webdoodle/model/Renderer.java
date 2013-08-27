/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import pl.scubed.webdoodle.utils.NameUtils;
import pl.scubed.webdoodle.xmlschema.TCanvas;
import pl.scubed.webdoodle.xmlschema.TPixel;

/**
 *
 * @author bartk
 */
@Stateless
@LocalBean
public class Renderer {

    @EJB
    private ConfigurationProvider configurationProvider;

    @Asynchronous
    public void deleteRendered(TCanvas canvas) {
        new File((configurationProvider.getRenderedPicturesDirectory() + NameUtils.makeImageFileName(canvas, configurationProvider.getFileFormat()))).delete();
        new File((configurationProvider.getThumbnailsDirectory() + NameUtils.makeThumbnailFileName(canvas, configurationProvider.getFileFormat()))).delete();
    }

    @Asynchronous
    public void render(final TCanvas canvas, final List<TPixel> pixels) {
        BufferedImage bufferedImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (final TPixel p : pixels) {
            graphics.setColor(colorFromPixel(p.getColor()));
            graphics.fillOval(p.getX(), p.getY(), 5, 5);
        }

        graphics.dispose();

        BufferedImage thumbnailBufferedImage = generateThumbnail(bufferedImage);

        try {
            writeImage(bufferedImage, configurationProvider.getRenderedPicturesDirectory() + NameUtils.makeImageFileName(canvas, configurationProvider.getFileFormat()));
            writeImage(thumbnailBufferedImage, configurationProvider.getThumbnailsDirectory() + NameUtils.makeThumbnailFileName(canvas, configurationProvider.getFileFormat()));
            canvas.setDownloadable(true);
        } catch (IOException ex) {
            Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Color colorFromPixel(final String colorString) {
        final String nohash = colorString.substring(1);
        return new Color(Integer.parseInt(nohash.substring(0, 2), 16),
                Integer.parseInt(nohash.substring(2, 4), 16),
                Integer.parseInt(nohash.substring(4, 6), 16));
    }

    private void writeImage(final BufferedImage bufferedImage, final String fileName) throws IOException {
        ImageIO.write(bufferedImage, configurationProvider.getFileFormat(), new File(fileName));
    }

    private BufferedImage generateThumbnail(final BufferedImage bufferedImage) {
        final int maxWidth = configurationProvider.getThumbnailMaxWidth();
        final int maxHeight = configurationProvider.getThumbnailMaxHeight();
        int thumbnailWidth;
        int thumbnailHeight;

        if (bufferedImage.getWidth() < bufferedImage.getHeight()) {
            thumbnailHeight = maxHeight;
            thumbnailWidth = bufferedImage.getWidth() / (bufferedImage.getHeight() / maxHeight);
        } else {
            thumbnailWidth = maxWidth;
            thumbnailHeight = bufferedImage.getHeight() / (bufferedImage.getWidth() / maxWidth);
        }
        BufferedImage thumbnaulBufferedImage = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);
        thumbnaulBufferedImage.getGraphics().drawImage(bufferedImage.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_SMOOTH), 0, 0, null);
        return thumbnaulBufferedImage;
    }
}
