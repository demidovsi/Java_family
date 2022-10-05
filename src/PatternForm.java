import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.prefs.Preferences;

public class PatternForm extends JPanel {
    private static final long serialVersionUID = 1L;
    Preferences userPrefs;
    UserLanguages languages;
    private final Font font = new Font("Arial", Font.BOLD, 16);
    boolean exist;
    public static JButton self = new JButton();  // для передачи форме message
    /* Конструктор класса */
    public PatternForm() {
        super();
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

    public void closeForm(){
        exist = false;
        saveSize();
    }
    public void setUserPreferences(Preferences user) {userPrefs = user;}
    public void setUserLanguages(UserLanguages lang) {languages = lang;}

}
