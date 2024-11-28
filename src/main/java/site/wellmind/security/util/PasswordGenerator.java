package site.wellmind.security.util;

import org.springframework.stereotype.Component;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.user.repository.AccountRoleRepository;

import java.security.SecureRandom;

/**
 * PasswordGenerator
 * <p>임시 비밀번호 생성을 위한 위한 util component</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @see AccountRoleRepository
 * @since 2024-11-27
 */
@Component
public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?/[]{}";
    private static final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;

    public String generatePassword(int length){
        if(length<8){
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR,"Password length must be at least 8 characters");
        }

        SecureRandom random=new SecureRandom();
        StringBuilder password=new StringBuilder();

        // 반드시 포함해야 하는 문자들
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // 나머지 문자 랜덤 생성
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        return shufflePassword(password.toString());
    }
    private String shufflePassword(String password) {
        SecureRandom random = new SecureRandom();
        char[] chars = password.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
