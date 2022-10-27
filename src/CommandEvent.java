import java.util.EventObject;

public class CommandEvent extends EventObject {

    private String message;

    public CommandEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public CommandEvent(Object source){
        this(source, "");
    }

    public CommandEvent(String s){
        this(null, s);
    }

    public String getMessage(){
        return message;
    }

    @Override
    public String toString(){
        return getClass().getName() + "[source = " + getSource() + ", message = " + message + "]";
    }
}