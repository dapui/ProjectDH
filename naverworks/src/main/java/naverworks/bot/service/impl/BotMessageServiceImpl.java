package naverworks.bot.service.impl;

import egovframework.naverworks.bot.service.BotMessageService;
import egovframework.naverworks.bot.vo.BirthdayUserInfoVO;
import egovframework.naverworks.bot.vo.BoardRequestDataVO;
import egovframework.naverworks.common.util.DateFormatUtils;
import egovframework.naverworks.common.util.WorksClientUtils;
import egovframework.naverworks.bot.vo.ApprovalRequestDataVO;
import egovframework.naverworks.common.vo.NaverWorksAppInfoVO;
import egovframework.naverworks.common.vo.ResponseDataVO;
import lombok.extern.log4j.Log4j2;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            message = approvalWithoutEasyApprovalMessageTemplate(approvalRequestData);
        } else {
            message = approvalMessageTemplate(approvalRequestData);
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
                    message = birthdayMessageTemplate(userInfo);
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
        List<String> targetMessageUsers = boardRequestData.getCn();
        // 메시지 전송 API 호출시 Request Body에 들어갈 데이터
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> content = new HashMap<String, Object>();

        try {
            Map<String, Object> message = boardMessageTemplate(boardRequestData);
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

    /*  전자결재 알림 메세지 템플릿 JSON */
    @Override
    public Map<String, Object> approvalMessageTemplate(ApprovalRequestDataVO approvalInfo) {
        logger.debug("BotMessageServiceImpl.approvalMessageTemplate() started");

        String status = approvalInfo.getStatus();
        String docTitle = approvalInfo.getDocTitle();
        String writerName = approvalInfo.getWriterName();
        String startDate = approvalInfo.getStartDate();
        String targetUri = approvalInfo.getTargetUri();
        String easyApproval = approvalInfo.getEasyApproval();

        StringBuffer message = new StringBuffer();
        message.append("{");
        message.append(" \"type\": \"flex\",");
        message.append(" \"altText\": \"[결재 " + status + "] 문서가 도착했습니다.\",");
        message.append(" \"contents\": {");
        message.append("    \"type\": \"bubble\",");
        message.append("    \"size\": \"kilo\",");
        message.append("    \"header\": {");
        message.append("        \"type\": \"box\",");
        message.append("        \"layout\": \"baseline\",");
        message.append("        \"contents\": [");
        message.append("        {");
        message.append("            \"type\": \"text\",");
        message.append("            \"text\": \"[결재 " + status + "] \\r\\n문서가 도착했습니다.\",");
        message.append("            \"wrap\": true,");
        message.append("            \"size\": \"md\",");
        message.append("            \"color\": \"#ffffff\",");
        message.append("            \"align\": \"center\",");
        message.append("            \"weight\": \"bold\"");
        message.append("        }");
        message.append("        ],");
        message.append("        \"backgroundColor\": \"#146bb8\"");
        message.append("    },");
        message.append("    \"body\": {");
        message.append("        \"type\": \"box\",");
        message.append("        \"layout\": \"vertical\",");
        message.append("        \"contents\": [");
        message.append("        {");
        message.append("            \"type\": \"box\",");
        message.append("            \"layout\": \"vertical\",");
        message.append("            \"contents\": [");
        message.append("                {");
        message.append("                \"type\": \"box\",");
        message.append("                \"layout\": \"horizontal\",");
        message.append("                \"contents\": [");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"제목\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"gravity\": \"center\",");
        message.append("                    \"flex\": 3,");
        message.append("                    \"color\": \"#146bb8\"");
        message.append("                },");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"" + docTitle + "\",");
        message.append("                    \"wrap\": false,");  // true:말줄임표X, false:말줄임표O
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"align\": \"end\",");
        message.append("                    \"flex\": 7");
        message.append("                }");
        message.append("                ]");
        message.append("            },");
        message.append("            {");
        message.append("                \"type\": \"separator\",");
        message.append("                \"margin\": \"lg\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"type\": \"box\",");
        message.append("                \"layout\": \"baseline\",");
        message.append("                \"contents\": [");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"기안자\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"color\": \"#146bb8\",");
        message.append("                    \"gravity\": \"center\",");
        message.append("                    \"flex\": 3");
        message.append("                },");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"" + writerName + "\",");
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"align\": \"end\",");
        message.append("                    \"flex\": 7");
        message.append("                }");
        message.append("                ],");
        message.append("                \"margin\": \"lg\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"type\": \"separator\",");
        message.append("                \"margin\": \"lg\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"type\": \"box\",");
        message.append("                \"layout\": \"baseline\",");
        message.append("                \"contents\": [");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"기안일시\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"color\": \"#146bb8\",");
        message.append("                    \"gravity\": \"center\",");
        message.append("                    \"flex\": 3");
        message.append("                },");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"" + startDate + "\",");
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"align\": \"end\",");
        message.append("                    \"flex\": 7");
        message.append("                }");
        message.append("                ],");
        message.append("                \"margin\": \"lg\"");
        message.append("            }");
        message.append("            ],");
        message.append("            \"paddingStart\": \"5px\",");
        message.append("            \"paddingEnd\": \"5px\"");
        message.append("        },");
        message.append("        {");
        message.append("            \"type\": \"box\",");
        message.append("            \"layout\": \"vertical\",");
        message.append("            \"contents\": [");
        message.append("            {");
        message.append("                \"type\": \"button\",");
        message.append("                \"action\": {");
        message.append("                    \"type\": \"uri\",");
        message.append("                    \"label\": \"결재 문서함\",");
        message.append("                    \"uri\": \"" + targetUri + "\"");
        message.append("                },");
        message.append("                \"style\": \"primary\",");
        message.append("                \"color\": \"#146bb8\",");
        message.append("                \"margin\": \"none\",");
        message.append("                \"height\": \"sm\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"type\": \"button\",");
        message.append("                \"action\": {");
        message.append("                    \"type\": \"uri\",");
        message.append("                    \"uri\": \"" + easyApproval + "\",");
        message.append("                    \"label\": \"간편 결재 진행\"");
        message.append("                },");
        message.append("                \"style\": \"secondary\",");
        message.append("                \"color\": \"#f4f4f4\",");
        message.append("                \"margin\": \"md\",");
        message.append("                \"height\": \"sm\"");
        message.append("            }");
        message.append("            ],");
        message.append("            \"margin\": \"xl\"");
        message.append("        }");
        message.append("        ]");
        message.append("    }");
        message.append("  }");
        message.append("}");

        Map<String, Object> messageContent = null;
        try {
            messageContent = jsonToMap(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.debug("BotMessageServiceImpl.approvalMessageTemplate() ended");
        return messageContent;
    }

    /*  전자결재 알림 메세지 템플릿 JSON - API 호출 시 간편 결재 url이 없을 경우 */
    @Override
    public Map<String, Object> approvalWithoutEasyApprovalMessageTemplate(ApprovalRequestDataVO approvalInfo) {
        logger.debug("BotMessageServiceImpl.approvalMessageTemplate() started");

        String status = approvalInfo.getStatus();
        String docTitle = approvalInfo.getDocTitle();
        String writerName = approvalInfo.getWriterName();
        String startDate = approvalInfo.getStartDate();
        String targetUri = approvalInfo.getTargetUri();

        StringBuffer message = new StringBuffer();
        message.append("{");
        message.append(" \"type\": \"flex\",");
        message.append(" \"altText\": \"[결재 " + status + "] 문서가 도착했습니다.\",");
        message.append(" \"contents\": {");
        message.append("    \"type\": \"bubble\",");
        message.append("    \"size\": \"kilo\",");
        message.append("    \"header\": {");
        message.append("        \"type\": \"box\",");
        message.append("        \"layout\": \"baseline\",");
        message.append("        \"contents\": [");
        message.append("        {");
        message.append("            \"type\": \"text\",");
        message.append("            \"text\": \"[결재 " + status + "] \\r\\n문서가 도착했습니다.\",");
        message.append("            \"wrap\": true,");
        message.append("            \"size\": \"md\",");
        message.append("            \"color\": \"#ffffff\",");
        message.append("            \"align\": \"center\",");
        message.append("            \"weight\": \"bold\"");
        message.append("        }");
        message.append("        ],");
        message.append("        \"backgroundColor\": \"#146bb8\"");
        message.append("    },");
        message.append("    \"body\": {");
        message.append("        \"type\": \"box\",");
        message.append("        \"layout\": \"vertical\",");
        message.append("        \"contents\": [");
        message.append("        {");
        message.append("            \"type\": \"box\",");
        message.append("            \"layout\": \"vertical\",");
        message.append("            \"contents\": [");
        message.append("                {");
        message.append("                \"type\": \"box\",");
        message.append("                \"layout\": \"horizontal\",");
        message.append("                \"contents\": [");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"제목\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"gravity\": \"center\",");
        message.append("                    \"flex\": 3,");
        message.append("                    \"color\": \"#146bb8\"");
        message.append("                },");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"" + docTitle + "\",");
        message.append("                    \"wrap\": false,");  // true:말줄임표X, false:말줄임표O
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"align\": \"end\",");
        message.append("                    \"flex\": 7");
        message.append("                }");
        message.append("                ]");
        message.append("            },");
        message.append("            {");
        message.append("                \"type\": \"separator\",");
        message.append("                \"margin\": \"lg\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"type\": \"box\",");
        message.append("                \"layout\": \"baseline\",");
        message.append("                \"contents\": [");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"기안자\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"color\": \"#146bb8\",");
        message.append("                    \"gravity\": \"center\",");
        message.append("                    \"flex\": 3");
        message.append("                },");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"" + writerName + "\",");
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"align\": \"end\",");
        message.append("                    \"flex\": 7");
        message.append("                }");
        message.append("                ],");
        message.append("                \"margin\": \"lg\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"type\": \"separator\",");
        message.append("                \"margin\": \"lg\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"type\": \"box\",");
        message.append("                \"layout\": \"baseline\",");
        message.append("                \"contents\": [");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"기안일시\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"color\": \"#146bb8\",");
        message.append("                    \"gravity\": \"center\",");
        message.append("                    \"flex\": 3");
        message.append("                },");
        message.append("                {");
        message.append("                    \"type\": \"text\",");
        message.append("                    \"text\": \"" + startDate + "\",");
        message.append("                    \"weight\": \"bold\",");
        message.append("                    \"size\": \"sm\",");
        message.append("                    \"align\": \"end\",");
        message.append("                    \"flex\": 7");
        message.append("                }");
        message.append("                ],");
        message.append("                \"margin\": \"lg\"");
        message.append("            }");
        message.append("            ],");
        message.append("            \"paddingStart\": \"5px\",");
        message.append("            \"paddingEnd\": \"5px\"");
        message.append("        },");
        message.append("        {");
        message.append("            \"type\": \"box\",");
        message.append("            \"layout\": \"vertical\",");
        message.append("            \"contents\": [");
        message.append("            {");
        message.append("                \"type\": \"button\",");
        message.append("                \"action\": {");
        message.append("                    \"type\": \"uri\",");
        message.append("                    \"uri\": \"" + targetUri + "\",");
        message.append("                    \"label\": \"결재 문서함\"");
        message.append("                },");
        message.append("                \"style\": \"secondary\",");
        message.append("                \"color\": \"#f4f4f4\",");
        message.append("                \"margin\": \"md\",");
        message.append("                \"height\": \"sm\"");
        message.append("            }");
        message.append("            ],");
        message.append("            \"margin\": \"xl\"");
        message.append("        }");
        message.append("        ]");
        message.append("    }");
        message.append("  }");
        message.append("}");

        Map<String, Object> messageContent = null;
        try {
            messageContent = jsonToMap(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.debug("BotMessageServiceImpl.approvalMessageTemplate() ended");
        return messageContent;
    }

    /* 생일자 알림 메세지 템플릿 JSON */
    @Override
    public Map<String, Object> birthdayMessageTemplate(BirthdayUserInfoVO userInfo) throws Exception {
        logger.debug("BotMessageServiceImpl.birthdayMessageTemplate() started");

        String birthday = dateFormatUtils.dateFormatMMDD(userInfo.getBirthday());
        System.out.println("birthday = " + birthday);
        String image = userInfo.getImage();
        String deptName = userInfo.getDeptName();
        String userName = userInfo.getUserName();

        StringBuffer message = new StringBuffer();
        message.append("{");
        message.append(" \"type\": \"flex\",");
        message.append(" \"altText\": \"오늘의 생일자를 알려드립니다.\",");
        message.append(" \"contents\": {");
        message.append("   \"type\": \"bubble\",");
        message.append("   \"size\": \"kilo\",");
        message.append("   \"body\": {");
        message.append("       \"type\": \"box\",");
        message.append("       \"layout\": \"vertical\",");
        message.append("       \"contents\": [");
        message.append("       {");
        message.append("           \"text\": \"" + birthday + "\",");
        message.append("           \"type\": \"text\",");
        message.append("           \"size\": \"lg\",");
        message.append("           \"align\": \"center\",");
        message.append("           \"weight\": \"bold\",");
        message.append("           \"color\": \"#333333\"");
        message.append("       },");
        message.append("       {");
        message.append("           \"type\": \"text\",");
        message.append("           \"text\": \"오늘의 생일자를 알려드립니다.\",");
        message.append("           \"color\": \"#146bb8\",");
        message.append("           \"weight\": \"bold\",");
        message.append("           \"size\": \"sm\",");
        message.append("           \"align\": \"center\"");
        message.append("       },");
        message.append("       {");
        message.append("           \"type\": \"box\",");
        message.append("           \"layout\": \"vertical\",");
        message.append("           \"contents\": [");
        message.append("           {");
        message.append("               \"type\": \"image\",");
        message.append("               \"url\": \"" + image + "\",");  // 생일자 사진 가져오기
        message.append("               \"size\": \"sm\",");
        message.append("               \"aspectMode\": \"cover\",");
        message.append("               \"align\": \"center\"");
        message.append("           }");
        message.append("           ],");
        message.append("           \"margin\": \"xxl\",");
        message.append("           \"cornerRadius\": \"50px\",");
        message.append("           \"borderWidth\": \"1px\",");
        message.append("           \"borderColor\": \"#22222220\",");
        message.append("           \"height\": \"60px\",");
        message.append("           \"width\": \"60px\",");
        message.append("           \"offsetStart\": \"85px\",");
        message.append("           \"offsetEnd\": \"85px\"");
        message.append("       },");
        message.append("       {");
        message.append("           \"type\": \"text\",");
        message.append("           \"text\": \"" + deptName + "\",");
        message.append("           \"color\": \"#aaaaaa\",");
        message.append("           \"size\": \"sm\",");
        message.append("           \"wrap\": true,");
        message.append("           \"align\": \"center\",");
        message.append("           \"margin\": \"lg\"");
        message.append("       },");
        message.append("       {");
        message.append("           \"type\": \"text\",");
        message.append("           \"text\": \"" + userName + "\",");
        message.append("           \"color\": \"#333333\",");
        message.append("           \"weight\": \"bold\",");
        message.append("           \"size\": \"lg\",");
        message.append("           \"margin\": \"md\",");
        message.append("           \"align\": \"center\"");
        message.append("       }");
        message.append("       ]");
        message.append("     }");
        message.append("   }");
        message.append("}");

        Map<String, Object> messageContent = null;
        try {
            messageContent = jsonToMap(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.debug("BotMessageServiceImpl.birthdayMessageTemplate() ended");

        return messageContent;
    }

    /* 게시판 알림 메세지 템플릿 JSON */
    @Override
    public Map<String, Object> boardMessageTemplate(BoardRequestDataVO boardInfo) throws Exception {
        logger.debug("BotMessageServiceImpl.boardMessageTemplate() started");

        String status = boardInfo.getStatus();
        String docTitle = boardInfo.getDocTitle();
        String deptName = boardInfo.getDeptName();
        String writerName = boardInfo.getWriterName();
        String writeDate = dateFormatUtils.dateFormatYYYYMMDD(boardInfo.getWriteDate());
        String targetUri = boardInfo.getTargetUri();

        StringBuffer message = new StringBuffer();
        message.append("{");
        message.append("    \"type\": \"flex\",");
        message.append("    \"altText\": \"게시판 알림이 도착했습니다.\",");
        message.append("    \"contents\": {");
        message.append("        \"type\": \"bubble\",");
        message.append("        \"body\": {");
        message.append("            \"type\": \"box\",");
        message.append("            \"layout\": \"vertical\",");
        message.append("            \"contents\": [");
        message.append("            {");
        message.append("                \"type\": \"text\",");
        message.append("                \"text\": \"" + status + "\",");
        message.append("                \"size\": \"sm\",");
        message.append("                \"color\": \"#146bb8\",");
        message.append("                \"weight\": \"bold\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"text\": \"" + docTitle + "\",");
        message.append("                \"type\": \"text\",");
        message.append("                \"margin\": \"lg\",");
        message.append("                \"weight\": \"bold\",");
        message.append("                \"color\": \"#222222\",");
        message.append("                \"size\": \"md\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"type\": \"separator\",");
        message.append("                \"margin\": \"xxl\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"layout\": \"horizontal\",");
        message.append("                \"type\": \"box\",");
        message.append("                \"contents\": [");
        message.append("                {");
        message.append("                    \"layout\": \"vertical\",");
        message.append("                    \"type\": \"box\",");
        message.append("                    \"contents\": [");
        message.append("                    {");
        message.append("                        \"text\": \"" + deptName + " " + writerName + "\",");
        message.append("                        \"type\": \"text\",");
        message.append("                        \"size\": \"xs\",");
        message.append("                        \"color\": \"#999999\",");
        message.append("                        \"align\": \"start\"");
        message.append("                    }");
        message.append("                    ]");
        message.append("                },");
        message.append("                {");
        message.append("                    \"layout\": \"vertical\",");
        message.append("                    \"type\": \"box\",");
        message.append("                    \"contents\": [");
        message.append("                    {");
        message.append("                        \"text\": \"" + writeDate + "\",");
        message.append("                        \"type\": \"text\",");
        message.append("                        \"size\": \"xs\",");
        message.append("                        \"color\": \"#999999\",");
        message.append("                        \"align\": \"end\"");
        message.append("                    }");
        message.append("                    ],");
        message.append("                    \"width\": \"80px\"");
        message.append("                }");
        message.append("                ],");
        message.append("                \"margin\": \"md\"");
        message.append("            },");
        message.append("            {");
        message.append("                \"layout\": \"vertical\",");
        message.append("                \"type\": \"box\",");
        message.append("                \"contents\": [");
        message.append("                {");
        message.append("                    \"type\": \"button\",");
        message.append("                    \"action\": {");
        message.append("                        \"type\": \"uri\",");
        message.append("                        \"label\": \"게시글로 이동\",");
        message.append("                        \"uri\": \"" + targetUri + "\"");
        message.append("                    },");
        message.append("                    \"style\": \"primary\",");
        message.append("                    \"color\": \"#146bb8\",");
        message.append("                    \"margin\": \"none\",");
        message.append("                    \"height\": \"sm\"");
        message.append("                }");
        message.append("                ],");
        message.append("                \"margin\": \"xl\"");
        message.append("            }");
        message.append("            ]");
        message.append("        }");
        message.append("    }");
        message.append("}");

        Map<String, Object> messageContent = null;
        try {
            messageContent = jsonToMap(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.debug("BotMessageServiceImpl.boardMessageTemplate() ended");

        return messageContent;
    }

    /* json -> map 변환 */
    @Override
    public Map<String, Object> jsonToMap(StringBuffer json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json.toString(), Map.class);
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
