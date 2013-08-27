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
abstract public class AbstractCanvasEvent {
    protected final TCanvas canvas;

    protected  AbstractCanvasEvent(final TCanvas canvas) {
        this.canvas = canvas;
    }

    public TCanvas getCanvas() {
        return canvas;
    }
    
}
