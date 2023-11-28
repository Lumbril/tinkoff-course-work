package com.example.resourceserver.repositories;

import com.example.resourceserver.entities.TypePet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypePetRepository extends JpaRepository<TypePet, Long> {
}
