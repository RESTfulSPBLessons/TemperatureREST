package club;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Сервисный класс, содержащий основной функционал: обработку респонсов, отправку запросов, пушей, работу с токенами.
 */
public class RifRestTool {
    private static Logger logger = LoggerFactory.getLogger(RifRestTool.class);
    private static Logger consoleLogger = LoggerFactory.getLogger("console_logger");

    public enum SCAN_TYPE {PASSPORT_SCAN, SNILS_SCAN, INN_SCAN}

    @Autowired
    private Environment env;

    /**
     * username токена
     */
    @Value("${userNameForToken}")
    private String userNameForToken;

    /**
     * password токена
     */
    @Value("${passwordForToken}")
    private String passwordForToken;

    /**
     * url на получение токена
     */
    @Value("${urlForToken}")
    private String urlForToken;

    @Value("${operatorTalkBankUser}")
    private String operatorTalkBankUser;

    @Value("${operatorTalkBankpassword}")
    private String operatorTalkBankpassword;

    @Value("${grant_type}")
    private String grant_type;

    /**
     * url на отправку скана пасспорта
     */
    @Value("${urlForScan}")
    private String urlForScan;

    /**
     * url запроса на проверку инн
     */
    @Value("${urlForInn}")
    private String urlForInn;

    /**
     * url запроса на прохождение УПРИД
     */
    @Value("${urlForUPRID}")
    private String urlForUPRID;

    /**
     * url запроса на выпуск ВПК
     */
    @Value("${urlForVPK}")
    private String urlForVPK;

    @Value("${urlForPush}")
    private String urlForPush;

    @Value("${urlClientInfo}")
    private String url4ClientInfo;

    /**
     * Отладочная фенечка для рандомной генерации телефона/пасспорта
     */
    @Value("${randomTelephonPassport}")
    private boolean randomTelephonPassport;

    RestTemplate restTemplate;

    public RifRestTool(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Возвращает Jsonobject делая запрос на инн.
     * @param surname
     * @param name
     * @param patronymic
     * @param birthdate
     * @param series
     * @param number
     * @return - Jsonobject с данными ИНН
     */
    public JsonObject getINN(String surname, String name, String patronymic, LocalDate birthdate, String series, String number) {
        return getResponse(
                urlForInn,
                HttpMethod.GET,
                createHeaderWithToken(MediaType.APPLICATION_JSON),
                JSONTemplate.create()
                        .add("surname",surname)
                        .add("name",name)
                        .add("patronymic",patronymic)
                        .add("birthdate",birthdate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        .add("identity_doc_type",21)
                        .add("identity_doc_number",series+number)
                        .toString());
    }

    /**
     * Отправка запроса.
     *
     * @param url - куда шлем
     * @param method - тип метода (Гет/пост)
     * @param headers - заголовки
     * @param requestBody
     * @return
     */

    public JsonObject getResponse(String url, HttpMethod method, HttpHeaders headers, Object requestBody) {
        long time = System.currentTimeMillis();


        consoleLogger.info("METHOD:   " + method.name());
        consoleLogger.info("URL:      " + url);
        consoleLogger.info("HEADERS:  ");
        if (consoleLogger.isDebugEnabled())
            headers.keySet().stream().forEach(headeName -> consoleLogger.info(headeName + ": " + String.join(", ", headers.getValuesAsList(headeName))));
        consoleLogger.info("REQUEST:  " + requestBody);

        logger.info(JSONTemplate.create()
                .add("METHOD",method.name())
                .add("URL", url)
                .add("HEADERS", headers.keySet().stream().map(k -> k+":"+headers.get(k)).collect(Collectors.toList()).toArray(new String[0]))
                .add("REQUEST", requestBody == null ? "null" : requestBody.toString()).toString());


        HttpEntity httpEntity = new HttpEntity(requestBody, headers);

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, method, httpEntity, String.class); // отправляем запрос, ждем ответ.
        } catch (HttpClientErrorException ex) {
            responseEntity = new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
        }

        JsonObject responseJson = null;
        if (responseEntity != null && responseEntity.getBody() != null)
            responseJson = JSONTemplate.fromString(responseEntity.getBody());

        time = System.currentTimeMillis() - time;

        // логгируем ответ
        consoleLogger.info("RESPONSE: " + responseJson);
        consoleLogger.info("TIME:     " + (time / 1000) + "c " + (time % 1000) + "ms");
        consoleLogger.info("#################################");


        logger.info(JSONTemplate.create()
                .add("RESPONSE", responseJson == null ? "null" : responseJson.toString())
                .add("TIME", (time / 1000) + "c " + (time % 1000) + "ms").toString());

        if (responseEntity.getStatusCode() != HttpStatus.OK)
            throw new RifHttpClientErrorException(responseEntity.getBody(), responseEntity.getStatusCode());

        return responseJson;
    }

