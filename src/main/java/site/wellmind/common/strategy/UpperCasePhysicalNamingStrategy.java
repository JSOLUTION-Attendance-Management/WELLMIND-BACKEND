package site.wellmind.common.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import site.wellmind.user.repository.UserTopRepository;
import site.wellmind.user.service.UserService;

/**
 * UpperCasePhysicalNamingStrategy
 * <p>DB Column 명 대문자로 저장하기 위한 strategy</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-06
 */
public class UpperCasePhysicalNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return null;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return null;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return new Identifier(identifier.getText().toUpperCase(),identifier.isQuoted());
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return null;
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return new Identifier(identifier.getText().toUpperCase(),identifier.isQuoted());
    }
}
