package client;


import java.net.InetAddress;
import java.net.Socket;


public class Client {

    private static Socket socket;


    public static void main( String[] args ) {

        try {

            String hostname = "localhost";
            int port = 25000;

            InetAddress address = InetAddress.getByName( hostname );
            socket = new Socket( address, port );

            InteractiveClient interactiveClient = new InteractiveClient( socket );

            interactiveClient.start();

            ReceptorClient receptorClient = new ReceptorClient( socket );

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
