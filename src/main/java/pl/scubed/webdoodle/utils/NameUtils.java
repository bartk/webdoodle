/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.utils;

import pl.scubed.webdoodle.xmlschema.TCanvas;

/**
 *
 * @author bartk
 */
public class NameUtils {

    public static String makeImageFileName(final TCanvas canvas, final String extension) {
        String uniqueName = canvas.getName() + "_" + canvas.getId() + "." + extension;
        return uniqueName;
    }

    public static String makeThumbnailFileName(final TCanvas canvas, final String extension) {
        return "thumb_" + makeImageFileName(canvas, extension);
    }
    private NameUtils(){}
    
}
