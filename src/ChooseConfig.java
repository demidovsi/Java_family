import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.nio.file.Path;
import java.util.prefs.Preferences;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.Container;
import java.lang.String;
import java.awt.event.*;

//import com.owlike.genson.Genson;
import org.json.*;

public class ChooseConfig extends JFrame {
    Preferences userPrefs;
    UserLanguages languages;
    JComboBox arrayConfig;
    JSONArray arrayJson;
    JButton apply, openFile, saveFile, create, delete, currentFont, close;
    JPanel panelTree;
    JLabel labelComboBox;
    JTable table;
    boolean exist = false;
    public static JButton self = new JButton();  // для передачи форме message
    private final JButton parentForm;
    private final Font font = new Font("Arial", Font.PLAIN, 14);
    /* Конструктор класса */
    public ChooseConfig(UserLanguages lang, Preferences user, JButton parentObject)
    {
        super("");
        parentForm = parentObject;
        userPrefs = user;
        languages = lang;
        self.addActionListener((e) -> changeLanguage());
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
        JPanel panelButton1 = new JPanel();
        GridLayout layout1 = new GridLayout(0, 3);
        panelButton1.setLayout(layout1);
        apply = new JButton("", new ImageIcon("images/apply.png"));
        apply.addActionListener((e) -> applyClick());
        panelButton1.add(apply);
        openFile = new JButton("", new ImageIcon("images/open-file.png"));
        openFile.addActionListener((e) -> refresh());
        panelButton1.add(openFile);
        saveFile = new JButton("", new ImageIcon("images/save-file.png"));
        panelButton1.add(saveFile);

        JPanel panelButton2 = new JPanel();
        GridLayout layout2 = new GridLayout(0, 4);
        panelButton2.setLayout(layout2);
        create = new JButton("", new ImageIcon("images/create.png"));
        panelButton2.add(create);
        delete = new JButton("", new ImageIcon("images/delete.png"));
        panelButton2.add(delete);
        currentFont = new JButton("", new ImageIcon("images/currentFont.png"));
        panelButton2.add(currentFont);
        close = new JButton("", new ImageIcon("images/exit.png"));
        panelButton2.add(close);
        close.addActionListener((e) -> closeForm());  // lambda для функционального интерфейса

        JPanel panelButtons = new JPanel();
        GridLayout layout = new GridLayout(2, 1);
        panelButtons.setLayout(layout);
        panelButtons.add(panelButton1);
        panelButtons.add(panelButton2);

// списки с информацией
        panelTree = new JPanel();
        BorderLayout layoutTree = new BorderLayout();
        panelTree.setLayout(layoutTree);
        JPanel panelComboBox = new JPanel();
        GridLayout layoutPanelComboBox = new GridLayout(1, 2);
        panelComboBox.setLayout(layoutPanelComboBox);
        labelComboBox = new JLabel();
        labelComboBox.setForeground(Color.BLUE);
        labelComboBox.setBackground(Color.WHITE);
        labelComboBox.setHorizontalAlignment(JLabel.RIGHT);
        panelComboBox.add(labelComboBox);
        arrayConfig = new JComboBox();
        panelComboBox.add(arrayConfig);

        panelTree.add(panelComboBox, BorderLayout.NORTH);
        TableModel dataModel = new DefaultTableModel() {
            @Override
            public int getRowCount() {
                return 7;
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (!exist) return null;
                int index = arrayConfig.getSelectedIndex();
                String name = arrayJson.getJSONObject(index).getString("name");
                String key = "", val = "";
                switch (rowIndex) {
                    case 0: {key = "url"; val = arrayJson.getJSONObject(index).getString(key); break;}
                    case 1: {key = "schema_name"; val = arrayJson.getJSONObject(index).getString(key); break;}
                    case 2: {key = "info_code"; val = arrayJson.getJSONObject(index).getString(key); break;}
                    case 3: {key = "interval"; val = Integer.toString(arrayJson.getJSONObject(index).getInt(key)); break;}
                    case 4: {key = "url_login"; val = arrayJson.getJSONObject(index).getString(key); break;}
                    case 5: {key = "user_name"; val = arrayJson.getJSONObject(index).getString(key); break;}
                    case 6: {key = "password"; val = arrayJson.getJSONObject(index).getString(key); break;}
                }
                switch (columnIndex){
                    case 0: if (rowIndex == 0) return name; else return "";
                    case 1: return key;
                    case 2: return val;
                    case 3: return val;
                }
                return null;
            }
            public boolean isCellEditable(int row, int col) {
                if (col == 2)  return true;
                else  return false;
            }
            public void setValueAt(Object value, int row, int col) {
                setValueAt(value, row, col);
            }
        };
        table = new JTable(dataModel);
        JScrollPane scrollTable = new JScrollPane(table);
        panelTree.add(scrollTable, BorderLayout.CENTER);

        File f = new File("config.json");
        JLabel panelStatus = new JLabel("  " + f.getAbsolutePath());
        panelStatus.setForeground(Color.BLUE);
// финальный аккорд
        Container container = getContentPane();
        container.add(panelButtons, BorderLayout.NORTH);
        container.add(panelTree, BorderLayout.CENTER);
        container.add(panelStatus, BorderLayout.SOUTH);
// Открытие окна
        pack();
        setVisible(true);
        changeLanguage();
        exist = true;
        changeFont(font);
        refresh();
    }
    public void makeSize() {
        setLocation(userPrefs.getInt("config_left", 100), userPrefs.getInt("config_top", 50));
        setSize(userPrefs.getInt("config_width", 600), userPrefs.getInt("config_height", 400));
    }
    public void saveSize() {
        userPrefs.putInt("config_width", getWidth());
        userPrefs.putInt("config_height", getHeight());
        userPrefs.putInt("config_top", getLocation().y);
        userPrefs.putInt("config_left", getLocation().x);
    }
    private void closeForm(){
        exist = false;
        saveSize();
        dispose();
    }
    private void refresh() {
        Path path = Paths.get("config.json");
        if (Files.exists(path)) {
            try {
                String contents = new String(Files.readAllBytes(path));
                arrayJson = new JSONArray(contents);
                exist = false;
                int index = arrayConfig.getSelectedIndex();
                String currentConfig = userPrefs.get("current_config", "");
                arrayConfig.removeAllItems();
                for (int i=0; i < arrayJson.length(); i++) {
                    String name = arrayJson.getJSONObject(i).getString("name");
                    if (currentConfig.equals(name)) index = i;
                    arrayConfig.addItem(name);
                    }
                exist = true;
                if (index == -1) index = 0;
                arrayConfig.setSelectedIndex(index);
            } catch (Exception e) {
                System.out.println("Failed to parse." + e);
            }
        }
    }
    private void changeLanguage() {
        setTitle(languages.getText("rest", 15, "Конфигурации соединения с REST API"));
        apply.setText(languages.getText("main", 3, "Выбрать"));
        openFile.setText(languages.getText("rest", 6, "Загрузить из файла"));
        saveFile.setText(languages.getText("rest", 5, "Сохранить в файл"));
        create.setText(languages.getText("one", 12, "Создать"));
        delete.setText(languages.getText("one", 13, "Удалить"));
        currentFont.setText(languages.getText("main", 1, "Фонт"));
        close.setText(languages.getText("main", 12, "Закрыть"));
        labelComboBox.setText(languages.getText("rest", 16, "Конфигурация:   "));
        panelTree.setBorder(BorderFactory.createTitledBorder(languages.getText("main", 18,
                "Коррекция конфигурации соединений")));
    }
    private void changeFont(Font font) {
        apply.setFont(font);
        close.setFont(font);
        table.setFont(font);
        create.setFont(font);
        close.setFont(font);
        openFile.setFont(font);
        saveFile.setFont(font);
        delete.setFont(font);
        currentFont.setFont(font);
        labelComboBox.setFont(font);
        arrayConfig.setFont(font);
        panelTree.setFont(font);
        table.getTableHeader().setFont(font);
        table.setRowHeight(font.getSize() + 5);
    }
    private void applyClick(){
        if (exist) {
            String currentConfig = (String) arrayConfig.getItemAt(arrayConfig.getSelectedIndex());
            userPrefs.put("current_config", currentConfig); // запомнить сделанный выбор
            try {
                ActionEvent event = new ActionEvent(parentForm, Event.F6, "changeLanguage");
                ActionListener[] listeners;
                listeners = parentForm.getActionListeners();
                listeners[0].actionPerformed(event);
            } catch (Exception e) {
                System.out.println("Error UnitConfig");
            }
            closeForm();  // закрыть форму
        }
    }
}
