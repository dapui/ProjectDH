package naverworks.common.vo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NaverWorksAppInfoVO {

    @Value("#{naverworks['naverworks.client']}")
    private String client;

    @Value("#{naverworks['naverworks.client_id']}")
    private String clientId;

    @Value("#{naverworks['naverworks.client_secret']}")
    private String clientSecret;

    @Value("#{naverworks['naverworks.service_account']}")
    private String serviceAccount;

    @Value("#{naverworks['naverworks.private_key']}")
    private String privateKey;

    @Value("#{naverworks['naverworks.oauth_scopes']}")
    private String oauthScopes;

    @Value("#{naverworks['naverworks.url_auth']}")
    private String urlAuth;

    @Value("#{naverworks['naverworks.url_api']}")
    private String urlApi;

    @Value("#{naverworks['naverworks.approval_bot_id']}")
    private String approvalBotId;

    @Value("#{naverworks['naverworks.birthday_bot_id']}")
    private String birthdayBotId;

    @Value("#{naverworks['naverworks.board_bot_id']}")
    private String boardBotId;

    public NaverWorksAppInfoVO() {}

    public NaverWorksAppInfoVO(String client, String clientId, String clientSecret, String serviceAccount, String privateKey, String oauthScopes, String urlAuth, String urlApi, String approvalBotId, String birthdayBotId, String boardBotId) {
        this.client = client;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.serviceAccount = serviceAccount;
        this.privateKey = privateKey;
        this.oauthScopes = oauthScopes;
        this.urlAuth = urlAuth;
        this.urlApi = urlApi;
        this.approvalBotId = approvalBotId;
        this.birthdayBotId = birthdayBotId;
        this.boardBotId = boardBotId;
    }

    public String getClient() {
        return client;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getServiceAccount() {
        return serviceAccount;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getOauthScopes() {
        return oauthScopes;
    }

    public String getUrlAuth() {
        return urlAuth;
    }

    public String getUrlApi() {
        return urlApi;
    }

    public String getApprovalBotId() {
        return approvalBotId;
    }

    public String getBirthdayBotId() {
        return birthdayBotId;
    }

    public String getBoardBotId() {
        return boardBotId;
    }
}
