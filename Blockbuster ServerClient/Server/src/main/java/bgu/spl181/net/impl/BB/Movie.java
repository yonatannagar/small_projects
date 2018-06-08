package bgu.spl181.net.impl.BB;

import java.io.Serializable;
import java.util.ArrayList;

public class Movie implements Serializable {
    private String id;
    private String name;
    private Integer price;
    private ArrayList<String> bannedCountries;
    private Integer availableAmount;
    private Integer totalAmount;

    public Movie(int id ,String moviename, int amount, int price, ArrayList<String> bannedCountries) {
        this.id= String.valueOf(id);
        name=moviename;
        totalAmount=amount;
        availableAmount=totalAmount;
        this.price=price;
        this.bannedCountries=bannedCountries;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getAvailableAmount() {
        return availableAmount;
    }

    public String generateBannedCountries() {
        String ans = " ";
        if(bannedCountries.isEmpty())
            return "";
        for(String curr: bannedCountries) {
            ans += "\""+curr+"\" ";
        }
        return ans.substring(0, ans.length()-1);
    }

    public boolean isBanned(String country){
        return bannedCountries.contains(country);
    }
    public void decAvailable(){
        availableAmount--;
    }

    public void incAvailable() {
        availableAmount++;
    }
    public boolean isRentedATM(){
        return !totalAmount.equals(availableAmount);
    }

    public void changePrice(int newPrice){
        price=newPrice;
    }
}
