package server;


import org.apache.log4j.Logger;
import server.model.Command;
import server.model.CommandType;
import server.model.Register;
import server.model.Request;
import server.model.Response;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.HashMap;


public class RequestProcessor implements Runnable {

    private static final Logger LOG = Logger.getLogger( RequestProcessor.class );

    private static final String FAIL_CODE = "Fail";

    private static final String FAIL_MESSAGE = "Unexpected error has occurred while processing request, sorry...";

    private static final String FAIL_NOT_FOUND_MESSAGE = "There's no suck key %s";

    private static final String FAIL_CREATE_MESSAGE = "Fail to create key %s with value %s, key may already exists";

    private static final String FAIL_UPDATE_MESSAGE = "Fail to update key %s to value %s";

    private static final String SUCCESS_CODE = "Success";

    private static final String SUCCESS_READ_MESSAGE = "The key %s has value %s";

    private static final String SUCCESS_CREATE_MESSAGE = "Key %s successfully created with value %s";

    private static final String SUCCESS_UPDATE_MESSAGE = "Key %s successfully updated to value %s";

    private static final String SUCCESS_DELETE_MESSAGE = "Key %s successfully deleted";

    private HashMap< BigInteger, byte[] > dataBase = new HashMap< BigInteger, byte[] >();

    private String logFile;


    public RequestProcessor( String logFile ) {

        this.logFile = logFile;
    }


    public void run() {

        LOG.info( "initializing RequestProcessor..." );

        recoverExistentDatabase();

        while ( true ) {

            Request request = null;
            try {

                request = RequestManager.retrieveRequestForProcessing();
            } catch ( InterruptedException e ) {

                LOG.error( "error while retrieving request", e );
            }

            if ( request != null ) {

                LOG.info( "processing request >> requestId: " + request.getRequestId() );

                Response response = handleRequest( request );

                LOG.info( "request processed, generated response is \"" + response.toString() + "\" >> requestId: " + request.getRequestId() );

                Socket socket = request.getClientSocket();

                try {

                    OutputStream os = socket.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter( os );
                    BufferedWriter bw = new BufferedWriter( osw );

                    bw.write( response.toString() + "\n" );
                    bw.flush();

                    LOG.info( "response sent successfully >> requestId: " + request.getRequestId() );

                } catch ( IOException e ) {

                    LOG.error( "error while sending response back to client >> requestId: " + request.getRequestId() );
                }
            }
        }
    }


    private void recoverExistentDatabase() {

        BufferedReader logFileReader;
        if ( !logFile.equals( "" ) ) {

            LOG.info( "recovering dataBase from file " + logFile );
            try {

                logFileReader = new BufferedReader( new FileReader( logFile ) );

                String commandLine = logFileReader.readLine();
                while ( commandLine != null ) {

                    LOG.info( "recovering command " + commandLine );
                    Command command = Command.retrieveCommand( commandLine );
                    processCUD( command );
                    commandLine = logFileReader.readLine();
                }
                logFileReader.close();

            } catch ( IOException e ) {

                LOG.info( "fail to recover dataBase from file " + logFile );
            }
        } else {

            LOG.info( "there's no file to recover, continuing..." );
        }
    }


    private Response handleRequest( Request request ) {

        Command command = request.getCommand();

        if ( command.getType() == CommandType.READ ) {

            return processRead( command );
        } else {

            return processCUD( command );
        }
    }


    private Response processRead( Command command ) {

        Register register = command.getRegister();

        if ( dataBase.containsKey( register.getKey() ) ) {
            register.setValue( doRead( register.getKey() ) );

            if ( register.getValue() != null ) {

                return createResponseByRegister( SUCCESS_CODE, SUCCESS_READ_MESSAGE, register );
            } else {

                return createResponseWithKey( FAIL_CODE, FAIL_NOT_FOUND_MESSAGE, register.getKey() );
            }
        } else {

            return createResponseWithKey( FAIL_CODE, FAIL_NOT_FOUND_MESSAGE, register.getKey() );
        }
    }


    private Response processCUD( Command command ) {

        Register register = command.getRegister();

        switch ( command.getType() ) {

            case CREATE:
                return processCreate( register );
            case UPDATE:
                return processUpdate( register );
            case DELETE:
                return processDelete( register );
        }

        return createGenericErrorResponse();
    }


    private byte[] doRead( BigInteger key ) {

        return dataBase.get( key );
    }


    private Response createResponseByRegister( String code, String message, Register register ) {

        return new Response( code, String.format( message, register.getKey(), register.getValueAsString() ) );
    }


    private Response createResponseWithKey( String code, String message, BigInteger key ) {

        return new Response( code, String.format( message, key ) );
    }


    private Response processCreate( Register register ) {

        if ( dataBase.containsKey( register.getKey() ) ) {

            return createCreateFailResponse( register );
        }

        boolean didWork = doCreate( register );

        if ( didWork ) {

            return createResponseByRegister( SUCCESS_CODE, SUCCESS_CREATE_MESSAGE, register );
        } else {

            return createResponseByRegister( FAIL_CODE, FAIL_CREATE_MESSAGE, register );
        }
    }


    private boolean doCreate( Register register ) {

        byte[] value = dataBase.putIfAbsent( register.getKey(), register.getValue() );

        return value == null;
    }


    private Response createCreateFailResponse( Register register ) {

        return new Response( FAIL_CODE, String.format( FAIL_CREATE_MESSAGE, register.getKey(), register.getValueAsString() ) );
    }


    private Response processUpdate( Register register ) {

        if ( dataBase.containsKey( register.getKey() ) ) {

            boolean didWork = doUpdate( register );

            if ( didWork ) {

                return createResponseByRegister( SUCCESS_CODE, SUCCESS_UPDATE_MESSAGE, register );
            } else {

                return createResponseByRegister( FAIL_CODE, FAIL_UPDATE_MESSAGE, register );
            }
        }
        return createResponseWithKey( FAIL_CODE, FAIL_NOT_FOUND_MESSAGE, register.getKey() );
    }


    private boolean doUpdate( Register register ) {

        byte[] value = dataBase.replace( register.getKey(), register.getValue() );

        return value != null;
    }


    private Response processDelete( Register register ) {

        if ( dataBase.containsKey( register.getKey() ) ) {

            boolean didWork = doDelete( register.getKey() );

            if ( didWork ) {

                return createResponseWithKey( SUCCESS_CODE, SUCCESS_DELETE_MESSAGE, register.getKey() );
            } else {

                return createResponseWithKey( FAIL_CODE, FAIL_NOT_FOUND_MESSAGE, register.getKey() );
            }
        }
        return createResponseWithKey( FAIL_CODE, FAIL_NOT_FOUND_MESSAGE, register.getKey() );
    }


    private boolean doDelete( BigInteger key ) {

        byte[] value = dataBase.remove( key );

        return value != null;

    }


    private Response createGenericErrorResponse() {

        return new Response( FAIL_CODE, FAIL_MESSAGE );
    }
}
