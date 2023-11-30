package naverworks.bot.service.impl;

import naverworks.bot.service.BotMessageService;
import naverworks.bot.service.JsonTemplateService;
import naverworks.bot.vo.BirthdayUserInfoVO;
import naverworks.bot.vo.BoardRequestDataVO;
import naverworks.common.util.DateFormatUtils;
import naverworks.common.util.WorksClientUtils;
import naverworks.bot.vo.ApprovalRequestDataVO;
import naverworks.common.vo.NaverWorksAppInfoVO;
import naverworks.common.vo.ResponseDataVO;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service("BotMessageService")
public class BotMessageServiceImpl implements BotMessageService {

    private static final Logger logger = LoggerFactory.getLogger(BotMessageServiceImpl.class);

    @Autowired
    private NaverWorksAppInfoVO naverWorksAppInfo;

    @Autowired
    private WorksClientUtils worksClientUtils;

    @Autowired
    private DateFormatUtils dateFormatUtils;

    @Resource(name = "JsonTemplateService")
    private JsonTemplateService jsonTemplateService;

    private String accessToken = "";

    /* 전자결재 알림 봇 호출 */
    @Override
    public ResponseDataVO callApprovalBot(String token, String botId, ApprovalRequestDataVO approvalRequestData) throws Exception {
        logger.debug("BotMessageServiceImpl.callApprovalBot() started");

        // 같은 클래스에서 호출이 가능하도록 전역변수로 accessToken 값을 대입
        accessToken = token;
        ResponseDataVO responseData = null;
        // 메시지를 전송할 유저 리스트
        List<String> targetMessageUsers = approvalRequestData.getCn();
        // 메시지 전송 API 호출시 Request Body에 들어갈 데이터
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> content = new HashMap<String, Object>();
        Map<String, Object> message;

        // 간편결재 url 값 유무에 따라 메세지 템플릿 지정
        if (approvalRequestData.getEasyApproval().equals("none")) {
            message = jsonTemplateService.approvalWithoutEasyApprovalMessageTemplate(approvalRequestData);
        } else {
            message = jsonTemplateService.approvalMessageTemplate(approvalRequestData);
        }

        try {
            // 결재자에게 메시지 보내기
            for (String user : targetMessageUsers) {
                params.put("content", content);
                params.put("accountId", user);
                params.put("content", message);

                // 호출 URL
                String uri = naverWorksAppInfo.getUrlApi() + "/v1.0/bots/" + botId + "/users/" + user + "/messages";
                responseData = worksClientUtils.post(accessToken, uri, params);

                // 응답의 상태코드가 200번대인지 확인
                if (worksClientUtils.startsWithTwo(responseData.getStatus())) {
                    responseData.setData("ok");
                } else {
                    throw new Exception(responseData.getData().toString());
                }
            }

            logger.debug("BotMessageServiceImpl.callApprovalBot() ended");

            return responseData;
        } catch (Exception e) {
            logger.error("Exception :: {}",  e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    /* 생일 알림 봇 호출 */
    @Override
    public ResponseDataVO callBirthdayBot(String token, String botId) {
        logger.debug("BotMessageServiceImpl.callBirthdayBot() started");

        // 같은 클래스에서 호출이 가능하도록 전역변수로 accessToken 값을 대입
        accessToken = token;
        ResponseDataVO responseData = null;

        try {
            // 메시지를 전송할 전직원 리스트
            List<Map<String, Object>> userDataList = searchMembers();
            // 전직원의 userId 리스트
            List<String> targetMessageUsers = searchMembersUserId(userDataList);
            // 메시지 전송 API 호출시 Request Body에 들어갈 데이터
            Map<String, Object> params = new HashMap<String, Object>();
            Map<String, Object> content = new HashMap<String, Object>();
            Map<String, Object> message;

            // 오늘 생일인 직원 리스트
            List<Map<String, Object>> birthdayUserList = searchBirthdayUserList(userDataList);
            // 생일인 직원이 없을 경우
            if (birthdayUserList == null || birthdayUserList.isEmpty()) {
                responseData.setStatus(200);
                responseData.setData("There is no employee whose birthday is today");

                return responseData;
            }

            // 생일 직원 정보 알림 보내기
            for (Map<String, Object> birthdayUser : birthdayUserList) {
                String userId = (String) birthdayUser.get("userId");
                BirthdayUserInfoVO userInfo = searchUserInfo(userId);
                userInfo.setImage(searchUserPhoto(userId));

                // 전직원에게 알림 보내기
                for (String user : targetMessageUsers) {
                    message = jsonTemplateService.birthdayMessageTemplate(userInfo);
                    params.put("content", content);
                    params.put("accountId", user);
                    params.put("content", message);

                    // 호출 URL
                    String uri = naverWorksAppInfo.getUrlApi() + "/v1.0/bots/" + botId + "/users/" + user + "/messages";
                    responseData = worksClientUtils.post(accessToken, uri, params);

                    // 응답의 상태코드가 200번대인지 확인
                    if (worksClientUtils.startsWithTwo(responseData.getStatus())) {
                        responseData.setData("ok");
                    } else {
                        throw new Exception();
                    }
                }
            }

            logger.debug("BotMessageServiceImpl.callBirthdayBot() ended");

            return responseData;
        } catch (Exception e) {
            responseData.setData(e.getMessage());

            logger.debug("BotMessageServiceImpl.callBirthdayBot() ended");

            return responseData;
        }
    }

    /* 게시판 알림 봇 호출 */
    @Override
    public ResponseDataVO callBoardBot(String token, String botId, BoardRequestDataVO boardRequestData) throws Exception {
        logger.debug("BotMessageServiceImpl.callBoardBot() started");

        // 같은 클래스에서 호출이 가능하도록 전역변수로 accessToken 값을 대입
        accessToken = token;
        ResponseDataVO responseData = null;
        // 메시지를 전송할 유저 리스트
        List<String> targetMessageUsers;
        // boardRequestData의 cn 유무에 따라 메시지를 전송할 유저 리스트 지정 (유: 해당 직원 리스트 / 무: 전직원)
        if (boardRequestData.getCn().isEmpty() || boardRequestData.getCn() == null) {
            // 메시지를 전송할 전직원 리스트
            List<Map<String, Object>> userDataList = searchMembers();
            // 전직원의 userId 리스트
            targetMessageUsers = searchMembersUserId(userDataList);
        } else {
            targetMessageUsers = boardRequestData.getCn();
        }

        // 메시지 전송 API 호출시 Request Body에 들어갈 데이터
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> content = new HashMap<String, Object>();

        try {
            Map<String, Object> message = jsonTemplateService.boardMessageTemplate(boardRequestData);
            // 메시지 보내기
            for (String user : targetMessageUsers) {
                params.put("content", content);
                params.put("accountId", user);
                params.put("content", message);

                // 호출 URL
                String uri = naverWorksAppInfo.getUrlApi() + "/v1.0/bots/" + botId + "/users/" + user + "/messages";
                responseData = worksClientUtils.post(accessToken, uri, params);

                // 응답의 상태코드가 200번대인지 확인
                if (worksClientUtils.startsWithTwo(responseData.getStatus())) {
                    responseData.setData("ok");
                } else {
                    throw new Exception(responseData.getData().toString());
                }
            }

            logger.debug("BotMessageServiceImpl.callBoardBot() ended");

            return responseData;
        } catch (Exception e) {
            logger.error("Exception :: {}",  e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    /* NAVER WORKS API 호출하여 구성원의 프로필 정보 조회 */
    @Override
    public BirthdayUserInfoVO searchUserInfo(String userId) {
        logger.debug("BotMessageServiceImpl.searchUserInfo() started");

        BirthdayUserInfoVO userInfo = new BirthdayUserInfoVO();
        ResponseDataVO responseData = null;

        try {
            // 호출 URL
            String uri = naverWorksAppInfo.getUrlApi() + "/v1.0/users/" + userId;
            responseData = worksClientUtils.get(accessToken, uri);

            // API 응답 Data
            Map<String, Object> responseMap = (Map<String, Object>) responseData.getData();

            // 생일 정보
            String birthday = "";
            if (responseMap.get("birthday") != null || responseMap.get("birthday").equals("")) {
                birthday = (String) responseMap.get("birthday");
            }

            // 이름 정보
            Map<String, Object> userNameData = (Map<String, Object>) responseMap.get("userName");
            String userName = (String) userNameData.get("lastName") + userNameData.get("firstName");

            // 조직 정보
            List<Map<String, Object>> organizations = (List<Map<String, Object>>) responseMap.get("organizations");
            List<Map<String, Object>> orgUnit = new ArrayList<>();  // 부서 정보

            // organizations(List)의 각 group에서 특정 키값(orgUnits)으로 조회
            String orgUnitName = naverWorksAppInfo.getClient();     // 부서명이 없을 경우 회사명
            for (Map<String, Object> group : organizations) {
                if (group.containsKey("orgUnits")) {
                    orgUnit = (List<Map<String, Object>>) group.get("orgUnits");
                }
            }

            if (organizations != null || !organizations.isEmpty()) {
                // orgUnit(List)의 각 group에서 특정 키값(orgUnitName)으로 조회
                for (Map<String, Object> group : orgUnit) {
                    if (group.containsKey("orgUnitName")) {
                        orgUnitName = (String) group.get("orgUnitName");
                    }
                }
            }

            userInfo.setBirthday(birthday);
            userInfo.setDeptName(orgUnitName);
            userInfo.setUserName(userName);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseData.setData(e.getMessage());
        }

        logger.debug("BotMessageServiceImpl.searchUserInfo() ended");

        return userInfo;
    }

    /* NAVER WORKS API 호출하여 구성원 사진 조회 - 조직연동 후 추가작업 예정 */
    @Override
    public String searchUserPhoto(String userId) {
        logger.debug("BotMessageServiceImpl.searchUserPhoto() started");

//        String filename = "https://static.worksmobile.net/static/pwe/wm/common/img_profile2.png";   // default 이미지
//        ResponseDataVO responseData = null;
//
//        try {
//            // 호출 URL
//            String uri = naverWorksAppInfo.getUrlApi() + "/v1.0/users/" + userId + "/photo";
//            String redirectedUrl = worksClientUtils.getRedirectionUrl(accessToken, uri);
//
//            logger.debug("### redirectedUrl => {}", redirectedUrl);
//
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            responseData.setData(e.getMessage());
//        }
//
//        logger.debug("BotMessageServiceImpl.searchUserPhoto() ended");

        return "https://static.worksmobile.net/static/pwe/wm/common/img_profile2.png";
    }

    /* NAVER WORKS API 호출하여 구성원 목록 조회 */
    @Override
    public List<Map<String, Object>> searchMembers() {
        logger.debug("BotMessageServiceImpl.searchMembers() started");

        List<Map<String, Object>> userDataList = null;
        ResponseDataVO responseData = null;

        try {
            // 호출 URL
            String uri = naverWorksAppInfo.getUrlApi() + "/v1.0/users";
            responseData = worksClientUtils.get(accessToken, uri);

            // API 응답 Data
            Map<String, Object> responseMap = (Map<String, Object>) responseData.getData();

            // 전 직원을 조회하여 userDataList에 담기
            userDataList = (List<Map<String, Object>>) responseMap.get("users");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseData.setData(e.getMessage());
        }

        logger.debug("BotMessageServiceImpl.searchMembers() ended");

        return userDataList;
    }

    /* 전 직원의 userId 조회 */
    @Override
    public List<String> searchMembersUserId(List<Map<String, Object>> userDataList) {
        logger.debug("BotMessageServiceImpl.searchMembersUserId() started");

        List<String> userIdList = new ArrayList<>();
        String userId = "";

        for (Map<String, Object> userData : userDataList) {
            if (userData.containsKey("userId")) {
                userId = (String) userData.get("userId");
                userIdList.add(userId);
            }
        }

        logger.debug("BotMessageServiceImpl.searchMembersUserId() ended");

        return userIdList;
    }

    /* 오늘 생일인 직원 조회 */
    @Override
    public List<Map<String, Object>> searchBirthdayUserList(List<Map<String, Object>> userDataList) {
        logger.debug("BotMessageServiceImpl.searchBirthdayUserList() started");

        List<Map<String, Object>> birthdayUserList = new ArrayList<>();
        Date nowDate = new Date();
        String today = dateFormatUtils.dateFormatMMDD(nowDate);
        String userId = "";
        String birthday = "";

        for (Map<String, Object> user : userDataList) {
            Map<String, Object> userData = new HashMap<>();

            if (user.containsKey("birthday")) {
                birthday = (String) user.get("birthday");

                // 생일 정보가 없을 경우
                if (birthday == null || birthday.equals("")) {
                    continue;
                }

                // 오늘이 생일인지 확인
                if (birthday.substring(5).equals(today)) {
                    userData.put("birthday", birthday);
                } else {
                    continue;
                }
            }

            if (user.containsKey("userId")) {
                userId = (String) user.get("userId");
                userData.put("userId", userId);
            }

            birthdayUserList.add(userData);
        }

        logger.debug("BotMessageServiceImpl.searchBirthdayUserList() ended");

        return birthdayUserList;
    }

}
