package proj.peer.message.subscriptions;

public class OperationSubscription {
    protected String operation;

    public OperationSubscription(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public int hashCode() {
        return operation.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  OperationSubscription)
            return operation.equals(((OperationSubscription) o).getOperation());
        return false;
    }
}
