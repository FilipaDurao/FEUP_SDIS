package proj.peer.log;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private  SimpleDateFormat dateFormat;
    private  String peerId;

    public LogFormatter(String peerId) {
        this.peerId = peerId;
        dateFormat = new SimpleDateFormat("hh:mm:ss");
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dateFormat.format(new Date(record.getMillis())));
        stringBuilder.append(" - [").append(this.peerId).append("]");
        stringBuilder.append(" - [").append(record.getLevel().getName()).append("] - ");
        Object[] parameters = record.getParameters();
        if (parameters != null) {
            stringBuilder.append("[");
            for (int i = 0; i < parameters.length; i++) {
                stringBuilder.append(parameters[i].toString());
                if (i < parameters.length - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("] - ");
        }

        stringBuilder.append(record.getMessage()).append("\n");

        return stringBuilder.toString();
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }
}
