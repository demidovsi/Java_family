import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.prefs.Preferences;
import com.toedter.components.JSpinField;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.border.TitledBorder;
import java.text.SimpleDateFormat;

public class Summary extends PatternForm {
    JButton refresh, before, after, current;
    JLabel labelInterval, labelYear;
    static JLabel labelSummary = new JLabel("Summary");
    static JLabel labelDays1 = new JLabel("label1");
    static JLabel labelDays2 = new JLabel("label2");
    static JLabel labelCategory = new JLabel("Category");
    JComboBox<String> intervals, chooseMonth;
    JSpinner day;
    JSpinner year;
    SpinnerNumberModel spinnerYear;
    SpinnerNumberModel spinnerDay;
    JCheckBox graph;
    int current_year, current_month, current_day;
    static Calendar calendar = Calendar.getInstance();
    private JTable table, tableCategory;
    private DefaultTableModel rootModel, rootModelCategory;
    static JSONArray masData; // массив считанной информации
    DefaultTableCellRenderer rightRenderer;
    DefaultTableCellRenderer centerRenderer;
    static Float[] array_days = new Float[31];  // сумма по дням или месяцам
    static String current_guid; // guid для вывода уточняющей таблицы по выбранной категории.
    static int countCategory;
    JPanel panelCategory;
    public Summary(JButton parentObject) {
        super(parentObject);
    }
    public void createGUI(){
// верхняя часть
        refresh = new JButton("", new ImageIcon("images/open-file.png"));
        refresh.addActionListener((e) -> refresh());
        labelInterval = new JLabel();
        intervals = new JComboBox();
        for (int i=1; i <= 3; i++) {intervals.addItem(Integer.toString(i));}
        intervals.addActionListener((e) -> intervalChanged());
        before = new JButton("-1", new ImageIcon("images/prev.png"));
        before.addActionListener((e) -> beforeClicked());
        chooseMonth = new JComboBox();
        for (int i=1; i <= 12; i++) {chooseMonth.addItem(Integer.toString(i));}
        chooseMonth.addActionListener((e) -> monthChanged());
        chooseMonth.setMaximumRowCount(12);
        labelYear = new JLabel();
        spinnerYear = new SpinnerNumberModel(2000, 2000, 3000, 1);
        year = new JSpinner(spinnerYear);
        spinnerYear.addChangeListener((e) -> refresh());

        spinnerDay = new SpinnerNumberModel(1, 1, 31, 1);
        day = new JSpinner(spinnerDay);
        spinnerDay.addChangeListener((e) -> refresh());
        day.setPreferredSize(new Dimension(40, 20));
        day.setAlignmentX(CENTER_ALIGNMENT);
        after = new JButton("+1", new ImageIcon("images/next.png"));
        after.addActionListener((e) -> afterClicked());
        current = new JButton();
        current.addActionListener((e) -> currentClicked());
        graph = new JCheckBox();

        final JPanel gui = new JPanel(new BorderLayout(5,5));
//        gui.setBorder( new TitledBorder("BorderLayout(5,5)") );
        JPanel panelButtons = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 3,3));
        panelButtons.add(labelInterval);
        panelButtons.add(intervals);
        panelButtons.add(before);
        panelButtons.add(labelYear);
        panelButtons.add(year);
        panelButtons.add(chooseMonth);
        panelButtons.add(day);
        panelButtons.add(after);
        panelButtons.add(graph);
        panelButtons.add(current);
        panelButtons.add(refresh);
        JScrollPane buttonScroll = new JScrollPane(panelButtons);
        buttonScroll.setPreferredSize(new Dimension(0, 50));
        gui.add(buttonScroll, BorderLayout.NORTH);
