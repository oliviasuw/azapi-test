/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.conf.api.JavaDocInfo;

/**
 *
 * @author bennyl
 */
public interface ControllerManipulator {

    boolean accept(BaseController container);

    Controller create(BaseController container);

    JavaDocInfo doc();

    Class controllerClass();
}
