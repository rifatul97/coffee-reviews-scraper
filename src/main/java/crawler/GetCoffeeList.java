package crawler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import model.CoffeeId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetCoffeeList {

//    private final String searchUrl = "https://www.coffeereview.com/advanced-search";
    private static final String searchUrl = "https://www.coffeereview.com/review";
//    private final String filterByNA = "/?keyword&locations=na";

    private static final String byPage = "/page/";
    private static final String SELECT_ALL_TITLES_XPATH = "//div[@class='row row-1']";

    public static List<CoffeeId> ByPage(WebClient client, int pageNum) throws Exception {
        final List<DomElement> reviewTitleListPerPage = getByPageNumber(client, pageNum)
                .getByXPath(SELECT_ALL_TITLES_XPATH);
        var coffeeReviewsTitleList = new ArrayList<CoffeeId>();
        for (DomElement dom: reviewTitleListPerPage) {
            final var coffeeId = extractCoffeeIds(dom);
            coffeeReviewsTitleList.add(coffeeId);
        }

        return coffeeReviewsTitleList;
    }

    private static CoffeeId extractCoffeeIds(DomElement dom) {
        var roaster = dom.getElementsByTagName("p").get(0).getTextContent();
        var name = dom.getElementsByTagName("p").get(1).getTextContent();
        var reviewDate = dom.getElementsByTagName("strong").get(0).getNextSibling().toString();
        var price = dom.getElementsByTagName("strong").get(1).getNextSibling().toString();
        var slug = dom.getElementsByTagName("a").get(0).getAttribute("href").substring("https://www.coffeereview.com/review/".length());
        return new CoffeeId(name, roaster, price, slug, reviewDate);
    }

    private static HtmlPage getByPageNumber(WebClient client, int pageNum) throws IOException {
        String url = searchUrl + ((pageNum > 1) ? byPage + pageNum : ""); //+ filterByNA;
        return client.getPage(url);
    }

}
