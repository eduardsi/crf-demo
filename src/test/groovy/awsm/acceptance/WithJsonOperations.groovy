package awsm.acceptance

import groovy.json.JsonBuilder
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

trait WithJsonOperations {

    static MockHttpServletRequestBuilder jsonPost(String url, body) {
        post(url)
                .content(new JsonBuilder(body).toPrettyString())
                .contentType(MediaType.APPLICATION_JSON)
    }

}