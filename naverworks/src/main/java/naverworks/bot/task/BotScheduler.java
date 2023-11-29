package naverworks.bot.task;

import egovframework.naverworks.bot.service.BotMessageService;
import egovframework.naverworks.common.service.JWTService;
import egovframework.naverworks.common.vo.NaverWorksAppInfoVO;
import egovframework.naverworks.common.vo.ResponseDataVO;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BotScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BotScheduler.class);

    @Autowired
    private NaverWorksAppInfoVO naverWorksAppInfo;

    @Resource(name = "JWTService")
    private JWTService jwtService;

    @Resource(name = "BotMessageService")
    private BotMessageService botMessageService;

    private String accessToken = "";

    /* 생일 알림 메세지 전송 - 매일 오전 7시 */
    @Scheduled(cron = "00 00 07 1/1 * MON-FRI")
    public JSONObject sendBirthdayBot() {
        logger.debug("NAVER WORKS Bot [BotScheduler.sendBirthdayBot()] started");

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

        logger.debug("NAVER WORKS Bot [BotScheduler.sendBirthdayBot()] ended");

        return result;
    }

}
