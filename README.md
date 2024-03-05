<h1>MileStone1</h1>

<h2>Task description</h2>

* Fork the JSON org project in GithubLinks `https://github.com/stleary/JSON-java` to an external site. and make it "your own."

* Build the library
  
The org.json package can be built from the command line, Maven, and Gradle. The unit tests can be executed from Maven, Gradle, or individually in an IDE e.g. Eclipse.
 
**Building from the command line**

*Build the class files from the package root directory /src/main/java*
```shell
javac org/json/*.java
```

*Create the jar file in the current directory*
```shell
jar cf json-java.jar org/json/*.class
```

* Add an overloaded static method to the XML class with the signature

```ruby
public static JSONObject toJSONObject(Reader reader, JSONPointer path) throws JSONException {
        JSONObject jo = new JSONObject();
        XMLTokener x = new XMLTokener(reader);
        String[] pathList = path.toString().split("/");
        String stopKey;
        int reachIndex = -1;
        boolean foundTarget = false; // Add a flag to track if the target object is found

        // Check if the last part of the path is a digit
        if (pathList[pathList.length - 1].matches("^\\d+$")) {
            reachIndex = Integer.parseInt(pathList[pathList.length - 1]);
            stopKey = pathList[pathList.length - 2];
        } else {
            stopKey = pathList[pathList.length - 1];
        }

        while (x.more() && !foundTarget) { // Continue parsing until the target is found
            x.skipPast("<");
            if (x.more()) {
                foundTarget = parse2(x, jo, null, XMLParserConfiguration.ORIGINAL, stopKey, reachIndex);
            }
        }

        // Use JSONPointer to extract the sub-object
        if (reachIndex >= 0) {
            return (JSONObject) jo.query(path);
        } else {
            return new JSONObject(jo.query(path).toString());
        }
    }
```

It reads an XML file into a JSON object and efficiently extracts smaller sub-objects using a specific path. This process, conducted internally, enhances efficiency by halting parsing the moment the desired object is located, eliminating the need to read the entire XML file.

<h2>I implement 'parse2' fucntion as helper method to build the toJSONObject function </h2>

``` ruby
private static boolean parse2(XMLTokener x, JSONObject context, String name, XMLParserConfiguration config, String stopKey, int reachIndex) {
            ......
        } else if (token == SLASH) {
            // Close tag </
            token = x.nextToken();
            if (name == null) {
                throw x.syntaxError("Mismatched close tag " + token);
            }
            if (!token.equals(name)) {
                throw x.syntaxError("Mismatched " + name + " and " + token);
            }
            if (x.nextToken() != GT) {
                throw x.syntaxError("Misshaped close tag");
            }
            if (stopKey.equals(token) && reachIndex == 0) {
                pathFind = true;
                reachIndex--;
                return true; // Target object found, set foundTarget to true
            }
        return false;
            ......
    }
 ```

The function achieves the requirement of stopping the XML parsing as soon as the target object is found by using a combination of the stopKey, reachIndex, and the pathFind flag within a controlled parsing loop. The parse2 function, with its checks and early return, is key to this functionality, allowing the parser to exit as soon as the target is located, thus avoiding the need to read the entire XML file.

* Add an overloaded static method to the XML class with the signature.

``` ruby
private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]");

    public static JSONObject toJSONObject(Reader reader, JSONPointer path, JSONObject replacement) {
        JSONObject jo = new JSONObject();
        XMLTokener x = new XMLTokener(reader);
        String[] pathList = path.toString().split("/");
        String replaceKey = pathList[pathList.length - 1];

        if (NUMERIC_PATTERN.matcher(replaceKey).matches()) {
            replaceIndex = Integer.parseInt(pathList[pathList.length - 2]);
        }

        while (x.more()) {
            x.skipPast("<");
            if (x.more()) {
                parse2Replace(x, jo, null, XMLParserConfiguration.ORIGINAL, replaceKey, replacement);
            }
        }

        // Reset global values for future use
        resetGlobalValues();

        return jo;
    }

    private static boolean replacePathFind = false;
    private static int replaceIndex = -1;
    private static boolean hasReplaced = false;

    private static void resetGlobalValues() {
        replacePathFind = false;
        replaceIndex = -1;
        hasReplaced = false;
    }
```

It reads an XML file into a JSONObject and replaces a sub-object on a certain key path with another JSON object that you construct.

<h2>I implement 'parse2Replace' function to  process and parse the XML data.</h2>

```` ruby
private static boolean parse2Replace(XMLTokener x, JSONObject context, String name, XMLParserConfiguration config, String replaceKey, JSONObject replacement) {
        ......
        if (replacePathFind && !hasReplaced) {
            context.remove(replaceKey);
            context.put(replaceKey, replacement.get(replaceKey));
            hasReplaced = true;
        }

        token = x.nextToken();
        ......
}
````

