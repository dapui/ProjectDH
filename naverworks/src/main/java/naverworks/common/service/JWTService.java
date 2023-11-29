package naverworks.common.service;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Map;

public interface JWTService {

    /* 현재 발급된 토큰값 리턴 */
    public String getToken() throws Exception;
    /* 서비스 계정으로 인증(JWT) - 토큰 발급 */
    public String getServerToken() throws Exception;
    /* v2 서비스계정을 통한 토큰 발급 */
    public Map<String, Object> getServerToken(String serverPrivateKey, String clientId, String clientSecret, String serviceAccount, String scope) throws Exception;
    /* Private Key 값 파싱 */
    public RSAPrivateKey getPrivateKeyFromString(String key) throws GeneralSecurityException;

}
