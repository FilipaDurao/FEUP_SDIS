package udp.server.operations.plates;

import udp.server.data.PlateRegistry;
import udp.server.operations.Operation;
import udp.utils.Request;

public class LookupPlate extends Operation {
    @Override
    public String perform(Request req) {
        String response = "-1";
        if (req.getArguments().size() >= 1) {
            response = PlateRegistry.getInstance().getOwner(req.getArgument(1));
        }
        return this.addRequestEnd(req, response);
    }
}
