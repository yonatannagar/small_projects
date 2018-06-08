package bgu.spl181.net.impl.BB;

import bgu.spl181.net.api.MessageEncoderDecoder;

import java.util.Arrays;

public class EncoderDecoder implements MessageEncoderDecoder<String> {
    private byte[] bytes = new byte[1 << 10];
    private int len = 0;

    public EncoderDecoder() {}

    public String decodeNextByte(byte nextByte) {
        if (nextByte == '\n') {
            return /*generateMessage*/(popString());
        }

        pushByte(nextByte);
        return null;
    }

    public byte[] encode(String message) {
        return ((message)/*.toString()*/ +"\n").getBytes();
    }
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len);
        len = 0;
        return result;
    }
/*
    private Message generateMessage(String input){
        String[] array = input.split(" ");
        switch(array[0]){
            case "REGISTER": {
                if(array.length==3)
                    return new Register(array[1], array[2], null);
                else
                    return new Register(array[1], array[2], array[3].substring(10, array[3].length()-1));
            }
            case "LOGIN": return new Login(array[1], array[2]);
            case "SIGNOUT": return new Signout();
            case "REQUEST": return generateRequest(array);
            default: return null;
        }
    }
    private Request generateRequest(String[] array) {
        switch (array[1]) {
            case "balance": {
                String argument = array[1] + " " + array[2];
                String param = "";
                if (array.length >= 4)
                    for (int i = 3; i < array.length; ++i)
                        param += " " + array[i];

                if (param.equals(""))
                    return new Request(argument, null);
                else
                    return new Request(argument, param);
            }
            default: {
                if (array.length == 2) return new Request(array[1], null);
                String args = "";
                for (int i = 2; i < array.length; ++i)
                    args += " " + array[i];
                return new Request(array[1], args);
            }
        }
    }
*/
}
