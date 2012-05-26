/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.lab;

import java.util.Date;

/**
 *
 * @author Inna
 */
public class ExperimentResult {
    String title;
    String imgUrl;
    Date date;
    String content;
    int id;
    String author;
    
    static int statId = 0 ;

    
    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }
  
    
    
    public String getDate() {
        return date.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getContent() {
        return content;
    }
        

    public ExperimentResult(String title, String imgUrl, Date date, String content) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.date = date;
        this.content = content;
        this.id = statId++;
        this.author = "Inna Gontmakher";
    }
    
    public ExperimentResult(String title, String imgUrl, Date date) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.date = date;
        this.content = "Bacon ipsum dolor sit amet ball tip bresaola dolore, mollit commodo capicola esse aliquip culpa salami dolor pork biltong. Labore chuck strip steak, t-bone pork belly pork chop rump frankfurter duis salami. Mollit rump excepteur pork loin occaecat, in shankle shoulder. Incididunt cupidatat velit, est voluptate labore ball tip pig nisi aliqua ea cillum non meatloaf. Boudin dolore adipisicing pig brisket, ad id sirloin eu beef ribs mollit excepteur spare ribs ut.Chuck laborum andouille, reprehenderit cupidatat velit mollit laboris swine boudin consectetur esse ham dolore incididunt. T-bone ad consectetur, kielbasa bresaola cow non velit. Brisket cillum tri-tip, andouille exercitation officia aliqua ham hock aliquip dolor est fugiat. Adipisicing deserunt laborum consectetur. Dolore sint minim irure tenderloin, pig eu. Chuck spare ribs veniam meatloaf, laborum minim nisi dolor leberkas nostrud.Speck qui tongue, consequat corned beef occaecat culpa eu pork chop pig magna flank beef ribs tail beef. Tri-tip frankfurter et jowl filet mignon beef. Nisi flank aliquip aute pancetta sausage proident. Enim aliquip chuck, reprehenderit chicken ham hock ball tip pork chop. Biltong ex labore rump tail chuck, dolore venison. Short ribs excepteur beef, ham hock flank in chicken corned beef.Ball tip elit enim, laborum sirloin do pork belly anim rump meatball tongue mollit chicken consequat dolor. Ribeye bresaola exercitation biltong, sint ground round do dolor hamburger kielbasa chicken shoulder. Bresaola capicola prosciutto, short ribs tempor pariatur shank irure. Speck flank drumstick ullamco dolore prosciutto, in nulla occaecat dolor dolore meatball laboris. Short ribs pork chop in ullamco cillum deserunt. Prosciutto consectetur culpa, sirloin pariatur nostrud capicola voluptate ad laborum cillum ut swine. Aute brisket in veniam adipisicing, t-bone jowl aliqua meatball cupidatat drumstick cillum ullamco magna.Spare ribs kielbasa tempor quis, dolore pancetta jowl tri-tip speck ad aliquip veniam magna. Andouille esse strip steak pork belly short loin. Ribeye meatloaf minim aute, spare ribs beef ribs veniam commodo salami pork loin in jerky tempor. Drumstick corned beef ham speck. Shank chuck id ut sunt cillum consequat, proident tenderloin.";
        this.id = statId++;
        this.author = "Inna Gontmakher";
    }
    
    
}
