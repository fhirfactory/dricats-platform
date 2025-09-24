package net.fhirfactory.dricats.middleware.oam.uitest;

import org.apache.camel.main.Main;

public class UitestApplication {
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        // Add our routes
        main.configure().addRoutesBuilder(new UitestOamRestRoute());
        // Start Camel (blocks)
        main.run(args);
    }
}
