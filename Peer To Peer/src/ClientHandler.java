import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static utils.Constants.*;

public class ClientHandler extends GeneralClient implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private String clientUsername;


    public ClientHandler(Socket socket){
        super(socket);
        try{
            this.clientUsername = getBufferedReader().readLine();
            clientHandlers.add(this);
        } catch (IOException e){
            closeEverything(getSocket(), getBufferedReader(), getBufferedWriter());
        }
    }

    @Override
    public void run() {
        String friendUsername = "";
        try{
            friendUsername = getBufferedReader().readLine();
        } catch (IOException e) {
            closeEverything(getSocket(), getBufferedReader(), getBufferedWriter());
        }

        while(getSocket().isConnected()){
            try{
                if(friendUsername.equals(RELOAD_CLIENT_LIST_MESSAGE)){
                    printAvailableClientList();
                    getBufferedReader().readLine();
                    friendUsername = getBufferedReader().readLine();
                }
                else if(friendUsername.equals(EXIT_APPLICATION_MESSAGE)){
                    clientHandlers.remove(this);
                }

                String messageToSend = getBufferedReader().readLine();
                if(messageToSend.equals(LEAVE_CHAT_MESSAGE)){
                    getBufferedReader().readLine();
                    friendUsername = getBufferedReader().readLine();
                }

                if(friendUsername.equals(PUBLIC_MESSAGE)){
                    broadcastPublicMessage(messageToSend);
                }
                else {
                    broadcastPrivateMessage(messageToSend, friendUsername);
                }
            } catch (IOException e){
                closeEverything(getSocket(), getBufferedReader(), getBufferedWriter());
            }
        }
    }

    public void broadcastPublicMessage(String messageToSend){
        for(ClientHandler clientHandler : clientHandlers){
            if(!clientHandler.clientUsername.equals(this.clientUsername)){
                bufferedWriterWrite(clientHandler, messageToSend);
            }
        }
    }

    public void broadcastPrivateMessage(String messageToSend, String clientUsername){
        for(ClientHandler clientHandler : clientHandlers){
            if(clientHandler.clientUsername.equals(clientUsername)){
                bufferedWriterWrite(clientHandler, messageToSend);
            }
        }
    }

    public void printAvailableClientList(){
        bufferedWriterWrite(this, "----------------------------------------");
        if(clientHandlers.size() == 1){
            bufferedWriterWrite(this, "There are no active clients at the moment");
            bufferedWriterWrite(this, "----------------------------------------");
            return;
        }
        bufferedWriterWrite(this, "Here is the list of available clients: ");
        for(ClientHandler clientHandler : clientHandlers){
            if(!clientHandler.clientUsername.equals(this.clientUsername)){
                bufferedWriterWrite(this, clientHandler.clientUsername);
            }
        }
        bufferedWriterWrite(this, "----------------------------------------");
    }

}
