package org.reussite.appui.support.dashboard.mapper;



import org.mapstruct.Mapper;
import org.reussite.appui.support.dashboard.domain.MonetaryAmount;
import org.reussite.appui.support.dashboard.model.MonetaryAmountEntity;

@Mapper(componentModel = "cdi")
public interface MonetaryAmountMapper {

	MonetaryAmountEntity toEntity(MonetaryAmount domain);
	MonetaryAmount toDomain(MonetaryAmountEntity entity);
    
}
