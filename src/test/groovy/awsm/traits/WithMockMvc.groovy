package awsm.traits

import groovy.json.JsonBuilder
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

trait WithMockMvc {

    @Autowired
    private WebApplicationContext webApplicationContext

    private MockMvc mvc

    @Before
    def setupMockMvc() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build()
    }

    ResultActions perform(RequestBuilder requestBuilder) {
        mvc.perform(requestBuilder)
    }


    static MockHttpServletRequestBuilder jsonPost(String url, body) {
        post(url)
                .content(new JsonBuilder(body).toPrettyString())
                .contentType(MediaType.APPLICATION_JSON)
    }

}