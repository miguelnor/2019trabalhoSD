package server;


import org.apache.log4j.Logger;
import server.model.Request;

import java.util.concurrent.LinkedBlockingQueue;


public class RequestManager implements Runnable {

    private static final Logger LOG = Logger.getLogger( RequestManager.class );

    private static final LinkedBlockingQueue< Request > F2 = new LinkedBlockingQueue< Request >();

    private static final LinkedBlockingQueue< Request > F3 = new LinkedBlockingQueue< Request >();


    public static Request retrieveRequestForLogging()
        throws InterruptedException {

        return F2.take();
    }


    public static Request retrieveRequestForProcessing()
        throws InterruptedException {

        return F3.take();
    }


    public void run() {

        LOG.info( "initializing RequestManager..." );

        while ( true ) {
            Request request = null;

            try {

                request = RequestReceiver.retrieveRequest();
            } catch ( InterruptedException e ) {

                LOG.error( "error while retrieving request", e );
            }

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
