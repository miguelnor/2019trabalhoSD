package client;


import java.io.BufferedReader;
import java.io.IOException;
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

            BufferedReader br = getReaderFromServer();

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


    protected BufferedReader getReaderFromServer()
        throws IOException {

        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader( is );
        return new BufferedReader( isr );
    }
}
