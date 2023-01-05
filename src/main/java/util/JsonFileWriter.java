package util;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import model.CoffeeReviewDetail;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonFileWriter {

    public static void appendToFile(Gson gson, List<?> entityList, String filePathToAppend) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePathToAppend);
             OutputStreamWriter isr = new OutputStreamWriter(fos,
                     StandardCharsets.UTF_8)) {
            gson.toJson(entityList, isr);
        }
    }

    public static String convertToJsonObject(Gson gson, Object entity) {
        var json = gson.toJson(entity, CoffeeReviewDetail.class);;
        return json;
    }

    public static void appendAtEnd(String json, String coffeeReviewDetails) throws FileNotFoundException {
        try (final RandomAccessFile randomAccessFile = new RandomAccessFile(coffeeReviewDetails, "rw");
             final Writer writer = JsonAppender.appendAtEnd(randomAccessFile) ) {
            CharStreams.copy(new StringReader(json), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
