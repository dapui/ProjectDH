package naverworks.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import egovframework.naverworks.common.vo.ResponseDataVO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class WorksClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(WorksClientUtils.class);

    /**
     * get 방식으로 호출
     * @param accessToken
     * @param url
     * @return
     */
    public InputStream getByUrl(String accessToken, String url) {

        try {
            // 호출 URL 셋팅
            URI uri = new URI(url);
            URIBuilder ub = new URIBuilder(uri);

            // 컨텐츠 타입
            String contentType = "application/x-www-form-urlencoded";

            //전송 준비
            HttpClient httpClient = HttpClientBuilder.create().build();

            //호출 하기 위해 준비
            HttpGet http = new HttpGet(ub.toString());
            http.addHeader("Authorization", "Bearer " + accessToken);
            http.addHeader("Content-Type", contentType);
            logger.debug("### request => {}, header[Content-Type={}] , content={} ", http.toString(), contentType);

            //호출
            HttpResponse response;
            response = httpClient.execute(http);
            logger.debug("### response :: {} ", response);

            //200이면 성공
            logger.debug("### response StatusCode :: {} ", response.getStatusLine().getStatusCode());

            //200이 아닐 경우 리턴 데이타 받아옴
            HttpEntity entity = response.getEntity();

            logger.debug( "contenttype={} {}, contentlength={}", entity.getContentType().getName(), entity.getContentType().getValue(), entity.getContentLength() );

            if( entity.getContentType().getValue().equalsIgnoreCase("application/json;charset=UTF-8") ) logger.debug("{}", entity.getContent().toString());

            return entity.getContent();

        } catch (Exception e) {
            logger.error("Exception :: {}",  e.getMessage(), e);
            return null;
        }
    }

    /**
     * get 방식으로 호출
     * @param accessToken
     * @param uri
     * @return
     */
    public ResponseDataVO get(String accessToken, String uri) {
        return this.get(accessToken, uri, null);
    }

    /**
     * get 방식으로 호출
     * @param accessToken
     * @param uri
     * @param params
     * @return
     */
    public ResponseDataVO get(String accessToken, String uri, List<NameValuePair> params) {

        ResponseDataVO responseData = new ResponseDataVO();

        try {
            // 호출 URL 셋팅
            URIBuilder ub = new URIBuilder(uri);

            if (params != null) {
                ub.addParameters(params);
            }

            // 컨텐츠 타입
            String contentType = "application/x-www-form-urlencoded";

            // 전송 준비
            HttpClient httpClient = HttpClientBuilder.create().build();

            // 호출 하기 위해 준비
            HttpGet http = new HttpGet(ub.toString());
            http.addHeader("Authorization", "Bearer " + accessToken);
            http.addHeader("Content-Type", contentType);
            logger.debug("### request => {}, header[Content-Type={}] , content={} ", http.toString(), new ObjectMapper().writeValueAsString(http.getAllHeaders()), contentType);

            // 호출
            HttpResponse response = httpClient.execute(http);
            logger.debug("### response :: {} ", response);

            // 200이면 성공
            logger.debug("### response StatusCode :: {} ", response.getStatusLine().getStatusCode());

            // 200이 아닐 경우 리턴 데이타 받아옴
            HttpEntity entity = response.getEntity();

            logger.debug("### response content type :: {}, {} ", entity.getContentType().getName(), entity.getContentType().getValue());

            String[] contentTypes = {"application/json", "text/html"};
            if ( Arrays.asList(contentTypes).contains(entity.getContentType().getValue().toLowerCase()) ) { // 리턴 형태가 파일이 아닌 경우
                String responseStr = EntityUtils.toString(entity);
                logger.debug("### response responseStr :: {}", responseStr);

                if (StringUtils.isEmpty(responseStr)) {
                    responseStr = "{}";
                }

                responseData.setStatus(response.getStatusLine().getStatusCode());
                responseData.setMessage("");
                responseData.setData(new ObjectMapper().reader().readValue(responseStr, Object.class));
            } else { // 리턴 형태가 파일인 경우
                responseData.setStatus(response.getStatusLine().getStatusCode());
                responseData.setMessage("file");
                responseData.setData(entity.getContent());
            }

            return responseData;

        } catch (Exception e) {
            logger.error("Exception :: {}",  e.getMessage(), e);
            responseData.setStatus(500);
            responseData.setMessage(e.getMessage());
            responseData.setData(e);

            return responseData;
        }

    }

    /**
     * post 형태로 데이타 전송
     * @param accessToken
     * @param uri
     * @param data
     * @return
     */
    public ResponseDataVO post(String accessToken, String uri, Object data) {

        ResponseDataVO responseData = new ResponseDataVO();

        try {
            // 호출 URL 셋팅
            URIBuilder ub = new URIBuilder(uri);

            //전송 준비
            HttpClient httpClient = HttpClientBuilder.create().build();

            //호출 하기 위해 준비
            HttpPost http = new HttpPost(ub.toString());
            http.addHeader("Authorization", "Bearer " + accessToken);

            String contentType = ContentType.APPLICATION_JSON.toString();
            http.addHeader("Content-Type", contentType);
            http.addHeader("Accept", contentType);
            if ( data != null ) {
                http.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(data), "UTF-8"));
            }
            logger.debug("### request => {}, header[Content-Type={}, Authorization={}] , content={} ", http.toString(), contentType, "Bearer " + accessToken, data);

            //호출
            HttpResponse response = httpClient.execute(http);
            logger.debug("### response :: {} ", response);

            //200이면 성공
            logger.debug("### response StatusCode :: {} ", response.getStatusLine().getStatusCode());

            //200이 아닐 경우 리턴 데이타 받아옴
            HttpEntity entity = response.getEntity();
            String responseStr = EntityUtils.toString(entity);
            logger.debug("### response responseStr :: {}", responseStr);

            if (StringUtils.isEmpty(responseStr)) {
                responseStr = "{}";
            }

            responseData.setStatus(response.getStatusLine().getStatusCode());
            responseData.setMessage("");
            responseData.setData(new ObjectMapper().reader().readValue(responseStr, Object.class));

            return responseData;

        } catch (Exception e) {
            logger.error("Exception :: {}",  e.getMessage(), e);
            responseData.setStatus(500);
            responseData.setMessage(e.getMessage());
            responseData.setData(e);

            return responseData;
        }

    }

    /**
     * patch 형태로 데이타 전송
     * @param accessToken
     * @param uri
     * @param data
     * @return
     */
    public ResponseDataVO patch( String accessToken, String uri, Object data ) {

        ResponseDataVO responseData = new ResponseDataVO();

        try {
            // 호출 URL 셋팅
            URIBuilder ub = new URIBuilder(uri);

            // 컨텐츠 타입
            String contentType = "application/json";

            //전송 준비
            HttpClient httpClient = HttpClientBuilder.create().build();

            //호출 하기 위해 준비
            HttpPatch http = new HttpPatch(ub.toString());
            http.addHeader("Authorization", "Bearer " + accessToken);
            http.addHeader("Accept", contentType);
            http.addHeader("Content-Type", contentType);
            if( data != null ) http.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(data), "UTF-8"));
            logger.debug("### request => {}, header[Content-Type={}] , content={} ", http.toString(), contentType, data);

            //호출
            HttpResponse response = httpClient.execute(http);
            logger.debug("### response :: {} ", response);

            //200이면 성공
            logger.debug("### response StatusCode :: {} ", response.getStatusLine().getStatusCode());

            //200이 아닐 경우 리턴 데이타 받아옴
            HttpEntity entity = response.getEntity();
            String responseStr = EntityUtils.toString(entity);
            logger.debug("### response responseStr :: {}", responseStr);

            if (StringUtils.isEmpty(responseStr)) {
                responseStr = "{}";
            }

            responseData.setStatus(response.getStatusLine().getStatusCode());
            responseData.setMessage("");
            responseData.setData(new ObjectMapper().reader().readValue(responseStr, Object.class));

            return responseData;

        } catch (Exception e) {
            logger.error("Exception :: {}",  e.getMessage(), e);
            responseData.setStatus(500);
            responseData.setMessage(e.getMessage());
            responseData.setData(e);

            return responseData;
        }

    }

    /**
     * put 형태로 데이타 전송
     * @param accessToken
     * @param uri
     * @param data
     * @return
     */
    public ResponseDataVO put( String accessToken, String uri, Object data ) {

        ResponseDataVO responseData = new ResponseDataVO();

        try {

            // 호출 URL 셋팅
            URIBuilder ub = new URIBuilder(uri);

            // 컨텐츠 타입
            String contentType = "application/json";

            //전송 준비
            HttpClient httpClient = HttpClientBuilder.create().build();

            //호출 하기 위해 준비
            HttpPut http = new HttpPut(ub.toString());
            http.addHeader("Authorization", "Bearer " + accessToken);
            http.addHeader("Accept", contentType);
            http.addHeader("Content-Type", contentType);
            if( data != null ) http.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(data), "UTF-8"));
            logger.debug("### request => {}, header[Content-Type={}] , content={} ", http.toString(), contentType, data);

            //호출
            HttpResponse response = httpClient.execute(http);
            logger.debug("### response :: {} ", response);

            //200이면 성공
            logger.debug("### response StatusCode :: {} ", response.getStatusLine().getStatusCode());

            //200이 아닐 경우 리턴 데이타 받아옴
            HttpEntity entity = response.getEntity();
            String responseStr = EntityUtils.toString(entity);
            logger.debug("### response responseStr :: {}", responseStr);

            if (StringUtils.isEmpty(responseStr)) {
                responseStr = "{}";
            }

            responseData.setStatus(response.getStatusLine().getStatusCode());
            responseData.setMessage("");
            responseData.setData(new ObjectMapper().reader().readValue(responseStr, Object.class));

            return responseData;

        } catch (Exception e) {
            logger.error("Exception :: {}",  e.getMessage(), e);
            responseData.setStatus(500);
            responseData.setMessage(e.getMessage());
            responseData.setData(e);

            return responseData;
        }

    }

    /**
     * delete 방식으로 호출
     * @param accessToken
     * @param path
     * @return
     */
    public ResponseDataVO delete( String accessToken, String path ) {
        return this.delete(accessToken, path, null);
    }

    /**
     * delete 방식으로 호출
     * @param uri
     * @param params
     * @return
     */
    public ResponseDataVO delete( String accessToken, String uri, List<NameValuePair> params ) {

        ResponseDataVO responseData = new ResponseDataVO();

        try {

            // 호출 URL 셋팅
            URIBuilder ub = new URIBuilder(uri);
            if(params != null) ub.addParameters(params);

            // 컨텐츠 타입
            String contentType = "application/x-www-form-urlencoded";

            //전송 준비
            HttpClient httpClient = HttpClientBuilder.create().build();

            //호출 하기 위해 준비
            HttpDelete http = new HttpDelete(ub.toString());
            http.addHeader("Authorization", "Bearer " + accessToken);
            http.addHeader("Content-Type", contentType);
            logger.debug("### request => {}, header[Content-Type={}] , content={} ", http.toString(), contentType);

            //호출
            HttpResponse response;
            response = httpClient.execute(http);
            logger.debug("### response :: {} ", response);

            //200이면 성공
            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("### response StatusCode :: {} ", statusCode);


            if (statusCode == 204) {
                responseData.setStatus(response.getStatusLine().getStatusCode());
                responseData.setMessage("");
                responseData.setData(new ObjectMapper().reader().readValue("{}", Object.class));

                return responseData;
            }

            //200이 아닐 경우 리턴 데이타 받아옴
            HttpEntity entity = response.getEntity();

            String responseStr = EntityUtils.toString(entity);
            logger.debug("### response responseStr :: {}", responseStr);

            if( StringUtils.isEmpty(responseStr) ) responseStr = "{}";

            responseData.setStatus(response.getStatusLine().getStatusCode());
            responseData.setMessage("");
            responseData.setData(new ObjectMapper().reader().readValue(responseStr, Object.class));

            return responseData;

        } catch (Exception e) {
            logger.error("Exception :: {}",  e.getMessage(), e);
            responseData.setStatus(500);
            responseData.setMessage(e.getMessage());
            responseData.setData(e);

            return responseData;
        }

    }

    /**
     * Post 전송 - FormUrlencoded 형
     * @param targetUrl
     * @param headers
     * @return
     */
    public String getByFormUrlencoded(String targetUrl, List<NameValuePair> headers) {

        String content = "";;

        try {
            //전송 준비Ø
            HttpClient httpClient = HttpClientBuilder.create().build();

            //get로 호출 하기 위해 준비
            HttpGet http = new HttpGet(targetUrl);
            http.addHeader("Content-Type", "application/x-www-form-urlencoded");
            headers.stream().forEach(o->{
                http.addHeader(o.getName(), o.getValue());
            });
            logger.debug("### Request={}", http.toString());

            //호출
            HttpResponse response = httpClient.execute(http);
            logger.debug("### Response={}", response.toString());

            //리턴 데이타 받아옴
            HttpEntity entity = response.getEntity();
            content = EntityUtils.toString(entity);

        } catch (ParseException | IOException e) {
            logger.error("### Exception={}", e.getMessage(), e);

        } finally {
            logger.debug("### Content={}", content);

        }

        return content;

    }

    /**
     * Post 전송 - FormUrlencoded 형, accesstoken 발급 받을 때 사용함
     * @param params
     * @return
     */
    public String postByFormUrlencoded(String targetUrl, List<NameValuePair> params) throws Exception {

        String content = "";;

        try {
            //전송 준비Ø
            HttpClient httpClient = HttpClientBuilder.create().build();

            //post로 호출 하기 위해 준
            HttpPost http = new HttpPost(targetUrl);
            http.addHeader("Content-Type", "application/x-www-form-urlencoded");
            http.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            logger.debug("### Request={}, params={}", http.toString(), params);

            //호출
            HttpResponse response = httpClient.execute(http);
            logger.debug("### Response={}", response.toString());

            //리턴 데이타 받아옴
            HttpEntity entity = response.getEntity();
            content = EntityUtils.toString(entity);

        } catch (ParseException | IOException e) {
            logger.error("### Exception={}", e.getMessage(), e);
            throw new Exception(e.getMessage());

        } finally {
            logger.debug("### Content={}", content);
        }

        return content;
    }

    // 숫자가 2로 시작하는지 확인하는 메서드 - 응답의 상태코드가 200번대인지 확인할 때 사용
    public boolean startsWithTwo(int number) {
        // 숫자를 문자열로 변환하여 첫 번째 문자가 '2'인지 확인
        String strNumber = Integer.toString(number);
        return strNumber.startsWith("2");
    }

}
