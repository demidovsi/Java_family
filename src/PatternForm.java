import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class PatternForm extends JPanel {
    static Preferences userPrefs;
    static UserLanguages languages;
    private final Font font = new Font("Arial", Font.BOLD, 16);
    static boolean exist;
    public final JButton parentForm;
    public static JButton self = new JButton();  // для передачи форме message
    /* Конструктор класса */
    public PatternForm(JButton parentObject) {
        super();
        parentForm = parentObject;
        self.addActionListener((e) -> changeLanguage());
        createGUI();
        makeSize();
        setVisible(true);
        exist = true;
        changeFont(font);
    }
    public void changeLanguage() {}
    public void createGUI() {}
    public void saveSize() {}
    public void makeSize() {}
    public void refresh() {}
    public void changeFont(Font font) {}
    public void closeForm() {exist = false; saveSize();}
    public void beforeWork(Preferences user, UserLanguages lang) {
        setUserPreferences(user);
        setUserLanguages(lang);
        prepareWork();
    }
    public void setUserPreferences(Preferences user) {userPrefs = user;}
    public void setUserLanguages(UserLanguages lang) {languages = lang;}
    public void prepareWork() {};
}
