package bgu.spl181.net.impl.BB;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{
    private String username;
    private String type="admin";
    private String password;
    private String country;
    ArrayList<RentedMovie> movies;
    private Integer balance;


    public User(String username, String password, String info) {
        this.username=username;
        this.password=password;
        this.type= "normal";
        if(info!=null) {
            info=info.substring(9, info.length()-1);
            country=info;
        }
        balance=0;
        movies=new ArrayList<>();

    }

    public void addMovie(Movie movie){
        movies.add(new RentedMovie(movie));
        balance = balance-movie.getPrice();
        }

    public void returnMovie(String movie) {
        movie=movie.substring(1, movie.length()-1);
        ArrayList<RentedMovie> moviesCopy = new ArrayList<>(movies);
        for(RentedMovie current:moviesCopy){
            if(current.getName().equals(movie))
                movies.remove(current);
        }
    }

    private class RentedMovie {
        private String id;
        private String name;

        public RentedMovie(Movie movie){
            id=movie.getId();
            name=movie.getName();
        }

        public String getName() {
            return name;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getBalance() {
        return balance;
    }

    public void incBalance(int amount){
        balance+=amount;
    }

    public String getCountry() {
        return country;
    }

    public boolean alreadyRenting(String movieName){
        movieName=movieName.substring(1, movieName.length()-1);
        for(RentedMovie current:movies){
            if(current.getName().equals(movieName))
                return true;
        }
        return false;
    }
    public boolean isAdmin(){
        return type.equals("admin");
    }
}