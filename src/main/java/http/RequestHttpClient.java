package http;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeaderElementIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHttpClient {

    private HttpGet request = new HttpGet("https://yandex.ru/maps/44/izhevsk");
    private static final String REGEX_CSRFTOKEN = "csrfToken\":\"[^\"]++";
    private static final String REGEX_COORDINATES = "\"coordinates\":\\[(\\d+.){4}";
    private CloseableHttpResponse response;
    private BufferedReader bufferedReader;
    private StringBuilder result;
    private Pattern pattern;
    private Matcher matcher;

    public String getCsrfToken() throws IOException {
        String csrfToken = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            response = client.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            result = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            pattern = Pattern.compile(REGEX_CSRFTOKEN);
            matcher = pattern.matcher(result);
            while (matcher.find()) {
                csrfToken = matcher.group();
            }
            csrfToken = csrfToken.substring(Objects.requireNonNull(csrfToken).lastIndexOf('\"') + 1);
            return csrfToken;
        }
    }

    public String getYandexuid() {
        HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));
        while (it.hasNext()) {
            HeaderElement elem = it.nextElement();
            if (elem.getName().equals("yandexuid")) {
                return elem.getValue();
            }
        }
        return null;
    }

    private URIBuilder generateCustomURI(String address) throws IOException {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("https");
        uriBuilder.setHost("yandex.ru");
        uriBuilder.setPath("/maps");
        uriBuilder.setParameter("text", address);
        uriBuilder.setParameter("lang", "ru_RU");
        uriBuilder.setParameter("csrfToken", getCsrfToken());
        return uriBuilder;
    }

    private String getRequest(String address) throws IOException {
        request = new HttpGet(String.valueOf(generateCustomURI(address)));
        request.setHeader("Cookie", "yandexuid=".concat(getYandexuid()));
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            response = client.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return String.valueOf(result);
        }
    }
    public String getCoordinates (String address) throws IOException {
        String coordinates = "";
        pattern = Pattern.compile(REGEX_COORDINATES);
        matcher = pattern.matcher(getRequest(address));
        while (matcher.find()) {
            coordinates = matcher.group();
        }
        return coordinates;
    }
}
