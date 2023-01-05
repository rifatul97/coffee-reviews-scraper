package crawler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.CoffeeId;
import model.CoffeeReviewDetail;
import util.JsonFileWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class GetCoffeeReviewDetails {
    private static final String SELECT_TABLE_XPATH = "//table[@class='review-template-table']";
    private static final String SELECT_SCORE_XPATH = "//span[@class='review-template-rating']";
    private static final String SELECT_ROASTER_XPATH = "//p[@class='review-roaster']";
    private static final String SELECT_TITLE_XPATH = "//h1[@class='review-title']";
    private static final String SELECT_REVIEWS_XPATH = "//div[@class='review-template']";
    static final String reviewUrl = "https://www.coffeereview.com";
    final WebClient client;
    final Gson gson;
    public static CoffeeId[] reviewsTitleList;
    public static int totalTimeElasped;

    public GetCoffeeReviewDetails(WebClient client, Gson gson) throws IOException, InterruptedException {
        this.client = client;
        this.gson = gson;
    }

    public CoffeeId[] loadCoffeeList(String filePth) throws IOException {
        reviewsTitleList = readCoffeeReviewTitleFromFile(gson, filePth);
        return reviewsTitleList;
    }

    public static void start(String fileToAppend, int from, int to) throws Exception {
        totalTimeElasped = 0;
        var client = new WebClient();
        var gson = new GsonBuilder().setPrettyPrinting().create();
        clientSetup(client);

        appendStringToFile("[", fileToAppend);

        for (int i = from; i <= to; i++) {
            System.out.println("Page Scraping: " + i + " / " + to);
            var listOfCoffeeByPage = GetCoffeeList.ByPage(client, i);
            List<String> coffeeReviewDetails = new ArrayList<>();
            sleepFor(2500);
            for (CoffeeId coffeeId : listOfCoffeeByPage) {
                var reviewPage = (HtmlPage) getByReviewLink(client, coffeeId);
                String jsonObject = JsonFileWriter.convertToJsonObject(gson, scrapeReviewDetail(reviewPage));
                coffeeReviewDetails.add(jsonObject);
                sleepFor(1000);
            }

            boolean isLast = false;
            if (i == to) {
                isLast = true;
            }
            appendListToFile(coffeeReviewDetails, fileToAppend, isLast);
        }

        appendStringToFile("]", fileToAppend);
        System.out.println("total time taken: " + (double) (totalTimeElasped) + " seconds");
    }

    private static void appendStringToFile(String str, String fileToAppend) {
        try (FileWriter fw = new FileWriter(fileToAppend, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(str);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    private static void appendListToFile(List<String> coffeeReviewDetails, String fileToAppend, boolean last) {
        try (FileWriter fw = new FileWriter(fileToAppend, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (int i = 0; i < coffeeReviewDetails.size(); i++) {
                out.println(coffeeReviewDetails.get(i));
                if (last && i == coffeeReviewDetails.size() - 1) {
                } else {
                    out.println(",");
                }
            }
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    private static void sleepFor(int timeToSleep) throws InterruptedException {
        Random rn = new Random();
        int sleepTime = rn.nextInt(timeToSleep) + 1000;
        Thread.sleep(sleepTime);
        totalTimeElasped += sleepTime;
    }

    private static void clientSetup(WebClient client) {
        client.getOptions().setUseInsecureSSL(true);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }

    private static String scrapCoffeeTitle(HtmlPage reviewPage) {
        var titleXPathSelect = reviewPage.getFirstByXPath(SELECT_TITLE_XPATH);
        if (titleXPathSelect != null) {
            if (titleXPathSelect instanceof HtmlHeading1) {
                return ((HtmlHeading1) titleXPathSelect).getTextContent();
            }
        }
        return "";
    }

    private static String scrapCoffeeScore(HtmlPage reviewPage) {
        var scoreXPathSelect = reviewPage.getFirstByXPath(SELECT_SCORE_XPATH);
        if (scoreXPathSelect != null) {
            if (scoreXPathSelect instanceof HtmlSpan) {
                return ((HtmlSpan) scoreXPathSelect).getTextContent();
            }
        }
        return "0";
    }

    public int numOfCoffeeTitles() {
        return reviewsTitleList.length;
    }

    private static CoffeeReviewDetail scrapeReviewDetail(HtmlPage reviewPage) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Roaster", scrapRoasterName(reviewPage));
        map.put("Title", scrapCoffeeTitle(reviewPage));
        map.put("Score", scrapCoffeeScore(reviewPage));
        map.putAll(scrapeCoffeeRatings(reviewPage));
        map.putAll(scrapeCoffeeDiscussions(reviewPage));

        return createCoffeeDetailEntity(map);
    }

    private static CoffeeReviewDetail createCoffeeDetailEntity(HashMap<String, String> map) {
        return new CoffeeReviewDetail(map.getOrDefault("Title", ""),
                map.getOrDefault("Roaster", ""),
                map.getOrDefault("Roaster Location", ""),
                map.getOrDefault("Coffee Origin", ""),
                map.getOrDefault("Roast Level", ""),
                Integer.parseInt(map.getOrDefault("Score", "0")),
                map.getOrDefault("Agtron", ""),
                Integer.parseInt(map.getOrDefault("Aroma", "0")),
                Integer.parseInt(map.getOrDefault("Acidity/Structure", "0")),
                Integer.parseInt(map.getOrDefault("Body", "0")),
                Integer.parseInt(map.getOrDefault("Flavor", "0")),
                Integer.parseInt(map.getOrDefault("Aftertaste", "0")),
                Integer.parseInt(map.getOrDefault("With Milk", "0")),
                map.getOrDefault("blindAssessment", ""),
                map.getOrDefault("Notes", ""),
                map.getOrDefault("Summary", ""));
    }

    private static Map<String, String> scrapeCoffeeDiscussions(HtmlPage reviewPage) {
        var reviewXPathSelect = reviewPage.getFirstByXPath(SELECT_REVIEWS_XPATH);
        HashMap<String, String> map = new HashMap<>();
        if (reviewXPathSelect != null) {
            if (reviewXPathSelect instanceof DomElement) {
                DomNodeList<DomNode> reviewsContainer = ((DomElement) reviewXPathSelect).getChildNodes();
                if (reviewsContainer.size() >= 5) {
                    String review = reviewsContainer.get(5).getTextContent();
                    if (review.startsWith("Blind Assessment:")) {
                        review = review.substring("Blind Assessment:".length());
                    }
                    map.put("blindAssessment", review.strip());
                }
                if (reviewsContainer.size() >= 7) {
                    String notes = reviewsContainer.get(7).getTextContent();
                    if (notes.startsWith("Notes:")) {
                        notes = notes.substring("Notes:".length());
                    }
                    map.put("Notes", notes.strip());
                }
                if (reviewsContainer.size() >= 9) {
                    String bottomLine = reviewsContainer.get(9).getTextContent();
                    if (bottomLine.startsWith("The Bottom Line:")) {
                        bottomLine = bottomLine.substring("The Bottom Line:".length());
                    }
                    map.put("Summary", bottomLine.strip());
                }
            }
        }
        return map;
    }

    private static String scrapRoasterName(HtmlPage reviewPage) {
        var roasterXPathSelect = reviewPage.getFirstByXPath(SELECT_ROASTER_XPATH);
        if (roasterXPathSelect != null) {
            if (roasterXPathSelect instanceof HtmlParagraph) {
                return ((HtmlParagraph) roasterXPathSelect).getTextContent();
            }
        }
        return "";
    }

    private static HashMap<String, String> scrapeCoffeeRatings(HtmlPage reviewPage) {
        List<HtmlTable> coffeeStatTables = reviewPage.getByXPath(SELECT_TABLE_XPATH);
        HashMap<String, String> map = new HashMap<>();
        for (HtmlTable table : coffeeStatTables) {
            for (HtmlTableRow row : table.getRows()) {
                String[] split = row.getVisibleText().split(":");
                if (split.length == 2) {
                    map.put(split[0].strip(), split[1].strip());
                }
            }
        }
        return map;

    }

    private CoffeeId[] readCoffeeReviewTitleFromFile(Gson gson, String coffeeReviewLinks) throws IOException {
        Path path = new File(coffeeReviewLinks).toPath();
        Gson gson1 = new Gson();
        try (Reader reader = Files.newBufferedReader(path,
                StandardCharsets.UTF_8)) {
            return gson1.fromJson(reader, CoffeeId[].class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static HtmlPage getByReviewLink(WebClient client, CoffeeId reviewsTitle) throws IOException {
        String url = reviewUrl + "/review/" + reviewsTitle.getSlug();
//        System.out.println("Getting Page for: " + url);
        return client.getPage(url);
    }
}
