package com.example.resourceserver.services;

import com.example.resourceserver.entities.TypePet;

import java.util.List;

public interface TypePetService {
    TypePet get(Long id);
    List<TypePet> getAll();
    TypePet create(TypePet typePet);
}