    static class RifHttpClientErrorException extends HttpClientErrorException {
        private String customMessage;
        public RifHttpClientErrorException(String customMessage, HttpStatus statusCode) {
            super(statusCode);
            this.customMessage = customMessage;
        }

        public String getCustomMessage() {
            return customMessage;
        }
    }

    /**
     * Отправляем Пуш рунету.
     *
     * @param json
     * @return
     */
    public JsonObject pushToRunet(JsonObject json) {
        return getResponse(urlForPush, HttpMethod.POST, createHeaderWithoutToken(MediaType.APPLICATION_JSON), json.toString());
    }

    /**
     * Проверка на УПРИД.
     *
     * @param phone
     * @param surname
     * @param name
     * @param patronymic
     * @param passport
     * @param inn
     * @param control_info
     * @param create_anonymous_account
     * @param used_inn_recognitions
     * @param passport_id
     * @return - JsonObject
     */
    public JsonObject getUPRID(String phone, String surname, String name, String patronymic, String passport,
                               String inn, String control_info, boolean create_anonymous_account,
                               String passport_id, String used_inn_recognitions) {
        if(Boolean.valueOf(env.getProperty("test")) && randomTelephonPassport) {
            Random random = new Random();
            phone = String.join("", Arrays.stream(new int[10]).mapToObj(i -> String.valueOf(random.nextInt(9))).collect(Collectors.toList()));
            passport = String.join("", Arrays.stream(new int[10]).mapToObj(i -> String.valueOf(random.nextInt(9))).collect(Collectors.toList()));
        }else {
            phone = phone.replaceAll("[^\\d]","");
            phone = phone.substring(phone.length()-10,phone.length());
        }

        return getResponse(urlForUPRID, HttpMethod.POST,
                createHeaderWithToken(MediaType.APPLICATION_JSON),
                JSONTemplate.create()
                        .add("phone", phone)
                        .add("surname", surname)
                        .add("name", name)
                        .add("patronymic", patronymic)
                        .add("passport", passport)
                        .add("inn", inn)
                        .add("control_info", control_info)
                        .add("create_anonymous_account", create_anonymous_account)
		                .add("used_recognitions", new String[]{passport_id}) // id распознования пасспорта
		                .add("used_inn_recognitions", new String[]{used_inn_recognitions}) // id распознования инн
                        .toString());
    }


    /**
     * Выпуск виртуальной карты.
     *
     * @param clientID
     * @return
     */
    public JsonObject getVPK(String clientID) {
        return getResponse((urlForVPK + clientID + "/virtual-cards"), HttpMethod.POST,
                createHeaderWithToken(MediaType.APPLICATION_JSON), null);
    }

    /**
     * Создаем заголовки без токена.
     *
     * @param type
     * @return
     */
    public HttpHeaders createHeaderWithoutToken(MediaType type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.setCacheControl("no-cache");
        return headers;
    }

    /**
     * Создаем заголовки с токеном.
     *
     * @param type
     * @return
     */
    public HttpHeaders createHeaderWithToken(MediaType type) {
        JsonObject token = getToken();
        HttpHeaders headers = createHeaderWithoutToken(type);
        headers.setBearerAuth(token.get("access_token").getAsString());
        return headers;
    }

    public JsonObject getScanData(MultipartFile file, SCAN_TYPE type) throws IOException {
        HttpHeaders headers = createHeaderWithToken(MediaType.MULTIPART_FORM_DATA);
        headers.setContentDisposition(
                ContentDisposition
                        .builder(file.getContentType())
                        .name(file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf(".")))
                        .filename(file.getOriginalFilename())
                        .build());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("name"    , file.getOriginalFilename());
        map.add("filename", file.getOriginalFilename());

        ByteArrayResource contentsAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename(){
                return file.getOriginalFilename();
            }
        };
        map.add("file", contentsAsResource);

        return getResponse(urlForScan+"/"+type, HttpMethod.POST, headers, map);
    }

    /**
     * Получение токена.
     * @return
     */
    public JsonObject getToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setCacheControl("no-cache");
        headers.setBasicAuth(userNameForToken,passwordForToken);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username"  ,operatorTalkBankUser);
        map.add("password"  ,operatorTalkBankpassword);
        map.add("grant_type",grant_type);

        return getResponse(urlForToken, HttpMethod.POST, headers, map);
    }

    public JsonObject getaccountApprovingResult(String clientID) {

        return getResponse(url4ClientInfo + clientID, HttpMethod.GET, createHeaderWithToken(MediaType.APPLICATION_JSON), null);
    }
}
