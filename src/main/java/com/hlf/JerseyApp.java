package com.hlf;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import org.springframework.http.MediaType;
import sun.misc.BASE64Encoder;

import java.io.File;

/**
 * Created by howard.fackrell on 9/13/16.
 */
public class JerseyApp {

    BASE64Encoder encoder = new BASE64Encoder();
    String auth = encoder.encode((Props.USERNAME + ":" + Props.PASSWORD).getBytes());

    public static void main(String... args) {
        JerseyApp jerseyApp = new JerseyApp();

        jerseyApp.doContentPost();
    }

    public void doGetForFolder() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource resource = client.resource(Props.URL + "/folders/0b02073880467b31/documents");
        String response = resource
                .header("Authorization", "Basic " + auth)
                .get(String.class);

        System.out.println(response);
    }

    public void doGetForFile() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource resource = client.resource(Props.URL + "/objects/0902073880467f56");
        String response = resource
                .header("Authorization", "Basic " + auth)
                .get(String.class);

        System.out.println(response);
    }

    public void doNoContentPost() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource resource = client.resource(Props.URL + "/folders/0b02073880467b31/documents");

        ClientResponse response = resource
                .header("Authorization", "Basic " + auth)
                .header("content-type", "application/vnd.emc.documentum+json")
                .entity("{ \"properties\" : { \"object_name\" : \"howard.txt\", \"r_object_type\" : \"dm_document\", \"title\" : \"abcdefg\"  } }")
                .post(ClientResponse.class);


        System.out.println(response.getStatus());
        System.out.println(response.getEntity(String.class));
    }


    public void doContentPost() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource resource = client.resource(Props.URL + "/folders/0b02073880467b31/documents?format=tiff");

        String object =
                "{\n" +
                "  \"properties\" : {\n" +
                "    \"r_object_type\" : \"oct_client_logo\",\n"  +
                "    \"a_content_type\" : \"tiff\",\n"  +
                "    \"logo_type\" : \"CMYK\",\n"  +
                "    \"object_name\" : \"logoJersey\",\n"  +
                "    \"client_number\" : \"0000110566\",\n"  +
                "    \"program_id\" : [ \"88\" ],\n"  +
                "    \"locale\" : [ \"en_US\", \"fr_CA\" ]\n"  +
                "  }\n" +
                "}";

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("content",
                new File("./src/main/resources/logo.tif"));

        MultiPart multipartEntity = new FormDataMultiPart()
                .field("object", object)
                .bodyPart(fileDataBodyPart);


        ClientResponse response = resource
                .header("Authorization", "Basic " + auth)
                .header("Accept", "application/vnd.emc.documentum+json")
                .header("Content-Type", "multipart/form-data")
                .post(ClientResponse.class, multipartEntity);


        System.out.println(response.getStatus());
        System.out.println(response.getEntity(String.class));
    }

}
