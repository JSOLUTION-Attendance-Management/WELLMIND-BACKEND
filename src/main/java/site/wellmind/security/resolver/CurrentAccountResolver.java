package site.wellmind.security.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.security.annotation.CurrentAccount;
import site.wellmind.security.provider.JwtTokenProvider;
import site.wellmind.user.domain.dto.AccountDto;

/**
 * CurrentAccountResolver
 * <p>CurrentAccount Custom Annotation 을 위한 Argument Resolver</p>
 *
 * @see CurrentAccount
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-20
 */
@Component
@RequiredArgsConstructor
public class CurrentAccountResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentAccount.class)!=null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String accessToken = jwtTokenProvider.getCookieValue(request, "accessToken");

        Long accountId=jwtTokenProvider.extractId(accessToken);
        String employeeId= jwtTokenProvider.extractEmployeeId(accessToken);
        String role= jwtTokenProvider.extractRoles(accessToken).toString();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().startsWith("ROLE_ADMIN"));

        if (accountId == null) {
            return ResponseEntity.status(ExceptionStatus.ACCOUNT_NOT_FOUND.getHttpStatus()).
                    body(Messenger.builder()
                            .message(ExceptionStatus.ACCOUNT_NOT_FOUND.getMessage())
                            .build());
        }

        return AccountDto.builder()
                .accountId(accountId)
                .employeeId(employeeId)
                .role(role)
                .isAdmin(isAdmin)
                .build();
    }
}
