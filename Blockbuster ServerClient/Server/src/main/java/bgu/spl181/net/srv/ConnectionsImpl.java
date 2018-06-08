package bgu.spl181.net.srv;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connectionsMap;
    private AtomicInteger uniqueId;
    private final Object o = new Object();

    public ConnectionsImpl() {
        connectionsMap= new ConcurrentHashMap<>();
        uniqueId=new AtomicInteger(0);
    }

    /**
     * sends a msg to given connectionId
     * @param connectionId
     * @param msg
     * @return true if sent, false if given connectionId doesn't exists
     */
    @Override
    public boolean send(int connectionId, T msg) {
        if (!connectionsMap.containsKey(connectionId))
            return false;
        connectionsMap.get(connectionId).send(msg);
        return true;
    }

    /**
     * sends given msg to all connected clients
     * @param msg
     */
    @Override
    public void broadcast(T msg) {
        for (Map.Entry<Integer, ConnectionHandler<T>> currClient : connectionsMap.entrySet())
            currClient.getValue().send(msg);
    }

    /**
     * disconnects a specific client
     * @param connectionId
     */
    @Override
    public void disconnect(int connectionId) {
        connectionsMap.remove(connectionId);
    }


    public int addConnection(ConnectionHandler<T> ch){
        synchronized (o) {
            connectionsMap.put(uniqueId.getAndIncrement(), ch);
            return uniqueId.get() - 1;
        }
    }
    public int getSize(){
        return connectionsMap.size();
    }

}
