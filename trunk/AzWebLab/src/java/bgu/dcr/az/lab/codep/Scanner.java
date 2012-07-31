/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab.codep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Inka
 */
public class Scanner {

    private final static Pattern classFinder = Pattern.compile("\\bclass\\b");
    private final static Pattern wordFinder = Pattern.compile("\\b[a-zA-Z0-9_]+\\b");

    public static List<String> extractClasses(File from) throws IOException {
        LinkedList<String> ret = new LinkedList<String>();
        BufferedReader br = new BufferedReader(new FileReader(from));
        String line;
        boolean foundClass = false;
        while ((line = br.readLine()) != null) {
            if (foundClass) {
                final Matcher wordMatcher = wordFinder.matcher(line);
                if (wordMatcher.find()) {
                    ret.add(wordMatcher.group(0));
                    foundClass = false;
                }
            } else {
                final Matcher matcher = classFinder.matcher(line);
                if (matcher.find()) {
                    final Matcher wordMatcher = wordFinder.matcher(line);
                    if (wordMatcher.find(matcher.end())) {
                        ret.add(wordMatcher.group(0));
                    } else {
                        foundClass = true;
                    }
                }
            }
        }

        br.close();
        return ret;
    }

    public static Set<File> extractDependencies(File f, Map<String, List<File>> deps) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String k : deps.keySet()) {
            sb.append("\\b").append(k).append("\\b").append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
//        System.out.println("" + sb);

        Pattern finder = Pattern.compile(sb.toString());

        HashSet<File> ret = new HashSet<File>();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            Matcher matcher = finder.matcher(line);
            while (matcher.find()) {
                ret.addAll(deps.get(matcher.group(0)));
            }
        }

        return ret;
    }

    public static String extractClassDescription(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        StringBuilder buf = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                buf.append(line).append("\n");
                if (classFinder.matcher(line).find()) {
                    String tilHere = buf.toString();
                    int jdocPos = tilHere.lastIndexOf("/**");
                    if (jdocPos >= 0) {
                       int jdocEnd = tilHere.lastIndexOf("*/");
                       if (jdocEnd >= 0){
                            String data = tilHere.substring(jdocPos + "/**".length(), jdocEnd);
                            return data.replaceAll("\n\\s*\\*\\s*", "\n").trim();
                       }else {
                           return "";
                       }
                    } else {
                        return "";
                    }
                }
            }
            
            return "";
        } finally {
            br.close();
        }
    }
}
