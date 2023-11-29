package naverworks.common.vo;

import org.springframework.http.HttpHeaders;

public class ResponseDataVO {

    private int status;
    private String message;
    private HttpHeaders httpHeaders;
    private Object data;

    public ResponseDataVO() {}

    public ResponseDataVO(int status, String message, HttpHeaders httpHeaders, Object data) {
        this.status = status;
        this.message = message;
        this.httpHeaders = httpHeaders;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
