package site.wellmind.transfer.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.wellmind.transfer.domain.vo.TransferType;

@Converter(autoApply = true)
public class TransferTypeConverter implements AttributeConverter<TransferType, String> {

    @Override
    public String convertToDatabaseColumn(TransferType attribute) {
        return attribute != null ? attribute.getKorean() : null;
    }

    @Override
    public TransferType convertToEntityAttribute(String dbData) {
        return dbData != null ? TransferType.fromKorean(dbData) : null;
    }
}