package server;


import org.apache.log4j.Logger;
import server.model.Command;
import server.model.CommandType;
import server.model.Register;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class RequestReceiver extends Thread {

    private static final Logger LOG = Logger.getLogger( RequestReceiver.class );

    private static final LinkedBlockingQueue< Command > F1 = new LinkedBlockingQueue< Command >();

    private static final AtomicInteger REQUESTS_RECEIVED = new AtomicInteger();

    private Socket socket;

    private int id;


    public RequestReceiver( Socket socket ) {

        this.id = REQUESTS_RECEIVED.incrementAndGet();
        this.socket = socket;
    }


    public static Command retrieveCommand() {

        return F1.poll();
    }


    @Override
    public void run() {

        LOG.info( "Processing request of client# " + this.id );

        try {

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader( is );
            BufferedReader br = new BufferedReader( isr );

            // TODO TIRAR
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter( os );
            BufferedWriter bw = new BufferedWriter( osw );

            while ( true ) {

                String request = br.readLine();
                if ( request == null ) {
                    break;
                }

                String requestUniqueID = UUID.randomUUID().toString();

                LOG.info( "Client#" + this.id + " >>  " + request + " unique requestId: " + requestUniqueID );

                // TODO MOVER ISTO PARA THREAD 2, SO RECUPERAR COMANDO PARA POR EM F3
                Command command = retrieveCommand( request );
                command.setRequestId( requestUniqueID );

                LOG.info( "Client#" + this.id + " >> Queuing command from requestId: " + command.getRequestId() );

                F1.put( command );

                // TODO TIRAR
                bw.write( "Queued! requestId: " + command.getRequestId() + "\n" );
                bw.flush();

            }

        } catch ( Exception e ) {

            LOG.error( "Client#" + this.id + " >> ERROR WHILE PROCESSING MESSAGE! ", e );
            terminateConnection( e );
        }

        LOG.info( "Client#" + this.id + " >>  Exited without saying goodbye..." );

    }


    private Command retrieveCommand( String request ) {

        Command command = new Command();
        String[] requestFields = request.split( ";" );

        if ( requestFields.length > 0 ) {

            command.setType( CommandType.valueOf( Integer.parseInt( requestFields[ 0 ] ) ) );

            Register register = new Register();
            register.setKey( new BigInteger( requestFields[ 1 ] ) );

            if ( requestFields.length > 2 ) {

                register.setValue( requestFields[ 2 ].getBytes() );
            }

            command.setRegister( register );
        }

        return command;
    }


    private void terminateConnection( Exception e ) {

        try {

            socket.close();
        } catch ( IOException ex ) {

            LOG.error( "Error while trying to close socket of Client#" + this.id, e );
        }
    }
}
