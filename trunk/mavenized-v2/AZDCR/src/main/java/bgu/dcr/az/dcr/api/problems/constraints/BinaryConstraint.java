/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.problems.constraints;

/**
 *
 * @author User
 */
public interface BinaryConstraint {
    int cost(int i, int vi, int j, int vj);
}
