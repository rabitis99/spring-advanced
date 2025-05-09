package org.example.expert.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.admin.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.admin.service.UserAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PatchMapping("/admin/users/{userId}")
    public ResponseEntity<Void> changeUserRole(@PathVariable long userId, @RequestBody UserRoleChangeRequest userRoleChangeRequest) {
        userAdminService.changeUserRole(userId, userRoleChangeRequest);
        return ResponseEntity.ok().build();
    }
}
