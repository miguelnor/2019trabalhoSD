package server;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;


public class Server {

    private static final Logger LOG = Logger.getLogger( RequestReceiver.class );

    static {
        BasicConfigurator.configure();
    }


    private static Properties getProperties()
        throws IOException {

        Properties properties = new Properties();
        InputStream propIn = Server.class.getClassLoader().getResourceAsStream( "server.properties" );
        properties.load( propIn );

        return properties;
    }


    public static void main( String[] args ) {

        try {

            Properties properties = getProperties();

            int port = Integer.parseInt( properties.getProperty( "server.port" ) );

            ServerSocket serverSocket = new ServerSocket( port );
            LOG.info( "Server started on port " + port );

            while ( true ) {

                RequestReceiver requestReceiver = new RequestReceiver( serverSocket.accept() );
                requestReceiver.run();
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
