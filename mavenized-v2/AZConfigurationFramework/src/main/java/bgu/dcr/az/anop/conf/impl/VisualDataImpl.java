/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.anop.conf.impl;

import bgu.dcr.az.anop.conf.VisualData;

/**
 *
 * @author User
 */
public class VisualDataImpl implements VisualData{
    private String displayName;
    private String iconPath;
    private String description;

    public VisualDataImpl(String displayName, String iconPath, String description) {
        this.displayName = displayName;
        this.iconPath = iconPath;
        this.description = description;
    }

    @Override
    public String displayName() {
        return this.displayName;
    }

    @Override
    public String iconPath() {
        return this.iconPath;
    }

    @Override
    public String description() {
        return this.description;
    }
    
}
