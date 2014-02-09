/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.delete.me;

import bgu.dcr.az.anop.reg.Register;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Shl
 */
@Register("some-class")
public class SomeClass {

    int i;
    private List<String> names = new LinkedList<>();
    private List<Boolean> yesnoes = new LinkedList<>();
    private List<SomeType> complexes = new LinkedList<>();

    public enum E {

        BAH, MEH;
    }
    E e;
    char c;
    boolean k;
    String j;
    double d;
    float f;
    short s;
    byte b;

    SomeType complex;

    public SomeType getComplex() {
        return complex;
    }

    public void setComplex(SomeType complex) {
        this.complex = complex;
    }

    /**
     * this is i description second attempt
     *
     * @propertyName the-i
     * @return
     */
    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    /**
     * @propertyName this-is-j
     * @return
     */
    public String getJ() {
        return j;
    }

    public void setJ(String j) {
        this.j = j;
    }

    public boolean getK() {
        return k;
    }

    public void setK(boolean k) {
        this.k = k;
    }

    public E getE() {
        return e;
    }

    public void setE(E e) {
        this.e = e;
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public short getS() {
        return s;
    }

    public void setS(short s) {
        this.s = s;
    }

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    /**
     * this is my first simple collection
     *
     * @return
     */
    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
    
    

    public List<Boolean> getBooleans() {
        return yesnoes;
    }
    
    

    @Register("some-type-base")
    public static class SomeType {

        int i;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

    }

    @Register("some-type-ext1")
    public static class ExtendedSomeType1 extends SomeType {

        int j;

        public int getJ() {
            return j;
        }

        public void setJ(int j) {
            this.j = j;
        }

    }

    public List<SomeType> getComplexes() {
        return complexes;
    }

    public void setComplexes(List<SomeType> complexes) {
        this.complexes = complexes;
    }

    @Register("some-type-ext2")
    public static class ExtendedSomeType2 extends SomeType {

        ExtendedSomeType1 other;
        int x;

        public ExtendedSomeType1 getOther() {
            return other;
        }

        public void setOther(ExtendedSomeType1 other) {
            this.other = other;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

    }

}
