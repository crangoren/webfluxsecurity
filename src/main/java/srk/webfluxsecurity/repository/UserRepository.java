package srk.webfluxsecurity.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import srk.webfluxsecurity.entity.UserEntity;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {

    Mono<UserEntity> findByUsername(String username);
}
