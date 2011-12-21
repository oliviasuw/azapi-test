package controllers;

import java.util.List;
import play.mvc.*;



public class Application extends Controller {

    public static void index() {
        String user = "inna";
        render(user);
    }

}