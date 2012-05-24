/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import java.util.Date;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Inna
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class PostsList {
    
    private Post[] posts = {new Post("A post from user A", "here you can find a post from user A, will be some content and maybe some statistics image", new Date()),
                            new Post("A post from user B", "here you can find a post from user B, will be some content and maybe some statistics image", new Date()),
                            new Post("A post from user B", "here you can find a post from user C, will be some content and maybe some statistics image", new Date())};

    public Post[] getPosts() {
        return posts;
    }
    
    public String title(Post p){
        return p.title;
    }
    
    public String content(Post p){
        return p.content;
    }
    
    public String date(Post p){
        return p.published.toString();
    }
    
    

}
