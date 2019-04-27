package server.model;


public class Response {

    private String returnCode;

    private String returnMessage;


    public Response( String code, String message ) {

        this.returnCode = code;
        this.returnMessage = message;
    }


    public String getReturnCode() {

        return returnCode;
    }


    public void setReturnCode( String returnCode ) {

        this.returnCode = returnCode;
    }


    public String getReturnMessage() {

        return returnMessage;
    }


    public void setReturnMessage( String returnMessage ) {

        this.returnMessage = returnMessage;
    }


    @Override
    public String toString() {

        return this.returnCode + " > " + returnMessage;
    }
}
