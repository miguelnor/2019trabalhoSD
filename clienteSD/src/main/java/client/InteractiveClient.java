package client;

import client.model.CommandEnum;
import client.model.Register;

import java.util.Scanner;

public class InteractiveClient extends Thread{

    private Boolean oneMoreInteraction = Boolean.TRUE;

    private Scanner reader = new Scanner(System.in);

    private int sleepTimeInMillis = 5000;

    private Register register;

    public void run() {
        String input;

        do{
            prepareForAnotherInteraction();
            showMenu();
            input = reader.nextLine();
            try {
                handleInputOption(input);
            }catch (NumberFormatException e){
                handleInputError();
            }
        }while (isOneMoreInteraction());

        terminateMenu();
        reader.close();
    }

    private void showMenu(){
        System.out.println("Menu Interativo");
        System.out.println("Escolha uma das opcoes a seguir ou digite \"sair\" para terminar o programa:");
        System.out.println("1.Create (criar) registro");
        System.out.println("2.Read (ler) registro");
        System.out.println("3.Update (atualizar) registro");
        System.out.println("4.Delete (excluir) registro");
        System.out.println("5.Sair");
        System.out.println("Opcao escolhida: ");
    }


    private void handleInputOption(String input) {
        if(!input.equalsIgnoreCase("sair")) {
            switch (CommandEnum.valueOf(Integer.parseInt(input))) {
                case CREATE:
                    commandCreate();
                    break;
                case READ:
                    commandRead();
                    break;
                case UPDATE:
                    commandUpdate();
                    break;
                case DELETE:
                    commandDelete();
                    break;
                case EXIT:
                    commandExit();
                    break;
                default:
                    handleInputError();
            }
        } else{
            commandExit();
        }

    }

    private void commandRead() {
        showReadInstructions();
        handleRead();
    }

    private void commandExit() {
        setOneMoreInteraction(Boolean.FALSE);
    }

    private void handleInputError() {
        System.out.println("Comando nao reconhecido.. \nTente novamente!");
    }


    private void terminateMenu(){
        System.out.println("Saindo...");
        try {
            Thread.sleep(getSleepTimeInMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Boolean isOneMoreInteraction() {
        return oneMoreInteraction;
    }

    public void setOneMoreInteraction(Boolean oneMoreInteraction) {
        this.oneMoreInteraction = oneMoreInteraction;
    }

    public int getSleepTimeInMillis() {
        return sleepTimeInMillis;
    }

    public void setSleepTimeInMillis(int sleepTimeInMillis) {
        this.sleepTimeInMillis = sleepTimeInMillis;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }
}
