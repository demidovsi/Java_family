import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class TForm extends JFrame {
    private static JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP); // Панель с вкладками
    private static Summary panelSumma;
    private Preferences userPrefs;
    private UserLanguages languages;
    private ChooseConfig chooseConfig;
    private ChooseLanguage chooseLanguage;
    private JButton self = new JButton();
    private JMenu console, compliment;
    private JMenuItem font, lang, exit, config;
    private JButton parentForm;
//--------------------------------------------------------
    /* Конструктор класса */
    public TForm(UserLanguages lang, Preferences user, JButton parentObject)
    {
        super("");
        parentForm = parentObject;
        userPrefs = user;
        languages = lang;
        RestAPI restApi = new RestAPI(userPrefs);
        // ------------------------
        restApi.get("MDMProxy.Inform", "", "", true, "");
        if (restApi.isOk()) {
            System.out.print("Okey: ");
            System.out.println(restApi.getResponseMessage());
        } else {
            System.out.print("Error " + Integer.toString(restApi.getResponseCode()) + ": ");
            System.out.println(restApi.getResponseMessage());
        };
        // --------------------------
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        // Создание строки главного меню
        JMenuBar menuBar = new JMenuBar();
        // Добавление в главное меню выпадающих пунктов меню
        menuBar.add(createConsole());
        menuBar.add(createOptions());
// Создание вкладок
        panelSumma = new Summary();
        panelSumma.setUserLanguages(languages);
        panelSumma.setUserPreferences(userPrefs);
        panelSumma.refresh();
        tabs.addTab("", panelSumma);
        add(tabs);

        JPanel panelOne = new JPanel();
        tabs.addTab("", panelOne);
        // Подключаем меню к интерфейсу приложения
        setJMenuBar(menuBar);
        addWindowListener(new WindowListener() {
                public void windowActivated(WindowEvent event) {}
                public void windowClosed(WindowEvent event) {}
                public void windowDeactivated(WindowEvent event) {}
                public void windowDeiconified(WindowEvent event) {}
                public void windowIconified(WindowEvent event) {}
                public void windowOpened(WindowEvent event) {}
                public void windowClosing(WindowEvent event) {
                    Object[] options = { "Yes", "NO!" };
                    int rc = JOptionPane.showOptionDialog(
                            event.getWindow(),
                            languages.getText("main", 17,"Закрыть окно?"),
                            languages.getText("main", 16, "Подтверждение"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);
                    if (rc == 0) {
                        event.getWindow().setVisible(false);
                        closeForm();
                    }
                }
            }
        );
        //
        self.addActionListener((e) -> {
            changeLanguage();
            // передать наверх
            ActionEvent event = new ActionEvent(parentForm, Event.F2, "changeLanguage");
            ActionListener[] listeners;
            listeners = parentForm.getActionListeners();
            listeners[0].actionPerformed(event);
        });
        changeLanguage();
        // Открытие окна
        setVisible(true);
    }
    public void makeSize() {
        /*        Загрузка геометрии и топологии         */
        setLocation(userPrefs.getInt("main_left", 100), userPrefs.getInt("main_top", 50));
        setSize(userPrefs.getInt("main_width", 1200), userPrefs.getInt("main_height", 800));
    }
    private void saveSize() {
        /*        Спасение геометрии и топологии         */
        userPrefs.putInt("main_width", getWidth());
        userPrefs.putInt("main_height", getHeight());
        userPrefs.putInt("main_top", getLocation().y);
        userPrefs.putInt("main_left", getLocation().x);
    }
    private void closeForm() {
        /*        Закрыть программу и все дочерние формы      */
        saveSize();
        panelSumma.saveSize();
        if (chooseConfig != null) chooseConfig.saveSize();
        if (chooseLanguage != null) chooseLanguage.saveSize();
        System.exit(0);
    }
    //--------------------------------------------------------
    private JMenu createConsole() {
        console = new JMenu();
        font = new JMenuItem("", new ImageIcon("images/font.png"));
        lang = new JMenuItem("");
        lang.addActionListener((e) -> {chooseLanguage = new ChooseLanguage(languages, userPrefs, self);
            chooseLanguage.makeSize();});
        exit = new JMenuItem("", new ImageIcon("images/exit.png"));
        exit.addActionListener((e) -> {closeForm();});
// Добавление к пункту меню
        console.add(font);
        console.add(lang);
        console.addSeparator();
        console.add(exit);
        return console;
    }
    private JMenu createOptions() {
        compliment = new JMenu("");
        config = new JMenuItem();
        config.addActionListener((e) -> {chooseConfig = new ChooseConfig(languages, userPrefs); chooseConfig.makeSize();});
// Добавление к пункту меню
        compliment.add(config);
        return compliment;
    }
    private void reSend(JFrame form, JButton self, String command){
        if (form != null) {
            ActionEvent event = new ActionEvent(parentForm, Event.F2, "changeLanguage");
            ActionListener[] listeners;
            listeners = self.getActionListeners();
            listeners[0].actionPerformed(event);
        }

    }
    private void changeLanguage() {
        /*        Формирование текстов по выбранному языку         */
        setTitle(languages.getText("main", -1, "Расходы семьи (Java)"));
        console.setText(languages.getText("main", 8, "Консоль"));
        font.setText(languages.getText("main", 1, "Выбрать фонт"));
        lang.setText(languages.getText("main", 2, "Выбрать язык"));
        exit.setText(languages.getText("main", 6, "Выход"));
        compliment.setText(languages.getText("main", 9, "Дополнительно"));
        config.setText(languages.getText("form", 11, "Настройки связи с БД"));
        reSend(chooseConfig, chooseConfig.self, "changeLanguage");
        tabs.setTitleAt(0, languages.getText("form", 7, "Сводные расходы"));
        tabs.setTitleAt(1, languages.getText("form", 8,"Суточные расходы"));
        panelSumma.changeLanguage();
    }
}
