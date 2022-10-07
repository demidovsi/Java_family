import java.util.prefs.Preferences;

class Main {
    public static void createGUI()
    {
        Preferences  userPrefs = Preferences.userRoot().node("family"); // параметры программы
        UserLanguages userLanguages = new UserLanguages(); // возможные языки и их использование
        userLanguages.setAppLanguage(userPrefs.get("language", "ru"));
        TForm form = new TForm(userLanguages, userPrefs);
        form.pack();
        form.makeSize();
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() { createGUI(); }
        });
    }
}
