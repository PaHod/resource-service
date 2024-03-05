package com.pahod.music.resourceservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StorageType {
    STAGING("STAGING"),
    PERMANENT("PERMANENT");

    final String value;
}