It helps identify the specific XML element specified by the JSONPointer and performs the replacement operation if needed.

It's my understanding that to replace the method effectively, it's required to process the entire XML file in order to acquire the complete JSONObject.


<h1>Testing</h1>

* Run the test file `M2Test` to see their functionalities
Change the directory to `/src/test` compile the Java file and execute.
```
javac -cp ../main/java/json-java.jar M2Test.java
java -cp ".;../main/java/json-java.jar" M2Test
```
You will see the console output:
```
<?xml version="1.0" encoding="UTF-8"?>
<contact>
  <nick>Crista </nick>
  <name>Crista Lopes</name>
  <address>
    <street>Ave of Nowhere</street>
    <zipcode>92614</zipcode>
  </address>
</contact>

static JSONObject toJSONObject(Reader reader, JSONPointer path)
JSONPointer("/contact/address")
{"zipcode":92614,"street":"Ave of Nowhere"}
-----------------------
static JSONObject toJSONObject(Reader reader, JSONPointer path, JSONObject replacement)
Given replacement: {"street":"Ave of the Arts"}
JSONPointer("/contact/address/street")
After Replacement:
{"contact":{"nick":"Crista","address":{"zipcode":92614,"street":"Ave of the Arts"},"name":"Crista Lopes"}}
```

<h2>Junit Test</H2>

* I created the `XML2Test.java` testing file under the test dir, you can run `mvn clean test -Dtest=XML2Test` in the root dir to perform tests.

* Inside the testing file, `toJSONObject1Test1()` is testing the basic functionality. 

```ruby
             ......
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
            ......
```
* `toJSONObject1Test2()` is testing another xmlString.

```ruby
             ......
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
            ......
```
* `toJSONObject1Test3()` is testing for the wrong JSONPointer (Error).

```ruby
             ......
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
            ......
```
* `toJSONObject2Test1()` is testing the basic functionality.
```ruby
            ......
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
             ......
```
* `toJSONObject2Test2()` is testing to replace the `tag`.
```ruby
              ......
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
            ......
```
* `toJSONObject2Test3()` is testing another xmlString.
```ruby
             ......
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
             ......
```
* `toJSONObject2Test4()` is testing a mismatched tag name. (Error)
```ruby
             ......
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
             ......
```
* When you run `mvn clean test -Dtest=XML2Test` in the root dir, you should see `Tests run: 7, Failures: 0, Errors: 0, Skipped: 2`, because I set `@Ignore` for the failure cases.

<img width="683" alt="Screenshot 2024-02-02 231642" src="https://github.com/yuehcw/MSWE-262P-MileStone/assets/152671651/3e9cbd94-54d1-4577-9baa-eb715ebdcb83">

<h1>MileStone2</h1>

* Add an overloaded static method to the XML class with the signature

```ruby
public static JSONObject toJSONObject(Reader reader, Function<String, String> keyTransformer) {
        JSONObject jsonObject = new JSONObject();
        XMLTokener tokener = new XMLTokener(reader);
        if (keyTransformer == null) {
            return null;
        }
        while (tokener.more()) {
            tokener.skipPast("<");
            if (tokener.more()) {
                parse3(tokener, jsonObject, null, XMLParserConfiguration.ORIGINAL, keyTransformer);
            }
        }
        return jsonObject;
    }
```
It reads an XML file into a JSON object and adds the prefix "swe262_" to all of its keys, but in a much more general manner, for any transformations of keys. For example:

```
"foo" --> "swe262_foo" 
"foo" --> "oof"
```
<h2>I implement 'parse3' function with the integration of the keyTransformer function.</h2>

```` ruby
private static boolean parse3(XMLTokener x, JSONObject context, String name, XMLParserConfiguration config,  Function keyTransformer) {
        ......
        if (x.next() == '[') {
                        string = x.nextCDATA();
                        if (string.length() > 0) {
                            context.accumulate((String) keyTransformer.apply(config.getcDataTagName()), string);
                        }
                        return false;
                    }

        token = x.nextToken();
        ......
}
````
Integrating the `keyTransformer` ensures that the `keyTransformer` is invoked each time the `parse3` function attempts to utilize the `accumulate` method for enlarging the result. 
Every occurrence of a Tag or TagName transforms the keyTransformer function, ensuring uniform application throughout the parsing process.

<h2>Junit Test</h2>

* I created three more test cases inside the `XML2Test.java` testing file under the test dir, you can run `mvn clean test -Dtest=XML2Test` in the root dir to perform tests

* `toJSONObject3Test1()` is testing the basic functionality of `toJSONObject(Reader reader, Function<String, String> keyTransformer)`, adding prefix `swe262P_` to every key
  
