/*
Форма вывода возможных языков общения для выбора одного из них.
 */
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.prefs.Preferences;
import java.awt.event.*;

public class ChooseLanguage extends JFrame {
    private boolean exist = false;
    private JButton parentForm;
    private Preferences userPrefs;
    private static UserLanguages languages;
    private static JButton apply = new JButton("", new ImageIcon("images/apply.png"));
    private static JButton close = new JButton("", new ImageIcon("images/exit.png"));
    private static Font font = new Font("Arial", Font.BOLD, 16);
    private JTable table;
    private DefaultTableModel rootModel;
    /* Конструктор класса */
    public ChooseLanguage(UserLanguages lang, Preferences user, JButton parentObject) {
        super();
        parentForm = parentObject;
        userPrefs = user;
        languages = lang;
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        addWindowListener(new WindowListener() {
                              public void windowActivated(WindowEvent event) {}
                              public void windowClosed(WindowEvent event) {}
                              public void windowDeactivated(WindowEvent event) {}
                              public void windowDeiconified(WindowEvent event) {}
                              public void windowIconified(WindowEvent event) {}
                              public void windowOpened(WindowEvent event) {}
                              public void windowClosing(WindowEvent event) {
                                  closeForm();
                              }
                          }
        );
// панель кнопок управления
        JPanel panelButtons = new JPanel();
        GridLayout layouts = new GridLayout(1, 2);
        panelButtons.setLayout(layouts);
        panelButtons.add(apply);
        panelButtons.add(close);
        close.addActionListener((e) -> closeForm());  // lambda для функционального интерфейса
        apply.addActionListener((e) -> applyClick());
// списки с информацией
        JPanel panelTable = new JPanel();
        panelTable.setBorder(BorderFactory.createTitledBorder(""));
        BorderLayout layoutTable = new BorderLayout();
        panelTable.setLayout(layoutTable);
        // определим колонки
        String[] columnNames = new String[] {"№№", "id", "Language"};
        // определим строки со значениями таблицы
        String[][] rowData = new String[languages.getCountLanguage()][3];
        for (int index=0; index < languages.getCountLanguage(); index++) {
            rowData[index][0] = Integer.toString(index + 1);
            rowData[index][1] = languages.getIdLanguage(index);
            rowData[index][2] = languages.getNameLanguage(index);
        }
        rootModel = new DefaultTableModel(rowData, columnNames);
        table = new JTable(rootModel);
        table.setRowSelectionInterval(languages.getCurrentIndex(), languages.getCurrentIndex());
        // в первой колонке выводить по центру
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
        panelTable.add(new JScrollPane(table), BorderLayout.CENTER);
// финальный аккорд
        Container container = getContentPane();
        container.add(panelButtons, BorderLayout.NORTH);
        container.add(panelTable, BorderLayout.CENTER);
// Открытие окна
        pack();
        setVisible(true);
        changeLanguage();
        changeFont(font);
        exist = true;
    }
    public void saveSize() {
        /*        Спасение геометрии и топологии         */
        exist = false;
        userPrefs.putInt("language_width", getWidth());
        userPrefs.putInt("language_height", getHeight());
        userPrefs.putInt("language_top", getLocation().y);
        userPrefs.putInt("language_left", getLocation().x);
    }
    public void makeSize() {
        /*        Загрузка геометрии и топологии         */
        setLocation(userPrefs.getInt("language_left", 100), userPrefs.getInt("language_top", 50));
        setSize(userPrefs.getInt("language_width", 300), userPrefs.getInt("language_height", 200));
    }
    private void changeLanguage() {
        /*        Формирование текстов по выбранному языку         */
        apply.setText(languages.getText("main", 3, "Выбрать"));
        close.setText(languages.getText("main", 12, "Закрыть"));
        apply.setToolTipText(languages.getText("language", 2, "Установить выбранный язык"));
        close.setToolTipText(languages.getText("language", 3, "Закрыть форму без изменения языка"));
        setTitle(languages.getText("language", 1, "Choice of language for the program"));
    }
    private void changeFont(Font font) {
        apply.setFont(font);
        close.setFont(font);
        table.setFont(font);
        table.getTableHeader().setFont(font);
        table.setRowHeight(font.getSize() + 5);
    }
    private void closeForm(){
        exist = false;
        saveSize();
        dispose();
    }
    private void applyClick(){
        if (exist) {
            int row = table.getSelectedRow();
            Object lang = table.getValueAt(row, 1);
            languages.setAppLanguage((String) lang);  // сменить текущий язык
            try {
                ActionEvent event = new ActionEvent(parentForm, Event.F2, "changeLanguage");
                ActionListener[] listeners;
                listeners = parentForm.getActionListeners();
                listeners[0].actionPerformed(event);
            } catch (Exception e) {
                lang = "ru";
                languages.setAppLanguage((String) lang);  // восстановить текущий язык
            }
            userPrefs.put("family_language", (String) lang);  // запомнить сделанный выбор
            closeForm();  // закрыть форму
        }
    }
}
