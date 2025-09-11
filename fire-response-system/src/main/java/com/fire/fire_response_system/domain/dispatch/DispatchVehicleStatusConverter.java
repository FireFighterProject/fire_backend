package com.fire.fire_response_system.domain.dispatch;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class DispatchVehicleStatusConverter
        implements AttributeConverter<DispatchVehicleStatus, Integer> {
    @Override public Integer convertToDatabaseColumn(DispatchVehicleStatus a) {
        return a == null ? null : a.getCode();
    }
    @Override public DispatchVehicleStatus convertToEntityAttribute(Integer db) {
        return DispatchVehicleStatus.from(db);
    }
}
