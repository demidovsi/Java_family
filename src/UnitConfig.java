import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class UnitConfig {
    static String currentNameConfig;  // имя текущей конфигурации
    static String url, userName, password, schemaName, infoCode;
    static int intervalConnection;
    public UnitConfig(Preferences user)
    {
        currentNameConfig = user.get("current_config", "Домашняя база");
        Path path = Paths.get("config.json");
        if (Files.exists(path)) {
            try {
                String contents = new String(Files.readAllBytes(path));
                contents = contents.trim();
                // убираем [ в начале и ] в конце
                if (contents.charAt(0) == '[') {contents = contents.substring(1, contents.length() - 1);}
                contents = "{\"configs\": [" + contents + "]}";
                JSONObject st = new JSONObject(contents);
                JSONArray configs = st.getJSONArray("configs");
                int count = configs.length();
                for (int i = 0; i < count; i++) {
                    if (configs.getJSONObject(i).getString("name").equals(currentNameConfig)) {
                        setUrl(configs.getJSONObject(i).getString("url"));
                        setUserName(configs.getJSONObject(i).getString("user_name"));
                        setPassword(configs.getJSONObject(i).getString("password"));
                        setInfoCode(configs.getJSONObject(i).getString("info_code"));
                        setSchemaName(configs.getJSONObject(i).getString("schema_name"));
                        setIntervalConnection(configs.getJSONObject(i).getInt("interval"));
                        break;
                    }
                }

            } catch (Exception e) {
                System.out.println("Error UnitConfig" + e.getMessage());
            }
        }
    }
    public String getUrl() {return url;}
    public String getUserName() {return userName;}
    public String getPassword() {return password;}
    public String getSchemaName() {return schemaName;}
    public String getInfoCode() {return infoCode;}
    public int getIntervalConnection() {return intervalConnection;}

    public void setUrl(String val) {url = val;}
    public void setUserName(String val) {userName = val;}
    public void setPassword(String val) {password = val;}
    public void setSchemaName(String val) {schemaName = val;}
    public void setInfoCode(String val) {infoCode = val;}
    public void setIntervalConnection(int val) {intervalConnection = val;}
}
