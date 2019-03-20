package proj.peer.message.subscriptions;

public class FileSubscription {

    protected String operation;
    protected String fileId;

    public FileSubscription(String operation, String fileId) {
        this.operation = operation;
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof FileSubscription) {
            FileSubscription other = (FileSubscription) o;
            return this.fileId.equals(other.getFileId()) && this.operation.equals(other.getOperation());
        }
        return false;
    }
}
