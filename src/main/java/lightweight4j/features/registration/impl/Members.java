package lightweight4j.features.registration.impl;

import org.springframework.data.repository.Repository;

import java.util.Optional;

interface Members extends Repository<Member, Long> {

    void save(Member member);

    Optional<Member> findById(Long id);

}
