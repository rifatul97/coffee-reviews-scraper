package util;

import com.google.gson.Gson;
import model.CoffeeReviewDetail;

import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonFileReader {

    final static String COFFEE_DETAIL_LIST_FILEPATH = "src/main/resources/coffee_reviews_details.json";

    public static CoffeeReviewDetail[] getCoffeeList(Gson gson) {
        Path path = new File(COFFEE_DETAIL_LIST_FILEPATH).toPath();
        try (Reader reader = Files.newBufferedReader(path,
                StandardCharsets.UTF_8)) {
            CoffeeReviewDetail[] coffeelist = gson.fromJson(reader, CoffeeReviewDetail[].class);
            return coffeelist;
        } catch (Exception e) {

        }
        return null;
    }

}
