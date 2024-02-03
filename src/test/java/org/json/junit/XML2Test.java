import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.XML;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import static com.sun.org.apache.xml.internal.serializer.Method.XML;
import static org.json.XML.toJSONObject;

public class XML2Test {
    @Test
    public void toJSONObject1Test1() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<contact>\n" +
                "  <nick>Crista </nick>\n" +
                "  <name>Crista Lopes</name>\n" +
                "  <address>\n" +
                "    <street>Ave of Nowhere</street>\n" +
                "    <zipcode>92614</zipcode>\n" +
                "  </address>\n" +
                "</contact>";

        Reader reader = new StringReader(xmlString);
        JSONPointer path = new JSONPointer("/contact/address");

        JSONObject jo = toJSONObject(reader, path);

        String expected = "{\"zipcode\":92614,\"street\":\"Ave of Nowhere\"}\n";
        JSONObject expected2 = new JSONObject(expected);

        assertEquals(expected2.toString(), jo.toString());
    }

    @Test
    public void toJSONObject1Test2() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<contact>\n" +
                "  <person>\n" +
                "    <nick>Crista</nick>\n" +
                "    <name>Crista Lopes</name>\n" +
                "    <address>\n" +
                "      <location>\n" +
                "        <street>Ave of Nowhere</street>\n" +
                "        <zipcode>92614</zipcode>\n" +
                "      </location>\n" +
                "    </address>\n" +
                "  </person>\n" +
                "</contact>";

        Reader reader = new StringReader(xmlString);
        JSONPointer path = new JSONPointer("/contact/person/address");

        JSONObject jo = toJSONObject(reader, path);

        String expected = "{\"location\":{\"zipcode\":92614,\"street\":\"Ave of Nowhere\"}}\n";
        JSONObject expected2 = new JSONObject(expected);

        assertEquals(expected2.toString(), jo.toString());
    }

    @Ignore
    @Test
    public void toJSONObject1Test3() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<contact>\n" +
                "  <nick>Crista </nick>\n" +
                "  <name>Crista Lopes</name>\n" +
                "  <address>\n" +
                "    <street>Ave of Nowhere</street>\n" +
                "    <zipcode>92614</zipcode>\n" +
                "  </address>\n" +
                "</contact>";

        Reader reader = new StringReader(xmlString);
        JSONPointer path = new JSONPointer("/contact/street");

        JSONObject jo = toJSONObject(reader, path);

        String expected = "{\"zipcode\":92614,\"street\":\"Ave of Nowhere\"}\n";
        JSONObject expected2 = new JSONObject(expected);

        assertEquals(expected2.toString(), jo.toString());
    }

    @Test
    public void toJSONObject2Test1() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<contact>\n" +
                "  <nick>Crista </nick>\n" +
                "  <name>Crista Lopes</name>\n" +
                "  <address>\n" +
                "    <street>Ave of Nowhere</street>\n" +
                "    <zipcode>92614</zipcode>\n" +
                "  </address>\n" +
                "</contact>";

        Reader reader = new StringReader(xmlString);
        JSONPointer path = new JSONPointer("/contact/address/street");
        JSONObject replacement = toJSONObject("<street>Ave of the Arts</street>\n");

        JSONObject jo = toJSONObject(reader, path, replacement);

        String expected = "{\"contact\":{\"nick\":\"Crista\",\"address\":{\"zipcode\":92614,\"street\":\"Ave of the Arts\"},\"name\":\"Crista Lopes\"}}\n";
        JSONObject expected2 = new JSONObject(expected);

        assertEquals(expected2.toString(), jo.toString());
    }

    @Test
    public void toJSONObject2Test2() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<contact>\n" +
                "  <nick>Crista </nick>\n" +
                "  <name>Crista Lopes</name>\n" +
                "  <address>\n" +
                "    <street>Ave of Nowhere</street>\n" +
                "    <zipcode>92614</zipcode>\n" +
                "  </address>\n" +
                "</contact>";

        Reader reader = new StringReader(xmlString);
        JSONPointer path = new JSONPointer("/contact/address/street");
        JSONObject replacement = toJSONObject("<avenue>Ave of the Arts</avenue>\n");

        JSONObject jo = toJSONObject(reader, path, replacement);

        String expected = "{\"contact\":{\"nick\":\"Crista\",\"address\":{\"zipcode\":92614,\"avenue\":\"Ave of the Arts\"},\"name\":\"Crista Lopes\"}}\n";
        JSONObject expected2 = new JSONObject(expected);

        assertEquals(expected2.toString(), jo.toString());
    }

    @Test
    public void toJSONObject2Test3() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<contact>\n" +
                "  <person>\n" +
                "    <nick>Crista</nick>\n" +
                "    <name>Crista Lopes</name>\n" +
                "    <address>\n" +
                "      <location>\n" +
                "        <street>Ave of Nowhere</street>\n" +
                "        <zipcode>92614</zipcode>\n" +
                "      </location>\n" +
                "    </address>\n" +
                "  </person>\n" +
                "</contact>";

        Reader reader = new StringReader(xmlString);
        JSONPointer path = new JSONPointer("/contact/person/nick");
        JSONObject replacement = toJSONObject("<nick>Professor</nick>\n");

        JSONObject jo = toJSONObject(reader, path, replacement);

        String expected = "{\"contact\":{\"person\":{\"nick\":\"Professor\",\"address\":{\"location\":{\"zipcode\":92614,\"street\":\"Ave of Nowhere\"}},\"name\":\"Crista Lopes\"}}}\n";
        JSONObject expected2 = new JSONObject(expected);

        assertEquals(expected2.toString(), jo.toString());
    }

    @Ignore
    @Test
    public void toJSONObject2Test4() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<contact>\n" +
                "  <nick>Crista </nick>\n" +
                "  <name>Crista Lopes</name>\n" +
                "  <address>\n" +
                "    <street>Ave of Nowhere</street>\n" +
                "    <zipcode>92614</zipcode>\n" +
                "  </address>\n" +
                "</contact>";

        Reader reader = new StringReader(xmlString);
        JSONPointer path = new JSONPointer("/contact/address/street");
        JSONObject replacement = toJSONObject("<tag>Ave of the Arts</street>\n");

        JSONObject jo = toJSONObject(reader, path, replacement);

        String expected = "{\"contact\":{\"nick\":\"Crista\",\"address\":{\"zipcode\":92614,\"street\":\"Ave of the Arts\"},\"name\":\"Crista Lopes\"}}\n";
        JSONObject expected2 = new JSONObject(expected);

        assertEquals(expected2.toString(), jo.toString());
    }
}
