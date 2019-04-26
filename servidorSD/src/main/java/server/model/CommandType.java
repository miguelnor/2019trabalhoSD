package server.model;


import java.util.HashMap;
import java.util.Map;


public enum CommandType {
    CREATE( 1 ),
    READ( 2 ),
    UPDATE( 3 ),
    DELETE( 4 ),
    EXIT( 5 );

    private int value;

    private static Map map = new HashMap();


    CommandType( int value ) {

        this.value = value;
    }

    static {
        for ( CommandType commandType : CommandType.values() ) {
            map.put( commandType.value, commandType );
        }
    }


    public static CommandType valueOf( int commandEnum ) {

        return (CommandType) map.get( commandEnum );
    }


    public int getValue() {

        return value;
    }
}
