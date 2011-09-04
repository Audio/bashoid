package pepa;


public final class HiddenTag {

    private String html;
    private String name;
    private String value;


    public HiddenTag(String html) {
        this.html = html;
        parseHTML();
    }

    private void parseHTML() {
        name = getAttributeValue("name");
        value = getAttributeValue("value");
    }

    private String getAttributeValue(String attributName) {
        final String toSearch = attributName  + "=\"";
        int beginPos = html.indexOf(toSearch) + toSearch.length();
        int endPos = html.indexOf("\"", beginPos);
        return html.substring(beginPos, endPos);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
