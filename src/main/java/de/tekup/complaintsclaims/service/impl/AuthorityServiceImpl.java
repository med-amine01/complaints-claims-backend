package de.tekup.complaintsclaims.service.impl;

import de.tekup.complaintsclaims.entity.Authority;
import de.tekup.complaintsclaims.enums.Authorities;
import de.tekup.complaintsclaims.repository.AuthorityRepository;
import de.tekup.complaintsclaims.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Override
    public List<String> findNonExistentAuthorities(Set<String> authorityNames) {
        return authorityRepository.findNonExistentAuthorities(authorityNames);
    }

    @Override
    public Authority createDefaultAuthority() {
        Authority defaultAuthority = new Authority();
        defaultAuthority.setName(Authorities.READ.name());
        return defaultAuthority;
    }
}