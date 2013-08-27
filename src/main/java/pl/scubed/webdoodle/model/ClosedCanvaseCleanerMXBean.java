/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.scubed.webdoodle.model;

/**
 *
 * @author bartk
 */
public interface ClosedCanvaseCleanerMXBean {
    void deleteClosedCanvases();
    void turnAutomaticDeletionOn();
    void turnAutomaticDeletionOff();
}
