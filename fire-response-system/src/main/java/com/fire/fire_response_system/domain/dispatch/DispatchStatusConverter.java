package com.fire.fire_response_system.domain.dispatch;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class DispatchStatusConverter implements AttributeConverter<DispatchStatus, Integer> {
    @Override public Integer convertToDatabaseColumn(DispatchStatus attr) {
        return attr == null ? null : attr.getCode();
    }
    @Override public DispatchStatus convertToEntityAttribute(Integer db) {
        return DispatchStatus.from(db);
    }
}
