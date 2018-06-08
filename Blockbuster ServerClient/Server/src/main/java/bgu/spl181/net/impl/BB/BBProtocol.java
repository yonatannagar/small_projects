package bgu.spl181.net.impl.BB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BBProtocol extends UserTextServiceProtocol {
    public BBProtocol(SharedData data) {
        super(data);
    }

    @Override
    public void request(String args) {
        List<String> parts = new ArrayList<>();
        Matcher m = Pattern.compile("\"(.*?)\"|\\S+")
                .matcher(args);
        while (m.find()) {
            parts.add(m.group());
        }

        switch(parts.get(0)){
            case "balance":{
                if(!isLoggedIn()) {
                    errorResponse("request "+parts.get(0));
                    return;
                }
                if(parts.get(1).equals("info")){
                    sendToClient("ACK balance "+data.getUserBalance(me.getUsername()));//me.getBalance());
                    break;
                }else if(parts.get(1).equals("add")){
                    int amount = Integer.parseInt(parts.get(2));
                    //me.incBalance(amount);
                    data.userIncBalance(me, amount);
                    sendToClient("ACK balance "+data.getUserBalance(me.getUsername())+" added "+amount);
                    break;
                }
            }
            case "info":{
                if(!isLoggedIn()) {
                    errorResponse("request "+parts.get(0)+" "+parts.get(1));
                    return;
                }
                if(parts.size()==1){ //response should be a movies list
                    ArrayList<String> l = data.getMoviesList();
                    String chained = "";
                    for(String s : l){
                        chained+=s+" ";
                    }
                    if(!chained.equals("")){
                        chained.substring(0, chained.length()-1);
                    }
                    sendToClient("ACK info "+chained);
                    return;
                }else{//specific movie info requested
                    if(data.getMovie(parts.get(1))!=null){
                        sendToClient("ACK info "+data.getMovieInfo(parts.get(1), false));
                        return;
                    } else{
                        errorResponse("request "+parts.get(0));
                        return;
                    }
                }
            }
            case "rent":{
                if(!rentChecker(parts.get(1))) {
                    errorResponse("request "+parts.get(0));
                    return;
                }else{
                    data.addMovieToUser(me, parts.get(1));
                    sendToClient("ACK rent "+parts.get(1)+" success");
                    broadcast(data.getMovieInfo(parts.get(1),true));
                    return;
                }
            }
            case "return":{
                if(!(returnChecker(parts.get(1)))){
                    errorResponse("request "+parts.get(0));
                    return;
                }else{
                    data.returnMovieFromUser(me, parts.get(1));
                    sendToClient("ACK return "+parts.get(1)+" success");
                    broadcast(data.getMovieInfo(parts.get(1),true));
                    return;
                }
            }
            default:{
                adminRequests(args);
            }

        }
        return;
    }

    private boolean rentChecker(String movieName) {
        return isLoggedIn() && data.getMovie(movieName) != null && data.getAvailableAmount(movieName) != 0 &&
                data.getPrice(movieName) <= me.getBalance() && !data.countryIsBanned(movieName, me.getCountry()) &&
                !me.alreadyRenting(movieName);
    }

    private boolean returnChecker(String movieName){
        return isLoggedIn() && data.getMovie(movieName) != null && me.alreadyRenting(movieName);
    }

    private void adminRequests(String args) {
        List<String> parts = new ArrayList<>();
        Matcher m = Pattern.compile("\"(.*?)\"|\\S+")
                .matcher(args);
        while (m.find()) {
            parts.add(m.group());
        }
        if(!isLoggedIn() || !me.isAdmin()) {
            errorResponse("request " + parts.get(0));
            return;
        }
        //user is an admin, deal with his request
        switch(parts.get(0)){
            case "addmovie":{
                if(data.getMovie(parts.get(1))!=null || Integer.parseInt(parts.get(2))<=0 ||
                        Integer.parseInt(parts.get(3))<=0) {
                    errorResponse("request " + parts.get(0));
                    return;
                }else{
                    ArrayList<String> bannedCountries=new ArrayList<>();
                    if(parts.size()>4){
                        for(int i=4; i<parts.size(); ++i)
                            bannedCountries.add(parts.get(i).substring(1, (parts.get(i)).length()-1));

                    }
                    data.addMovie(parts.get(1), Integer.parseInt(parts.get(2)), Integer.parseInt(parts.get(3)),
                            bannedCountries);
                    sendToClient("ACK addmovie "+parts.get(1)+" success");
                    broadcast(data.getMovieInfo(parts.get(1),true));
                    break;
                }
            }
            case "remmovie":{
                if(data.getMovie(parts.get(1))==null || data.movieIsRented(parts.get(1))) {
                    errorResponse("request " + parts.get(0));
                    return;
                }else{
                    data.removeMovie(parts.get(1));
                    sendToClient("ACK remmovie "+parts.get(1)+" success");
                    broadcast(parts.get(1)+" removed");
                    break;
                }
            }
            case "changeprice":{
                if(data.getMovie(parts.get(1))==null || Integer.parseInt(parts.get(2))<1) {
                    errorResponse("request " + parts.get(0));
                    return;
                }else{
                    data.movieChangePrice(parts.get(1), parts.get(2));
                    sendToClient("ACK changeprice "+parts.get(1)+" success");
                    broadcast(data.getMovieInfo(parts.get(1),true));
                    break;
                }

            }
        }
    }


}
