package com.pahod.music.resourceservice.web.mapper;

import com.pahod.music.resourceservice.entity.AudioResourceEntity;
import com.pahod.music.resourceservice.web.dto.AudioResourceResponse;
import com.pahod.music.resourceservice.web.dto.ResourceDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

  ResourceDTO modelToDTO(AudioResourceEntity resource);

  AudioResourceResponse modelToResponse(AudioResourceEntity resource);

  //    @Mapping(target = "id", ignore = true)
  //    AudioResourceEntity dtoToModel(ResourceDTO dto);
}
