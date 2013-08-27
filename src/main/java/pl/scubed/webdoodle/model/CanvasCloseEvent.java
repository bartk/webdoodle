/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.model;

import pl.scubed.webdoodle.xmlschema.TCanvas;

/**
 *
 * @author bartk
 */
class CanvasCloseEvent extends AbstractCanvasEvent {
    public CanvasCloseEvent(final TCanvas canvas) {
        super(canvas);
    }
}
