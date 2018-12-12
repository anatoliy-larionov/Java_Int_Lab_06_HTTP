package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(RequestHttpClient.class);
    private static final String ADDRESS_CUSTOM = "Москва";

    public static void main(String[] args) throws IOException {
        RequestHttpClient requestHttpClient = new RequestHttpClient();
        logger.info(requestHttpClient.getCsrfToken());
        logger.info("yandexuid = {}", requestHttpClient.getYandexuid());
        logger.info(requestHttpClient.getCoordinates(ADDRESS_CUSTOM));
    }
}