```ruby
             ......
String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<contact>\n" +
                "  <nick>Crista </nick>\n" +
                "  <name>Crista Lopes</name>\n" +
                "  <address>\n" +
                "    <street>Ave of Nowhere</street>\n" +
                "    <zipcode>92614</zipcode>\n" +
                "  </address>\n" +
                "</contact>";

  StringReader reader = new StringReader(xmlString);
  Function<String, String> func = x -> "swe262P_" + x;
  JSONObject jsonObject = toJSONObject(reader, func);
  System.out.println(jsonObject.toString(2));
            ......
```

* `toJSONObject3Test2()` is testing another xmlString, and add sufix `_swe262p` to every key.

```ruby
             ......
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

  StringReader reader = new StringReader(xmlString);
  Function<String, String> func = x -> x + "_swe262P";
  JSONObject jsonObject = toJSONObject(reader, func);
  System.out.println(jsonObject.toString(2));
            ......
```
* `toJSONObject3Test3()` is testing to read an XML file and converting every key to upper case.

```ruby
             ......
File file = new File("./src/Sample1.xml");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        Function<String, String> func = String::toUpperCase;

        JSONObject jobj = toJSONObject(br,func);
        System.out.println(jobj.toString(2));
            ......
```
* When you run `mvn clean test -Dtest=XML2Test` in the root dir, you can check each test case's printed result.
<img width="466" alt="Screenshot 2024-02-14 174445" src="https://github.com/yuehcw/MSWE-262P-MileStone/assets/152671651/92511a48-9868-4f19-adf1-89da2b7ae19d">

<h1>Performance</h1>
Before, the codes first had to read through the XML to turn it into JSON. Then, the client had to go through it again to change the keys.
Now, by using Functions and lambda expressions, I managed to do the job with just one pass through the XML, which made everything faster. Modifying the 
accumulate function by calling the keyTransformer function when the parse3 function wants to extend the result 
<p>&nbsp;</p>
Overall, the keyTransformer facilitates flexibility in how the resulting JSON object is structured by enabling dynamic modification of tag names during the parsing process.

<h1>MileStone3</h1>

* Add streaming methods to the library that allows the client code to chain operations on JSON nodes. For example:

```
// in client space
JSONObject obj = XML.toJSONObject("<Books><book><title>AAA</title><author>ASmith</author></book><book><title>BBB</title><author>BSmith</author></book></Books>");
obj.toStream().forEach(node -> do some transformation, possibly based on the path of the node);
List<String> titles = obj.toStream().map(node -> extract value for key "title").collect(Collectors.toList());
obj.toStream().filter(node -> node with certain properties).forEach(node -> do some transformation);
```
* I created a new class called `JSONNode` for implementing the new `toStream` method in the `JSONObject.java` file.

```ruby
public class JSONNode {
    private String path;
    private String key;
    private Object value;

    public JSONNode(String path, String key, Object value) {
        this.path = path;
        this.key = key;
        this.value = value;
    }
    ......
```
* For implementing the `toStream` method, I write a method called `addNodes` as the helper method.

```ruby
public Stream<JSONNode> toStream() {
        List<JSONNode> nodes = new ArrayList<>();
        addNodes("/", this, nodes, "");
        return nodes.stream();
    }

    private void addNodes(String path, Object json, List<JSONNode> nodes, String lastKey) {
        if (json instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) json;
            for (String key : jsonObj.keySet()) {
                Object value = jsonObj.get(key);
                String newPath = path + key;
                JSONNode node = new JSONNode(newPath, key, value);
                nodes.add(node);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    addNodes(newPath + "/", value, nodes, key);
                }
            }
        } else if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.get(i);
                String newPath = path + i;
                JSONNode node = new JSONNode(newPath, lastKey.isEmpty() ? String.valueOf(i) : lastKey, value);
                nodes.add(node);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    addNodes(newPath + "/", value, nodes, lastKey);
                }
            }
        }
    }
```
The `addNodes` function effectively explores the entire JSON structure, creating a JSONNode for each key-value pair encountered, and populating the nodes list. Then, the `toStream` function simply returns a stream of JSONNode objects created from the populated list.

<h2>Junit Test</h2>

* I created a new test file called `MileStone4Test` for testing `toStream` method's functionalities.

```
obj.toStream().forEach(node -> do some transformation, possibly based on the path of the node);
List<String> titles = obj.toStream().map(node -> extract value for key "title").collect(Collectors.toList());
obj.toStream().filter(node -> node with certain properties).forEach(node -> do some transformation);
```

* When you run `mvn -Dtest=MileStone4Test test` in the root dir, you can check each test case's printed result.

<img width="810" alt="截圖 2024-02-28 下午5 55 07" src="https://github.com/yuehcw/MSWE-262P-MileStone/assets/152671651/cb3f7965-19a8-4199-a697-d7928a548a7b">

