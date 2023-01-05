package model;

//public record CoffeeId(String name,
//                       String roaster,
//                       String price,
//                       String slug,
//                       String date){}

public class CoffeeId {

    public String name;
    public String roaster;
    public String price;
    public String slug;
    public String date;

    public CoffeeId(String name, String roaster, String price, String slug, String date) {
        this.name = name;
        this.roaster = roaster;
        this.price = price;
        this.slug = slug;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoaster() {
        return roaster;
    }

    public void setRoaster(String roaster) {
        this.roaster = roaster;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}