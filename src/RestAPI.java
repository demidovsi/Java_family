import java.util.prefs.Preferences;
import java.net.*;
import java.io.*;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestAPI {
    private final Preferences userPreferences;
    private int responseCode;
    private String responseMessage;
    private UnitConfig unitConfig;
    public RestAPI(Preferences user){
        userPreferences = user;
    }
    public String getUrl() {
        if (unitConfig == null) unitConfig = new UnitConfig(userPreferences);
        return unitConfig.getUrl();
    }
    public void login(){
        unitConfig = new UnitConfig(userPreferences);
        String text = "{\"login\": \"" + unitConfig.getUserName() +
                "\", \"password\": \"" + unitConfig.getPassword() + "\"}";
        text = "{\"params\": " + text + "}";
        try {
            URL url = new URL(unitConfig.getUrl() + "v1/login");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            try {
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setConnectTimeout(30000);
                con.setReadTimeout(30000);
                con.setDoOutput(true);
                try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())) {
                    writer.write(text);
                }
                if (con.getResponseCode() != 200) {
                    responseCode = con.getResponseCode();
                    responseMessage = con.getResponseMessage();
                    System.err.println("connection failed");
                }
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    responseCode = con.getResponseCode();
                    responseMessage = Optional.of(reader.lines().collect(Collectors.joining(System.lineSeparator()))).get();
                }
                } catch (final Exception ex) {
                    responseCode = con.getResponseCode();
                    responseMessage = con.getResponseMessage();
                }
        } catch (final Exception ex) {
            responseCode = -1;
            responseMessage = "No connection to the server!";
        }
    }
    public void sendMess(String directive, String mes, String params){
        unitConfig = new UnitConfig(userPreferences);
        try {
            URL url = new URL(unitConfig.getUrl() + mes);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            try {
                con.setRequestMethod(directive);
                con.setRequestProperty("Content-Type", "application/json");
                con.setConnectTimeout(30000);
                con.setReadTimeout(30000);
                con.setDoOutput(true);
                try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())) {
                    writer.write(params);
                }
                if (con.getResponseCode() != 200) {
                    responseCode = con.getResponseCode();
                    responseMessage = con.getResponseMessage();
                    System.err.println("connection failed");
                }
                try(BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(con.getInputStream(), Charset.forName("utf-8")))) {
                        new InputStreamReader(con.getInputStream()))) {
                    responseCode = con.getResponseCode();
                    responseMessage = Optional.of(reader.lines().collect(Collectors.joining(System.lineSeparator()))).get();
                }
                } catch (final Exception ex) {
                    responseCode = con.getResponseCode();
                    responseMessage = con.getResponseMessage();
                }
        } catch (final Exception ex) {
            responseCode = -1;
            responseMessage = "No connection to the server!";
        }
    }
    public void get(String mes) {
        if (unitConfig == null) unitConfig = new UnitConfig(userPreferences);
        try {
            URL url = new URL(unitConfig.getUrl() + mes);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
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
            } catch (final Exception ex) {
                responseCode = con.getResponseCode();
                responseMessage = con.getResponseMessage();
            }
        } catch (Exception e) {
            responseCode = -1;
            responseMessage = "No connection to the server!";
        }
    }
    public boolean isOk() {return responseCode == 200;}
    public String getResponseMessage() {return responseMessage;}
    public int getResponseCode() {return responseCode;}
}
