package com.example.wanted.security;

import com.example.wanted.domain.repository.MemberRepository;
import com.example.wanted.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new UserDetailsImpl(memberRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDetail())));
    }
}
