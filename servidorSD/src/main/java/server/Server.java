package server;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;


public class Server {

    private static final Logger LOG = Logger.getLogger( RequestReceiver.class );

    private static final Properties serverProperties = loadProperties();

    static {
        BasicConfigurator.configure();
    }


    private static Properties loadProperties() {

        Properties properties = null;

        try {

            properties = new Properties();
            InputStream propIn = Server.class.getClassLoader().getResourceAsStream( "server.properties" );
            properties.load( propIn );

        } catch ( IOException e ) {

            LOG.error( "error while trying to retrieve properties file!!!!", e );
        }

        return properties;
    }


    public static void main( String[] args ) {

        try {

            String logFile = serverProperties.getProperty( "server.log.file" );

            Thread requestProcessor = new Thread( new RequestProcessor( logFile ) );
            requestProcessor.start();

            Thread requestLogger = new Thread( new RequestLogger( logFile ) );
            requestLogger.start();

            Thread requestManager = new Thread( new RequestManager() );
            requestManager.start();

            int port = Integer.parseInt( serverProperties.getProperty( "server.port" ) );

            ServerSocket serverSocket = new ServerSocket( port );
            LOG.info( "Server started on port " + port );

            while ( true ) {

                Thread requestReceiver = new Thread( new RequestReceiver( serverSocket.accept() ) );
                requestReceiver.start();
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
