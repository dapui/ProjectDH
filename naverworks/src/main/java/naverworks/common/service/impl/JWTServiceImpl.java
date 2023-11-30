package naverworks.common.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import naverworks.common.service.JWTService;
import naverworks.common.util.WorksClientUtils;
import naverworks.common.vo.NaverWorksAppInfoVO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service("JWTService")
public class JWTServiceImpl implements JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTServiceImpl.class);

    @Autowired
    private NaverWorksAppInfoVO naverWorksAppInfo;

    @Autowired
    private WorksClientUtils worksClient;

    private String accessToken = "";

    /* 현재 발급된 토큰값 리턴 */
    @Override
    public String getToken() throws Exception {

        try {
            if (StringUtils.isEmpty(accessToken) || accessToken.equals("") || accessToken == null) {
                accessToken = getServerToken();
            }

            return accessToken;
        } catch (Exception e) {
            logger.error("Exception :: {}", e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    /* 서비스 계정으로 인증(JWT) - 토큰 발급 */
    @Override
    public String getServerToken() throws Exception {

        String serverPrivateKey = naverWorksAppInfo.getPrivateKey();
        String clientId = naverWorksAppInfo.getClientId();
        String clientSecret = naverWorksAppInfo.getClientSecret();
        String serviceAccount = naverWorksAppInfo.getServiceAccount();
        String scope = naverWorksAppInfo.getOauthScopes();

        try {
            Map<String, Object> resultMap = getServerToken(serverPrivateKey, clientId, clientSecret, serviceAccount, scope);
            logger.debug("### resultMap => {}", resultMap.toString());
            String accessToken = (String) resultMap.get("access_token");
            logger.debug("### accessToken => {}", accessToken);

            if (accessToken == null) {
                throw new Exception(resultMap.toString());
            }

            return accessToken;

        } catch (Exception e) {
            logger.error("Exception :: {}", e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    /* v2 서비스계정을 통한 토큰 발급 */
    @Override
    public Map<String, Object> getServerToken(String serverPrivateKey, String clientId, String clientSecret, String serviceAccount, String scope) throws Exception {
        logger.debug("JWTServiceImpl.getServerToken() started");

        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");
        headers.put("typ", "JWT");
        Date iat = new Date();
        Date exp = DateUtils.addMinutes(new Date(), 30);
        String grantType = URLEncoder.encode("urn:ietf:params:oauth:grant-type:jwt-bearer", "UTF-8");

        // RSA
        RSAPublicKey publicKey = null;  // Get the key instance
        RSAPrivateKey privateKey = getPrivateKeyFromString(serverPrivateKey);
        Algorithm algorithmRS = Algorithm.RSA256(publicKey, privateKey);
        String assertion = JWT.create()
                .withHeader(headers)
                .withIssuer(clientId)
                .withSubject(serviceAccount)
                .withIssuedAt(iat)
                .withExpiresAt(exp)
                .sign(algorithmRS);

        // Parameter
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("assertion", assertion));
        params.add(new BasicNameValuePair("grant_type", grantType));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        params.add(new BasicNameValuePair("scope", scope));

        try {
            String content = worksClient.postByFormUrlencoded(naverWorksAppInfo.getUrlAuth() + "/oauth2/v2.0/token", params);
            Map<String, Object> resultMap = new ObjectMapper().readValue(content, HashMap.class);
            logger.debug("JWTServiceImpl.getServerToken() ended");

            return resultMap;
        } catch (Exception e) {
            logger.error("Exception :: {}", e.getMessage(), e);
            throw new Exception(e.getMessage());
        }

    }

    /* Private Key 값 파싱 */
    @Override
    public RSAPrivateKey getPrivateKeyFromString(String key) throws GeneralSecurityException {

        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);

        return privKey;
    }

}