// средняя часть
        JPanel panelTable = new JPanel(new BorderLayout(5,5));
        rootModel = new DefaultTableModel(new String[1][33], new String[33]){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        table = new JTable(rootModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        ListSelectionModel selModel = table.getSelectionModel();
        selModel.addListSelectionListener((e) -> rowSelected());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(table);
        panelTable.add(tableScroll, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new GridLayout(3, 1));
        statusBar.add(labelSummary);
        statusBar.add(labelDays1);
        statusBar.add(labelDays2);
        panelTable.add(statusBar, BorderLayout.SOUTH);
        labelSummary.setForeground(Color.RED);
        labelDays1.setForeground(Color.BLUE);
        labelDays2.setForeground(Color.BLUE);

        panelCategory = new JPanel(new BorderLayout(3,3));
        panelCategory.setBorder( new TitledBorder("Категория") );
        JScrollPane panelCategoryScroll = new JScrollPane(panelCategory);
        rootModelCategory = new DefaultTableModel(new String[1][4], new String[] {"Дата", "Истрачено", "Комментарий", "ID"}){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tableCategory = new JTable(rootModelCategory);
        tableCategory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane tableScrollCategory = new JScrollPane(tableCategory);
        panelCategory.add(tableScrollCategory, BorderLayout.CENTER);
        tableCategory.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
//                    System.out.println("row=" + row);
                    String dateRow = (String) table.getModel().getValueAt(row, 0);
                    int idRow = Integer.parseInt((String) table.getModel().getValueAt(row, 3));

                    ActionEvent event = new ActionEvent(parentForm, Event.F8, dateRow + "/" + idRow);
                    ActionListener[] listeners;
                    listeners = parentForm.getActionListeners();
                    listeners[0].actionPerformed(event);
                }
            }
        });

        JPanel statusBarCategory = new JPanel(new GridLayout(1, 1));
        statusBarCategory.add(labelCategory);
        panelCategory.add(statusBarCategory, BorderLayout.SOUTH);

        JPanel panelParam =  new JPanel(new GridLayout(1, 1));
        // здесь может быть кое-что
        panelCategory.add(panelParam, BorderLayout.EAST);

        rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        Dimension panelTablePreferred = panelTable.getPreferredSize();
        tableScrollCategory.setPreferredSize(
                new Dimension(panelTablePreferred.width, panelTablePreferred.height/10));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                panelTable,
                panelCategoryScroll);
        gui.add( splitPane, BorderLayout.CENTER );

        add(gui);
        setLayout(new GridLayout(1, 1));
        self.addActionListener((e) -> {
            if (e.getID() == Event.F2) refresh();  // изменение вкличны JSpinner
        });
    }
    public void changeLanguage() {
        if (languages != null) {
            refresh.setText(languages.getText("hist", 10, "Прочитать"));
            labelInterval.setText(languages.getText("one", 9, "Интервал") + '=');
            labelYear.setText(languages.getText("one", 10, "Год") + "=");
            graph.setText(languages.getText("summary", 1, "График"));

            boolean ex = exist;
            exist = false;
            int index = intervals.getSelectedIndex();
            switch (index) {
                case 0 -> current.setText(languages.getText("hist", 8, "Сегодня"));
                case 1 -> current.setText(languages.getText("one", 7, "Текущий месяц"));
                case 2 -> current.setText(languages.getText("one", 8, "Текущий год"));
            }

            index = chooseMonth.getSelectedIndex();
            chooseMonth.removeAllItems();
            for (int i=1; i <= 12; i++) {
                chooseMonth.addItem(languages.getText("months", i, Integer.toString(i)));
            }
            chooseMonth.setSelectedIndex(index);

            index = intervals.getSelectedIndex();
            intervals.removeAllItems();
            intervals.addItem(languages.getText("log", 2, "Сутки"));
            intervals.addItem(languages.getText("one", 11, "Месяц"));
            intervals.addItem(languages.getText("one", 10, "Год"));
            intervals.setSelectedIndex(index);

            table.getColumnModel().getColumn(0).setHeaderValue(languages.getText("one", 3, "Тип расхода"));
            table.getColumnModel().getColumn(1).setHeaderValue(languages.getText("one", 2, "Истрачено"));

            tableCategory.getColumnModel().getColumn(0).setHeaderValue(languages.getText("one", 1, "Дата"));
            tableCategory.getColumnModel().getColumn(1).setHeaderValue(languages.getText("one", 2, "Истрачено"));
            tableCategory.getColumnModel().getColumn(2).setHeaderValue(languages.getText("one", 4, "Комментарий"));
            tableCategory.getColumnModel().getColumn(3).setHeaderValue("ID");
            if (intervals.getSelectedIndex() == 2)
                for (int i=1; i<=12; i++)
                    table.getColumnModel().getColumn(i+1).setHeaderValue(languages.getText("months", i, "Месяц"));

            showStatusBar(intervals.getSelectedIndex());
            labelCategory.setText(languages.getText("main", 19, "Строк") + countCategory);
            exist = ex;
        }
    }
    private String getTextRequest(int index, String schemaName) {
        // формирование текста запроса информации в зависимости от интервала и его характеристик
        String result = "function/" + schemaName + "/p_history_rashod";
        String txt = "";
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        switch (index) {
            case 0 -> {  // сутки
                Calendar calendar1 = new GregorianCalendar((Integer) spinnerYear.getValue(), chooseMonth.getSelectedIndex(), (Integer) spinnerDay.getValue());
                txt = "'" + formatDate.format(calendar1.getTime()) + "'";
                calendar1.add(Calendar.DAY_OF_MONTH, 1);
                txt = txt + ",'" + formatDate.format(calendar1.getTime()) + "'";
            }
            case 1 -> {  // месяц
                result = result + "_month";
                Calendar calendar1 = new GregorianCalendar((Integer) spinnerYear.getValue(), chooseMonth.getSelectedIndex(), 1);
                txt = "'" + formatDate.format(calendar1.getTime()) + "'";
                calendar1.add(Calendar.MONTH, 1);
                txt = txt + ",'" + formatDate.format(calendar1.getTime()) + "'";
            }
            case 2 -> {  // Год
                result = result + "_year";
                Calendar calendar1 = new GregorianCalendar((Integer) spinnerYear.getValue(), 0, 1);
                txt = "'" + formatDate.format(calendar1.getTime()) + "'";
                calendar1.add(Calendar.YEAR, 1);
                txt = txt + ",'" + formatDate.format(calendar1.getTime()) + "'";
            }
        }
        result = result + "?text=" + txt;
//        System.out.println(result);
        return result;
    }
    private void changeDate(int value) {
        int index = intervals.getSelectedIndex();
        Calendar calendar1 = new GregorianCalendar((Integer) spinnerYear.getValue(), chooseMonth.getSelectedIndex(), (Integer) spinnerDay.getValue());
        switch (index) {
            case 0 ->  calendar1.add(Calendar.DAY_OF_MONTH, value);
            case 1 ->  calendar1.add(Calendar.MONTH, value);
            case 2 ->  calendar1.add(Calendar.YEAR, value);
        }
        exist = false;
        year.setValue(calendar1.get(Calendar.YEAR));
        chooseMonth.setSelectedIndex(calendar1.get(Calendar.MONTH));
        day.setValue(calendar1.get(Calendar.DAY_OF_MONTH));
        exist = true;
        refresh();

    }
    private void beforeClicked() { changeDate(-1); }
    private void afterClicked() { changeDate(+1);}
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
    private String[] makeRow(JSONObject param, int index){
        int l = 0;
        Float value;
        String key;
        switch (index) {
            case 0 ->  l = 2;
            case 1 ->  l = 2 + (Integer) spinnerDay.getMaximum();
            case 2 ->  l = 2 + 12;
        }
        String[] arr = new String[l];
        arr[0] = param.getString("cat_name");
        if (!param.isNull("money")) {
            arr[1] = new DecimalFormat("#.00").format(param.getFloat("money"));
        }
        if (index == 0) {
            array_days[0] = array_days[0] + param.getFloat("money");
        }
        else
            for (int i=1; i <= l-2; i++){
                key = Integer.toString(i);
                if (!param.isNull(key)) {
                    value = param.getFloat(key);
                    arr[i + 1] = new DecimalFormat("#.00").format(value);
                    array_days[i - 1] = array_days[i - 1] + value;
                }
        }
        return arr;
    }
    private void showStatusBar(int index) {
        Float summa = 0f;
        for (Float unit : array_days) summa = summa + unit;
        labelSummary.setText("    " + languages.getText("one", 5, "Суммарно") + '=' +
                new DecimalFormat("#.00").format(summa));
        if (index == 0) { labelDays1.setText(""); labelDays2.setText("");}
        else {

            int count = rootModel.getColumnCount() - 2;
            String st1 = ""; String st2 =""; String st; String stName;
            for (int i=0; i<count; i++){
                if (array_days[i] != 0) {
                    st = new DecimalFormat("#").format(array_days[i]);
                    stName = (String) table.getColumnModel().getColumn(i + 2).getHeaderValue();
                    if (index == 1) stName = "[" + stName + "]";
                    stName = stName + '=';
                    if (i < count / 2)
                        st1 = st1 + stName + st + " ";
                    else
                        st2 = st2 + stName + st + " ";
                }
            }
            labelDays1.setText(" " + st1);
            labelDays2.setText(" " + st2);
        }
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
    public void refresh() {
        if (exist) {
            exist = false;
            panelCategory.setBorder( new TitledBorder(""));
            intervalChanged();
            monthChanged();  // проверить день месяца на всякий случай (февраль)
            int index = intervals.getSelectedIndex();
            String schemaName = new UnitConfig(userPrefs).getSchemaName();
            String mes = getTextRequest(index, schemaName);
            RestAPI restAPI = new RestAPI(userPrefs);
            restAPI.get(mes);
            if (restAPI.isOk()) {
                masData = new JSONArray("[]");
                JSONArray units = new JSONArray(restAPI.getResponseMessage());
                switch (index) {
                    case 0 -> { rootModel.setColumnCount(2); }
                    case 1 -> { rootModel.setColumnCount(2 + (Integer) spinnerDay.getMaximum()); }
                    case 2 -> { rootModel.setColumnCount(2 + 12);}
                }
                for (int i=0; i < units.length(); i++) {
                    switch (index) {
                        case 0 -> { masData.put(makeData(units.getJSONObject(i), 0, 0));}
                        case 1-> {
                            for (int j=1; j <= (Integer) spinnerDay.getMaximum(); j++)
                                table.getColumnModel().getColumn(j + 1).setHeaderValue(Integer.toString(j));
                            masData.put(makeData(units.getJSONObject(i), 8, 31));
                        }
                        case 2 -> { masData.put(makeData(units.getJSONObject(i), 8, 12)); }
                    }
                }
//                System.out.println(Integer.toString(masData.length()));
                table.getColumnModel().getColumn(0).setPreferredWidth(200);
                table.getColumnModel().getColumn(1).setPreferredWidth(100);
                rootModel.setRowCount(0);
                Arrays.fill(array_days, 0f);
                for (int i=1; i<rootModel.getColumnCount(); i++)
                    table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
                for (int i=0; i < masData.length(); i++) {
                    if (masData.getJSONObject(i).getInt("lev") == 1 & !masData.getJSONObject(i).isNull("money")) {
//                    if (masData.getJSONObject(i).getInt("lev") == 1) {
                        rootModel.addRow(makeRow(masData.getJSONObject(i), index));
                    }
                }
                current_guid = ""; // вывести таблицу по категории
                exist = true;
                try {
                    table.changeSelection(0, 0, false, false);
                    showStatusBar(index);
                } catch (Exception err) {
                    current_guid = "1111";
                    showTable("");
                }
            }
            changeLanguage();
        }
    }
    private void intervalChanged(){
        int index = intervals.getSelectedIndex();
        chooseMonth.setVisible(index == 0 || index == 1);
        day.setVisible(index == 0);
        if (exist) { refresh(); } // вывести информацию
    }
    private void monthChanged(){
        Calendar calendar1 = new GregorianCalendar((Integer) spinnerYear.getValue(), chooseMonth.getSelectedIndex(), 1);
        int countDays = calendar1.getActualMaximum(Calendar.DAY_OF_MONTH);
        if ((Integer) spinnerDay.getValue() > countDays) day.setValue(countDays);
        spinnerDay.setMaximum(countDays);
        if (exist) refresh();
    }
    private void currentClicked(){
        if (exist) {
            Calendar calendar1 = new GregorianCalendar();
            exist = false;
            year.setValue(calendar1.get(Calendar.YEAR));
            day.setValue(calendar1.get(Calendar.DAY_OF_MONTH));
            chooseMonth.setSelectedIndex(calendar1.get(Calendar.MONTH));
            exist = true;
            monthChanged();
        }
    }
    public void setUserPreferences(Preferences user){
        userPrefs = user;
        boolean ex = exist;
        exist = false;
        int index = userPrefs.getInt("summary_index_interval", 0);
        intervals.setSelectedIndex(index);

        current_year = userPrefs.getInt("summary_year", calendar.get(Calendar.YEAR));
        current_month = userPrefs.getInt("summary_month", calendar.get(Calendar.MONTH));
        current_day = userPrefs.getInt("summary_day", calendar.get(Calendar.DAY_OF_MONTH));
        year.setValue(current_year);
        if (current_month != -1) { chooseMonth.setSelectedIndex(current_month - 1);}
        day.setValue(current_day);
        exist = ex;
    }
    public void saveSize() {
        current_day = (Integer) spinnerDay.getValue();
        current_month = chooseMonth.getSelectedIndex() + 1;
        current_year = (Integer) spinnerYear.getValue();
        int index = intervals.getSelectedIndex();
        userPrefs.putInt("summary_year", current_year);
        userPrefs.putInt("summary_month", current_month);
        userPrefs.putInt("summary_day", current_day);
        userPrefs.putInt("summary_index_interval", index);
    }
    private void rowSelected() {
        if (exist) {
            int[] selectedRows = table.getSelectedRows();
            for (int selIndex : selectedRows) {
                String value = (String) rootModel.getValueAt(selIndex, 0);
                for (int j=0; j<masData.length(); j++) {
                    if (masData.getJSONObject(j).getString("cat_name").equals(value)) {
                        String guid = masData.getJSONObject(j).getString("guid");
                        showTable(guid);
                        break;
                    }
                }
                panelCategory.setBorder( new TitledBorder(value) );
                break;
            }
        }
    }
    private void showTable(String guid) {
        if (!current_guid.equals(guid)) {
            current_guid = guid;
            tableCategory.getColumnModel().getColumn(0).setPreferredWidth(150);
            tableCategory.getColumnModel().getColumn(1).setPreferredWidth(100);
            tableCategory.getColumnModel().getColumn(2).setPreferredWidth(300);
            tableCategory.getColumnModel().getColumn(3).setPreferredWidth(100);
            tableCategory.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tableCategory.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
            tableCategory.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            tableCategory.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
            rootModelCategory.setRowCount(0);
            countCategory = 0;
            for (int i=0; i < masData.length(); i++)
            {
                JSONObject unit = masData.getJSONObject(i);
                if (!unit.isNull("guid_parent"))
                    try {
                        if (unit.getInt("lev") == 2 & !unit.isNull("money") & unit.getString("guid_parent").equals(guid))
                        {
                            String[] row = new String[4];
                            row[0] = unit.getString("dt").split("T")[0];
                            row[1] = new DecimalFormat("#.00").format(unit.getFloat("money"));
                            row[2] = translateFromBase(unit.getString("comment"));
                            row[3] = Integer.toString(unit.getInt("id"));
                            rootModelCategory.addRow(row);
                            countCategory++;
                        }
                    } catch (Exception err) {
                        System.out.println(err.getMessage());
                    }
            }
            labelCategory.setText(languages.getText("main", 19, "Строк") + countCategory);
        }
    }
    public void changeFont(Font font) {
        table.getTableHeader().setFont(font);
        tableCategory.getTableHeader().setFont(font);
    }
}
