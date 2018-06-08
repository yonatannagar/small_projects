package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.impl.BB.BBProtocol;
import bgu.spl181.net.impl.BB.EncoderDecoder;
import bgu.spl181.net.impl.BB.SharedData;
import bgu.spl181.net.srv.Server;

public class TPCMain {
    public static void main(String[] args){
        //int port = Integer.parseInt(args[0]);
        int port = 7777;
        SharedData data = new SharedData();

        Server.threadPerClient(
                port,
                () -> new BBProtocol(data),
                EncoderDecoder::new
        ).serve();
    }
}
