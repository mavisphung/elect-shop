package me.huypc.elect_shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huypc.elect_shop.entity.User;
import me.huypc.elect_shop.generated.api.UserApi;
import me.huypc.elect_shop.generated.dto.CartItemUpsertForm;
import me.huypc.elect_shop.generated.dto.OrderPlaceForm;
import me.huypc.elect_shop.service.CartService;

@RestController
@PreAuthorize("hasAuthority('ROLE_USER')")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final CartService cartService;

    @Override
    public ResponseEntity<Void> addToUserCart(@Valid CartItemUpsertForm cartItemUpsertForm) {
        User currentUser = getCurrentUser();
        cartService.addToCart(currentUser, cartItemUpsertForm);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> removeFromUserCart(@Valid CartItemUpsertForm cartItemUpsertForm) {
        User currentUser = getCurrentUser();
        cartService.removeFromCart(currentUser, cartItemUpsertForm);
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public ResponseEntity<Void> placeUserOrder(@Valid OrderPlaceForm orderPlaceForm) {
        User currentUser = getCurrentUser();
        cartService.placeOrder(currentUser, orderPlaceForm);
        return ResponseEntity.ok().build();
    }

}
