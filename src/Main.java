import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.prefs.Preferences;

class Main {
    private static Preferences userPrefs;  // параметры программы
    private static UserLanguages userLanguages = new UserLanguages(); // возможные языки и их использование
//    private static JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP); // Панель с вкладками
    private static JButton createApp; // кнопка создания новой схемы БД
    private static JButton self = new JButton();
//    private static Summary panelSumma;

    public static void createGUI()
    {
        userPrefs = Preferences.userRoot().node("family");
        userLanguages.setAppLanguage(userPrefs.get("family_language", "ru"));

        TForm form = new TForm(userLanguages, userPrefs, self);
// Создание вкладок
//        panelSumma = new Summary();
//        panelSumma.setUserLanguages(userLanguages);
//        panelSumma.setUserPreferences(userPrefs);
//        panelSumma.refresh();
//        tabs.addTab("", panelSumma);
//
//        JPanel panelOne = new JPanel();
//        tabs.addTab("", panelOne);
// панель кнопок управления
        JPanel panelButtons = new JPanel();
        createApp = new JButton();
        panelButtons.add(createApp, "North");
// Модель данных списка
        Vector<String> data = new Vector<>();
    // это временно до чтения из базы данных
        data.add("family - расходы семьи Демидовых");
        data.add("bms - строения семьи");

        JComboBox comboBox = new JComboBox(data);
        comboBox.setEditable(false);
        comboBox.setMaximumRowCount(20);
        panelButtons.add(comboBox);
// финальный аккорд
        Container container = form.getContentPane();
        container.add(panelButtons, "South");
//        container.add(tabs);
//
        form.pack();
        form.makeSize();
        self.addActionListener((e) -> changeLanguage());
        changeLanguage();
    }
    public static void changeLanguage() {
        createApp.setText(userLanguages.getText("one", 12,"Создать"));
        createApp.setToolTipText(userLanguages.getText("form", 4,"Создать новую систему и схему базы данных"));
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }
}
