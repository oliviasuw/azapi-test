/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.evt;

import com.google.gson.JsonElement;

/**
 *
 * @author bennyl
 */
public interface EventListener {
    void onEvent(JsonElement e);
}
