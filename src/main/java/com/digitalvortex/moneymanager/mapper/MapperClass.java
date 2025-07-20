package com.digitalvortex.moneymanager.mapper;

import com.digitalvortex.moneymanager.dto.ProfileDTO;
import com.digitalvortex.moneymanager.model.Profile;

public class MapperClass {
    public static Profile convertDTOtoEntity(ProfileDTO profileDTO){
        Profile profile = new Profile();

        profile.setId(profileDTO.getId());
        profile.setFullName(profile.getFullName());
        profile.setEmail(profileDTO.getEmail());
        profile.setPassword(profileDTO.getPassword());
        profile.setProfileImageUrl(profileDTO.getProfileImageUrl());
        profile.setCreatedAt(profileDTO.getCreatedAt());
        profile.setUpdatedAt(profileDTO.getUpdatedAt());

        return profile;

    }

    public static ProfileDTO convertEntityToDTo(Profile profile){
        ProfileDTO profileDTO = new ProfileDTO();

        profileDTO.setId(profile.getId());
        profileDTO.setFullName(profile.getFullName());
        profileDTO.setEmail(profile.getEmail());
     //   profileDTO.setPassword(profile.getPassword());
        profileDTO.setProfileImageUrl(profile.getProfileImageUrl());
        profileDTO.setCreatedAt(profile.getCreatedAt());
        profileDTO.setUpdatedAt(profile.getUpdatedAt());

        return profileDTO;

    }
}
