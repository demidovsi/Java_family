import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.tableeditors.DateTimeTableEditor;
import com.github.lgooddatepicker.tableeditors.DateTableEditor;
import com.toedter.components.JSpinField;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class OneDay  extends PatternForm {
    DatePicker datePicker;
    static JLabel labelSummary = new JLabel("Summary");
    float summa = 0;
    static JSONArray masData; // массив считанной информации
    JPanel gui, panelUnit, panelTable;
    JScrollPane panelUnitScroll;
    JButton refresh, before, after, current, create, delete, save;
     static JLabel labelCount = new JLabel("Count");
    private JTable table;
    DefaultTableCellRenderer rightRenderer;
    DefaultTableCellRenderer centerRenderer;
    private DefaultTableModel rootModel;
    private static int countRow;
    public void createGUI() {
        gui = new JPanel(new BorderLayout(5,5));

        JPanel panelButtons = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 3,3));
        refresh = new JButton("", new ImageIcon("images/mail-read.png"));
        refresh.addActionListener((e) -> refresh());
        create = new JButton("", new ImageIcon("images/create.png"));
        delete = new JButton("", new ImageIcon("images/delete.png"));
        refresh.addActionListener((e) -> refresh());
        before = new JButton("-1", new ImageIcon("images/prev.png"));
        before.addActionListener((e) -> beforeClicked());
        after = new JButton("+1", new ImageIcon("images/next.png"));
        after.addActionListener((e) -> afterClicked());
        current = new JButton();
        current.addActionListener((e) -> currentClicked());
        save = new JButton("-1", new ImageIcon("images/save-file.png"));
        datePicker = new DatePicker();
        datePicker.setDateToToday();
        datePicker.addDateChangeListener((e) -> refresh());
        panelButtons.add(refresh);
        panelButtons.add(before);
        panelButtons.add(datePicker);
        panelButtons.add(after);
        panelButtons.add(current);
        panelButtons.add(create);
        panelButtons.add(delete);
        panelButtons.add(save);

        JScrollPane buttonScroll = new JScrollPane(panelButtons);
        buttonScroll.setPreferredSize(new Dimension(0, 50));
        gui.add(buttonScroll, BorderLayout.NORTH);
