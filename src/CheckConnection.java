import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckConnection extends Thread  {
    int interval;
    JButton formParent;
    boolean needStop;
    boolean exist = true;
    RestAPI restAPI;
    String informFromProxy = "";
    String textErrorConnection = "";
// конструктор
    CheckConnection(RestAPI rest, JButton parent, int timeOut) {
        formParent = parent;
        interval = timeOut;
        restAPI = rest;
    }
    private void sendEvt(int id, String mes) {
        ActionEvent event = new ActionEvent(formParent, id, mes );
        ActionListener[] listeners;
        listeners = formParent.getActionListeners();
        listeners[0].actionPerformed(event);
    }
    @Override
    public void run() {
        System.out.println("CheckConnection Started");
        while (!needStop) {
            try {
                restAPI.get("MDMProxy.Inform");
                int current_interval = interval;
                if (restAPI.isOk()) {
                    textErrorConnection = "";
                    sendEvt(Event.F3, "");  // нет ошибки соединения
                    String new_inform = restAPI.getUrl() + " " + restAPI.getResponseMessage();
                    if (!new_inform.equals(informFromProxy)) {
                        informFromProxy = new_inform;
                        sendEvt(Event.F4, informFromProxy);  // характеристики БД
                    }
                    if (!exist) {
                        exist = true;
                        sendEvt(Event.F5, "");  // восстановление соединения и нужно на всякий случай сделать login
                    }
                } else {  // нет соединения
                    textErrorConnection = "No connect with PROXY ";
                    current_interval = interval / 10;
                    sendEvt(Event.F3,  textErrorConnection + restAPI.getUrl());
                    if (exist) {  // пропажа соединения
                        exist = false;
                        informFromProxy = "";
                        sendEvt(Event.F4, informFromProxy);  // характеристики БД
                    }
                }
                Thread.sleep(current_interval * 1000);
            } catch (Exception err) {System.out.println(err.getMessage());}
        }
        System.out.println("CheckConnection Finished");
    }
    public void setNeedStop(boolean value) {needStop = value; }
}
