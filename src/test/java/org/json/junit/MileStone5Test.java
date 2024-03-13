import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class MileStone5Test {

    private JSONObject convertValuesToString(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                value = convertValuesToString((JSONObject) value);
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                JSONArray newArray = new JSONArray();
                for (int i = 0; i < array.length(); i++) {
                    Object element = array.get(i);
                    if (element instanceof JSONObject) {
                        newArray.put(convertValuesToString((JSONObject) element));
                    } else {
                        newArray.put(String.valueOf(element));
                    }
                }
                value = newArray;
            } else {
                value = String.valueOf(value);
            }
            result.put(key, value);
        }
        return result;
    }

    @Test
    public void testTransformJSONObjectKeyAsyn(){
        try {
            FileReader reader = new FileReader("src/test/java/org/json/junit/data/exampleXML.xml");
            Function<String, String> keyTransformer = (key) -> "swe262_" + key;
            Consumer<Exception> exceptionHandler = (e) -> e.printStackTrace();

            Future<JSONObject> futureActual = XML.toJSONObject(reader, keyTransformer, exceptionHandler);
            String expect = "{\n" +
                    "  \"swe262_library\": {\n" +
                    "    \"swe262_book\": [\n" +
                    "      {\n" +
                    "        \"swe262_title\": \"Programming Languages\",\n" +
                    "        \"swe262_author\": \"Robert W. Sebesta\",\n" +
                    "        \"swe262_isbn\": \"9780133943023\",\n" +
                    "        \"swe262_published\": \"2016\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"swe262_title\": \"Clean Code: A Handbook of Agile Software Craftsmanship\",\n" +
                    "        \"swe262_author\": \"Robert C. Martin\",\n" +
                    "        \"swe262_isbn\": \"9780132350884\",\n" +
                    "        \"swe262_published\": \"2008\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"swe262_title\": \"Design Patterns: Elements of Reusable Object-Oriented Software\",\n" +
                    "        \"swe262_author\": \"Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides\",\n" +
                    "        \"swe262_isbn\": \"9780201633610\",\n" +
                    "        \"swe262_published\": \"1994\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";

            // block the execution until the task is complete
            while(!futureActual.isDone()) {
                System.out.println("Calculating...");
                Thread.sleep(300);
            }

            JSONObject actual = futureActual.get();

            actual = convertValuesToString(actual);

            org.json.junit.Util.compareActualVsExpectedJsonObjects(actual, new JSONObject(expect));
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Fail to close file.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Something wrong while blockign futureActual.isDone().");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("error found when executing futureActual.get().");
            e.printStackTrace();
        }
    }


    @Test
    public void testTransformJSONObjectKeyAsynReturnType() {
        try {
            FileReader reader = new FileReader("src/test/java/org/json/junit/data/exampleXML.xml");
            Function<String, String> keyTransformer = (key) -> "swe262_" + key;
            Consumer<Exception> exceptionHandler = (e) -> e.printStackTrace();

            Future<JSONObject> futureActual = XML.toJSONObject(reader, keyTransformer, exceptionHandler);

            while (!futureActual.isDone()) {
                System.out.println("Calculating...");
                Thread.sleep(300);
            }

            JSONObject actual = futureActual.get();

            assertTrue(actual instanceof JSONObject);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTransformJSONObjectKeyAsynExceptionHandler() {
        try {
            FileReader reader = new FileReader("src/test/java/org/json/junit/data/exampleXML.xml");
            Consumer<Exception> exceptionHandler = (e) -> System.out.println("OMG ERROR!!");

            Future<JSONObject> futureActual = XML.toJSONObject(reader, null, exceptionHandler);
            assertNull(futureActual);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
