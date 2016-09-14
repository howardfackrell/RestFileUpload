package com.hlf;

import com.google.gson.Gson;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class RestTemplateApp
{
    Gson gson = new Gson();
    public static void main( String ... args ) {

        System.out.println( "Begin:" );
        RestTemplateApp app = new RestTemplateApp();

        app.postWithStringProperties();
        app.postWithMapProperties();
        System.out.println( "Finished" );

    }

    public void doGetWithRequestInterceptor() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setInterceptors(new ArrayList() {{
            add(new BasicAuthorizationRequestInterceptor(Props.USERNAME, Props.PASSWORD));
        }});

        ResponseEntity result = restTemplate.getForEntity(
                Props.URL + "/folders/0b02073880467b31",
                String.class);
        System.out.println("Status: " + result.getStatusCode());
        System.out.println("Headers: " + result.getHeaders());
        System.out.println("Body: " + result.getBody());
    }

    public void doGetWithHeader() {
        BASE64Encoder encoder = new BASE64Encoder();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encoder.encode((Props.USERNAME + ":" + Props.PASSWORD).getBytes()));

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        Map<String, String> entity = new HashMap<>();
        ResponseEntity result = restTemplate.exchange(
                Props.URL + "/folders/0b02073880467b31",
                HttpMethod.GET,
                requestEntity,
                String.class);
        System.out.println("Status: " + result.getStatusCode());
        System.out.println("Headers: " + result.getHeaders());
        System.out.println("Body: " + result.getBody());
    }

    public void postWithStringProperties() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setInterceptors(new ArrayList() {{
            add(new BasicAuthorizationRequestInterceptor(Props.USERNAME, Props.PASSWORD));
//            add(new LoggingRequestInterceptor()); // for request loggin
        }});

        // object
        String properties =
                "{\n" +
                        "  \"properties\" : {\n" +
                        "    \"r_object_type\" : \"oct_client_logo\",\n"  +
                        "    \"a_content_type\" : \"tiff\",\n"  +
                        "    \"logo_type\" : \"CMYK\",\n"  +
                        "    \"object_name\" : \"logoSpringString\",\n"  +
                        "    \"client_number\" : \"0000110566\",\n"  +
                        "    \"program_id\" : [ \"88\" ],\n"  +
                        "    \"locale\" : [ \"en_US\", \"fr_CA\" ]\n"  +
                        "  }\n" +
                        "}";
        HttpHeaders objectHeaders = new HttpHeaders();
        objectHeaders.add("Content-Type", "application/vnd.emc.documentum+json");
        HttpEntity<String> object = new HttpEntity<>(properties, objectHeaders);


        // content
        HttpHeaders contentHeaders = new HttpHeaders();
        contentHeaders.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        File file = new File("./src/main/resources/logo.tif");
        HttpEntity<Resource> content = new HttpEntity<>((Resource) new FileSystemResource(file), contentHeaders);

        // request entity
        HttpHeaders entityHeaders = new HttpHeaders();
        entityHeaders.add("Content-Type", "multipart/form-data");
        LinkedMultiValueMap<String, Object> requestMap = new LinkedMultiValueMap();
        requestMap.add("object", object);
        requestMap.add("content", content);

        HttpEntity<LinkedMultiValueMap> requestEntity = new HttpEntity<LinkedMultiValueMap>(requestMap, entityHeaders);

        ResponseEntity response = restTemplate.exchange(Props.URL + "/folders/0b02073880467b31/documents?format=tiff",
                HttpMethod.POST,
                requestEntity,
                String.class);
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Headers: " + response.getHeaders());
        System.out.println("Body: " + response.getBody());
    }

    public void postWithMapProperties() {
        RestTemplate restTemplate = new RestTemplate();

        //this converter is needed in order for sprint to convert the properties Map to JSON
        FormHttpMessageConverter converter = new FormHttpMessageConverter();
        converter.addPartConverter(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(converter);

        restTemplate.setInterceptors(new ArrayList() {{
            add(new BasicAuthorizationRequestInterceptor(Props.USERNAME, Props.PASSWORD));
//            add(new LoggingRequestInterceptor());  // for request loggin
        }});

        // object
        Map<String, Object> properties = logo_properties("logoSpringMap", "0000110566", list("88"), list("en_US", "fr_CA"));
        HttpHeaders objectHeaders = new HttpHeaders();
        objectHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Map<String, Object>> object = new HttpEntity<>(properties, objectHeaders);

        // content
        HttpHeaders contentHeaders = new HttpHeaders();
        contentHeaders.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        File file = new File("./src/main/resources/logo.tif");
        HttpEntity<Resource> content = new HttpEntity<>((Resource) new FileSystemResource(file), contentHeaders);

        // request entity
        HttpHeaders entityHeaders = new HttpHeaders();
        entityHeaders.add("Content-Type", "multipart/form-data");

        LinkedMultiValueMap<String, Object> requestMap = new LinkedMultiValueMap();
        requestMap.add("object", object);
        requestMap.add("content", content);

        HttpEntity<LinkedMultiValueMap> requestEntity = new HttpEntity<LinkedMultiValueMap>(requestMap, entityHeaders);

        ResponseEntity response = restTemplate.exchange(Props.URL + "/folders/0b02073880467b31/documents?format=tiff",
                HttpMethod.POST,
                requestEntity,
                String.class);
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Headers: " + response.getHeaders());
        System.out.println("Body: " + response.getBody());

    }

    private <T> List<T>  list(T ... ts) {
        List<T> list = new ArrayList<>();
        for (T t : ts) {
            list.add(t);
        }
        return list;
    }


    Map<String, Object> logo_properties(String name, String clientId, List<String>  programIds, List<String> locales) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("r_object_type", "oct_client_logo");
        properties.put("a_content_type", "tiff");
        properties.put("logo_type", "CMYK");
        properties.put("object_name", name);
        properties.put("client_number", clientId);
        properties.put("program_id", programIds);
        properties.put("locale", locales);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("properties", properties);
        return metadata;
    }
}

class BasicAuthorizationRequestInterceptor implements ClientHttpRequestInterceptor {
    BASE64Encoder encoder = new BASE64Encoder();
    final String username;
    final String password;
    final String auth;
    public BasicAuthorizationRequestInterceptor(String username, String password) {
        this.username = username;
        this.password = password;
        auth = encoder.encode((username + ":" + password).getBytes());
    }
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        httpRequest.getHeaders().add("Authorization", "Basic " + auth);
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}


class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        ClientHttpResponse response = execution.execute(request, body);

        log(request,body,response);

        return response;
    }

    private void log(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        System.out.println("VERBOSE: " + new String(body));
    }
}

