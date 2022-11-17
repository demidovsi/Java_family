import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class TForm extends JFrame {
    private final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP); // Панель с вкладками
    private static Summary summary;
    private static OneDay oneDay;
    private final Preferences userPrefs;
    private final UserLanguages languages;
    private ChooseConfig chooseConfig;
    private ChooseLanguage chooseLanguage;
    private final JButton self = new JButton();
    private JMenu console, compliment;
//    private static JButton createApp; // кнопка создания новой схемы БД
    private final JLabel statusBar = new JLabel("");
    private JMenuItem font, lang, exit, config;
    private final JComboBox apps = new JComboBox();
    private String token;
    private static RestAPI restAPI;
    private CheckConnection checkConnection;
    private static boolean exist;
//--------------------------------------------------------
/* Конструктор класса */
    public TForm(UserLanguages lang, Preferences user)
    {
        super("");
        userPrefs = user;
        languages = lang;
        statusBar.setForeground(Color.red);
        restAPI = new RestAPI();
        RestAPI.userPreferences = userPrefs;
        RestAPI.unitConfig = new UnitConfig(userPrefs);

        makeLogin();
        // --------------------------
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
// Создание строки главного меню
        JMenuBar menuBar = new JMenuBar();
// Добавление в главное меню выпадающих пунктов меню
        menuBar.add(createConsole());
        menuBar.add(createOptions());
// Создание вкладок
        summary = new Summary(self);
        summary.beforeWork(userPrefs, languages);
//        summary.refresh();
        tabs.addTab("", summary);

        oneDay = new OneDay(self);
        oneDay.beforeWork(userPrefs, languages);
//        oneDay.refresh();
        tabs.addTab("", oneDay);

        add(tabs, BorderLayout.CENTER);
// панель кнопок управления
//        createApp = new JButton();
//        createApp.addActionListener((e) -> createAppClick());
        apps.setEditable(false);
        apps.setMaximumRowCount(20);

        JPanel panelButtons = new JPanel();
//        panelButtons.add(createApp, "North");
        panelButtons.add(apps, BorderLayout.NORTH);

        JPanel panelStatusBar = new JPanel();
        panelStatusBar.add(statusBar, BorderLayout.SOUTH);

        JPanel panelCommon = new JPanel();
        panelCommon.add(panelButtons, BorderLayout.NORTH);
        panelCommon.add(panelStatusBar, BorderLayout.SOUTH);
        add(panelCommon, BorderLayout.SOUTH);

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
            if (e.getID() == Event.F2) changeLanguage();  // смена языка
            if (e.getID() == Event.F3) statusBar.setText(e.getActionCommand());  // смена состояния соединения с БД
            if (e.getID() == Event.F4) {  // смена базы или версии
                setTitle(languages.getText("main", -1, "Расходы семьи (Java)") + " " + e.getActionCommand());
                summary.refresh();
                oneDay.refresh();
            }
            if (e.getID() == Event.F5) {  // восстановление соединения
                makeLogin();
                summary.refresh();
                oneDay.refresh();
            }
            if (e.getID() == Event.F6) {
                checkConnection.needStop = true;
                makeLogin();
                checkConnection = new CheckConnection(restAPI, self, new UnitConfig(userPrefs).getIntervalConnection());
                checkConnection.start();
                summary.refresh();
                oneDay.refresh();
            }
            if (e.getID() == -1001) {
                String[] par = e.getActionCommand().split("/");
                oneDay.setSelectedRow(Integer.parseInt(par[1]), par[0]);
                tabs.setSelectedIndex(1);
            }
        });
        changeLanguage();
// Открытие окна
        setVisible(true);
// поток контроля соединения
        checkConnection = new CheckConnection(restAPI, self, new UnitConfig(userPrefs).getIntervalConnection());
        checkConnection.start();
    }
    public void makeSize() {
        /*        Загрузка геометрии и топологии         */
        setLocation(userPrefs.getInt("main_left", 100), userPrefs.getInt("main_top", 50));
        setSize(userPrefs.getInt("main_width", 1200), userPrefs.getInt("main_height", 800));
        tabs.setSelectedIndex(userPrefs.getInt("page_index", 0));
    }
    public void makeLogin(){
        restAPI.login();
        if (restAPI.isOk()) {
            JSONObject st = new JSONObject(restAPI.getResponseMessage());
            token = st.getString("accessToken");
//            System.out.println("token=" + token);
            loadApp(restAPI);
        }
        else {
            token = null;
            System.out.println("login ERROR: " + restAPI.getResponseCode() + " = " +
                    restAPI.getResponseMessage());
        }
    }
    private void saveSize() {
        /*        Спасение геометрии и топологии         */
        userPrefs.putInt("main_width", getWidth());
        userPrefs.putInt("main_height", getHeight());
        userPrefs.putInt("main_top", getLocation().y);
        userPrefs.putInt("main_left", getLocation().x);
        userPrefs.putInt("page_index", tabs.getSelectedIndex());
    }
    private void closeForm() {
        /*        Закрыть программу и все дочерние формы      */
        saveSize();
        summary.closeForm();
        oneDay.closeForm();
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
        config.addActionListener((e) -> {
            chooseConfig = new ChooseConfig(languages, userPrefs, self);
            chooseConfig.makeSize();
        });
// Добавление к пункту меню
        compliment.add(config);
        return compliment;
    }
    private void reSend(JFrame form, JButton self, String command){
        if (form != null) {
            ActionEvent event = new ActionEvent(form, Event.F2, command);
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
//        createApp.setText(languages.getText("one", 12,"Создать"));
//        createApp.setToolTipText(languages.getText("form", 4,"Создать новую систему и схему базы данных"));
        summary.changeLanguage();
        oneDay.changeLanguage();
    }
    private void loadApp(RestAPI restAPI){
        if (token != null) {
            restAPI.get("v1/app?token=" + token);
            if (restAPI.isOk()) {
                exist = false;
                int index = apps.getSelectedIndex();
                apps.removeAllItems();
                JSONArray units = new JSONArray(restAPI.getResponseMessage());
                UnitConfig unitConfig = new UnitConfig(userPrefs);
                String currentSchemaName = unitConfig.getSchemaName();
                String st, schemaName;
                for (int i = 0; i < units.length(); i++) {
                    schemaName = units.getJSONObject(i).getString("code");
                    st = "(" + schemaName + ") " + units.getJSONObject(i).getString("description");
                    apps.addItem(st);
                    if (currentSchemaName.equals(schemaName)) index = i;
                }
                exist = true;
                apps.setSelectedIndex(index);
            }
        }
    }
}
