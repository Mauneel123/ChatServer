import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    private Thread t;

    /* ChatClient constructor
     * @param server - the ip address of the server as a string
     * @param port - the port number the server is hosted on
     * @param username - the username of the user connecting
     */
    private ChatClient(String username, int port, String server) {
        this.username=username;
        this.server = server;
        this.port=port;
    }

    public ChatClient(String username, int port){
        this(username,port,"localhost");
    }

    public ChatClient(String username){
        this(username,1500);
    }



    /**
     * Attempts to establish a connection with the server
     * @return boolean - false if any errors occur in startup, true if successful
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (ConnectException e){
            System.out.println("A server is not currently running at this location");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Attempt to create output stream
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Attempt to create input stream
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Create client thread to listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }




    /*
     * Sends a string to the server
     * @param msg - the message to be sent
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
       
        ChatClient client=null;
        if(args.length==0){
            client = new ChatClient("CS 180");
        }else if(args.length==1){
            client = new ChatClient(args[0]);
        }else if(args.length==2){
            try {
                client = new ChatClient(args[0], Integer.parseInt(args[1]));
            }catch(NumberFormatException e){

            }
        }else if(args.length>=3){
            try{
                client = new ChatClient(args[0], Integer.parseInt(args[1]), args[2]);
            }catch(NumberFormatException e){

            }
        }

        // Create your client and start it
        if(!client.start()){
            return;
        }

        //Listens to user in console
        Scanner scanner = new Scanner(System.in);
        String input;
        while(scanner.hasNextLine()){
            input = scanner.nextLine();
            if(input.equalsIgnoreCase("/logout")){
                try {
                    client.sendMessage((new ChatMessage(1)));
                    client.sOutput.flush();
                    try{
                        client.t.join();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    client.sOutput.close();
                    client.sInput.close();
                    client.socket.close();
                    break;
                }catch (IOException e){

                }
            }else if(input.contains("/ttt")){
                int spaceCount=0;
                for(int i=0;i<input.length();i++){
                    if(input.charAt(i)==' '){
                        spaceCount++;
                    }
                }
                if(spaceCount==1) {
                    client.sendMessage(new ChatMessage(4, "", input.substring(input.indexOf(' ')+1,input.length())));
                }else if(spaceCount==2){
                    client.sendMessage(new ChatMessage(4,input.substring(input.lastIndexOf(' ')+1,input.length()),input.substring(input.indexOf(' ')+1,input.lastIndexOf(' '))));
                }else{
                    System.out.println("Please use /ttt command in the form '/ttt <opponent>' or '/ttt <opponent> ,<move>");
                }
            }else if(input.equalsIgnoreCase("/list")) {
                client.sendMessage(new ChatMessage(3));
            }else if(input.contains("/msg")) {
                input=input.substring(input.indexOf(' ')+1,input.length());
                client.sendMessage(new ChatMessage(2,input.substring(input.indexOf(' ')+1,input.length()), input.substring(0,input.indexOf(' '))));
            }else{
                client.sendMessage(new ChatMessage(0, input, "everyone"));
            }
        }


        // Send an empty message to the server

    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            try {
                while(socket.isConnected()) {
                    String msg = (String) sInput.readObject();
                    if(msg.equalsIgnoreCase("/logout")){
                        sInput.close();
                        break;
                    }
                    System.out.println(msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}