package com.example.resourceserver.services;

import com.example.resourceserver.dto.request.PetRequest;
import com.example.resourceserver.entities.Pet;
import com.example.resourceserver.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PetService {
    Pet get(Long id);
    List<Pet> getAllByUser(Long userId);
    Pet create(PetRequest petRequest, Long userId);
    Pet update(PetRequest petRequest);
    Pet updateImage(Long petId, Long userId, MultipartFile file);
    void delete(Long petId);
    void deleteByIdAndUser(Long petId, Long userId);
}
