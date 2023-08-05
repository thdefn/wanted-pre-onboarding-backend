package com.example.wanted.controller;

import com.example.wanted.dto.PostDto;
import com.example.wanted.dto.PostForm;
import com.example.wanted.security.UserDetailsImpl;
import com.example.wanted.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    private ResponseEntity<PostDto> create(@Valid @RequestBody PostForm form,
                                           @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(postService.create(form, user.getMember()));
    }

    @PutMapping("/{postId}")
    private ResponseEntity<PostDto> update(@Valid @RequestBody PostForm form,
                                           @PathVariable Long postId,
                                           @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(postService.update(postId, form, user.getMember()));
    }

    @GetMapping
    private ResponseEntity<Page<PostDto>> read(@RequestParam(required = false, defaultValue = "0") int page) {
        return ResponseEntity.ok(postService.read(page));
    }

    @GetMapping("/{postId}")
    private ResponseEntity<PostDto> detail(@PathVariable Long postId,
                                           @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(postService.detail(postId, user.getMember()));
    }

    @DeleteMapping("/{postId}")
    private ResponseEntity<Void> delete(@PathVariable Long postId,
                                        @AuthenticationPrincipal UserDetailsImpl user) {
        postService.delete(postId, user.getMember());
        return ResponseEntity.ok().build();
    }
}
