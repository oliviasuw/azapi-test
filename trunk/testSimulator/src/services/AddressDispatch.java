/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Eran
 */
public class AddressDispatch {

    private HashMap<String, Integer> homeAddresses;
    private HashMap<String, Integer> workAddresses;

    public AddressDispatch(String filename) throws IOException {
        homeAddresses = new HashMap<>();
        workAddresses = new HashMap<>();
        parseInput(filename);
    }
    
    public String dispatchHomeAddress(){
        return dispatchAddress(homeAddresses);
    }
    
    public String dispatchWorkAddress(){
        return dispatchAddress(workAddresses);
    }
    
    private String dispatchAddress(HashMap<String, Integer> addrs){
        for(Map.Entry<String, Integer> entry : addrs.entrySet()){
            if(entry.getValue() > 0){
                entry.setValue(entry.getValue() - 1);
                return entry.getKey();
            }
        }
        System.out.println("no available address!");
        return null;
    }

    /**
     * Parsing input file. Lines in the file must maintain a certain pattern.
     * #agents at the 'H' vertices and the 'W' vertices must
     * be equal. (H and W stands for home and work accordingly)
     *
     * @param filename
     */
    private void parseInput(String filename) throws FileNotFoundException, IOException {
        int w = 0, h = 0, amount;
        String[] tokens;
        String s;
        
        File input = new File(filename);
        FileReader reader = new FileReader(input);
        BufferedReader in = new BufferedReader(reader);
        
        while ((s = in.readLine()) != null) {
            tokens = s.split(" ");
            //line pattern: [H/h/W/w] [vertex-name] [amount-of-agents]
            if (!s.matches("[WwHh][ \\t]+\\w+[ \\t]+\\d+")) {
                throw new UnsupportedOperationException("Address Dispatch:: Unknown line structure!");
            }
            
            amount = Integer.parseInt(tokens[2]);

            if (tokens[0].equalsIgnoreCase("h")) {
                h += amount;
                homeAddresses.put(tokens[1], amount);
            } else {
                w += amount;
                workAddresses.put(tokens[1], amount);
            }
        }
        if(h != w)
            throw new UnsupportedOperationException("number of agents at 'H' vertices and 'W' vertices isn't equal!");
    }
}
