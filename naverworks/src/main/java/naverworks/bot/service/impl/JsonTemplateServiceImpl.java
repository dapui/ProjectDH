package naverworks.bot.service.impl;

import naverworks.bot.service.JsonTemplateService;
import naverworks.bot.vo.ApprovalRequestDataVO;
import naverworks.bot.vo.BirthdayUserInfoVO;
import naverworks.bot.vo.BoardRequestDataVO;
import naverworks.common.util.DateFormatUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("JsonTemplateService")
public class JsonTemplateServiceImpl implements JsonTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(JsonTemplateServiceImpl.class);

    @Autowired
    private DateFormatUtils dateFormatUtils;

    /*  전자결재 알림 메세지 템플릿 JSON */
    @Override
    public Map<String, Object> approvalMessageTemplate(ApprovalRequestDataVO approvalInfo) {
        logger.debug("JsonTemplateServiceImpl.approvalMessageTemplate() started");

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

        logger.debug("JsonTemplateServiceImpl.approvalMessageTemplate() ended");
        return messageContent;
    }

    /*  전자결재 알림 메세지 템플릿 JSON - API 호출 시 간편 결재 url이 없을 경우 */
    @Override
    public Map<String, Object> approvalWithoutEasyApprovalMessageTemplate(ApprovalRequestDataVO approvalInfo) {
        logger.debug("JsonTemplateServiceImpl.approvalMessageTemplate() started");

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

        logger.debug("JsonTemplateServiceImpl.approvalMessageTemplate() ended");
        return messageContent;
    }

    /* 생일자 알림 메세지 템플릿 JSON */
    @Override
    public Map<String, Object> birthdayMessageTemplate(BirthdayUserInfoVO userInfo) throws Exception {
        logger.debug("JsonTemplateServiceImpl.birthdayMessageTemplate() started");

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

        logger.debug("JsonTemplateServiceImpl.birthdayMessageTemplate() ended");

        return messageContent;
    }

    /* 게시판 알림 메세지 템플릿 JSON */
    @Override
    public Map<String, Object> boardMessageTemplate(BoardRequestDataVO boardInfo) throws Exception {
        logger.debug("JsonTemplateServiceImpl.boardMessageTemplate() started");

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

        logger.debug("JsonTemplateServiceImpl.boardMessageTemplate() ended");

        return messageContent;
    }

    /* json -> map 변환 */
    @Override
    public Map<String, Object> jsonToMap(StringBuffer json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json.toString(), Map.class);
    }

}
