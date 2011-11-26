package bashoid;


public interface AddonListener {

    public void sendAddonAction(String target, String msg);
    public void sendAddonMessage(String target, String msg);
    public void sendAddonMessageToChannels(String msg);

    public String getBotNickname();

}
