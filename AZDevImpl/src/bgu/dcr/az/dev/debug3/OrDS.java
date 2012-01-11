/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public class OrDS {
    int[] next;
    int[] prev;
    int[] place;

    public OrDS(int size) {
        next = new int[size];
        prev = new int[size];
        place = new int[size];
        randomizeOrder();
    }
    
    private void randomizeOrder(){        
        Integer[] order;
        LinkedList<Integer> orderTemp = new LinkedList<Integer>();
        for (int i=0; i<next.length; i++){
            orderTemp.add(i);
        }
        Collections.shuffle(orderTemp);
        order = orderTemp.toArray(new Integer[0]);
        for (int i=0; i<next.length; i++){
            next[order[i]] = order[(i+1)%next.length];
            place[order[i]] = i;
        }
        
        for (int i=0; i<next.length; i++){
            prev[next[i]] = i;
        }
        
//        System.out.println("next: " + Arrays.toString(next));
//        System.out.println("prev: " + Arrays.toString(prev));
//        System.out.println("place: " + Arrays.toString(place));
//        System.out.println("order: " + Arrays.toString(order));
    }

    
    public static void main(String[] args){
        new OrDS(5);
    }
    
    
    
    public boolean isFirst(int who){
        return next[next.length-1] == who;
    }
    
    public boolean isLast(int who){
        return prev[0] == who;
    }
    
    public int next(int who){
        return next[who];
    }
    
    public int prev(int who){
        return prev[who];
    }
    
    public boolean isLesser(int a, int b){
        return place[a] < place[b];
    }
}
