package com.jumptospring.example.uesr;

import com.jumptospring.example.error.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password){
        SiteUser user = new SiteUser();
        //BCrypt 해싱함수를 사용해서 암호화

        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);
        user.setEmail(email);
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username){
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);

        if(siteUser.isPresent()){
            return siteUser.get();
        } else{
            throw new DataNotFoundException("siteUser not found");
        }
    }
}
