package udp.server.operations.plates;

import udp.server.data.PlateRegistry;
import udp.server.operations.Operation;
import udp.utils.Request;

public class RegisterPlate extends Operation {
    @Override
    public String perform(Request req) {
        String response = "-1";
        if (req.getArguments().size() >= 2) {
            response = PlateRegistry.getInstance().addRegistry(req.getArgument(0), req.getArgument(1));
        }
        return this.addRequestEnd(req, response);
    }
}
