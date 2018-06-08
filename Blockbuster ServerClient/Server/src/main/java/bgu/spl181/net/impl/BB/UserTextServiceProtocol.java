package bgu.spl181.net.impl.BB;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;

import java.util.concurrent.ConcurrentHashMap;

public abstract class UserTextServiceProtocol implements BidiMessagingProtocol<String>{
    protected SharedData data; //given from server during creation
    private Connections<String> connections;
    private int connectionId;
    private boolean loggedIn;
    private boolean shouldTerminate=false;
    protected User me;
    static private final ConcurrentHashMap<Integer, String> loggedInUsers = new ConcurrentHashMap<>();


    public UserTextServiceProtocol(SharedData data) {
        this.data = data;
    }

    public void start(int connectionId, Connections<String> connections) {
        this.connectionId=connectionId;
        this.connections=connections;
    }

    public void process(String msg) {
        String[] prefixSplit = msg.split(" ",2);
        switch(prefixSplit[0]){
            case "REGISTER" :{
                String[] args = prefixSplit[1].split(" ");
                if(args.length==2) { //there's no info at args[2]
                    if (register(args[0], args[1], null)) {
                        sendToClient("ACK registration succeeded");
                        return;
                    }
                    else {
                        errorResponse("registration");
                        return;
                    }
                } else {
                    if (register(args[0], args[1], args[2])) {
                        sendToClient("ACK registration succeeded");
                        return;
                    }
                    else {
                        errorResponse("registration");
                        return;
                    }
                }
            }
            case "LOGIN" :{
                String[] args = prefixSplit[1].split(" "); //[0]=user, [1]=pass
                if(login(args[0], args[1])) {
                    sendToClient("ACK login succeeded");
                    return;
                }
                else {
                    errorResponse("login");
                    return;
                }
            }
            case "SIGNOUT" :{
                if(signout()) {
                    return;
                }else{
                    errorResponse("signout");
                    return;
                }
            }
            case "REQUEST" :{
                request(prefixSplit[1]);
                return;
            }
        }
    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public boolean login(String username, String password) {
        synchronized (loggedInUsers) {
            if (loggedIn || loggedInUsers.containsValue(username))
                return false;
            else{
                if(data.login(username, password)) {
                    me=data.getUser(username);
                    loggedInUsers.put(connectionId, username);
                    loggedIn=true;
                    return true;
                }
            }
            return false;
        }
    }

    public boolean register(String username, String password, String info){
        if(loggedIn)
            return false;
        if(data.register(username, password, info))
            return true;
        return false;
    }

    public boolean signout(){
        synchronized (loggedInUsers) {
            if (!loggedIn)
                return false;
            else {
                sendToClient("ACK signout succeeded");
                loggedInUsers.remove(connectionId);
                shouldTerminate = true;
                me = null;
                connections.disconnect(connectionId);
                return true;
            }
        }
    }
    public abstract void request(String args); //TO BE IMPLEMENTED IN SUCCESSORS

    /**
     * broadcasts msg to all logged in users at given moment
     * @param msg
     */
    public void broadcast(String msg){
        for(Integer connId : loggedInUsers.keySet()){
            connections.send(connId, "BROADCAST movie "+msg);
        }
    }

    public void errorResponse(String commandName){
        connections.send(connectionId, "ERROR "+commandName+" failed");
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void sendToClient(String msg){
        connections.send(connectionId, msg);
    }
}
