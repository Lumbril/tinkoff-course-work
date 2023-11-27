package com.example.resourceserver.services.impl;

import com.example.resourceserver.dto.request.PetRequest;
import com.example.resourceserver.entities.Pet;
import com.example.resourceserver.repositories.PetRepository;
import com.example.resourceserver.services.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {
    private final PetRepository petRepository;
    private final TypePetServiceImpl typePetService;
    private final UserServiceImpl userService;

    @Override
    public Pet get(Long id) {
        return petRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Pet> getAllByUser(Long userId) {
        return petRepository.findAllByUserId_Id(userId);
    }

    @Override
    public Pet create(PetRequest petRequest, Long userId) {
        return petRepository.save(Pet.builder()
                        .typePetId(typePetService.get(petRequest.getTypeId()))
                        .gender(petRequest.getGender())
                        .name(petRequest.getName())
                        .userId(userService.getById(userId))
                .build());
    }

    @Override
    public Pet update(PetRequest petRequest) {
        return null;
    }

    @Override
    public void delete(Long petId) {
        petRepository.deleteById(petId);
    }
}
