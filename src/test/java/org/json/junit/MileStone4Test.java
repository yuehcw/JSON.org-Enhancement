import org.json.JSONNode;
import org.json.JSONObject;
import org.json.XML;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;

public class MileStone4Test {
    @Test
    public void testMileStone4() {
        JSONObject obj = XML.toJSONObject("<Books><book><title>AAA</title><author>ASmith</author></book><book><title>BBB</title><author>BSmith</author></book></Books>");

        //obj.toStream().forEach(node -> do some transformation, possibly based on the path of the node);
        obj.toStream().forEach(node -> {
            String path = node.getPath();
            Object value = node.getValue();
            String transformedKey = node.getKey();
            String transformedValue = value.toString();

            if (path.contains("author")) {
                node.setKey(transformedKey.toUpperCase());
                node.setValue(transformedValue.toUpperCase());
                System.out.println(node.toString());
            }
        });
        System.out.println("------------------");

        //List<String> titles = obj.toStream().map(node -> extract value for key "title").collect(Collectors.toList());
        List<String> titles = obj.toStream().filter(node -> node.getPath().contains("title"))
                .map(node -> node.getValue().toString())
                .collect(Collectors.toList());
        assertEquals(titles.size(), 2);
        System.out.println(titles.get(0));
        System.out.println(titles.get(1));
        System.out.println("------------------");

        //obj.toStream().filter(node -> node with certain properties).forEach(node -> do some transformation);
        obj.toStream().filter(node -> node.getKey().equals("author"))
                .forEach(node -> {
                    System.out.println(node.getValue().toString().replace("Smith", "Kevin"));
                });
    }
}
