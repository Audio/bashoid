package addon.movie;

import bashoid.Addon;
import bashoid.Message;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.WebPage;

public class Movie extends Addon {

    private static final String ENCODING = "UTF-8";
    private static final String REACT_MESSAGE = "movie ";

    class MovieData {
        String title;
        String link_csfd;
        String link_imdb;
        String country;
        String year;
        String length;
        String director;
        String genre;
        String rating_csfd;
        String rating_imdb;

        /*public void print() {
            System.out.print("Title: " + title + "\n" +
                    "Link_csfd: " + link_csfd + "\n" +
                    "Link_imdb: " + link_imdb + "\n" +
                    "Country: " + country + "\n" +
                    "Year: " + year + "\n" +
                    "Length: " + length + "\n" +
                    "Director: " + director + "\n" +
                    "Genre: " + genre  + "\n" +
                    "Rating_csfd: " + rating_csfd + "\n" +
                    "Rating_imdb: " + rating_imdb + "\n");
        }*/

        @Override
        public String toString() {
            // Prometheus (2012), USA, 124 min, csfd: 70%, imdb: 7.5
            return title + " (" + year +"), " + country + ", " + length + ", Rating: " +
                    rating_csfd + " (csfd.cz), " + rating_imdb + " (imdb.com)";
        }
    }

    private String SearchMovieData(String movieSearch) throws Exception {
        String link = GetMovieLink(movieSearch);
        return GetMovieData(link);
    }

    private String GetMovieData(String link) throws Exception {
        MovieData data = new MovieData();
        data.link_csfd = link;

        LoadMovieData(data);

        if (data.link_imdb != null)
            LoadImdbData(data);

        return data.toString();
    }

    private String GetMovieLink(String movieSearch) throws Exception {
        movieSearch = movieSearch.replaceAll(" ", "+");
        String url = "http://www.csfd.cz/hledat/?q=" + movieSearch;
        WebPage page = WebPage.loadWebPage(url, ENCODING);
        Element movies = Jsoup.parse( page.getContent() ).getElementById("search-films");
        Element firstMovie = movies.getElementsByClass("subject").first();
        Element movieLink = firstMovie.getElementsByTag("a").first();
        String link = "http://www.csfd.cz" + movieLink.attr("href");
        return link;
    }

    private void LoadMovieData(MovieData data) throws Exception {
        WebPage page = WebPage.loadWebPage(data.link_csfd, ENCODING);
        Element movieInfo = Jsoup.parse( page.getContent() ).getElementById("profile");
        {
            Element title = movieInfo.getElementsByTag("h1").first();
            data.title = title.text().trim();

            Element genre = movieInfo.getElementsByClass("genre").first();
            data.genre = genre.text().trim();

            Element origin = movieInfo.getElementsByClass("origin").first();
            String[] originParts = origin.text().split(",");
            data.country = originParts[0].trim();
            data.year = originParts[1].trim();
            data.length = originParts[2].trim();

            Elements otherData = movieInfo.getElementsByTag("h4");
            for (Element ele : otherData) {
                if (ele.text().equals("Režie:"))  {
                    Element directorLink = ele.parent().getElementsByTag("a").first();
                    data.director = directorLink.text();
                    break;
                }
            }
        }

        Element sidebar = Jsoup.parse( page.getContent() ).getElementById("sidebar");
        {
            Element rating = sidebar.getElementById("rating");
            Element average = rating.getElementsByClass("average").first();
            data.rating_csfd = average.text().trim();

            Element share = sidebar.getElementById("share");
            Element links = share.getElementsByClass("links").first();
            Element childImdb = links.getElementsByClass("imdb").first();
            Element imdb = childImdb.parent();
            data.link_imdb = imdb.attr("href");
        }
    }

    private void LoadImdbData(MovieData data) throws Exception {
        WebPage page = WebPage.loadWebPage(data.link_imdb, ENCODING);

        Element sidebar = Jsoup.parse( page.getContent() ).getElementsByClass("star-box-giga-star").first();
        data.rating_imdb = sidebar.text().trim();
    }

    @Override
    public boolean shouldReact(Message message) {
        return message.text.trim().startsWith(REACT_MESSAGE) ||
                message.text.trim().startsWith("http://www.csfd.cz");
    }

    @Override
    protected void setReaction(Message message) {
        try {
            String result;
            if (message.text.startsWith(REACT_MESSAGE)) {
                String movieSearch = message.text.substring(REACT_MESSAGE.length());
                result = SearchMovieData(movieSearch);
            }
            else
                result = GetMovieData(message.text);
            reaction.add( result );
        } catch (Exception e) {
            System.out.println(e);
            if (message.text.startsWith(REACT_MESSAGE))
                setError("Cannot load given URL.", e);
        }
    }
}
