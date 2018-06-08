package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.impl.BB.BBProtocol;
import bgu.spl181.net.impl.BB.EncoderDecoder;
import bgu.spl181.net.impl.BB.SharedData;
import bgu.spl181.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args){
        //int port = Integer.parseInt(args[0]);
        int port = 7777;
        SharedData data = new SharedData();


        Server.reactor(
                Runtime.getRuntime().availableProcessors()+1,
                port,
                () -> new BBProtocol(data),
                EncoderDecoder::new
        ).serve();

    }
}
