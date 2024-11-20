package site.wellmind.security.annotation;

import site.wellmind.security.resolver.CurrentAccountIdResolver;
import site.wellmind.security.resolver.CurrentAccountResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * CurrentAccount
 * <p>jwt에서 USER_IDX 또는 ADMIN_IDX, Role, isAdmin 여부를 추출하도록 하는 Custom Annotation</p>
 *
 * @see CurrentAccountResolver
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-20
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentAccount {
}
