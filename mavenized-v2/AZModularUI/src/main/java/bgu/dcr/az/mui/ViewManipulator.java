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
public interface ViewManipulator  {
    boolean accept(ViewContainer container);
    View create(ViewContainer container);
    JavaDocInfo doc();
}
