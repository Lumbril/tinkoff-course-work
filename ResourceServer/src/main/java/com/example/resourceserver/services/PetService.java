package com.example.resourceserver.services;

import com.example.resourceserver.dto.request.PetRequest;
import com.example.resourceserver.entities.Pet;
import com.example.resourceserver.entities.User;

import java.util.List;

public interface PetService {
    Pet get(Long id);
    List<Pet> getAllByUser(Long userId);
    Pet create(PetRequest petRequest, Long userId);
    Pet update(PetRequest petRequest);
    void delete(Long petId);
}
