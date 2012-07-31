package bgu.dcr.az.srccon.util;

import java.io.IOException;

public class Shells {

    public static void execJarAndWait(String jar, String classToRun, String... args) throws IOException, InterruptedException {
        Process p = execJar(jar, classToRun, args);
        StreamGobbler.gobble(p.getInputStream());
        p.waitFor();
    }
    
    private static String[] array(String... array){
        return array;
    }
    
    private static String[] array(String[] left, String... right){
        String[] all = new String[left.length + right.length];
        System.arraycopy(left, 0, all, 0, left.length);
        System.arraycopy(right, 0, all, left.length, right.length);
        return all;
    }

    public static Process execJar(String jar, String classToRun, String... args) throws IOException {
        String[] cmd = array(array("java", "-Xmx1024m", "-cp", jar, classToRun), args);
        
        System.out.println("executing jar: ");
        for (String p : cmd) {
            System.out.print(p);
            System.out.print(" ");
        }
        System.out.println();
        
        ProcessBuilder pbuild = new ProcessBuilder(cmd);
        pbuild.redirectErrorStream(true);
        Process p = pbuild.start();
        return p;
    }
}
