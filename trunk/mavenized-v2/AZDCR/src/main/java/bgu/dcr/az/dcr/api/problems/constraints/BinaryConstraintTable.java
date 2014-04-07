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
public class BinaryConstraintTable implements BinaryConstraint {

    int[][] table;

    public BinaryConstraintTable(int domainSize) {
        this.table = new int[domainSize][domainSize];
    }

    public BinaryConstraintTable(int domain1Size, int domain2Size) {
        this.table = new int[domain1Size][domain2Size];
    }

    public void setCost(int vi, int vj, int cost){
        table[vi][vj] = cost;
    }
    
    @Override
    public int cost(int i, int vi, int j, int vj) {
        return table[vi][vj];
    }
}
