package addon.steam;

import bashoid.Addon;
import bashoid.Message;
import java.util.*;
import utils.WebPage;

public class Steam extends Addon {

    private static final String ENCODING = "UTF-8";

    public boolean containsOnlyDigits(String str) {
        if (str == null || str.length() == 0)
            return false;

        for (int i = 0; i < str.length(); i++)
            if (!Character.isDigit(str.charAt(i)))
                return false;

        return true;
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
        int startIdx = pageContent.indexOf("var rgGames");
        int endIdx = pageContent.indexOf(";", startIdx);
        return pageContent.substring(startIdx, endIdx);
    }

    public List<String> parseGamesString(String gamesString) {
        List<String> games = new ArrayList<>();
        for (int idx = gamesString.indexOf("name"); idx != -1; idx = gamesString.indexOf("name", idx))
        {
            int startIdx = idx+7;
            idx = gamesString.indexOf("\"", startIdx);
            games.add(gamesString.substring(startIdx, idx));
        }
        return games;
    }
    public String SelectRandomGame(String profileId) throws Exception {
        String gamesString = loadGames(profileId, "games?tab=all");
        List<String> games = parseGamesString(gamesString);
        int gamePosition = new Random().nextInt(games.size());
        return games.get(gamePosition);
    }

    public String SelectMostPlayedWeek(String profileId) throws Exception {
        String gamesString = loadGames(profileId, "games?tab=recent");
        List<String> games = parseGamesString(gamesString);
        return games.get(0);
    }

    public String SelectMostPlayedEver(String profileId) throws Exception {
        String gamesString = loadGames(profileId, "games?tab=all");
        List<String> games = parseGamesString(gamesString);
        return games.get(0);
    }

    public String GetGamesCount(String profileId) throws Exception {
        String gamesString = loadGames(profileId, "games?tab=all");
        List<String> games = parseGamesString(gamesString);
        return Integer.toString(games.size());
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
            switch (messageParts[2])
            {
                case "random":      result = SelectRandomGame(profileId); break;
                case "most_week":   result = SelectMostPlayedWeek(profileId); break;
                case "most_ever":   result = SelectMostPlayedEver(profileId); break;
                case "count":       result = GetGamesCount(profileId); break;
                default:            result = "Co?"; break;
            }
            reaction.add( result );
        } catch (Exception e) {
            setError("Cannot load given URL.", e);
        }
    }

}
