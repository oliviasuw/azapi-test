/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testsimulator;

import agents.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import agentData.Data;
import data.events.api.SimulatorEvent;
import data.events.impl.EventWriter;
import data.events.impl.MoveEvent;
import data.events.impl.ParkingEvent;
import data.events.impl.ParkingEvent.CarType;
import data.events.impl.ParkingEvent.InOut;

import data.events.impl.TickEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import services.ClockService;
import services.RoadService;
import services.Service;
import statistics.Utility;


/**
 *
 * @author Eran
 */
public class TestSimulator {
    
    public static EventWriter eventWriter;
    public static Output output;
    
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        String outFile = String.format("file_%da_%dt.bin", Utility.SIMULATOR_AGENTS, Utility.SIMULATOR_TICKS);
        initOutput(outFile);
        
        HashMap<Class<? extends Service>, Service> s = new HashMap<>();
        HashMap<Class<? extends Data>, Data> d = new HashMap<>();
        s.put(RoadService.class, new RoadService());
        s.put(ClockService.class, new ClockService());
        
        System.out.println("\n**** Services' Initialization ****");
        for(Service ser : s.values()){
            System.out.printf("%s:: initializing data ... ", ser.getClass().getSimpleName());
            ser.init();
            System.out.println("ready!");
        }
        
        System.out.println("\n**** Agents' Initialization ****");
        Agent[] agents = new Agent[Utility.SIMULATOR_AGENTS];
        for (int i = 0; i < agents.length; i++) {
            if(Utility.rand.nextDouble() < Utility.PROB_EMPLOYMENT_RATIO)
                agents[i] = new Employee(s,d);
            else
                agents[i] = new UnEmployed(s,d);
            System.out.printf("%s[%d]:: initializing data ... ",agents[i].getClass().getSimpleName(), i);
            agents[i].init();
            System.out.println("ready!");
        }
        
        System.out.println("\n**** Starting the simulation ****");
        double printPulse = 0.1, p = 0;
        for (int i = 0; i < Utility.SIMULATOR_TICKS; i++) {
            eventWriter.writeTick(output, i);
            System.out.println("TICK " + i);
            
            for (Agent agent : agents) {
                agent.run();
            }
            
            for(Service ser : s.values())
                ser.tick();
            
//            if( (i + 1) % 100 == 0)
//                System.out.println( 100.0*(i + 1)/Utility.SIMULATOR_TICKS + "% done ...");
            if(((double)i)/Utility.SIMULATOR_TICKS > p)
                System.out.printf("%f%% done ...\n",(p += printPulse)*100);
        }
        
        System.out.println("\n**** Simulation completed ****");
    }

    private static void initOutput(String file_name) throws FileNotFoundException {
        output = new Output(new FileOutputStream(file_name));
        Kryo kryo = new Kryo();
        kryo.register(MoveEvent.class);
        kryo.register(TickEvent.class);
        kryo.register(SimulatorEvent.class);
        kryo.register(ParkingEvent.class);
        kryo.register(InOut.class);
        kryo.register(CarType.class);
        eventWriter = new EventWriter(kryo);
    }
}
