/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.info;

import bgu.dcr.az.mui.Controller;

/**
 *
 * @author bennyl
 */
public class TokenSetChangedInfo {

    Controller source;

    public TokenSetChangedInfo(Controller source) {
        this.source = source;
    }

    public Controller getSource() {
        return source;
    }

}
