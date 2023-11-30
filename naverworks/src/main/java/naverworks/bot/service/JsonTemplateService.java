package naverworks.bot.service;

import naverworks.bot.vo.ApprovalRequestDataVO;
import naverworks.bot.vo.BirthdayUserInfoVO;
import naverworks.bot.vo.BoardRequestDataVO;

import java.util.Map;

public interface JsonTemplateService {

    /**  전자결재 알림 메세지 템플릿 JSON */
    public Map<String, Object> approvalMessageTemplate(ApprovalRequestDataVO userInfo);
    /**  전자결재 알림 메세지 템플릿 JSON - API 호출 시 간편 결재 url이 없을 경우 */
    public Map<String, Object> approvalWithoutEasyApprovalMessageTemplate(ApprovalRequestDataVO approvalInfo);
    /** 생일자 알림 메세지 템플릿 JSON */
    public Map<String, Object> birthdayMessageTemplate(BirthdayUserInfoVO userInfo) throws Exception;
    /** 게시판 알림 메세지 템플릿 JSON */
    public Map<String, Object> boardMessageTemplate(BoardRequestDataVO boardInfo) throws Exception;
    /** json -> map 변환 */
    public Map<String, Object> jsonToMap(StringBuffer json) throws Exception;

}
