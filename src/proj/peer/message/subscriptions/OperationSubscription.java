package proj.peer.message.subscriptions;

public class OperationSubscription {
    protected String operation;
    protected String version;

    public OperationSubscription(String operation, String version) {
        this.operation = operation;
        this.version = version;
    }

    public String getOperation() {
        return operation;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return (operation + version).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  OperationSubscription)
            return operation.equals(((OperationSubscription) o).getOperation()) && version.equals(((OperationSubscription) o).getVersion());
        return false;
    }
}
