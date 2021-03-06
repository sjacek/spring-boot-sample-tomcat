/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.tomcat;

import org.apache.coyote.AbstractProtocol;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;

import static java.nio.charset.Charset.forName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.StreamUtils.copyToString;

/**
 * Basic integration tests for demo application.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class SampleTomcatApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ApplicationContext applicationContext;

    @Value("${local.server.port}")
    private int port;

    private String uri() {
        return "http://localhost:" + port;
    }
    @Test
	public void testHome() throws Exception {
		ResponseEntity<String> entity = restTemplate.getForEntity(uri(), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(OK);
		assertThat(entity.getBody()).isEqualTo("Hello World");
	}

	@Test
	public void testCompression() throws Exception {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Accept-Encoding", "gzip");
		HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
		ResponseEntity<byte[]> entity = restTemplate.exchange(uri(), GET, requestEntity, byte[].class);
		assertThat(entity.getStatusCode()).isEqualTo(OK);
		try (GZIPInputStream inflater = new GZIPInputStream(new ByteArrayInputStream(entity.getBody()))) {
			assertThat(copyToString(inflater, forName("UTF-8"))).isEqualTo("Hello World");
		}
	}

//	@Test
//	public void testTimeout() throws Exception {
//		ServletWebServerApplicationContext context = (ServletWebServerApplicationContext) applicationContext;
//		TomcatWebServer embeddedServletContainer = (TomcatWebServer) context.getWebServer();
//		ProtocolHandler protocolHandler = embeddedServletContainer.getTomcat().getConnector().getProtocolHandler();
//		int timeout = ((AbstractProtocol<?>) protocolHandler).getConnectionTimeout();
//		assertThat(timeout).isEqualTo(5000);
//	}

}
