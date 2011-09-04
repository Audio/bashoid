package bashoid;


public interface AddonListener {

    public void sendAddonMessage(String target, String msg);
    public void sendAddonMessageToChannels(String msg);

}
