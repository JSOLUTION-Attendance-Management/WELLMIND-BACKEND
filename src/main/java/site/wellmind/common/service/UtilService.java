package site.wellmind.common.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.twilio.rest.lookups.v1.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.dto.MailDto;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * UtilService
 * <p>null 체크 여부 등 각 서비스에서 공통적으로 사용되는 util 로직이 모여 있는 서비스</p>
 * @since 2024-11-23
 * @version 1.0
 */
@Slf4j(topic = "UtilService")
@Service
@RequiredArgsConstructor
public class UtilService {
    public String getE164FormatPhoneNumber(String phoneNum){
        if(phoneNum.isEmpty()){
            throw new GlobalException(ExceptionStatus.INVALID_INPUT);
        }
        try{
            PhoneNumberUtil phoneNumberUtil=PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber parsedPhoneNumber1 = phoneNumberUtil.parse(phoneNum, "KR");
            String result=phoneNumberUtil.format(parsedPhoneNumber1,PhoneNumberUtil.PhoneNumberFormat.E164);

            log.info("getE164FormatPhoneNumber : {}",result);
            return result;
        }catch (NumberParseException e){
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR,"Wrong in tranform phoneNum");
        }
    }
    public String[] getNullPropertyNames(Object source){
        if (source == null) {
            throw new IllegalArgumentException("Source object must not be null");
        }

        return Arrays.stream(BeanUtils.getPropertyDescriptors(source.getClass()))
                .map(PropertyDescriptor::getReadMethod)
                .filter(Objects::nonNull)
                .map(readMethod ->{
                    try{
                        Object value=readMethod.invoke(source);
                        return value==null?readMethod.getName():null;
                    }catch(Exception e){
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }

    public <S, D> void mapFields(S source, D destination) {
        if (source == null || destination == null) {
            return;
        }

        Arrays.stream(source.getClass().getDeclaredFields()).forEach(field -> {
            try {
                field.setAccessible(true);
                Object value = field.get(source);

                if ("id".equals(field.getName()) || "regDate".equals(field.getName()) || "modDate".equals(field.getName())) {
                    return;
                }

                if (value != null) { // null 체크
                    Field destField = destination.getClass().getDeclaredField(field.getName());
                    destField.setAccessible(true);
                    destField.set(destination, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // 소스에 있는 필드가 대상에 없거나, 접근 불가한 경우는 무시
                log.warn("Field mapping skipped for: {}", field.getName(), e);
            }
        });

    }
}
