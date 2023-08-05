package com.example.wanted.mockuser;

import com.example.wanted.domain.constants.MemberType;
import com.example.wanted.domain.model.Member;
import com.example.wanted.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    private static final Member member = Member.builder()
            .id(1L)
            .email("abcde@gmail.com")
            .password("12345678")
            .type(MemberType.USER)
            .build();

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(new UserDetailsImpl(member), null,
                Stream.of(MemberType.USER.name()).map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
        context.setAuthentication(authentication);
        return context;
    }
}
