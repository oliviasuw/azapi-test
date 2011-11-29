/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.tests;

import bc.proxies.Operation;
import bc.proxies.Wrapper;
import bc.proxies.Wrapper.WrapConfigurator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bennyl
 */
public class Test {

    public static void main(String[] args) {
        Pattern compatabilityPattern1 = Pattern.compile("(\\s*((\\d+)\\s*=\\s*(\\d+))\\s*,)*\\s*((\\d+)\\s*=\\s*(\\d+))\\s*");
        Pattern splitter1 = Pattern.compile(",*\\s*\\d+\\s*=\\s*\\d+");
        Pattern seperator1 = Pattern.compile(",*\\s*(\\d+)\\s*=\\s*(\\d+)");

        Pattern compatiblityPattern2 = Pattern.compile("\\s*(\\d+)\\s*vs\\.{0,1}\\s*(\\d+)\\s*");
        
        

        String x = "1   =2, 2 =   3,   4 = 5, 7 a= 9";
        if (!compatabilityPattern1.matcher(x).matches()){
            System.out.println("Bad!!!");
            return;
        }
        
        Matcher m = splitter1.matcher(x);
        while (m.find()) {
            String g0 = m.group();
            Matcher mm = seperator1.matcher(g0);
            mm.find();
            System.out.println("xsi: '" + g0 + "' ");
            System.out.println("= g1:'" + mm.group(1) + "', g2:'" + mm.group(2) + "'");

        }
        //        String[] xs = s.matcher(x)
        //        for (String xsi : xs) {
        //            Matcher m = sep.matcher(xsi);
        //            System.out.println("xsi: '" + xsi + "' ");
        //            System.out.println("= g1:'" + m.group(1) + "', g2:'" + m.group(2) + "'");
        //        }

    }
}