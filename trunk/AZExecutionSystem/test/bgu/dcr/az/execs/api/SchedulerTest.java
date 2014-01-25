/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api;

import bgu.dcr.az.execs.MultithreadedScheduler;
import bgu.dcr.az.execs.ThreadSafeProcTable;
import bgu.dcr.az.execs.api.utils.ForkingProcess;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author bennyl
 */
public class SchedulerTest {

    public SchedulerTest() {
    }

    @Test
    public void testSchedule() throws Exception {
        final ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        for (int j = 0; j < 1; j++) {

            MultithreadedScheduler sched = new MultithreadedScheduler(newCachedThreadPool);
            final ProcTable table = new ThreadSafeProcTable();
            int nproc = 10;
            for (int i = 0; i < nproc; i++) {
                table.add(new ForkingProcess(table.nextProcessId()));
//                table.add(new CreazyWakingProc(table.nextProcessId(), i, nproc));
//            table.add(new SimpleSteppingProc(table.nextProcessId(), i));
            }
//            startQueryThread(table);

            TerminationReason result = sched.schedule(table, 8);
            System.out.println("" + result);
            if (result.isError()) {
                return;
            }
        }
    }

    private void startQueryThread(final ProcTable table) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SchedulerTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("Query" + table);
            }
        }).start();
    }

}
