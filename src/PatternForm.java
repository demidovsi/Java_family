import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    public String translateFromBase(String st) {
        return st.replace("~LF~", "\n")
                .replace("~A~", "(")
                .replace("~B~", ")")
                .replace("~a1~", "@")
                .replace("~a2~", ",")
                .replace("~a3~", "=")
                .replace("~a4~", "\"")
                .replace("~a5~", "'")
                .replace("~a6~", ":")
                .replace("~b1~", "/")
                ;
    }
    public String translateToBase(String st) {
        return st.replace( "\n", "~LF~")
                .replace( "(", "~A~")
                .replace( ")", "~B~")
                .replace( "@", "~a1~")
                .replace( ",", "~a2~")
                .replace( "=", "~a3~")
                .replace( "\"", "~a4~")
                .replace( "'", "~a5~")
                .replace( ":", "~a6~")
                .replace( "/", "~b1~")
                ;
    }
    public void sendMessage(int id, String text) {
        ActionEvent event = new ActionEvent(parentForm, id, text);
        ActionListener[] listeners = parentForm.getActionListeners();
        for (ActionListener listener : listeners)
            listener.actionPerformed(event);
    }
}
