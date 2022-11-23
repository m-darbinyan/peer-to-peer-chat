import java.io.*;
import java.net.*;
import java.util.Scanner;

import static utils.Constants.*;

public class Client extends GeneralClient{
    private String username;

    public Client(Socket socket, String username){
        super(socket);
        this.username = username;
    }

    public void sendPublicMessage(){
        bufferedWriterWrite(this, username);
        bufferedWriterWrite(this, PUBLIC_MESSAGE);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Now you can send messages to all the users");
        System.out.println("(Type 'leave' to exit the public chat)");
        while(getSocket().isConnected()){
            String messageToSend = scanner.nextLine();
            if(messageToSend.equals(LEAVE_CHAT_MESSAGE)){
                bufferedWriterWrite(this, LEAVE_CHAT_MESSAGE);
                this.chooseOperation();
            }
            else{
                bufferedWriterWrite(this, "(public) " + username + ": " + messageToSend);
            }
        }
    }

    public void sendPrivateMessage(){
        bufferedWriterWrite(this, username);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Who do you want to send message to: ");
        String friendUsername = scanner.nextLine();
        bufferedWriterWrite(this, friendUsername);

        System.out.println("Now you can send messages to " + friendUsername);
        System.out.println("(Type 'leave' to exit the private chat)");

        while(getSocket().isConnected()){
            String messageToSend = scanner.nextLine();
            if(messageToSend.equals(LEAVE_CHAT_MESSAGE)){
                bufferedWriterWrite(this, LEAVE_CHAT_MESSAGE);
                this.chooseOperation();
            }
            else{
                bufferedWriterWrite(this, "(private) " + username + ": " + messageToSend);
            }
        }
    }

    public void reloadClientList(){
        bufferedWriterWrite(this, username);
        bufferedWriterWrite(this, RELOAD_CLIENT_LIST_MESSAGE);
        this.chooseOperation();
    }

    public void exitApplication(){
        bufferedWriterWrite(this, username);
        bufferedWriterWrite(this, EXIT_APPLICATION_MESSAGE);
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while(getSocket().isConnected()){
                    try{
                        msgFromGroupChat = getBufferedReader().readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e){
                        closeEverything(getSocket(), getBufferedReader(), getBufferedWriter());
                    }
                }

            }
        }).start();
    }

    public void chooseOperation(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("In our chat you can perform different actions.");
        System.out.println("Here is the list of available operations which you can choose by writing command's name.");
        String command = "";
        while(!command.equalsIgnoreCase(EXIT_APPLICATION_MESSAGE)) {
            System.out.println("Choose the operation: ");
            System.out.println("1. Public Message - " + PUBLIC_MESSAGE);
            System.out.println("2. Private Message - " + PRIVATE_MESSAGE);
            System.out.println("3. Reload client list - " + RELOAD_CLIENT_LIST_MESSAGE);
            System.out.println("4. Exit the application - " + EXIT_APPLICATION_MESSAGE);
            command = scanner.nextLine();
            switch (command) {
                case PUBLIC_MESSAGE:
                    this.listenForMessage();
                    this.sendPublicMessage();
                    break;
                case PRIVATE_MESSAGE:
                    this.listenForMessage();
                    this.sendPrivateMessage();
                    break;
                case RELOAD_CLIENT_LIST_MESSAGE:
                    this.listenForMessage();
                    this.reloadClientList();
                    break;
                case EXIT_APPLICATION_MESSAGE:
                    this.listenForMessage();
                    this.exitApplication();
                    System.exit(0);
            }
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the chat application.");
        System.out.println("Here you can send public messages to all the users as well as private messages to a particular user.");
        Thread.sleep(3000);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.chooseOperation();
    }
}
