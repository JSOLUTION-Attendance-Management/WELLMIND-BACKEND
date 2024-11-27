package site.wellmind.user.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.wellmind.user.domain.vo.JobType;
import site.wellmind.user.domain.vo.MaritalType;

@Converter(autoApply = true)
public class JobTypeConverter implements AttributeConverter<JobType, String> {

    @Override
    public String convertToDatabaseColumn(JobType attribute) {
        return attribute != null ? attribute.getKorean() : null;
    }

    @Override
    public JobType convertToEntityAttribute(String dbData) {
        return dbData != null ? JobType.fromKorean(dbData) : null;
    }
}