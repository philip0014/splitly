package com.example.splitly.model.entity;

import com.example.splitly.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = User.COLLECTION_NAME)
public class User implements UserDetails {

    public static final String COLLECTION_NAME = "user";

    @Id
    private String id;
    private String username;
    private String password;
    private String email;

    @Builder.Default
    private String profileUrl = "/storage/image/profile/default.png";

    @Builder.Default
    private String locale = "en";

    @Builder.Default
    private List<String> friends = new ArrayList<>();

    @Builder.Default
    private List<String> bills = new ArrayList<>();

    @Builder.Default
    private List<UserRole> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(userRole -> new SimpleGrantedAuthority(userRole.toString()))
            .collect(toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
