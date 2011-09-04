package bashoid;

public interface MessageListener {

    public void sendMessageListener(String target, String msg);
    public void sendMessageToChannelsListener(String msg);
}
