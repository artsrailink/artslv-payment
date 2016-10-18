package co.id.artslv.repository;

import co.id.artslv.lib.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String>{
    User findOneByRqid(String rqid);
}
