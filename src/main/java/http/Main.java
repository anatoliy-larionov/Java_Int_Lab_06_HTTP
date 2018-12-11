package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(RequestHttpClient.class);
    private static final String ADDRESS_CUSTOM = "Можга Единства 23";

    public static void main(String[] args) throws IOException, URISyntaxException {
        RequestHttpClient requestHttpClient = new RequestHttpClient();
        logger.info("csrfToken = {}", requestHttpClient.getCsrfToken());
        logger.info("yandexuid = {}", requestHttpClient.getYandexuid());
        logger.info("coordinates = {}", requestHttpClient.getCoordinates(ADDRESS_CUSTOM));
    }
}
