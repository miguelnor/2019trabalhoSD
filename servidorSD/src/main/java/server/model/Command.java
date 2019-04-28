package server.model;


import java.math.BigInteger;


public class Command {

    private CommandType type;

    private Register register;


    public static Command retrieveCommand( String request ) {

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


    @Override
    public String toString() {
        // format are OP;KEY;VALUE for create and update and OP;KEY for read and delete

        CommandType commandType = this.getType();

        String commandString = String.valueOf( commandType.getValue() );
        commandString = commandString.concat( ";" + this.getRegister().getKey().toString() );

        if ( commandType == CommandType.CREATE || commandType == CommandType.UPDATE ) {

            commandString = commandString.concat( ";" + this.getRegister().getValueAsString() );
        }

        return commandString;
    }
}
