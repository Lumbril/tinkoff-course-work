package com.example.resourceserver.services;

import com.example.resourceserver.dto.request.PetRequest;
import com.example.resourceserver.entities.Pet;
import com.example.resourceserver.entities.TypePet;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.entities.enums.Role;
import com.example.resourceserver.repositories.PetRepository;
import com.example.resourceserver.services.impl.MediaServiceImpl;
import com.example.resourceserver.services.impl.PetServiceImpl;
import com.example.resourceserver.services.impl.TypePetServiceImpl;
import com.example.resourceserver.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PetServiceTests {
    @Mock
    private PetRepository petRepository;

    @Mock
    private TypePetServiceImpl typePetService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private MediaServiceImpl mediaService;

    @InjectMocks
    private PetServiceImpl petService;

    private final User tempUser = new User(1L, 1L,
            "string", null,
            null,Role.ROLE_USER);
    private final TypePet tempTypePet = new TypePet(1L, "собака");
    private final Pet tempPet = new Pet(1L, tempTypePet, "Жучка", false, null, tempUser);
    private final String imgUrl = "path/to/img.png";
    private final MultipartFile tempImg = new MockMultipartFile("img.png", "img.png", "image/png",
            new byte[]{0, 0, 0, 0, 0, 0});
    private final Pet tempPetWithImg = new Pet(1L, tempTypePet, "Жучка", false, imgUrl, tempUser);
    private final PetRequest tempPetRequest = new PetRequest(1L, "Жучка", false);

    @Test
    public void getByIdTest() {
        Mockito.doReturn(Optional.of(tempPet)).when(petRepository).findById(anyLong());

        Pet pet = petService.get(1L);

        assertEquals(tempPet, pet);
        verify(petRepository, times(1)).findById(1L);
    }

    @Test
    public void getAllByUserTest() {
        Mockito.doReturn(List.of(tempPet)).when(petRepository).findAllByUserId_Id(anyLong());

        List<Pet> pets = petService.getAllByUser(1L);

        assertIterableEquals(List.of(tempPet), pets);
    }

    @Test
    public void createTest() {
        Mockito.doReturn(tempPet).when(petRepository).save(any());
        Mockito.doReturn(tempTypePet).when(typePetService).get(anyLong());
        Mockito.doReturn(tempUser).when(userService).getById(anyLong());

        Pet pet = petService.create(tempPetRequest, tempUser.getId());

        assertEquals(tempPet, pet);
        verify(typePetService, times(1)).get(1L);
        verify(userService, times(1)).getById(1L);
    }

    @Test
    public void updateImage() {
        Mockito.doReturn(Optional.of(tempPet)).when(petRepository).findByIdAndUserId_Id(anyLong(), anyLong());
        Mockito.doReturn(imgUrl).when(mediaService).saveImage(any());
        Mockito.doReturn(tempPetWithImg).when(petRepository).save(any());

        Pet pet = petService.updateImage(1L, 1L, tempImg);

        assertEquals(tempPetWithImg, pet);
        verify(petRepository, times(1)).findByIdAndUserId_Id(1L, 1L);
        verify(mediaService, times(1)).saveImage(tempImg);
    }

    @Test
    public void deleteByIdTest() {
        Mockito.doNothing().when(petRepository).deleteById(anyLong());

        petService.delete(1L);

        verify(petRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteByIdAndUserTest() {
        Mockito.doReturn(Optional.of(tempPet)).when(petRepository).findByIdAndUserId_Id(anyLong(), anyLong());
        Mockito.doNothing().when(petRepository).deleteById(anyLong());

        petService.deleteByIdAndUser(1L, 1L);

        verify(petRepository, times(1)).findByIdAndUserId_Id(1L, 1L);
        verify(petRepository, times(1)).deleteById(1L);
    }
}
