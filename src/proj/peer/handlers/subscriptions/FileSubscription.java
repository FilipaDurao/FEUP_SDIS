package proj.peer.handlers.subscriptions;

public class FileSubscription extends OperationSubscription {

    protected String fileId;

    public FileSubscription(String operation, String fileId, String version) {
        super(operation, version);
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof FileSubscription) {
            FileSubscription other = (FileSubscription) o;
            return this.fileId.equals(other.getFileId()) && this.operation.equals(other.getOperation());
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return (operation + fileId + version).hashCode();
    }
}
