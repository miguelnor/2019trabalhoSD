package client;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class ReceptorClient implements Runnable {

    private Socket socket;


    public ReceptorClient( Socket socket ) {

        this.socket = socket;
    }


    public void run() {

        try {

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader( is );
            BufferedReader br = new BufferedReader( isr );

            while ( true ) {

                String message = br.readLine();

                if ( message == null ) {

                    break;
                }

                System.out.println( "Resposta do servidor: " );
                System.out.println( message );
            }

        } catch ( Exception e ) {

            e.printStackTrace();
        }
    }
}
