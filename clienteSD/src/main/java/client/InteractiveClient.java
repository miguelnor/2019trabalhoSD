package client;


import server.model.Register;
import server.model.CommandType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


public class InteractiveClient implements Runnable {

    private Boolean oneMoreInteraction = Boolean.TRUE;

    private Scanner reader = new Scanner( System.in );

    private Register register;

    private Socket socket;


    public InteractiveClient( Socket socket ) {

        this.socket = socket;
    }


    public void run() {

        String input;

        do {
            prepareForAnotherInteraction();
            showMenu();
            input = reader.nextLine();

            try {

                handleInputOption( input );
                Thread.sleep( 200 );
            } catch ( NumberFormatException e ) {

                handleInputError();
            } catch ( InterruptedException e ) {

                e.printStackTrace();
            }
        }
        while ( isOneMoreInteraction() );

        terminateMenu();
        reader.close();
    }


    private void prepareForAnotherInteraction() {

        setRegister( new Register() );
    }


    private void showMenu() {

        System.out.println( "\nMenu Interativo" );
        System.out.println( "Escolha uma das opcoes a seguir ou digite \"sair\" para terminar o programa:" );
        System.out.println( "1.Create (criar) registro" );
        System.out.println( "2.Read (ler) registro" );
        System.out.println( "3.Update (atualizar) registro" );
        System.out.println( "4.Delete (excluir) registro" );
        System.out.println( "5.Sair" );
        System.out.print( "Opcao escolhida: " );
    }


    private void handleInputOption( String input ) {

        if ( !input.equalsIgnoreCase( "sair" ) ) {

            switch ( CommandType.valueOf( Integer.parseInt( input ) ) ) {

                case CREATE:
                    commandCreateOrUpdate( CommandType.CREATE );
                    break;

                case READ:
                    commandReadOrDelete( CommandType.READ );
                    break;

                case UPDATE:
                    commandCreateOrUpdate( CommandType.UPDATE );
                    break;

                case DELETE:
                    commandReadOrDelete( CommandType.DELETE );
                    break;

                case EXIT:
                    commandExit();
                    break;

                default:
                    handleInputError();
            }
        } else {
            commandExit();
        }

    }


    protected void sendRequestToServer( String message ) {

        try {

            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter( os );
            BufferedWriter bw = new BufferedWriter( osw );
            System.out.println( "Enviando para o servidor request = " + message );
            bw.write( message + "\n" );
            bw.flush();

        } catch ( IOException e ) {

            System.out.println( "ERRO AO ENVIAR MSG PARA SERVIDOR" );
        }
    }


    private void commandCreateOrUpdate( CommandType operation ) {

        showCreateOrUpdateInstructions( operation );
        boolean didProperKey = handleEnterKey();

        if ( didProperKey ) {

            showEnterValueInstructions();
            handleEnterValue();
            sendCreateOrUpdateRequestToServer( operation );
        }
    }


    private void showCreateOrUpdateInstructions( CommandType operation ) {

        if ( operation.equals( CommandType.UPDATE ) ) {

            System.out.println( "3.Update (atualizar) registro" );
            System.out.println( "Entre com a chave do registro que deseja alterar (inteiro)..." );

        } else {

            System.out.println( "1.Create (criar) registro:" );
            System.out.println( "O registro e uma tupla de Chave e Valor..." );
            System.out.println( "Entre com a Chave desejada (inteiro)..." );
        }
    }


    private boolean handleEnterKey() {

        System.out.print( "chave: " );
        if ( validEntryForBigInteger() ) {

            getRegister().setKey( reader.nextBigInteger() );
            flush();
            return true;
        } else {

            System.out.println( "Entrada nao e valida! Tente novamente..." );
            return false;
        }

    }


    private void flush() {

        reader.nextLine();
    }


    private void showEnterValueInstructions() {

        System.out.println( "Entre com o valor desejado (sem restricoes) ..." );
    }


    private void handleEnterValue() {

        System.out.print( "valor: " );

        getRegister().setValue( reader.nextLine().getBytes() );
    }


    private void sendCreateOrUpdateRequestToServer( CommandType operation ) {

        // format is OP;KEY;VALUE
        String message = operation.getValue() + ";" + getRegister().getKey() + ";" + getRegister().getValueAsString();
        sendRequestToServer( message );
    }


    private void commandReadOrDelete( CommandType operation ) {

        showReadOrDeleteInstructions( operation );
        handleReadOrDelete( operation );
    }


    private void showReadOrDeleteInstructions( CommandType operation ) {

        if ( operation.equals( CommandType.READ ) ) {

            System.out.println( "2.Read (ler) registro:" );
            System.out.println( "Entre com o id do registro que deseja efetuar a leitura..." );

        } else {

            System.out.println( "4.Delete (excluir) registro:" );
            System.out.println( "Entre com o id do registro que deseja excluir..." );
        }
    }


    private void handleReadOrDelete( CommandType operation ) {

        System.out.print( "id: " );
        if ( validEntryForBigInteger() ) {

            getRegister().setKey( reader.nextBigInteger() );
            flush();
            sendReadOrDeleteRequestToServer( operation );
        } else {

            System.out.println( "Entrada nao e valida! Tente novamente..." );
        }
    }


    private boolean validEntryForBigInteger() {

        return reader.hasNextBigInteger();
    }


    private void sendReadOrDeleteRequestToServer( CommandType operation ) {

        // format is OP;KEY
        String message = operation.getValue() + ";" + getRegister().getKey();
        sendRequestToServer( message );
    }


    private void commandExit() {

        setOneMoreInteraction( Boolean.FALSE );
    }


    private void handleInputError() {

        System.out.println( "Comando nao reconhecido.. \nTente novamente!" );
    }


    private void terminateMenu() {

        System.out.println( "Saindo..." );
    }


    private Boolean isOneMoreInteraction() {

        return oneMoreInteraction;
    }


    private void setOneMoreInteraction( Boolean oneMoreInteraction ) {

        this.oneMoreInteraction = oneMoreInteraction;
    }


    private Register getRegister() {

        return register;
    }


    private void setRegister( Register register ) {

        this.register = register;
    }
}
