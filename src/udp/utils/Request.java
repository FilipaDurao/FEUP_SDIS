package udp.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class Request {

    private String operation;
    private ArrayList<String> arguments;

    public Request(String requestString) {
        String[] responseTokens = requestString.split(" ");
        operation = responseTokens[0].toUpperCase();
        arguments = new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(responseTokens, 1, responseTokens.length)));
    }

    public String getOperation() {
        return operation;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public String getArgument(int index) {
        if(index < arguments.size())
            return arguments.get(index);
        else
            return "";
    }

    public void addArgument(int index, String argument) {
        this.arguments.add(index, argument);
    }

    public void addArgument(String argument) {
        this.arguments.add(argument);
    }

    @Override
    public String toString() {
        StringBuilder messageBuilder = new StringBuilder();
        for (String argument : arguments) {
            messageBuilder.append(argument).append(" ");
        }
        return operation + " " + messageBuilder.toString();
    }

    public byte[] getBytes() {
        return this.toString().getBytes();
    }
}
