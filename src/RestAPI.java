import javax.net.*;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.HttpURLConnection;
import java.util.prefs.Preferences;
//import java.io.BufferedReader;
import java.net.*;
import java.io.*;

public class RestAPI {
    private Preferences userPreferences;
    private int responseCode;
    private String responseMessage;
    public RestAPI(Preferences user){
        userPreferences = user;
    };
    public String get(String mes, String param, String language,  boolean showError, String tokenUser) {
        try {
            UnitConfig unitConfig = new UnitConfig(userPreferences);
            final URL url = new URL(unitConfig.getUrl() + mes);
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(100);
            con.setReadTimeout(100);
            try {
                InputStream input = con.getInputStream();
                responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(input));
                String inputLine;
                final StringBuilder content = new StringBuilder();
                while (true) {
                    inputLine = in.readLine();
                    if (inputLine == null) break;
                    content.append(inputLine);
                }
                responseMessage = content.toString();
                return responseMessage;
            } catch (final Exception ex) {
                responseCode = con.getResponseCode();
                responseMessage = con.getResponseMessage();
//                ex.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            responseCode = -1;
            responseMessage = "Нет соединения с сервером";
            return null;
        }
    }
    public boolean isOk() {return responseCode == 200;}
    public String getResponseMessage() {return responseMessage;}
    public int getResponseCode() {return responseCode;}
}