// таблица с информацией
        panelTable = new JPanel(new BorderLayout(5,5));
        rootModel = new DefaultTableModel(new String[1][6], new String[] {
                "Дата", "Истрачено", "Тип расхода", "Комментарий", "ID", "Status"}){
            public boolean isCellEditable(int row, int column){
                return column != 4;
            }
        };
        rootModel.addTableModelListener((e) -> tableChanged(e));
        table = new JTable(rootModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setFillsViewportHeight(true);
        rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        ListSelectionModel selModel = table.getSelectionModel();
        selModel.addListSelectionListener((e) -> rowSelected());
        JScrollPane tableScroll = new JScrollPane(table);
        panelTable.add(tableScroll, BorderLayout.CENTER);
        JPanel statusBar = new JPanel(new GridLayout(2, 1));
        statusBar.add(labelCount);
        statusBar.add(labelSummary);
        labelSummary.setForeground(Color.RED);
        panelTable.add(statusBar, BorderLayout.SOUTH);

        // Add the DateTimeTableEditor as the default editor and renderer for
        // the LocalDateTime data type.
        table.setDefaultEditor(LocalDate.class, new DateTableEditor());
        table.setDefaultRenderer(LocalDate.class, new DateTableEditor());
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setCellEditor(table.getDefaultEditor(LocalDate.class));
        column.setCellRenderer(table.getDefaultRenderer(LocalDate.class));

// финальный аккорд
        gui.add(panelTable);
        add(gui);
        setLayout(new GridLayout(1, 1));
    }

    public void changeLanguage() {
        if (languages != null) {
            refresh.setText(languages.getText("hist", 10, "Прочитать"));
            current.setText(languages.getText("hist", 8, "Сегодня"));
            save.setText(languages.getText("one", 6, "Сохранить"));
            labelCount.setText(languages.getText("main", 19, "Строк") + countRow);
            labelSummary.setText("    " + languages.getText("one", 5, "Суммарно") + '=' +
                    new DecimalFormat("#.00").format(summa));
            table.getColumnModel().getColumn(0).setHeaderValue(languages.getText("one", 1, "Дата"));
            table.getColumnModel().getColumn(1).setHeaderValue(languages.getText("one", 2, "Истрачено"));
            table.getColumnModel().getColumn(2).setHeaderValue(languages.getText("one", 3, "Тип расхода"));
            table.getColumnModel().getColumn(3).setHeaderValue(languages.getText("one", 4, "Комментарий"));
        }
    }
    private String getTextRequest(String schemaName) {
        // формирование текста запроса информации в зависимости от интервала и его характеристик
        String stLocalDate = datePicker.getDate().toString();
        String stLocalDateNext = datePicker.getDate().plusDays(1).toString();
        String result = "function/" + schemaName + "/p_history_rashod?text='" + stLocalDate + "','" + stLocalDateNext + "'";
//        result = "v1/content/" + schemaName + "/rashod?where='dt>='" + stLocalDate + "' and dt<'" + stLocalDateNext + "''";
//        result = "v1/content/" + schemaName + "/rashod?where=dt>='" + stLocalDate + "'";
        return result;
    }
    private JSONObject makeData(JSONObject data, int iFrom, int count) {
        JSONObject result = new JSONObject();
        result.put("cat_name", data.get("1"));
        result.put("money", data.get("2"));
        result.put("comment", data.get("3"));
        result.put("guid", data.get("4"));
        result.put("guid_parent", data.get("5"));
        result.put("lev", data.get("6"));
        result.put("dt", data.get("7"));
        result.put("id", data.get("8"));
        for (int i=1; i <= count; i++) result.put(Integer.toString(i), data.get(Integer.toString(iFrom + i)));
//        System.out.println(result);
        return result;
    }
    private String translateFromBase(String st) {
        return st.replace("~LF~", "\n")
                .replace("~A~", "(")
                .replace("~B~", ")")
                .replace("~a1~", "@")
                .replace("~a2~", ",")
                .replace("~a3~", "=")
                .replace("~a4~", "\"")
                .replace("~a5~", "'")
                .replace("~a6~", ":")
                ;
    }
    private String getCatName(String guid_parent) {
        for (int i=0; i < masData.length(); i++) {
            JSONObject unit = masData.getJSONObject(i);
            if (!unit.isNull("guid") && unit.getString("guid").equals(guid_parent)) {
                return unit.getString("cat_name");
            }
        }
        return "";
    }
    public void refresh() {
        delete.setEnabled(false);
        save.setEnabled(false);
        exist = false;
        String schemaName = new UnitConfig(userPrefs).getSchemaName();
        String mes = getTextRequest(schemaName);
//        System.out.println(mes);
        RestAPI restAPI = new RestAPI(userPrefs);
        restAPI.get(mes);
        if (restAPI.isOk()) {
            masData = new JSONArray("[]");
            JSONArray units = new JSONArray(restAPI.getResponseMessage());
            for (int i=0; i < units.length(); i++)
                { masData.put(makeData(units.getJSONObject(i), 0, 0)); }
            rootModel.setRowCount(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(120);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(300);
            table.getColumnModel().getColumn(4).setPreferredWidth(80);
            table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            countRow = 0;
            summa = 0;
            for (int i=0; i < masData.length(); i++)
            {
                JSONObject unit = masData.getJSONObject(i);
                if (!unit.isNull("guid_parent"))
                    try {
                        if (unit.getInt("lev") == 2 & !unit.isNull("money"))
                        {
                            String[] row = new String[6];
                            float money = unit.getFloat("money");
                            summa = summa + money;
//                            row[0] = unit.getString("dt").split("T")[0];
                            row[1] = new DecimalFormat("#.00").format(money);
                            row[2] = getCatName(unit.getString("guid_parent"));
                            row[3] = translateFromBase(unit.getString("comment"));
                            row[4] = Integer.toString(unit.getInt("id"));
                            row[5] = "";
                            rootModel.addRow(row);
                            rootModel.setValueAt(datePicker.getDate(), countRow, 0);
                            countRow++;
                        }
//                        System.out.println(table.getValueAt(0,0));
                    } catch (Exception err) {System.out.println(err.getMessage());}
            }
        }
        changeLanguage();
        exist = true;
        try {
            table.changeSelection(0, 0, false, false);
        } catch (Exception err) {
            delete.setEnabled(false);
        }
    }
    public void changeFont(Font font) {
        table.getTableHeader().setFont(font);
    }
    public void beforeClicked(){
        datePicker.setDate(datePicker.getDate().minusDays(1));
    }
    public void afterClicked(){
        datePicker.setDate(datePicker.getDate().plusDays(1));
    }
    public void currentClicked() {
        datePicker.setDateToToday();
    }
    private void rowSelected() {
        if (exist) {
            delete.setEnabled(false);
            if (countRow !=0) {
                int[] selectedRows = table.getSelectedRows();
                for (int selIndex : selectedRows) {
                    delete.setEnabled(true);
                    break;
                }
            }
        }
    }
    private void tableChanged(TableModelEvent e){
        if (exist)
        {
            exist = false;
            int row = e.getFirstRow();
            rootModel.setValueAt("Change", row, 5);
            save.setEnabled(true);
            exist = true;
        }
    }
}
