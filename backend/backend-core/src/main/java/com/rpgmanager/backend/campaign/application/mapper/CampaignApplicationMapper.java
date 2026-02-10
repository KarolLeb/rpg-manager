package com.rpgmanager.backend.campaign.application.mapper;

import com.rpgmanager.backend.campaign.application.dto.CampaignDto;
import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import org.mapstruct.Mapper;

/** Mapper for converting between Campaign domain objects and DTOs. */
@Mapper(componentModel = "spring")
public interface CampaignApplicationMapper {

  /**
   * Converts a CampaignDomain object to a CampaignDto.
   *
   * @param domain the domain object
   * @return the DTO
   */
  CampaignDto toDto(CampaignDomain domain);
}
