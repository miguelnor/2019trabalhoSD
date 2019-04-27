package server;


import org.apache.log4j.Logger;
import server.model.CommandType;
import server.model.Request;

import java.io.FileWriter;
import java.io.IOException;


public class RequestLogger implements Runnable {

    private static final Logger LOG = Logger.getLogger( RequestLogger.class );

    private String logFile;


    public RequestLogger( String logFile ) {

        this.logFile = logFile;
    }


    public void run() {

        LOG.info( "initializing RequestLogger..." );

        while ( true ) {

            Request request = RequestManager.retrieveRequestForLogging();

            if ( request != null && request.getCommand().getType() != CommandType.READ ) {

                LOG.info( "processing request for logging... requestId:" + request.getRequestId() );

                writeOnFile( this.logFile, request.getCommand().toString() );
            }
        }
    }


    private void writeOnFile( String file, String command ) {

        try {

            FileWriter fileWriter = new FileWriter( file, true );
            fileWriter.write( command + System.getProperty( "line.separator" ) );
            fileWriter.close();

        } catch ( IOException e ) {

            LOG.error( "error while trying to write on logfile!", e );
        }
    }
}
