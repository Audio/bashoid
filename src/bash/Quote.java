package bash;


public class Quote implements Comparable<Quote> {

    private int id;
    private String[] content;
    private int score;

    public Quote(String[] content, int score, int id) {
        this.id = id;
        this.content = content;
        this.score = score;
    }

    public int lines() {
        return content.length;
    }

    public String getTextId() {
        return String.valueOf(id);
    }

    public String[] getContent() {
        return content;
    }

    public int compareTo(Quote other) {
        if (score < other.score)
            return 1;
        else if (score > other.score)
            return -1;

        return 0;
    }

}
