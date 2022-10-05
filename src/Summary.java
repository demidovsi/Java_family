import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Vector;
import java.util.prefs.Preferences;
import com.toedter.calendar.*;
import com.toedter.components.JSpinField;

public class Summary extends PatternForm {
    JButton refresh, prev, next, current;
    JLabel labelInterval, labelYear;
    JComboBox intervals, months;
    JSpinField day, year;
    JCheckBox graph;
    int current_year, current_month, current_day;
    public void createGUI(){
        System.out.println("createGUI");
// панель кнопок управления
        JPanel panelButton1 = new JPanel();
        GridLayout layout1 = new GridLayout(0, 7, 5, 0);
        panelButton1.setLayout(layout1);
        JPanel panelButton2 = new JPanel();
        GridLayout layout2 = new GridLayout(0, 7, 5, 0);
        panelButton2.setLayout(layout2);
        refresh = new JButton("", new ImageIcon("images/mail-read.png"));
        labelInterval = new JLabel();
        intervals = new JComboBox();
        for (int i=1; i <= 3; i++) {intervals.addItem(Integer.toString(i));}
        intervals.addActionListener((e) -> intervalChanged());
        prev = new JButton("-1", new ImageIcon("images/prev.png"));
        months = new JComboBox();
        for (int i=1; i <= 12; i++) {months.addItem(Integer.toString(i));}
        months.setMaximumRowCount(12);
        day = new JSpinField();
        day.setMinimum(1);
        labelYear = new JLabel();
        year = new JSpinField();
        year.setMaximum(2050);
        year.setMinimum(2000);
        next = new JButton("+1", new ImageIcon("images/next.png"));
        current = new JButton();
        graph = new JCheckBox();
        panelButton1.add(labelInterval);
        panelButton1.add(intervals);
        panelButton2.add(prev);
        panelButton2.add(labelYear);
        panelButton2.add(year);
        panelButton2.add(months);
        panelButton2.add(day);
        panelButton2.add(next);
        panelButton1.add(graph);
        panelButton1.add(current);
        panelButton1.add(refresh);
// финальный аккорд
        add(panelButton2, BorderLayout.CENTER);
        add(panelButton1, BorderLayout.NORTH);
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
                case 0: {current.setText(languages.getText("hist", 8, "Сегодня")); break;}
                case 1: {current.setText(languages.getText("one", 7, "Текущий месяц")); break;}
                case 2: {current.setText(languages.getText("one", 8, "Текущий год")); break;}
            }

            index = months.getSelectedIndex();
            months.removeAllItems();
            for (int i=1; i <= 12; i++) {
                months.addItem(languages.getText("months", i, Integer.toString(i)));
            }
            months.setSelectedIndex(index);

            index = intervals.getSelectedIndex();
            intervals.removeAllItems();
            intervals.addItem(languages.getText("log", 2, "Сутки"));
            intervals.addItem(languages.getText("one", 11, "Месяц"));
            intervals.addItem(languages.getText("one", 10, "Год"));
            intervals.setSelectedIndex(index);

            exist = ex;
        }

    }
    public void refresh() {
        if (userPrefs != null) {
            System.out.println("refresh");
            intervalChanged();
        }
    }
    private void intervalChanged(){
        if (exist) {
            System.out.println("intervalChanged");
            int index = intervals.getSelectedIndex();
            months.setVisible(index == 0 || index == 1);
            day.setVisible(index == 0);
            changeLanguage();
        }
    }
    public void setUserPreferences(Preferences user){
        userPrefs = user;
        boolean ex = exist;
        exist = false;
        int index = userPrefs.getInt("family_summary_index_interval", 0);
        intervals.setSelectedIndex(index);

        Calendar calendar = Calendar.getInstance();
        current_year = userPrefs.getInt("family_summary_year", calendar.get(Calendar.YEAR));
        current_month = userPrefs.getInt("family_summary_month", calendar.get(Calendar.MONTH));
        current_day = userPrefs.getInt("family_summary_day", calendar.get(Calendar.DAY_OF_MONTH));
        year.setValue(current_year);
        if (current_month != -1) { months.setSelectedIndex(current_month - 1);}
        day.setValue(current_day);
        exist = ex;
    }
    public void saveSize() {
        current_day = day.getValue();
        current_month = months.getSelectedIndex() + 1;
        current_year = year.getValue();
        int index = intervals.getSelectedIndex();
        userPrefs.putInt("family_summary_year", current_year);
        userPrefs.putInt("family_summary_month", current_month);
        userPrefs.putInt("family_summary_day", current_day);
        userPrefs.putInt("family_summary_index_interval", index);
    }
}
