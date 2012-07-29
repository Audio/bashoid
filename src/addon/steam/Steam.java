package addon.steam;

import bashoid.Addon;
import bashoid.Message;
import java.util.*;
import utils.WebPage;

public class Steam extends Addon {

    private static final String ENCODING = "UTF-8";

    private static boolean containsOnlyDigits(String str) {
        if (str == null || str.length() == 0)
            return false;

        for (int i = 0; i < str.length(); i++)
            if (!Character.isDigit(str.charAt(i)))
                return false;

        return true;
    }

    private String extractParameterValue(String str, String parameterName) {
        int paraIdx = str.indexOf("\""+parameterName+"\"");
        if (paraIdx < 0)
            return "";
        int startIdx = str.indexOf(":", paraIdx)+1;
        if (startIdx <= -1)
            return "";
        String value;
        int endIdx = str.indexOf("\"", startIdx+1);
        if (endIdx <= -1)
            value = str.substring(startIdx);
        else
            value = str.substring(startIdx, endIdx);
        value = value.replaceAll("\"", "");
        return value;
    }

    public String loadGames(String profileId, String parameters) throws Exception {
        String url = "http://steamcommunity.com/";
        if (containsOnlyDigits(profileId))
            url += "profiles/";
        else
            url += "id/";
        url += profileId + "/" + parameters;
        WebPage page = WebPage.loadWebPage(url, ENCODING);
        String pageContent = page.getContent();
        int lineIdx = pageContent.indexOf("var rgGames");
        if (lineIdx <= -1) {
            reaction.add("Invalid profile!");
            return null;
        }
        int startIdx = pageContent.indexOf("[", lineIdx)+1;
        int endIdx = pageContent.indexOf("];", startIdx);
        String gamesString = pageContent.substring(startIdx, endIdx);
        // chop first and last char
        gamesString = gamesString.substring(1, gamesString.length()-1);
        return gamesString;
    }

    public String[] getGamesData(String gamesString) {
        return gamesString.split("\\},\\{");
    }
    public List<String> parseGamesData(String gamesString, String parameter) {
        List<String> games = new ArrayList<String>();
        String[] data = getGamesData(gamesString);
        for (String str : data)
            games.add(extractParameterValue(str, parameter));

        return games;
    }
    public String SelectRandomGame(String profileId) throws Exception {
        String gamesString = loadGames(profileId, "games?tab=all");
        List<String> games = parseGamesData(gamesString, "name");
        int gamePosition = new Random().nextInt(games.size());
        return games.get(gamePosition);
    }

    public String SelectMostPlayedWeek(String profileId) throws Exception {
        String gamesString = loadGames(profileId, "games?tab=recent");
        List<String> games = parseGamesData(gamesString, "name");
        List<String> hours = parseGamesData(gamesString, "hours");
        List<String> hours_ever = parseGamesData(gamesString, "hours_forever");
        return "Game: " + games.get(0) + ", Played in last two weeks: " + hours.get(0) + "h, Total played: " + hours_ever.get(0) + "h";
    }

    public String SelectMostPlayedEver(String profileId) throws Exception {
        String gamesString = loadGames(profileId, "games?tab=all");
        List<String> games = parseGamesData(gamesString, "name");
        List<String> hours_ever = parseGamesData(gamesString, "hours_forever");
        return "Game: " + games.get(0) + " Total played: " + hours_ever.get(0) + "h";
    }

    public String GetGamesCount(String profileId) throws Exception {
        String gamesString = loadGames(profileId, "games?tab=all");
        List<String> games = parseGamesData(gamesString, "name");
        return Integer.toString(games.size());
    }

    public String GetPlayedWeek(String profileId) throws Exception {
        String gamesString = loadGames(profileId, "games?tab=recent");
        List<String> games = parseGamesData(gamesString, "hours");
        float total = 0;
        for (String str : games) {
            try {
                total += Float.parseFloat(str);
            }
            catch(Exception e) {
                System.out.println(str);
            }
        }

        return Float.toString(total)+"h";
    }

    public String GetPlayedEver(String profileId) throws Exception {
        String gamesString = loadGames(profileId, "games?tab=all");
        List<String> games = parseGamesData(gamesString, "hours_forever");
        float total = 0;
        for (String str : games) {
            try {
                total += Float.parseFloat(str);
            }
            catch(Exception e) {
                System.out.println(str);
            }
        }

        return Float.toString(total)+"h";
    }

    @Override
    public boolean shouldReact(Message message) {
        return message.text.startsWith("steam ") && message.text.split(" ").length == 3;
    }

    @Override
    protected void setReaction(Message message) {
        try {
            String[] messageParts = message.text.split(" ");
            String profileId = messageParts[1];

            String result;
            if(messageParts[2].equals("random"))           result = SelectRandomGame(profileId);
            else if(messageParts[2].equals("most_week"))   result = SelectMostPlayedWeek(profileId);
            else if(messageParts[2].equals("most_ever"))   result = SelectMostPlayedEver(profileId);
            else if(messageParts[2].equals("count"))       result = GetGamesCount(profileId);
            else if(messageParts[2].equals("played_ever")) result = GetPlayedEver(profileId);
            else if(messageParts[2].equals("played_week")) result = GetPlayedWeek(profileId);
            else                                           result = "Co?";

            reaction.add( result );
        } catch (Exception e) {
            System.out.println(e);
            setError("Cannot load given URL.", e);
        }
    }

}
