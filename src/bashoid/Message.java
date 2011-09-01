package bashoid;


public class Message {

    public String channel;
    public String author;
    public String login;
    public String hostname;
    public String text;
    
    public Message(String channel, String author, String login, String hostname, String text) {
        this.channel = channel;
        this.author = author;
        this.login = login;
        this.hostname = hostname;
        this.text = text;
    }

}
