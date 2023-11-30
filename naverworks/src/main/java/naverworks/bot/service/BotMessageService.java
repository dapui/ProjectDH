package naverworks.bot.service;

import naverworks.bot.vo.ApprovalRequestDataVO;
import naverworks.bot.vo.BirthdayUserInfoVO;
import naverworks.bot.vo.BoardRequestDataVO;
import naverworks.common.vo.ResponseDataVO;

import java.util.List;
import java.util.Map;

public interface BotMessageService {

    /** 전자결재 알림 봇 호출 */
    public ResponseDataVO callApprovalBot(String token, String botId, ApprovalRequestDataVO approvalRequestData) throws Exception;
    /* 생일 알림 봇 호출 */
    public ResponseDataVO callBirthdayBot(String token, String botId);
    /** 게시판 알림 봇 호출 */
    public ResponseDataVO callBoardBot(String token, String botId, BoardRequestDataVO boardRequestData) throws Exception;
    /** NAVER WORKS API 호출하여 구성원의 프로필 정보 조회 */
    public BirthdayUserInfoVO searchUserInfo(String userId);
    /* NAVER WORKS API 호출하여 구성원 사진 조회 */
    public String searchUserPhoto(String userId);
    /** NAVER WORKS API 호출하여 구성원 목록 조회 */
    public List<Map<String, Object>> searchMembers();
    /** 전 직원의 userId 조회 */
    public List<String> searchMembersUserId(List<Map<String, Object>> userDataList);
    /** 오늘 생일인 직원 조회 */
    public List<Map<String, Object>> searchBirthdayUserList(List<Map<String, Object>> userDataList);

}
