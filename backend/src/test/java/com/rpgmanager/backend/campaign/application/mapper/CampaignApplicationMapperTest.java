package com.rpgmanager.backend.campaign.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.campaign.application.dto.CampaignDto;
import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CampaignApplicationMapperTest {

  private final CampaignApplicationMapper mapper =
      Mappers.getMapper(CampaignApplicationMapper.class);

  @Test
  void toDTO_shouldMapAllFields() {
    CampaignDomain domain = Instancio.create(CampaignDomain.class);
    CampaignDto dto = mapper.toDto(domain);

    assertThat(dto).isNotNull();
    assertThat(dto.getId()).isEqualTo(domain.getId());
    assertThat(dto.getName()).isEqualTo(domain.getName());
  }

  @Test
  void toDTO_shouldHandleNull() {
    assertThat(mapper.toDto(null)).isNull();
  }
}
