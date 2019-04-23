package client.model;

import java.util.HashMap;
import java.util.Map;

public enum CommandEnum {
    CREATE(1),READ(2),UPDATE(3),DELETE(4),EXIT(5);

    private int value;

    private static Map map = new HashMap();

    CommandEnum(int value){
        this.value = value;
    }

    static {
        for (CommandEnum commandEnum : CommandEnum.values()){
            map.put(commandEnum.value, commandEnum);
        }
    }

    public static CommandEnum valueOf(int commandEnum){
        return (CommandEnum) map.get(commandEnum);
    }

    public int getValue(){
        return value;
    }
}
