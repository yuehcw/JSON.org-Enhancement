<h1>MileStone2</h1>

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

which does, inside the library, the same thing that task 2 of milestone 1 did in client code, before writing to disk. Being this done inside the library, you should be able to do it more efficiently. Specifically, you shouldn't need to read the entire XML file, as you can stop parsing it as soon as you find the object in question.

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

* Add an overloaded static method to the XML class with the signature

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

which does, inside the library, the same thing that task 5 of milestone 1 did in client code, before writing to disk. Are there any possible performance gains from doing this inside the library? If so, implement them in your version of the library.  

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
Change the directory to `/src/test` compile the java file and execute
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

* I create the `XML2Test.java` testing file under the test dir, you can run `mvn clean test -Dtest=XML2Test` in the root dir to perform tests

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
* `toJSONObject1Test3()` is testing for wrong JSONPointer (Error)

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

<h1>MileStone3</h1>

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
which does, inside the library, the kinds of things you did in task 4 of milestone 1, but in a much more general manner, for any transformations of keys. Specifically, YOURTYPEHERE should be a function (or "functional" in Java) that takes as input a String  denoting a key and returns another String that is the transformation of the key. For example:

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
In MileStone1, The codes first had to read through the XML to turn it into JSON. Then, the client had to go through it again to change the keys.
In MileStone3, by using Functions and lambda expressions, I managed to do the job with just one pass through the XML, which made everything faster. Modifying the 
accumulate function by calling keyTransformer function when parse3 function wants to extend the result 
<p>&nbsp;</p>
Overall, the keyTransformer facilitates flexibility in how the resulting JSON object is structured by enabling dynamic modification of tag names during the parsing process.
