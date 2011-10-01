/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pseq;

import bgu.csp.az.impl.prob.MatrixProblem;
import bgu.csp.az.api.Problem;
import bgu.csp.az.impl.pseq.RandomProblemSequence.Configuration;
import bam.utils.FileUtils;
import bgu.csp.az.api.pseq.ProblemSequence;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;



/**
 *
 * @author miclando
 */
public class SavedProblemSequence implements ProblemSequence {
    private Configuration sd;
    private int numberOfProblems;
    private File[] problemList;
    private File path;

    /**
     * constructr
     */
    public SavedProblemSequence() {
        this.sd=null;
        this.problemList=null;
        this.path=null;
        this.numberOfProblems=-1;
    }
    
    /**
     * the method loads the mata data file for the sequence 
     * and the names of the files holding the problems
     * @param path the raletive path of the folder holding the problam files
     * @param mataDataFile the name of the file in the dir holding the mata data
     * @return true if the data was loaded successfully
     */
    public boolean loadFiles(String path,String mataDataFile){
        try {
            this.sd=FileUtils.unPersistObject(new File(path+"/"+mataDataFile), Configuration.class);
            this.path=new File(path);
            problemList=this.path.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    if(name.matches(".*MataData")){
                        return false;
                    }
                    return true;
                }
            });
            this.numberOfProblems=problemList.length-1;
        } catch (ClassNotFoundException ex) {
            System.out.print("failed to load the class");
            return false;
        } catch (FileNotFoundException ex) {
            System.out.print("the path is invalid");
            return false;
        } catch (IOException ex) {
            System.out.print("the path is invalid");
            return false;
        }
        return true;
    }
    
    
    @Override
    public Problem next() {
        MatrixProblem p=null;
        try{
            p= FileUtils.unPersistObject(this.problemList[problemList.length-this.numberOfProblems-1], MatrixProblem.class );
        }
        catch(Exception ex){
            System.out.println("failed to load Problem "+this.problemList[problemList.length-this.numberOfProblems-1].getName());
        }
        this.numberOfProblems--;
        return p;
    }

    @Override
    public boolean hasNext() {
        if(this.numberOfProblems<0){
            return false;
        }
        return true;
    }


    /*********************getters**************************/
    public int getD() {
        return this.sd.getDomainSize();
    }

    public int getMaxCost() {
        return this.sd.getMaxCost();
    }

    public int getN() {
        return this.sd.getNumberOfVariables();
    }

    public float getP1() {
        return this.sd.getP1();
    }

    public float getP2() {
        return this.sd.getP2();
    }

    public long getSeed() {
        return this.sd.getSeed();
    }

    public int getNumberOfProblems() {
        return this.problemList.length;
    }
    
     
    
}
