package oersi;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.io.HttpOpener;
import org.metafacture.io.HttpOpener.Method;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Tests for {@link JsonApiReader}.
 *
 * @author Fabian Steeg
 *
 */
@RunWith(Parameterized.class)
public final class TestJsonApiReader {

    private static final Object[][] PARAMS = new Object[][] { //
            { "/test/path?skipCount=0&maxItems=10", //
                    Method.POST, "{\"criterias\":[],\"facettes\":[]}", "nodes", "skipCount=", 10 },
            { "/test/path?page=1", //
                    Method.GET, null, "data", "page=", 1 } };

    private static final int REQUEST_LIMIT = 20;
    private static final int EXISTING_RECORDS = 15;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String url;
    private Method method;
    private String body;
    private String recordPath;
    private String pageParam;
    private int stepSize;
    private JsonApiReader reader;
    private InOrder inOrder;

    public TestJsonApiReader(String url, HttpOpener.Method method, String body, String recordPath,
            String pageParam, int stepSize) {
        this.url = url;
        this.method = method;
        this.body = body;
        this.recordPath = recordPath;
        this.pageParam = pageParam;
        this.stepSize = stepSize;
    }

    @Mock
    private ObjectReceiver<String> receiver;
    @Captor
    private ArgumentCaptor<String> captor;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig()
            .jettyAcceptors(Runtime.getRuntime().availableProcessors()).dynamicPort());

    @Before
    public void setup() {
        initMocks();
        initReader();
    }

    private void initReader() {
        reader = new JsonApiReader();
        reader.setLimit(REQUEST_LIMIT);
        reader.setWait(0);
        reader.setMethod(method);
        reader.setBody(body);
        reader.setRecordPath(recordPath);
        reader.setPageParam(pageParam);
        reader.setStepSize(stepSize);
        reader.setReceiver(receiver);
    }

    private void initMocks() {
        MockitoAnnotations.initMocks(this);
        // Mock POST API with 'skipCount=' query param and results in 'nodes':
        stubFor(WireMock.request("POST", WireMock.urlEqualTo("/test/path?skipCount=0&maxItems=10"))
                .willReturn(WireMock.ok().withBody("{\"nodes\":[{},{},{},{},{},{},{},{},{},{}]}")));
        stubFor(WireMock.request("POST", WireMock.urlEqualTo("/test/path?skipCount=10&maxItems=10"))
                .willReturn(WireMock.ok().withBody("{\"nodes\":[{},{},{},{},{}]}")));
        stubFor(WireMock.request("POST", WireMock.urlEqualTo("/test/path?skipCount=20&maxItems=10"))
                .willReturn(WireMock.ok().withBody("{\"nodes\":[]}")));
        // Mock GET API with 'page=' query param and results in 'data':
        stubFor(WireMock.request("GET", WireMock.urlEqualTo("/test/path?page=1"))
                .willReturn(WireMock.ok().withBody("{\"data\":[{},{},{},{},{},{},{},{},{},{}]}")));
        stubFor(WireMock.request("GET", WireMock.urlEqualTo("/test/path?page=2"))
                .willReturn(WireMock.ok().withBody("{\"data\":[{},{},{},{},{}]}")));
        stubFor(WireMock.request("GET", WireMock.urlEqualTo("/test/path?page=3"))
                .willReturn(WireMock.ok().withBody("{\"data\":[]}")));
        inOrder = Mockito.inOrder(receiver);
    }

    @Test
    public void testShouldProcess() {
        reader.process(wireMockRule.baseUrl() + url);
        inOrder.verify(receiver, Mockito.calls(EXISTING_RECORDS)).process(captor.capture());
        assertNotNull(new JSONObject(captor.getValue()));
    }

}
