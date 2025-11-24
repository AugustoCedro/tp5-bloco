package org.example.app;

import io.javalin.Javalin;
import org.example.controller.ClientController;
import org.example.controller.UserController;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        Javalin app = Javalin.create().start(7000);

        new ClientController(app);
        new UserController(app);
    }
}
