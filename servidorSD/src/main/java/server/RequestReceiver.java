package server;


import org.apache.log4j.Logger;
import server.model.Command;
import server.model.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class RequestReceiver implements Runnable {

    private static final Logger LOG = Logger.getLogger( RequestReceiver.class );

    private static final LinkedBlockingQueue< Request > F1 = new LinkedBlockingQueue< Request >();

    private static final AtomicInteger REQUESTS_RECEIVED = new AtomicInteger();

    private Socket socket;

    private int id;


    public RequestReceiver( Socket socket ) {

        this.id = REQUESTS_RECEIVED.incrementAndGet();
        this.socket = socket;
    }


    public static Request retrieveRequest() {

        return F1.poll();
    }


    public void run() {

        LOG.info( "Processing request of client# " + this.id );

        try {

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader( is );
            BufferedReader br = new BufferedReader( isr );

            while ( true ) {

                String line = br.readLine();
                if ( line == null ) {
                    break;
                }

                Request request = new Request( socket );

                LOG.info( "Client#" + this.id + " >>  " + line + " unique requestId: " + request.getRequestId() );

                Command command = Command.retrieveCommand( line );
                request.setCommand( command );

                LOG.info( "Client#" + this.id + " >> Queuing command from requestId: " + request.getRequestId() );

                F1.put( request );

            }

        } catch ( Exception e ) {

            LOG.error( "Client#" + this.id + " >> ERROR WHILE PROCESSING MESSAGE! ", e );
            terminateConnection( e );
        }

        LOG.info( "Client#" + this.id + " >>  Exited without saying goodbye..." );

    }


    private void terminateConnection( Exception e ) {

        try {

            socket.close();
        } catch ( IOException ex ) {

            LOG.error( "Error while trying to close socket of Client#" + this.id, e );
        }
    }
}
