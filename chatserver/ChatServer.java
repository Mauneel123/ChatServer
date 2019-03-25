import javafx.scene.input.DataFormat;
import java.util.Date;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;


final class ChatServer {
    private static int uniqueId = 0;
    // Data structure to hold all of the connected clients
    private final List<ClientThread> clients = new ArrayList<>();
    private final List<TicTacToeGame> games = new ArrayList<>();
    private final int port;			// port the server is hosted on


    private ChatServer(int port) {
        this.port = port;
    }

  
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Socket socket = serverSocket.accept();
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
                clients.add((ClientThread) r);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  
    private synchronized void directMessage(String message, String username) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String formattedMessage = format.format(now) + " " + message;
        System.out.print(formattedMessage);

        for (ClientThread clientThread : clients) {
            if (clientThread.username.equalsIgnoreCase(username)) {
                clientThread.writeMessage(formattedMessage);
            }
        }
    }

    private synchronized void broadcast(String message){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        for(int i=0;i<clients.size();i++){
            clients.get(i).writeMessage(format.format(now)+" "+message);
        }
    }

    private synchronized void remove(int id){
        for(int i=0;i<games.size();i++){
            if(games.get(i).getPlayerX().equalsIgnoreCase(clients.get(id).username)||games.get(i).getPlayerO().equalsIgnoreCase(clients.get(id).username)){
                games.remove(i);
            }
        }
        clients.remove(id);
    }

    private void close(){
        for(int i=0;i<clients.size();i++){
            try {
                clients.get(i).socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            remove(i);
        }
    }



    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        ChatServer server=null;
        if(args.length==0){
            server = new ChatServer(1500);
        }else{
            try {
                server = new ChatServer(Integer.parseInt(args[0]));
            }catch(NumberFormatException e){

            }
        }
        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;                  // The socket the client is connected to
        ObjectInputStream sInput;       // Input stream to the server from the client
        ObjectOutputStream sOutput;     // Output stream to the client from the server
        String username;                // Username of the connected client
        ChatMessage cm;                 // Helper variable to manage messages
        int id;

        /*
         * socket - the socket the client is connected to
         * id - id of the connection
         */
        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            boolean unique;
            boolean noSpace;
            do {
                unique=true;
                noSpace=true;
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).username.equalsIgnoreCase(username) && clients.get(i).id != id) {
                        unique=false;
                        try {
                            sOutput.writeObject("This username is taken, please choose another");
                            ChatMessage cm = (ChatMessage) sInput.readObject();
                            username=cm.getMessage();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (username.indexOf(' ')!=-1) {
                    noSpace=false;
                    try {
                        sOutput.writeObject("No spaces allowed in usernames, please pick another");
                        ChatMessage cm = (ChatMessage) sInput.readObject();
                        username=cm.getMessage();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }while(!unique||!noSpace);
        }

        private boolean writeMessage(String msg){
            if(socket.isConnected()){
                try {
                    sOutput.writeObject(msg);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            return socket.isConnected();
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            broadcast(username+" has joined the server!");
            while(socket.isConnected()) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(cm.getRecipient().equalsIgnoreCase(username)){
                    try{
                        sOutput.writeObject("You cannot send things to yourself");
                        continue;
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                if (cm.getType() == 1) {
                    try {
                        sOutput.writeObject("/logout");
                        sOutput.flush();
                        remove(clients.indexOf(this));
                        broadcast(username + " has left the server");
                        this.socket.close();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(cm.getType()==2){
                    boolean exists=false;
                    for(int i=0;i<clients.size();i++) {
                        if(cm.getRecipient().equalsIgnoreCase(clients.get(i).username)){
                            directMessage(username+"->"+cm.getRecipient()+": "+cm.getMessage(),cm.getRecipient());
                            directMessage(username+"->"+cm.getRecipient()+": "+cm.getMessage(),username);
                            exists=true;
                            break;
                        }
                    }
                    if(!exists) {
                        try {
                            sOutput.writeObject("There is no one by that name on this server");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else if(cm.getType()==3) {
                    for(int i=0;i<clients.size();i++){
                        if(i!=clients.indexOf(this)){
                            try {
                                sOutput.writeObject(clients.get(i).username);
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }else if(cm.getType()==4) {
                    boolean foundGame=false;
                    boolean busy=false;
                    int game=-1;
                    for(int i=0;i<games.size();i++){
                        if(games.get(i).getPlayerX().equalsIgnoreCase(this.username)||games.get(i).getPlayerO().equalsIgnoreCase(this.username)){
                            if(games.get(i).getPlayerX().equalsIgnoreCase(cm.getRecipient())||games.get(i).getPlayerO().equalsIgnoreCase(cm.getRecipient())){
                                foundGame=true;
                                game=i;
                                if(cm.getMessage().equalsIgnoreCase("")){
                                    try{
                                        sOutput.writeObject(games.get(i).toString());
                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }
                                }else{
                                    int move=-1;
                                    try{
                                        move=Integer.parseInt(cm.getMessage());
                                    }catch(NumberFormatException e){
                                        try {
                                            sOutput.writeObject("Please pass integer moves");
                                            break;
                                        }catch (IOException f){
                                            f.printStackTrace();
                                        }
                                    }
                                    int place = games.get(i).place(move,username);
                                    if(place==-1){
                                        try{
                                            sOutput.writeObject("It is not your turn");
                                        }catch (IOException e){
                                            e.printStackTrace();
                                        }
                                    }else if(place==-2){
                                        try{
                                            sOutput.writeObject("That spot it already taken");
                                        }catch (IOException e){
                                            e.printStackTrace();
                                        }
                                    }else{
                                        directMessage(games.get(i).toString(),username);
                                        directMessage(games.get(i).toString(),cm.getRecipient());
                                        directMessage("Server: "+username+" has taken their turn.  It is your turn now",cm.getRecipient());
                                    }
                                    break;
                                }
                            }else{
                                try{
                                    sOutput.writeObject("You are already playing a game");
                                    busy=true;
                                    break;
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }else if(games.get(i).getPlayerX().equalsIgnoreCase(cm.getRecipient())||games.get(i).getPlayerO().equalsIgnoreCase(cm.getRecipient())){
                            try{
                                sOutput.writeObject("User is already playing a game");
                                busy=true;
                                break;
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    if(!foundGame&!busy){
                        if(cm.getMessage().equalsIgnoreCase("")) {
                            games.add(new TicTacToeGame(username,cm.getRecipient()));
                            directMessage("Server: "+username+" has started a game with you",cm.getRecipient());
                            directMessage(TicTacToeGame.getSampleBox(),cm.getRecipient());
                            try{
                                sOutput.writeObject(TicTacToeGame.getSampleBox());
                                sOutput.writeObject("New game created\nNow place your move");
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }else{
                            try{
                                sOutput.writeObject("You currently do not have a game running with this person");
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    if(foundGame){
                        if(games.get(game).isOver()) {
                            String opponent="";
                            int result = games.get(game).checkwinner();
                            if(games.get(game).getPlayerX().equalsIgnoreCase(username)){
                                for(int i=0;i<clients.size();i++){
                                    if(clients.get(i).username.equalsIgnoreCase(games.get(game).getPlayerO())){
                                        opponent=clients.get(i).username;
                                    }
                                }
                            }else if(games.get(game).getPlayerO().equalsIgnoreCase(username)){
                                for(int i=0;i<clients.size();i++){
                                    if(clients.get(i).username.equalsIgnoreCase(games.get(game).getPlayerX())){
                                        opponent=clients.get(i).username;
                                    }
                                }
                            }
                            try{
                                directMessage(games.get(game).toString(),opponent);
                                if(result==0) {
                                    sOutput.writeObject("The game was a draw");
                                    directMessage("Server: The game was a draw",opponent);
                                }else if(result==1){
                                    sOutput.writeObject(games.get(game).getPlayerO()+" won the game");
                                    directMessage("Server: "+games.get(game).getPlayerO()+" won the game",opponent);
                                }else if(result ==2){
                                    sOutput.writeObject(games.get(game).getPlayerX()+" won the game");
                                    directMessage("Server: "+games.get(game).getPlayerX()+" won the game",opponent);
                                }
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                            games.remove(game);
                        }
                    }
                } else{
                        broadcast(username+":"+cm.getMessage());
                }
                // Send message back to the client
            }
        }
    }
}