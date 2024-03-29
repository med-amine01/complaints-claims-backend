package de.tekup.complaintsclaims.config;


import de.tekup.complaintsclaims.entity.Authority;
import de.tekup.complaintsclaims.entity.Role;
import de.tekup.complaintsclaims.entity.User;
import de.tekup.complaintsclaims.enums.Authorities;
import de.tekup.complaintsclaims.enums.Roles;
import de.tekup.complaintsclaims.repository.AuthorityRepository;
import de.tekup.complaintsclaims.repository.RoleRepository;
import de.tekup.complaintsclaims.repository.UserRepository;
import de.tekup.complaintsclaims.service.SecretKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class InitUsers {

    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecretKeyService secretKeyService;

    @Transactional
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Authority readAuthority = createAuthority(Authorities.READ.name());
        Authority writeAuthority = createAuthority(Authorities.WRITE.name());
        Authority deleteAuthority = createAuthority(Authorities.DELETE.name());

        createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
        Role roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

        if (null == roleAdmin) return;

        User admin = new User();
        admin.setName("admin");
        admin.setPassword(passwordEncoder.encode("test"));
        admin.setEmail("admin@gmail.com");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(roleAdmin);
        admin.setRoles(adminRoles);

        KeyPair keyPair = secretKeyService.generateSecretKeys();
        String privateKey = secretKeyService.encode(keyPair.getPrivate().getEncoded());
        String publicKey = secretKeyService.encode(keyPair.getPublic().getEncoded());
        admin.setPrivateKey(privateKey);
        admin.setPublicKey(publicKey);

        if (userRepository.existsByName(admin.getName())) return;

        userRepository.save(admin);
    }

    @Transactional
    public Authority createAuthority(String name) {
        Authority authority = authorityRepository.findByName(name);

        if (null == authority) {
            authority = new Authority();
            authority.setName(name);
            return authorityRepository.save(authority);
        }

        return authority;
    }

    @Transactional
    public Role createRole(String name, Collection<Authority> authorities) {
        Role role = roleRepository.findByRoleName(name);

        if (null == role) {
            role = new Role();
            role.setRoleName(name);
            role.setAuthorities(authorities);
            return roleRepository.save(role);
        }

        return role;
    }
}