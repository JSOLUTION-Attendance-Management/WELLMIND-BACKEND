package site.wellmind.security.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import site.wellmind.security.annotation.CurrentAccountId;
import site.wellmind.security.provider.JwtTokenProvider;

/**
 * CurrentAccountIdResolver
 * <p>CurrentAccountId Custom Annotation 을 위한 Argument Resolver</p>
 *
 *
 * @see CurrentAccountId
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-20
 */
@Component
@RequiredArgsConstructor
public class CurrentAccountIdResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentAccountId.class)!=null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request=(HttpServletRequest) webRequest.getNativeRequest();
        String accessToken=jwtTokenProvider.getCookieValue(request,"accessToken");
        return jwtTokenProvider.extractId(accessToken);
    }
}
