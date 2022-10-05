import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserLanguages {
    int count_language = 0;  // количество возможных языков
    String[] idLanguages;  // идентификаторы возможных языков
    String[] nameLanguages;  // тексты имени возможных языков
    String appLanguage = "en";  // идентификатор текущего языка
    JSONObject currentLanguage;  // состав текстов выбранного языка

    /* Конструктор класса */
    public UserLanguages() {
        load();
    }

    private void load() {
        /*
        Читаем список возможных языков.
         */
        Path path = Paths.get("languages/texts.json");
        if (Files.exists(path)) {
            try {
                String contents = new String(Files.readAllBytes(path));
                JSONObject langs = new JSONObject(contents);
                JSONArray arrayLangs = langs.getJSONArray("languages");
                System.out.println(arrayLangs);
                count_language = arrayLangs.length();
                idLanguages = new String[count_language];
                nameLanguages = new String[count_language];
                for (int i = 0; i < count_language; i++) {
                    idLanguages[i] = arrayLangs.getJSONObject(i).getString("id");
                    nameLanguages[i] = arrayLangs.getJSONObject(i).getString("text");
                }
            } catch (Exception e) {
                System.out.println("Failed to parse." + e);
            }
        }

    }

    public int getCountLanguage() {
        return count_language;
    }

    public String getAppLanguage() {
        return appLanguage;
    }

    public void setAppLanguage(String value) {
        /*
        Устанавливаем выбранный язык и считываем словарь массивов словарей.
         */
        appLanguage = value;
        Path path = Paths.get("languages/texts_" + appLanguage + ".json");
        if (Files.exists(path)) {
            try {
                String contents = new String(Files.readAllBytes(path));
                currentLanguage = new JSONObject(contents);
            } catch (Exception e) {
                System.out.println("Failed to parse." + e);
            }
        }
    }
    public String getText(String key, int id, String comment){
        String result = comment;
        try {
            JSONArray arrayLangs = currentLanguage.getJSONArray(key);
            for (int i = 0; i < arrayLangs.length(); i++) {
                if (arrayLangs.getJSONObject(i).getInt("id") == id) {
                    result = arrayLangs.getJSONObject(i).getString("text");
                    break;
                }
            }
        } catch (Exception e) {};
        return result;
    }
    public String getIdLanguage(int index) {
        if (index < idLanguages.length) return idLanguages[index]; else return "";
    }
    public String getNameLanguage(int index) {
        if (index < nameLanguages.length) return nameLanguages[index]; else return "";
    }
    public int getCurrentIndex(){
        for (int i=0; i < idLanguages.length; i++) {
            if (appLanguage.equals(idLanguages[i])) {
                return i;
            }
        }
        return 0;
    }
}