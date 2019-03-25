import java.io.Serializable;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;
    private int type;
    private String message;
    private String recipient;

    static final int MESSAGE = 0, LOGOUT = 1, DM = 2, LIST = 3, TICTACTOE = 4;


    public ChatMessage(int type){
        this(type,"","");
    }

    public ChatMessage(int type, String message){
        this.type=type;
        this.message=message.substring(0,message.indexOf(' '));
        this.recipient=recipient.substring(message.indexOf(' '),message.length());
    }

    public ChatMessage(int type, String message, String recipient){
        this.type=type;
        this.message=message;
        this.recipient=recipient;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }
}