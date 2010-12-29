package bash;

public class Quote implements Comparable {

    private int    id;
    private String content;
    private int    score;

    public Quote(String value, int score, int id) {
        this.id = id;
        this.content = value;
        this.score = score;
    }

    public int lines() {
      int lastIndex = -5;
      int count = 0;

      while (true) {
        lastIndex = content.indexOf( "<br />", lastIndex + 5);
        if (lastIndex != -1)
          ++count;
        else
          return count + 1;
      }
    }

    public String getTextId() {
      return String.valueOf(id);
    }

    public String getContent() {
        return content;
    }

    public int compareTo(Object o) {
        Quote other = (Quote) o;

        if (score < other.score)
            return 1;
        else if (score > other.score)
            return -1;

        return 0;
    }

}
