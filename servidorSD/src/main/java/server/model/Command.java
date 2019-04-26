package server.model;


public class Command {

    private CommandType type;

    private Register register;

    private String requestId;


    public CommandType getType() {

        return type;
    }


    public void setType( CommandType type ) {

        this.type = type;
    }


    public Register getRegister() {

        return register;
    }


    public void setRegister( Register register ) {

        this.register = register;
    }


    public String getRequestId() {

        return requestId;
    }


    public void setRequestId( String requestId ) {

        this.requestId = requestId;
    }
}
