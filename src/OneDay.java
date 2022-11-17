import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.tableeditors.DateTableEditor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.LocalDate;
import javax.swing.JSpinner.DefaultEditor;

public class OneDay  extends PatternForm {
    int selectedId = 0;
    DatePicker datePicker;
    static JLabel labelSummary = new JLabel("Summary");
    Double summa = 0d;
    JPanel gui, panelTable;
    JButton refresh, before, after, current, create, delete, save;
     static JLabel labelCount = new JLabel("Count");
    private JTable table;
    DefaultTableCellRenderer rightRenderer;
    DefaultTableCellRenderer centerRenderer;
    private DefaultTableModel rootModel;
    private static int countRow;
    JSONArray categories;
    SpinnerEditor spinnerEditor;
    SpinnerNumberModel coefficient;
    JButton makeMultiply;
    public OneDay(JButton parentObject) {
        super(parentObject);
    }
    public void createGUI() {
        gui = new JPanel(new BorderLayout(5,5));

        JPanel panelButtons = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 3,3));
        refresh = new JButton("", new ImageIcon("images/open-file.png"));
        refresh.addActionListener((e) -> refresh());
        create = new JButton("", new ImageIcon("images/create.png"));
        create.addActionListener((e) -> createClick());
        delete = new JButton("", new ImageIcon("images/delete.png"));
        delete.addActionListener((e) -> deleteClick());
        before = new JButton("-1", new ImageIcon("images/prev.png"));
        before.addActionListener((e) -> beforeClicked());
        after = new JButton("+1", new ImageIcon("images/next.png"));
        after.addActionListener((e) -> afterClicked());
        current = new JButton();
        current.addActionListener((e) -> currentClicked());
        save = new JButton("-1", new ImageIcon("images/save-file.png"));
        save.addActionListener((e) -> saveClick());
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
        rootModel = new DefaultTableModel(new String[1][10], new String[] {
                "Дата", "Истрачено", "Тип расхода", "Комментарий", "ID", "Status",
                "Дата1", "Истрачено1", "Тип расхода1", "Комментарий1"}){
            public boolean isCellEditable(int row, int column){
                return column < 4;
            }
        };
        rootModel.addTableModelListener((e) -> tableChanged());
        table = new JTable(rootModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        ListSelectionModel selModel = table.getSelectionModel();
        selModel.addListSelectionListener((e) -> rowSelected());
        JScrollPane tableScroll = new JScrollPane(table);
        panelTable.add(tableScroll, BorderLayout.CENTER);
//        JPanel statusBar = new JPanel(new GridLayout(3, 1));
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 3,3));
        JScrollPane statusbarScroll = new JScrollPane(statusBar);
        statusbarScroll.setPreferredSize(new Dimension(0, 60));

        JPanel panelButtons1 = new JPanel();
        coefficient = new SpinnerNumberModel(0.0d, 0, 1000, 1.0d);
        JSpinner coefficientSpinner = new JSpinner(coefficient);
        JButton makeDivision = new JButton("1/K");
        makeDivision.addActionListener((e) -> coefficientClick());
        makeMultiply = new JButton("*");
        makeMultiply.addActionListener((e) -> makeMultiplyClick());
        panelButtons1.add(new JLabel("K$="));
        panelButtons1.add(coefficientSpinner);
        panelButtons1.add(makeDivision);
        panelButtons1.add(makeMultiply);

        statusBar.add(labelSummary);
        statusBar.add(panelButtons1);
        statusBar.add(labelCount);
        labelSummary.setForeground(Color.RED);
        panelTable.add(statusbarScroll, BorderLayout.SOUTH);

        // Add the DateTimeTableEditor as the default editor and renderer for
        // the LocalDateTime data type.
        table.setDefaultEditor(LocalDate.class, new DateTableEditor());
        table.setDefaultRenderer(LocalDate.class, new DateTableEditor());
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setCellEditor(table.getDefaultEditor(LocalDate.class));
        column.setCellRenderer(table.getDefaultRenderer(LocalDate.class));

        table.getColumnModel().getColumn(3).setCellEditor(table.getDefaultEditor(String.class));
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor((loadCategory())));
        spinnerEditor = new SpinnerEditor(self);
        table.getColumnModel().getColumn(1).setCellEditor(spinnerEditor);
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
            create.setText(languages.getText("one", 12, "Создать"));
            delete.setText(languages.getText("one", 13, "Удалить"));
            labelCount.setText("        " + languages.getText("main", 19, "Строк") + countRow);
            labelSummary.setText("    " + languages.getText("one", 5, "Суммарно") + '=' +
                    new DecimalFormat("#.00").format(summa) + "                       ");
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
//        String result = "function/" + schemaName + "/p_history_rashod?text='" + stLocalDate + "','" + stLocalDateNext + "'";
//        String result = "v1/content/" + schemaName + "/rashod?where=dt>='" + stLocalDate + "'+and+dt<'" + stLocalDateNext + "'";
        return "v1/content/" + schemaName + "/rashod?where=dt>='" + stLocalDate + "'+and+dt<'" + stLocalDateNext + "'";
    }
    private int getCatId(String catName) {
        for (int i=0; i < categories.length(); i++) {
            if (catName.equals(categories.getJSONObject(i).getString("sh_name"))) {
                return categories.getJSONObject(i).getInt("id");
            }
        }
        return -1;
    }
    private void stopCelling(){
        table.getColumnModel().getColumn(3).getCellEditor().stopCellEditing();
        table.getColumnModel().getColumn(2).getCellEditor().stopCellEditing();
        table.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();
        table.getColumnModel().getColumn(0).getCellEditor().stopCellEditing();
    }
    private void defineEnabled(boolean value) {
        delete.setEnabled(value);
        makeMultiply.setEnabled(value);
    }
    public void refresh() {
        defineEnabled(false);
        stopCelling();
        exist = false;
        String schemaName = new UnitConfig(userPrefs).getSchemaName();
        String mes = getTextRequest(schemaName);
        RestAPI restAPI = new RestAPI();
        restAPI.get(mes);
        if (restAPI.isOk()) {
            JSONArray units = new JSONArray(restAPI.getResponseMessage());
            rootModel.setRowCount(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(300);
            table.getColumnModel().getColumn(4).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
            for (int i=6; i<10; i++) {
                table.getColumnModel().getColumn(i).setMinWidth(0);
                table.getColumnModel().getColumn(i).setMaxWidth(0);
                table.getColumnModel().getColumn(i).setWidth(0);
            }
            countRow = 0;
            summa = 0d;
            for (int i=0; i < units.length(); i++)
            {
                JSONObject unit = units.getJSONObject(i);
                try {
                        String[] row = new String[10];
                        Double money;
                        money = unit.isNull("money") ? 0d : unit.getDouble("money");
                        summa = summa + money;
                        row[2] = unit.getString("name_cat_id");
                        row[8] = row[2];
                        row[3] = translateFromBase(unit.getString("comment"));
                        row[9] = row[3];
                        row[4] = Integer.toString(unit.getInt("id"));
                        row[5] = "";
                        rootModel.addRow(row);
                        rootModel.setValueAt(datePicker.getDate(), countRow, 0);
                        rootModel.setValueAt(money, countRow, 1);
                        rootModel.setValueAt(money, countRow, 7);
                        rootModel.setValueAt(datePicker.getDate(), countRow, 6);
                        countRow++;
                } catch (Exception err) {System.out.println(err.getMessage());}
            }
        }
        changeLanguage();
        exist = true;
        try {
            table.changeSelection(0, 0, false, false);
        } catch (Exception err) {
            System.out.println("OneDay.refresh: " + err);
        }
        setSaveEnabled();
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
            defineEnabled(false);
            stopCelling();
            if (countRow !=0) {
                int[] selectedRows = table.getSelectedRows();
                selectedId = Integer.parseInt((String) rootModel.getValueAt(selectedRows[0], 4));
//                for (int selIndex : selectedRows) {
//                    defineEnabled(true);
//                    break;
//            }
            defineEnabled(true);
        }
        }
    }
    public void setSelectedRow(int id, String newDate) {
        exist = false;
        String[] dat = newDate.split("-");
        LocalDate localDate = LocalDate.of(Integer.parseInt(dat[0]), Integer.parseInt(dat[1]), Integer.parseInt(dat[2]));
        datePicker.setDate(localDate);
        exist = true;
        for (int i=0; i<rootModel.getRowCount(); i++) {
            if (Integer.parseInt((String) rootModel.getValueAt(i, 4)) == id) {
                table.changeSelection(i, 0, false, false);
                break;
            }
        }
    }
    private void tableChanged(){
        if (exist) {
            String val;
            String val_init;
            exist = false;
            int row = table.getSelectedRow();
            String st = "";
            for (int index = 6; index < 10; index++) {
                if (index == 7) {
                    if (row == spinnerEditor.aRow) {
                        Object val_new = spinnerEditor.getCellEditorValue();
                        rootModel.setValueAt(val_new, row, index - 6);
    //                    val = new DecimalFormat("#.00").format((Double) rootModel.getValueAt(row, index - 6));
                        val = new DecimalFormat("#.00").format((Double) val_new);
                        val_init = new DecimalFormat("#.00").format((Double) rootModel.getValueAt(row, index));
                        if (!val.equals(val_init)) { st = "Change"; break; }
                    }
                }
                if (index == 9 || index == 8) {
                    if (!((String) rootModel.getValueAt(row, index - 6)).equals((String) rootModel.getValueAt(row, index))) { st = "Change"; break; }
                }
                if (index == 6)
                {
                    if (rootModel.getValueAt(row, 0) != rootModel.getValueAt(row, index)) { st = "Change"; break; }
                }
            }
            rootModel.setValueAt(st, row, 5);
            setSaveEnabled();
            exist = true;
        }
    }
    private void setSaveEnabled(){
        boolean need = false;
        for (int i=0; i < rootModel.getRowCount(); i++){
            if (!((String) rootModel.getValueAt(i, 5)).equals("")) {
                need = true;
                break;
            }
        }
        save.setEnabled(need);
    }
    private JComboBox loadCategory(){
        JComboBox bx = new JComboBox();
        RestAPI restAPI = new RestAPI();
        restAPI.get("v1/content/" + new UnitConfig(userPrefs).getSchemaName() +"/categor?column_order=sh_name");
        if (restAPI.isOk()) {
            categories = new JSONArray(restAPI.getResponseMessage());
            for (int i=0; i < categories.length(); i++) {
                bx.addItem(categories.getJSONObject(i).getString("sh_name"));
            }
        }
        bx.setMaximumRowCount(30);
        return bx;
    }
    private void createClick(){
        if (exist) {
            exist = false;
            Object[] row = new Object[10];
            Double money = 0.0d;
            row[2] = "";
            row[8] = row[2];
            row[3] = "";
            row[9] = row[3];
            row[4] = "";
            row[5] = "";
            rootModel.addRow(row);
            rootModel.setValueAt(datePicker.getDate(), countRow, 0);
            rootModel.setValueAt(money, countRow, 1);
            rootModel.setValueAt(money, countRow, 7);
            rootModel.setValueAt(datePicker.getDate(), countRow, 6);
            table.changeSelection(countRow, 0, false, false);
            countRow++;
            exist = true;
            changeLanguage();
        }
    }
    public static class SpinnerEditor extends DefaultCellEditor
    {
        public int aRow = -1;
        JSpinner sp;
        DefaultEditor defaultEditor;
        JTextField text;
        JButton parentForm;
        // Initialize the spinner
        public SpinnerEditor(JButton form) {
            super(new JTextField());
            SpinnerNumberModel model = new SpinnerNumberModel(0.0d, null, null, 1.0d);
            sp = new JSpinner(model);
            JSpinner.NumberEditor editor = new JSpinner.NumberEditor(sp);
            sp.setEditor(editor);
            parentForm = form;
            sp.addChangeListener((e) -> sendMessage());
            defaultEditor = ((DefaultEditor)sp.getEditor());
            text = defaultEditor.getTextField();
        }
        // Prepare the spinner component and return it
        public Component getTableCellEditorComponent(JTable table, Object
                value, boolean isSelected, int row, int column)
        {
            aRow = row;
            sp.setValue(value);
            return sp;
        }
        // Returns the current value of the spinners
        public Object getCellEditorValue() {
            return sp.getValue();
        }
        private void sendMessage() {
            ActionEvent event = new ActionEvent(parentForm, -1001, "changeValue");
            ActionListener[] listeners;
            listeners = parentForm.getActionListeners();
            listeners[0].actionPerformed(event);
        }
    }
    private void saveClick() {
        JSONObject result = new JSONObject();
        JSONArray values = new JSONArray();
        RestAPI restAPI = new RestAPI();
        restAPI.login();
        if (restAPI.isOk()) {
            JSONObject stObject = new JSONObject(restAPI.getResponseMessage());
            String token = stObject.getString("accessToken");
            for (int i = 0; i < rootModel.getRowCount(); i++) {
                String st = (String) rootModel.getValueAt(i, 5);
                if (!st.equals(""))  // будем записывать
                {
                    JSONObject result1 = new JSONObject();
                    st = (String) rootModel.getValueAt(i, 4);  // ID
                    if (st.equals("")) result1.put("id", 0);
                    else result1.put("id", Integer.valueOf(st));
                    int num = getCatId((String) rootModel.getValueAt(i, 2));  // категория
                    if (num == -1) result1.put("cat_id", "Null");
                    else result1.put("cat_id", num);
                    st = rootModel.getValueAt(i, 0).toString(); // дата
                    result1.put("dt", "'" + st + "'");
                    st = (String) rootModel.getValueAt(i, 3); // комментарий
                    result1.put("comment", translateToBase(st));
                    result1.put("money", (Double) rootModel.getValueAt(i, 1)); // расход
                    values.put(result1);
                }
            }
            result.put("schema_name", new UnitConfig(userPrefs).getSchemaName());
            result.put("object_code", "rashod");
            result.put("values", values);
            JSONObject params = new JSONObject();
            params.put("params", result.toString());
            params.put("token", token);

            restAPI.sendMess("PUT", "v1/objects", params.toString());
            if (restAPI.isOk()) refresh();
            else {
                Object[] options = { "Ok" };
                JOptionPane.showOptionDialog(
                        this, restAPI.getUrl() + "\n" +
                        "PUT v1/objects\n" + restAPI.getResponseCode() + " " + restAPI.getResponseMessage(),
                        "Ошибка записи в БД",
                        JOptionPane.OK_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null, options, options[0]);
            }
        }
    }
    private int defineObjectMdmId(){
        String schemaName = new UnitConfig(userPrefs).getSchemaName();
        RestAPI restAPI = new RestAPI();
        String mes = "v1/MDM/objects?usl=app_code='" + schemaName + "'+and+code='rashod'";
        restAPI.get(mes);
        if (restAPI.isOk()) {
            JSONArray answer = new JSONArray(restAPI.getResponseMessage());
            return answer.getJSONObject(0).getInt("id");
        }
        else {
            Object[] options = { "Ok" };
            JOptionPane.showOptionDialog(
                    this, restAPI.getUrl() + "\n" +
                            "GET " + mes + "\n" + restAPI.getResponseCode() + " " + restAPI.getResponseMessage(),
                    "Ошибка чтения из БД",
                    JOptionPane.OK_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null, options, options[0]);
            return -1;
        }
    }
    private void deleteClick() {
        stopCelling();
        int row = table.getSelectedRow();
        String stId = (String) rootModel.getValueAt(row, 4);
        if (stId.equals("")) {
            exist = false;
            rootModel.removeRow(row);
            countRow--;
            if (row >= countRow) row--;
            table.changeSelection(row, 0, false, false);
            exist = true;
        } else {
            RestAPI restAPI = new RestAPI();
            restAPI.login();
            if (restAPI.isOk()) {
                String token = new JSONObject(restAPI.getResponseMessage()).getString("accessToken");
                Object[] options = {"Yes", "NO!"};
                int rc = JOptionPane.showOptionDialog(
                        this,
                        languages.getText("modeler", 9, "Удалить запись [" + stId + ']'),
                        languages.getText("main", 16, "Подтверждение"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
                if (rc == 0) {
                    JSONObject params = new JSONObject();
                    params.put("token", token);
                    int objectMDMId = defineObjectMdmId();
                    if (objectMDMId != -1) {
                        restAPI.sendMess("DELETE", "v1/object/" + objectMDMId + "/" + stId, params.toString());
                        if (restAPI.isOk()) refresh();
                        else {
                            JOptionPane.showOptionDialog(
                                    this, restAPI.getUrl() + "\n" +
                                            "DELETE v1/object\n" + restAPI.getResponseCode() + " " + restAPI.getResponseMessage(),
                                    "Ошибка записи в БД",
                                    JOptionPane.OK_OPTION,
                                    JOptionPane.ERROR_MESSAGE,
                                    null, options, options[0]);
                        }

                    }
                }
            }
        }
        setSaveEnabled();
        changeLanguage();
    }
    private void coefficientClick(){
        if ((Double) coefficient.getValue() != 0)
            coefficient.setValue(1.0d / (Double) coefficient.getValue());
    }
    public void saveSize() {
        userPrefs.putDouble("coefficient", (Double) coefficient.getValue());
    }
    public void prepareWork(){
        coefficient.setValue(userPrefs.getDouble("coefficient", 1.0d));
    }
    private void makeMultiplyClick(){
        int row = table.getSelectedRow();
        Double value = (Double) rootModel.getValueAt(row, 1) * (Double) coefficient.getValue();
        String stDouble = String.format("%.2f", value);
        value = Double.parseDouble(stDouble.replace(",","."));
        rootModel.setValueAt(value, row, 1);
        spinnerEditor.getTableCellEditorComponent(table, value, true, row, 1);
    }
}
