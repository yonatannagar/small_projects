package bgu.spl181.net.impl.BB;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedData{
    private ArrayList<User> usersList;
    private ReentrantReadWriteLock usersLock;
    private ArrayList<Movie> moviesList;
    private ReentrantReadWriteLock moviesLock;

    private AtomicInteger nextMovieId;


    public SharedData() {
        usersLock=new ReentrantReadWriteLock();
        moviesLock=new ReentrantReadWriteLock();
        generateBaseData();
        nextMovieId=new AtomicInteger(moviesList.size()+1);


    }
    private void generateBaseData(){
        Users baseUsers = null;
        boolean usersExists=false;
        try(JsonReader reader = new JsonReader(new FileReader("Database/Users.json"))) {
            baseUsers = new Gson().fromJson(reader, Users.class);
            usersExists=true;
        }catch (IOException ex) {//file not found- creating empty file
            usersList=new ArrayList<>();
        }

        Movies baseMovies = null;
        boolean moviesExists=false;
        try(JsonReader reader = new JsonReader(new FileReader("Database/Movies.json"))) {
            baseMovies = new Gson().fromJson(reader, Movies.class);
            moviesExists=true;
        }catch (IOException ex) {
            moviesList=new ArrayList<>();
        }

        if(usersExists)
            usersList=baseUsers.getUsers();
        else { //create new empty Users.Json through exportUsers
            exportUsers();
        }
        if(moviesExists)
            moviesList=baseMovies.getMovies();
        else { //create new empty Movies.Json through exportMovies
            exportMovies();
        }



    }

    public void userIncBalance(User user, int amount) {
        try {
            usersLock.writeLock().lock();
            user.incBalance(amount);
        }finally {
            usersLock.writeLock().unlock();
            exportUsers();
        }
    }

    public int getUserBalance(String username) {
        try {
            usersLock.readLock().lock();
            return getUser(username).getBalance();
        }finally{
            usersLock.readLock().unlock();
        }
    }

    class Users{
        ArrayList<User> users;
        protected ArrayList<User> getUsers(){return users;}
        protected void setUsers(ArrayList<User> users) {
            this.users = users;
        }
    }
    class Movies{
        ArrayList<Movie> movies;
        protected ArrayList<Movie> getMovies(){return movies;}
        protected void setMovies(ArrayList<Movie> movies){
            this.movies=movies;
        }
    }

    public void exportUsers(){
        //usersLock: read=unlocked, write=locked
        usersLock.readLock().lock();
        Users toPrint = new Users();
        toPrint.setUsers(usersList);
        try (Writer writer = new FileWriter("Database/Users.json")) {
            Gson gson = new Gson();
            gson.toJson(toPrint, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            usersLock.readLock().unlock();
        }
    }
    public void exportMovies(){
        //moviesLock: read=unlocked, write=locked
        moviesLock.readLock().lock();
        Movies toPrint = new Movies();
        toPrint.setMovies(moviesList);
        try (Writer writer = new FileWriter("Database/Movies.json")) {
            Gson gson = new Gson();
            gson.toJson(toPrint, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            moviesLock.readLock().unlock();
        }

    }
    public boolean login(String username, String password) {
        //usersLock: read=unlocked, write=unlocked
        usersLock.readLock().lock();
        try {
            User client = findUser(username);
            return client != null && client.getPassword().equals(password);
        }finally {
            usersLock.readLock().unlock();
        }
    }

    public boolean register(String username, String password, String info){
        //usersLock: read=locked, write=locked
        usersLock.writeLock().lock();
        try {
            if (findUser(username) != null)
                return false; //USER ALREADY REGISTERED
            User toReg;
            if (info != null)
                toReg = new User(username, password, info);
            else
                toReg = new User(username, password, null);
            usersList.add(toReg);
        }finally {
            usersLock.writeLock().unlock();
            exportUsers();
        }
        return true;
    }

    private User findUser(String username) {
        usersLock.readLock().lock();
        try {
            for (User curr : usersList) {
                if (curr.getUsername().equals(username))
                    return curr;
            }
            return null;
        }finally {
            usersLock.readLock().unlock();
        }
    }

    public User getUser(String username) {
        usersLock.readLock().lock();
        try {
            return findUser(username);
        }finally {
            usersLock.readLock().unlock();
        }
    }

    public ArrayList<String> getMoviesList(){
        moviesLock.readLock().lock();
        try {
            ArrayList<String> list = new ArrayList<>();
            for (Movie current : moviesList) {
                list.add("\"" + current.getName() + "\"");
            }
            return list;
        }finally {

            moviesLock.readLock().unlock();

        }
    }

    public Movie getMovie(String name){
        moviesLock.readLock().lock();
        try {
            name = name.substring(1, name.length() - 1);
            for (Movie current : moviesList) {
                if (current.getName().equals(name))
                    return current;
            }
            return null;
        }finally {
            moviesLock.readLock().unlock();
        }
    }

    public String getMovieInfo(String movieName, boolean isBroadcast) {
        moviesLock.readLock().lock();
        try {
            Movie askedForMovie = getMovie(movieName);
            String ans = movieName + " " + askedForMovie.getAvailableAmount() + " " + askedForMovie.getPrice();
            if (!isBroadcast) {
                ans += askedForMovie.generateBannedCountries();
                return ans;
            } else
                return ans;
        }finally {
            moviesLock.readLock().unlock();
        }
    }

    public int getAvailableAmount(String movie){
        moviesLock.readLock().lock();
        try {
            return getMovie(movie).getAvailableAmount();
        }finally {
            moviesLock.readLock().unlock();
        }
    }

    public int getPrice(String movie){
        moviesLock.readLock().lock();
        try {
            return getMovie(movie).getPrice();
        }finally {
            moviesLock.readLock().unlock();
        }
    }

    public boolean countryIsBanned(String movie, String country){
        Movie currMovie = getMovie(movie);
        moviesLock.readLock().lock();
        try {

            return currMovie.isBanned(country);
        }finally {
            moviesLock.readLock().unlock();
        }
    }

    public void addMovieToUser(User user, String movieName){
        Movie movie = getMovie(movieName);
        usersLock.writeLock().lock();
        moviesLock.writeLock().lock();
        try {
            user.addMovie(movie);
            movie.decAvailable();
        }finally {
            moviesLock.writeLock().unlock();
            usersLock.writeLock().unlock();
            exportUsers();
            exportMovies();
        }
    }

    public void returnMovieFromUser(User user, String movieName){
        //usersLock: read=locked, write=locked
        //moviesLock: read=locked, write=locked
        Movie movie = getMovie(movieName);
        moviesLock.writeLock().lock();
        usersLock.writeLock().lock();
        try {
            user.returnMovie(movieName);
            movie.incAvailable();
        }finally {
            moviesLock.writeLock().unlock();
            usersLock.writeLock().unlock();
            exportUsers();
            exportMovies();
        }
    }

    public void addMovie(String moviename, int amount, int price, ArrayList<String> bannedCountries){
        //moviesLock: read=locked, write=locked
        moviesLock.writeLock().lock();
        try {
            moviename = moviename.substring(1, moviename.length() - 1);

            Movie movie = new Movie(highestMovieId()+1, moviename, amount, price, bannedCountries);
            moviesList.add(movie);
        }finally {
            moviesLock.writeLock().unlock();
            exportMovies();
        }
    }
    private int highestMovieId(){
        return Integer.parseInt(moviesList.get(moviesList.size()-1).getId());
    }

    public boolean movieIsRented(String moviename) {
        //moviesLock: read=unlocked, write=locked
        moviesLock.readLock().lock();
        try {
            return getMovie(moviename).isRentedATM();
        }finally {
            moviesLock.readLock().unlock();
        }
    }

    public void removeMovie(String moviename){
        //moviesLock: read=locked, write=locked
        moviesLock.writeLock().lock();
        try {
            moviesList.remove(getMovie(moviename));
        }finally {
            moviesLock.writeLock().unlock();
            exportMovies();
        }
    }

    public void movieChangePrice(String moviename, String newPrice){
        //moviesLock: read=locked, write=locked
        moviesLock.writeLock().lock();
        try {
            getMovie(moviename).changePrice(Integer.parseInt(newPrice));
        }finally {
            moviesLock.writeLock().unlock();
            exportMovies();
        }
    }
}
