package server;


import org.apache.log4j.Logger;
import server.model.Request;

import java.util.concurrent.LinkedBlockingQueue;


public class RequestManager implements Runnable {

    private static final Logger LOG = Logger.getLogger( RequestManager.class );

    private static final LinkedBlockingQueue< Request > F2 = new LinkedBlockingQueue< Request >();

    private static final LinkedBlockingQueue< Request > F3 = new LinkedBlockingQueue< Request >();


    public static Request retrieveRequestForLogging() {

        return F2.poll();
    }


    public static Request retrieveRequestForProcessing() {

        return F3.poll();
    }


    public void run() {

        LOG.info( "initializing RequestManager..." );

        while ( true ) {
            Request request = RequestReceiver.retrieveRequest();

            if ( request != null ) {

                try {

                    F2.put( request );
                    F3.put( request );
                    LOG.info( "request successfully manage! requestId: " + request.getRequestId() );

                } catch ( InterruptedException e ) {

                    LOG.error( "error while managing request! requestId: " + request.getRequestId(), e );
                }
            }
        }

    }
}
