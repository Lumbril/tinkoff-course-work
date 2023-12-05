package com.example.resourceserver.services;

import com.example.resourceserver.entities.TypePet;
import com.example.resourceserver.repositories.TypePetRepository;
import com.example.resourceserver.services.impl.TypePetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class TypePetServiceTests {
    @Mock
    private TypePetRepository typePetRepository;

    @InjectMocks
    private TypePetServiceImpl typePetService;

    private final TypePet tempTypePet = new TypePet(1L, "Собака");

    @Test
    public void getTest() {
        Mockito.doReturn(Optional.of(tempTypePet)).when(typePetRepository).findById(any());

        TypePet typePet = typePetService.get(1L);

        assertEquals(tempTypePet, typePet);
    }

    @Test
    public void getAllTest() {
        Mockito.doReturn(List.of(tempTypePet)).when(typePetRepository).findAll();

        List<TypePet> typePets = typePetService.getAll();

        assertIterableEquals(List.of(tempTypePet), typePets);
    }

    @Test
    public void createTest() {
        Mockito.doReturn(tempTypePet).when(typePetRepository).save(any());

        TypePet typePet = typePetService.create(tempTypePet);

        assertEquals(tempTypePet,typePet);
    }
}
