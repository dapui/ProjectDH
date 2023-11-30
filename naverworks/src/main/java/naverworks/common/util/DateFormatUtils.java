package naverworks.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateFormatUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateFormatUtils.class);

    /* MM-dd :: date -> string */
    public String dateFormatMMDD(Date date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        String strDate = simpleDateFormat.format(date);

        return strDate;
    }

    /* MM월 dd일 :: string -> date -> string*/
    public String dateFormatMMDD(String dateInfo) throws Exception {

        // 문자열을 Date 객체로 파싱
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        try {
            date = inputFormat.parse(dateInfo);
        } catch (ParseException e) {
            logger.error("Exception :: {}",  e.getMessage(), e);
            throw new Exception(e.getMessage());
        }

        // 출력 형식 지정
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM월 dd일");

        // Date 객체를 원하는 형식으로 포맷팅
        String outputDateString = outputFormat.format(date);

        return outputDateString;

    }

    /* YYYY-MM-dd ::  date -> string */
    public String dateFormatYYYYMMDD(Date date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = simpleDateFormat.format(date);

        return strDate;
    }

    /* YYYY-MM-dd :: string -> date -> string */
    public String dateFormatYYYYMMDD(String dateInfo) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateInfo);
        } catch (ParseException e) {
            logger.error("Exception :: {}",  e.getMessage(), e);
            throw new Exception(e.getMessage());
        }

        String strDate = simpleDateFormat.format(date);

        return strDate;
    }
}
