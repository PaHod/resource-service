package com.pahod.music.resourceservice.web.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeletedResourcesIDs {
  List<Integer> ids;
}
