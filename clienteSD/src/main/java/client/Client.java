package client;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;


public class Client {

    private static Socket socket;


    protected static Properties getProperties()
        throws IOException {

        Properties properties = new Properties();
        InputStream propIn = Client.class.getClassLoader().getResourceAsStream( "client.properties" );
        properties.load( propIn );

        return properties;
    }


    public static void main( String[] args ) {

        try {

            Properties properties = getProperties();

            String hostname = properties.getProperty( "server.hostname" );
            int port = Integer.parseInt( properties.getProperty( "server.port" ) );

            InetAddress address = InetAddress.getByName( hostname );
            socket = new Socket( address, port );

            Thread interactiveClient = new Thread( new InteractiveClient( socket ) );

            interactiveClient.start();

            Thread receptorClient = new Thread( new ReceptorClient( socket ) );

            receptorClient.start();

            interactiveClient.join();

            Thread.sleep( 5000 );

        } catch ( Exception e ) {

            e.printStackTrace();

        } finally {

            try {

                socket.close();

            } catch ( Exception e ) {

                e.printStackTrace();

            }
        }
    }
}
