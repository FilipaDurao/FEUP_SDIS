package udp.server.operations;

import udp.utils.Request;

public class Operation implements OperationInterface {
    @Override
    public String perform(Request req) {
        return req.toString();
    }

    protected String addRequestEnd(Request request, String response) {
        return response + "\n" + request.toString() + "\n";
    }
}
