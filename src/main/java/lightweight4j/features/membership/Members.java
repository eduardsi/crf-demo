package lightweight4j.features.membership;

import org.springframework.data.repository.Repository;

interface Members extends Repository<Member, Long> {

    void save(Member member);

}
