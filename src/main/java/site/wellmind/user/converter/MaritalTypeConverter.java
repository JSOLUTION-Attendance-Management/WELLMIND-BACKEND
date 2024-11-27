package site.wellmind.user.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;
import site.wellmind.transfer.domain.vo.TransferType;
import site.wellmind.user.domain.vo.MaritalType;

@Converter(autoApply = true)
public class MaritalTypeConverter implements AttributeConverter<MaritalType, String> {

    @Override
    public String convertToDatabaseColumn(MaritalType attribute) {
        return attribute != null ? attribute.getKorean() : null;
    }

    @Override
    public MaritalType convertToEntityAttribute(String dbData) {
        return dbData != null ? MaritalType.fromKorean(dbData) : null;
    }
}