package model;

public record CoffeeReviewDetail(String Name, String Roaster, String Location, String Origin, String RoasterLevel,
                                 int Score, String Agtron, int Aroma, int Acidity, int Body, int Flavor, int AfterTaste,
                                 int WithMilk, String BlindReview, String Notes, String Summary) {

    /**
     * replace("[^ ]+\\.[^ ]+", "")
     *                 .replace("Visit.*\\n?", "");
     */

    public String getFullReviewText() {
        System.out.println();
        String full = BlindReview + Notes + Summary;
        String fullA = full.replaceAll("[^ ]+\\.[^ ]+", "");
        String fullB = fullA.toLowerCase().replaceAll("visit.*\\n?", "");
        System.out.println(fullB);
        return fullB;
    }
}
