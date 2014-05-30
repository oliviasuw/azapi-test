/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui;

import bgu.dcr.az.conf.modules.ModuleContainer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * a view container is a set of views that accept to be contained by it. view
 * container also contain a set of tokens, these tokens is there to supply
 * additional information for the views that will help them to decide if they
 * wish to join
 *
 * @author bennyl
 */
public class ViewContainer extends ModuleContainer implements Iterable<View> {

    private Set<String> tokens = new HashSet<>();

    public ViewContainer() {
    }

    public ViewContainer(ViewContainer parent) {
        setParent(parent);
    }

    public ViewContainer(ViewContainer parent, String... tokens) {
        setParent(parent);
        this.tokens.addAll(Arrays.asList(tokens));
    }

    public Iterable<String> tokens() {
        return tokens;
    }

    public boolean hasToken(String token) {
        return tokens.contains(token);
    }

    @Override
    public Iterator<View> iterator() {
        return requireAll(View.class).iterator();
    }

    @Override
    public ViewContainer parent() {
        return (ViewContainer) super.parent(); 
    }

}
