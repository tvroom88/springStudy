package com.example.boardproejct;

import com.example.boardproejct.entity.BoardUser;
import com.example.boardproejct.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final ArticleRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BoardUser boardUser = this.userRepository.findByusername(username);
        if (boardUser == null) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        return new User(boardUser.getUsername(), boardUser.getPassword(), authorities);
    }
}