package com.pahod.music.resourceservice.web.mapper;

import com.pahod.music.resourceservice.entity.AudioResourceEntity;
import com.pahod.music.resourceservice.web.dto.AudioResource;
import com.pahod.music.resourceservice.web.dto.AudioResourceInfoResponse;
import com.pahod.music.resourceservice.web.dto.AudioResourceSavedResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

  AudioResource modelToAudioResponse(AudioResourceEntity resource);

  AudioResourceSavedResponse modelToSavedResponse(AudioResourceEntity resource);

  AudioResourceInfoResponse modelToInfoResponse(AudioResourceEntity resource);
}
