package oersi;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
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
                    "post", "{\"criterias\":[],\"facettes\":[]}", "nodes", "skipCount", false, 10 },
            { "/test/path", //
                    "post", "{\"from\":0}", "hits.hits", "from", true, 10 },
            { "/test/path?page=1", //
                    "get", null, "data", "page", false, 1 } };

    private static final int REQUEST_LIMIT = 20;
    private static final int EXISTING_RECORDS = 15;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> siteMaps() {
        return Arrays.asList(PARAMS);
    }

    private String url;
    private String method;
    private String body;
    private String recordPath;
    private String pageParam;
    private boolean pageInBody;
    private int stepSize;
    private JsonApiReader reader;
    private InOrder inOrder;

    public TestJsonApiReader(String url, String method, String body, String recordPath,
            String pageParam, boolean pageInBody, int stepSize) {
        this.url = url;
        this.method = method;
        this.body = body;
        this.recordPath = recordPath;
        this.pageParam = pageParam;
        this.pageInBody = pageInBody;
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
        reader.setTotalLimit(REQUEST_LIMIT);
        reader.setWait(0);
        reader.setMethod(method);
        reader.setBody(body);
        reader.setRecordPath(recordPath);
        reader.setPageParam(pageParam);
        reader.setPageInBody(pageInBody);
        reader.setStepSize(stepSize);
        reader.setHeader("x:123\ny:456");
        reader.setReceiver(receiver);
    }

    private void initMocks() {
        MockitoAnnotations.initMocks(this);
        mockGetWithPagingInParam();
        mockPostWithPagingInParam();
        mockPostWithPagingInBody();
        inOrder = Mockito.inOrder(receiver);
    }

    private void mockGetWithPagingInParam() {
        stubFor(WireMock.request("GET", WireMock.urlEqualTo("/test/path?page=1"))
                .willReturn(WireMock.ok().withBody("{\"data\":[{},{},{},{},{},{},{},{},{},{}]}")));
        stubFor(WireMock.request("GET", WireMock.urlEqualTo("/test/path?page=2"))
                .willReturn(WireMock.ok().withBody("{\"data\":[{},{},{},{},{}]}")));
        stubFor(WireMock.request("GET", WireMock.urlEqualTo("/test/path?page=3"))
                .willReturn(WireMock.ok().withBody("{\"data\":[]}")));
    }

    private void mockPostWithPagingInParam() {
        stubFor(WireMock.request("POST", WireMock.urlEqualTo("/test/path?skipCount=0&maxItems=10"))
                .willReturn(WireMock.ok().withBody("{\"nodes\":[{},{},{},{},{},{},{},{},{},{}]}")));
        stubFor(WireMock.request("POST", WireMock.urlEqualTo("/test/path?skipCount=10&maxItems=10"))
                .willReturn(WireMock.ok().withBody("{\"nodes\":[{},{},{},{},{}]}")));
        stubFor(WireMock.request("POST", WireMock.urlEqualTo("/test/path?skipCount=20&maxItems=10"))
                .willReturn(WireMock.ok().withBody("{\"nodes\":[]}")));
    }

    private void mockPostWithPagingInBody() {
        stubFor(WireMock.request("POST", WireMock.urlEqualTo("/test/path"))
                .withRequestBody(equalToJson("{\"from\": 0}")).willReturn(WireMock.ok()
                        .withBody("{\"hits\":{\"hits\":[{},{},{},{},{},{},{},{},{},{}]}}")));
        stubFor(WireMock.request("POST", WireMock.urlEqualTo("/test/path"))
                .withRequestBody(equalToJson("{\"from\": 10}"))
                .willReturn(WireMock.ok().withBody("{\"hits\":{\"hits\":[{},{},{},{},{}]}}")));
        stubFor(WireMock.request("POST", WireMock.urlEqualTo("/test/path"))
                .withRequestBody(equalToJson("{\"from\": 20}"))
                .willReturn(WireMock.ok().withBody("{\"hits\":{\"hits\":[]}}")));
    }

    @Test
    public void testShouldProcess() {
        reader.process(wireMockRule.baseUrl() + url);
        inOrder.verify(receiver, Mockito.calls(EXISTING_RECORDS)).process(captor.capture());
        assertNotNull(new JSONObject(captor.getValue()));
        inOrder.verifyNoMoreInteractions();
    }

}
