package com.example.tudor.foodhunt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * Created by Catalin on 15/05/2018.
 * ---- catalin.lgg3@gmail.com ----
 */

class ServerGetter extends Thread {

    AtomicReference<Vector<FoodHunt.Product>> produseKaufland = null;
    AtomicReference<Vector<FoodHunt.Product>> produseCarrefour = null;
    AtomicReference<Vector<FoodHunt.MagazinMap>> magazine = null;

    private static String getUrlSource(String url) throws IOException {
        URL w = new URL(url);
        URLConnection yc = w.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            a.append(inputLine);
            a.append('\n');
        }
        in.close();

        return a.toString();
    }

    public void run() {
        Vector<FoodHunt.Product> produseKaufland = new Vector<>();
        Vector<FoodHunt.Product> produseCarrefour = new Vector<>();
        Vector<FoodHunt.MagazinMap> magazins = new Vector<>();

        try {
            String rawData = getUrlSource("http://foodhunt.ro/getters/mainGetter.php");
            String[] magazine = rawData.split("===");

            for (String magazin : magazine) {
                FoodHunt.Magazin currentMagazin = null;
                if (!magazin.equals("")) {
                    String[] products = magazin.split("<br>");
                    for (String product : products) {
                        if (product.startsWith("Kaufland"))
                            currentMagazin = FoodHunt.Magazin.KAUFLAND;
                        else if (product.startsWith("Carrefour"))
                            currentMagazin = FoodHunt.Magazin.CARREFOUR;
                        else {
                            String[] attributes = product.split(":::");

                            if(attributes.length == 6) {
                                attributes[2] = attributes[2].replace(",", ".");
                                attributes[3] = attributes[3].replace(",", ".");

                                float vechi = 0;
                                float nou = 0;

                                try {
                                    vechi = Float.parseFloat(attributes[2].replace(" ", ""));
                                } catch (Exception ignored){}
                                try{
                                    nou = Float.parseFloat(attributes[3].replace(" ",""));
                                } catch (Exception ignored) {}

                                FoodHunt.Product produs = new FoodHunt.Product(attributes[0], attributes[1], attributes[4], attributes[5], vechi, nou);
                                if (currentMagazin == FoodHunt.Magazin.KAUFLAND)
                                    produseKaufland.add(produs);
                                else if (currentMagazin == FoodHunt.Magazin.CARREFOUR)
                                    produseCarrefour.add(produs);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.produseKaufland = new AtomicReference<>(produseKaufland);
        this.produseCarrefour = new AtomicReference<>(produseCarrefour);

        try {
            String rawData = getUrlSource("http://foodhunt.ro/getters/magazineGetter.php");
            String[] magazine = rawData.split("<br>");

            for (String magazin : magazine) {

                String[] attributes = magazin.split(":::");

                if(attributes.length == 3) {
                    FoodHunt.Magazin current = null;

                    if (attributes[0].startsWith("Kaufland"))
                        current = FoodHunt.Magazin.KAUFLAND;
                    else if (attributes[0].startsWith("Carrefour"))
                        current = FoodHunt.Magazin.CARREFOUR;

                    //nume,lat,lng
                    FoodHunt.MagazinMap magazinMap = new FoodHunt.MagazinMap(current, Double.parseDouble(attributes[1]), Double.parseDouble(attributes[2]));
                    magazins.add(magazinMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.magazine = new AtomicReference<>(magazins);
    }
}

public class FoodHunt {

    enum Magazin {KAUFLAND, CARREFOUR}

    private Vector<Product> produseKaufland;
    private Vector<Product> produseCarrefour;
    private Vector<Product> toateProdusele;

    private Vector<MagazinMap> magazine;

    public FoodHunt() {
        produseKaufland = new Vector<>();
        produseCarrefour = new Vector<>();
        toateProdusele = new Vector<>();

        magazine = new Vector<>();

        loadAllProductsFromServer();
    }

    static class Tuple{
        MagazinMap magazinMap;
        Product product;

        Tuple(MagazinMap magazinMap, Product product){
            this.magazinMap = magazinMap;
            this.product = product;
        }

        public String toString(){
            return magazinMap + " - " + product;
        }
    }

    static class MagazinMap {
        Magazin magazin;
        double latitude;
        double longitude;

        MagazinMap(Magazin magazin, double latitude, double longitude) {
            this.magazin = magazin;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String toString(){
            return magazin + " - LAT: "+ latitude + ",LNG: " + longitude;
        }
    }

    static class Product {
        String name;
        String category;
        String quanity;
        String photoURL;
        float pretVechi;
        float pretNou;

        Product(String name, String category, String quanity, String photoURL, float pretVechi, float pretNou) {
            this.name = name.replace("\t","").replace("  ","").replace("ă","a").replace("â","a").replace("î","i").replace("ș","s").replace("ț","t");
            this.category = category.replace("\t","").replace("  ","");
            this.quanity = quanity.replace("\t","").replace("  ","");
            this.photoURL = photoURL.replace("\t","").replace("  ","");
            this.pretVechi = pretVechi;
            this.pretNou = pretNou;
        }

        boolean matchesAllTags(String[] tags) {
            for (String tag : tags)
                if (!tag.equals("Toate") && !Pattern.compile(Pattern.quote(tag), Pattern.CASE_INSENSITIVE).matcher(this.name).find())
                    return false;

            return true;
        }

        public String toString(){
            return name + ", "+quanity + ", " + pretNou + " lei";
        }
    }

    Vector<Product> getProduseKaufland(){
        return produseKaufland;
    }

    Vector<Product> getProduseCarrefour(){
        return produseCarrefour;
    }

    Vector<Product> getToateProdusele(){ return  toateProdusele; }

    Vector<MagazinMap> getMagazine(){
        return magazine;
    }

    private void loadAllProductsFromServer() {
        ServerGetter serverGetter = new ServerGetter();
        try {
            serverGetter.start();
            serverGetter.join();
            produseKaufland = serverGetter.produseKaufland.get();
            produseCarrefour = serverGetter.produseCarrefour.get();

            toateProdusele.addAll(produseCarrefour);
            toateProdusele.addAll(produseKaufland);

            magazine = serverGetter.magazine.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Vector<Product> searchProduct(String search, Vector<Product> collection) {
        String[] tags = search.split(" ");

        Vector<Product> result = new Vector<>();

        for (Product product : collection) {
            if (product.matchesAllTags(tags))
                result.add(product);
        }

        return result;
    }

    Vector<String> getCategories(Vector<Product> collection){
        Vector<String> categories = new Vector<>();

        for(Product p : collection)
            if(!categories.contains(p.category))
                categories.add(p.category);

        return categories;
    }

    Vector<Product> findAllCategory(String category, Vector<Product> collection){
        Vector<Product> result = new Vector<>();

        for(Product p : collection)
            if(category.equals(p.category) || category.equals("Toate"))
                result.add(p);

        return result;
    }

    private Product lowestPrice(Vector<Product> collection){

        if(collection.size() == 0)
            return null;

        Product best = collection.firstElement();

        for(Product p : collection)
            if(p.pretNou < best.pretNou)
                best = p;

        return best;
    }

    Tuple smartSearch(String search,double lat, double lng){
        //search in all stores
        Vector<Product> prodKaufland = searchProduct(search,produseKaufland);
        Vector<Product> prodCarrefour = searchProduct(search,produseCarrefour);
        //get store with best product
        Product bestKaufland = lowestPrice(prodKaufland);
        Product bestCarrefour = lowestPrice(prodCarrefour);

        //avoid exceptions
        if(bestKaufland == null)
            if(bestCarrefour == null)
                return null;
            else
                return new Tuple(getClosestStore(Magazin.CARREFOUR,lat,lng),bestCarrefour);

        if(bestCarrefour == null)
            return new Tuple(getClosestStore(Magazin.KAUFLAND,lat,lng),bestKaufland);

        //normal logic
        if(bestKaufland.pretNou < bestCarrefour.pretNou)
            return new Tuple(getClosestStore(Magazin.KAUFLAND,lat,lng),bestKaufland);

        return new Tuple(getClosestStore(Magazin.CARREFOUR,lat,lng),bestCarrefour);
    }

    double distanceBetween(double lat1, double lng1, double lat2, double lng2){
        double init =  Math.sqrt(Math.pow(lat1 - lat2,2) + Math.pow(lng1 - lng2,2));

        double v1 = Math.toRadians(lat1);
        double v2 = Math.toRadians(lat2);

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(dLat) * Math.cos(dLat) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double R = 6371e3;

        return R*c;
    }

    private MagazinMap getClosestStore(Magazin magazin, double lat, double lng){
        MagazinMap magazinMap = null;
        double minDist = Double.MAX_VALUE;

        for(MagazinMap m : magazine){
            if(m.magazin == magazin){
                double dist = distanceBetween(lat,lng,m.latitude,m.longitude);
                if(dist < minDist) {
                    magazinMap = m;
                    minDist = dist;
                }
            }
        }
        return  magazinMap;
    }
}