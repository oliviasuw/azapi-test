/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.abm.api;

import java.util.Random;

/**
 *
 * @author Eran
 */
public interface WorldGenerator {
    void generate(World w, Random rnd);
}
