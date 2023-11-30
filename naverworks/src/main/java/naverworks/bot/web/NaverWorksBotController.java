package naverworks.bot.web;

import naverworks.bot.service.BotMessageService;
import naverworks.bot.vo.BoardRequestDataVO;
import naverworks.common.service.JWTService;
import naverworks.bot.vo.ApprovalRequestDataVO;
import naverworks.common.vo.NaverWorksAppInfoVO;
import naverworks.common.vo.ResponseDataVO;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequestMapping("/naverworksbots")
@RestController
public class NaverWorksBotController {

    private static final Logger logger = LoggerFactory.getLogger(NaverWorksBotController.class);

    @Autowired
    private NaverWorksAppInfoVO naverWorksAppInfo;

    @Resource(name = "JWTService")
    private JWTService jwtService;

    @Resource(name = "BotMessageService")
    private BotMessageService botMessageService;

    private String accessToken = "";

    /* 전자결재 알림 */
    @PostMapping(value = "/approval", produces = "application/json;charset=utf-8")
    public JSONObject sendApprovalBot(@RequestBody ApprovalRequestDataVO requestData) {
        logger.debug("NAVER WORKS Bot [POST /naverworksbots/approval] started");

        // 요청받은 RequestBody의 데이터 중 선택적 값 처리 = 간편결재 url (easyApproval)
        Optional<String> easyApprovalOptional = Optional.ofNullable(requestData.getEasyApproval());
        String easyApproval = easyApprovalOptional.orElse("none");
        requestData.setEasyApproval(easyApproval);

        JSONObject result = new JSONObject();
        String botId = naverWorksAppInfo.getApprovalBotId();

        try {
            accessToken = jwtService.getToken();
            ResponseDataVO responseData = botMessageService.callApprovalBot(accessToken, botId, requestData);
            result.put("status", responseData.getData());
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        logger.debug("NAVER WORKS Bot [POST /naverworksbots/approval] ended");

        return result;
    }

    /* 게시판 알림 */
    @PostMapping(value = "/board", produces = "application/json;charset=utf-8")
    public JSONObject sendBoardBot(@RequestBody BoardRequestDataVO requestData) {
        logger.debug("NAVER WORKS Bot [POST /naverworksbots/board] started");

        // 요청받은 RequestBody의 데이터 중 선택적 값 처리 = 알림 메시지를 받을 구성원의 cn
        Optional<List<String>> cnOptional = Optional.ofNullable(requestData.getCn());
        List<String> cn = cnOptional.orElse(Arrays.asList());
        requestData.setCn(cn);

        JSONObject result = new JSONObject();
        String botId = naverWorksAppInfo.getBoardBotId();

        try {
            accessToken = jwtService.getToken();
            ResponseDataVO responseData = botMessageService.callBoardBot(accessToken, botId, requestData);
            result.put("status", responseData.getData());
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        logger.debug("NAVER WORKS Bot [POST /naverworksbots/board] ended");

        return result;
    }

    /* 생일 알림 */
    @PostMapping(value = "/birthday", produces = "application/json;charset=utf-8")
    public JSONObject sendBirthdayBot() {
        logger.debug("NAVER WORKS Bot [POST /naverworksbots/birthday] started");

        JSONObject result = new JSONObject();
        String botId = naverWorksAppInfo.getBirthdayBotId();

        try {
            accessToken = jwtService.getToken();
            ResponseDataVO responseData = botMessageService.callBirthdayBot(accessToken, botId);
            result.put("status", responseData.getData());
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        logger.debug("NAVER WORKS Bot [POST /naverworksbots/birthday] ended");

        return result;
    }

}
