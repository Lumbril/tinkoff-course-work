package com.example.resourceserver.services.impl;

import com.example.resourceserver.entities.TypePet;
import com.example.resourceserver.repositories.TypePetRepository;
import com.example.resourceserver.services.TypePetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypePetServiceImpl implements TypePetService {
    private final TypePetRepository typePetRepository;

    @Override
    public TypePet get(Long id) {
        return typePetRepository.findById(id).orElse(null);
    }

    @Override
    public List<TypePet> getAll() {
        return typePetRepository.findAll();
    }

    @Override
    public TypePet create(TypePet typePet) {
        return typePetRepository.save(typePet);
    }
}
