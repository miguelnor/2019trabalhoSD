package server.model;


import java.net.Socket;
import java.util.UUID;


public class Request {

    private Command command;

    private Socket clientSocket;

    private String requestId;


    public Request( Socket clientSocket ) {

        this.clientSocket = clientSocket;
        this.requestId = UUID.randomUUID().toString();
    }


    public Command getCommand() {

        return command;
    }


    public void setCommand( Command command ) {

        this.command = command;
    }


    public Socket getClientSocket() {

        return clientSocket;
    }


    public void setClientSocket( Socket clientSocket ) {

        this.clientSocket = clientSocket;
    }


    public String getRequestId() {

        return requestId;
    }


    public void setRequestId( String requestId ) {

        this.requestId = requestId;
    }
}
