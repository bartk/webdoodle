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
class CanvasDeleteEvent extends AbstractCanvasEvent {
    public CanvasDeleteEvent(final TCanvas canvas) {
        super(canvas);
    }
}
