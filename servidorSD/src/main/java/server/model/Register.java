package server.model;


import java.math.BigInteger;


public class Register {

    private BigInteger key;

    private byte[] value;


    public BigInteger getKey() {

        return key;
    }


    public void setKey( BigInteger key ) {

        this.key = key;
    }


    public byte[] getValue() {

        return value;
    }


    public void setValue( byte[] value ) {

        this.value = value;
    }
}
