# Snapshot testing with Java
_Avoid serial assertions when testing serializable objects._

## Simple usage
```java
@Test
public void test() {
    String s = "StringToTest";

    assertThat(s, matchesSnapshot());
}
```
On the first execution of this test, it will generate a snapshot file in resources folder. This file looks like this:
```json
"StringToTest"
```
This file **must be committed**.
Next executions of this test will read said file and compare with actual value.

Assertion will fail if serialized values are different.

We use [Jackson](https://github.com/FasterXML/jackson) to handle serialization/deserialization to JSON format.
This library therefore can handle any object that is serializable by Jackson.

## Real world example
Full example available [here](https://github.com/Zenika/java-snapshot-matcher/tree/master/snapshot-matcher-example).

I want to test a converter, which transforms a `Planet` into a `PlanetDTO`.
```java
@Component
public class PlanetConverter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    public PlanetDTO convertPlanet(Planet planet) {
        PlanetDTO dto = objectMapper.convertValue(planet, PlanetDTO.class);

        dto.films = planet.filmsUrls.stream()
                .map(filmUrl -> restTemplate.getForObject(filmUrl, FilmDTO.class))
                .collect(Collectors.toList());

        dto.residents = planet.residentsUrls.stream()
                .map(filmUrl -> restTemplate.getForObject(filmUrl, PeopleDTO.class))
                .collect(Collectors.toList());

        return dto;
    }
}
```

As it can be difficult to generate input object and to perform assertions on returned object, we use the snapshot matcher to handle assertions.

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class PlanetConverterTest {

    // RestTemplate needs to be mocked to ensure stability of snapshots.
    // Note that ObjectMapper is not mocked
    @Mock
    private RestTemplate restTemplate;

    @Autowired
    @InjectMocks
    private PlanetConverter converter;

    // Create input objects from JSON files
    private PeopleDTO lukeSkywalker = fromJson("luke-skywalker", PeopleDTO.class);
    private FilmDTO attackOfTheClones = fromJson("attack-of-the-clones", FilmDTO.class);
    private Planet tatooine = fromJson("tatooine", Planet.class);

    @Before
    public void setUp() {
        // Mock return values of RestTemplate
        when(restTemplate.getForObject("https://swapi.co/api/people/1/", PeopleDTO.class))
                .thenReturn(lukeSkywalker);
        when(restTemplate.getForObject("https://swapi.co/api/films/5/", FilmDTO.class))
                .thenReturn(attackOfTheClones);
    }

    @Test
    public void converterTest() {
        // Call method
        PlanetDTO planet = converter.convertPlanet(tatooine);

        // Do assertion
        assertThat(planet, matchesSnapshot());
    }
}
```

## Activate snapshot writing
Snapshots must be written at the same time as the tests.
It is necessary to avoid writing them in CI environments.

To activate snapshot writing, pass the VM option `-Dtest.snapshots.write`.

### With Maven
```bash
mvn -Dtest.snapshots.write test
```

### With Intellij
Set VM option inside test run configuration.

![](./writesnapshot-intellij.png)

## Limitations
- Only one snapshot assertion per test method
