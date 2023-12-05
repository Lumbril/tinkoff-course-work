package com.example.resourceserver.controllers;

import com.example.resourceserver.dto.response.ErrorResponse;
import com.example.resourceserver.dto.response.FriendResponse;
import com.example.resourceserver.dto.response.UserResponse;
import com.example.resourceserver.entities.Friend;
import com.example.resourceserver.exceptions.FriendException;
import com.example.resourceserver.services.impl.FriendServiceImpl;
import com.example.resourceserver.utils.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/friend")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Friends", description = "Методы для друзей")
@CrossOrigin("*")
@RequiredArgsConstructor
public class FriendController {
    private final FriendServiceImpl friendService;

    @Operation(summary = "Получить список друзей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FriendResponse.class))
                            )
                    }
            )
    })
    @GetMapping("/list")
    public ResponseEntity<?> getFriends(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Friend> friends = friendService.getFriends(userDetails.getId());
        List<FriendResponse> friendResponseList = friends.stream()
                .map(friend ->
                        FriendResponse.builder()
                                .id(friend.getId())
                                .user(UserResponse.builder()
                                        .id(friend.getUser2().getId())
                                        .username(friend.getUser2().getUsername())
                                        .firstName(friend.getUser2().getFirstName())
                                        .lastName(friend.getUser2().getLastName())
                                        .build())
                                .status(friend.getStatus())
                                .build()
                ).toList();

        return ResponseEntity.ok().body(friendResponseList);
    }

    @Operation(summary = "Получить список входящих запросов на дружбу")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FriendResponse.class))
                            )
                    }
            )
    })
    @GetMapping("/request/list")
    public ResponseEntity<?> getIncomingRequests(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Friend> friends = friendService.getIncomingRequests(userDetails.getId());
        List<FriendResponse> friendResponseList = friendResponseListWithUser1(friends);

        return ResponseEntity.ok().body(friendResponseList);
    }

    @Operation(summary = "Получить список подписчиков")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FriendResponse.class))
                            )
                    }
            )
    })
    @GetMapping("/subscriber/list")
    public ResponseEntity<?> getSubscribers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Friend> friends = friendService.getSubscribers(userDetails.getId());
        List<FriendResponse> friendResponseList = friendResponseListWithUser1(friends);

        return ResponseEntity.ok().body(friendResponseList);
    }

    @Operation(summary = "Сделать запрос на дружбу")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FriendResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @PostMapping("/{id}")
    public ResponseEntity<?> createFriendRequest(@PathVariable Long id,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        Friend friend = friendService.createFriendRequest(userDetails.getId(), id);

        return ResponseEntity.ok().body(FriendResponse.builder()
                        .id(friend.getId())
                        .user(UserResponse.builder()
                                .id(friend.getUser2().getId())
                                .username(friend.getUser2().getUsername())
                                .firstName(friend.getUser2().getFirstName())
                                .lastName(friend.getUser2().getLastName())
                                .build())
                        .status(friend.getStatus())
                .build());
    }

    @Operation(summary = "Принять запрос (добавить в друзья)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @PostMapping("/request/{id}")
    public ResponseEntity<?> acceptRequest(@PathVariable Long id,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        friendService.moveToFriend(id, userDetails.getId());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить запрос (оставить в подписчиках)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FriendResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @DeleteMapping("/request/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Friend friend = friendService.moveToSubscribers(id, userDetails.getId());

        return ResponseEntity.ok().body(FriendResponse.builder()
                        .id(friend.getId())
                        .user(UserResponse.builder()
                                .id(friend.getUser1().getId())
                                .username(friend.getUser1().getUsername())
                                .firstName(friend.getUser1().getFirstName())
                                .lastName(friend.getUser1().getLastName())
                                .build())
                        .status(friend.getStatus())
                .build());
    }

    @Operation(summary = "Удалить из друзей (перевести в подписчики)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteFriend(@PathVariable("user_id") Long userId,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        friendService.removeFromFriend(userId, userDetails.getId());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить из подписчиков")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @DeleteMapping("/subscribe/my/{user_id}")
    public ResponseEntity<?> deleteSubscriber(@PathVariable("user_id") Long userId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        friendService.removeFromSubscribers(userId, userDetails.getId());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Список отправленных запросов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FriendResponse.class))
                            )
                    }
            )
    })
    @GetMapping("/request/my/list")
    public ResponseEntity<?> getMyRequests(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Friend> friends = friendService.getRequests(userDetails.getId());

        return ResponseEntity.ok().body(friendResponseListWithUser2(friends));
    }

    @Operation(summary = "Удалить свой запрос на дружбу")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    }
            )
    })
    @DeleteMapping("/request/my/{id}")
    public ResponseEntity<?> deleteMyRequest(@PathVariable Long id,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        friendService.removeRequest(id, userDetails.getId());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Список на кого подписан")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FriendResponse.class))
                            )
                    }
            )
    })
    @GetMapping("/subscriptions/list")
    public ResponseEntity<?> getMySubscriptions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Friend> friends = friendService.getSubscriptions(userDetails.getId());

        return ResponseEntity.ok().body(friendResponseListWithUser2(friends));
    }

    @Operation(summary = "Отписаться")
    @DeleteMapping("/subscription/{user_id}")
    public ResponseEntity<?> unsubscribe(@PathVariable("user_id") Long userId,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        friendService.unsubscribe(userDetails.getId(), userId);

        return ResponseEntity.ok().build();
    }

    private List<FriendResponse> friendResponseListWithUser1(List<Friend> friends) {
        return friends.stream()
                .map(friend -> FriendResponse.builder()
                        .id(friend.getId())
                        .user(UserResponse.builder()
                                .id(friend.getUser1().getId())
                                .username(friend.getUser1().getUsername())
                                .firstName(friend.getUser1().getFirstName())
                                .lastName(friend.getUser1().getLastName())
                                .build()
                        )
                        .status(friend.getStatus())
                        .build()
                ).toList();
    }

    private List<FriendResponse> friendResponseListWithUser2(List<Friend> friends) {
        return friends.stream()
                .map(friend -> FriendResponse.builder()
                        .id(friend.getId())
                        .user(UserResponse.builder()
                                .id(friend.getUser2().getId())
                                .username(friend.getUser2().getUsername())
                                .firstName(friend.getUser2().getFirstName())
                                .lastName(friend.getUser2().getLastName())
                                .build()
                        )
                        .status(friend.getStatus())
                        .build()
                ).toList();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestValue(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("Ошибка валидации")
                        .build()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("Пользователь с таким id не найден")
                        .build()
        );
    }

    @ExceptionHandler(FriendException.class)
    public ResponseEntity<ErrorResponse> handleFriendException(FriendException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error(e.getMessage())
                        .build()
        );
    }
}
