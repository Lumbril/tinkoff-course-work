package com.example.resourceserver.repositories;

import com.example.resourceserver.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByIdAndUserId_Id(Long petId, Long userId);
    List<Pet> findAllByUserId_Id(Long userId);
}
