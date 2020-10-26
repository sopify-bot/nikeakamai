package nike.persioninit;

import nike.platform.persion.Persion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PersionInitDao  extends JpaRepository<Persion, Long> {

    @Query(value = "select * from persion where phone_number is null and client_id is not null and first_name is null",nativeQuery = true)//
    List<Persion> findPersionsByPhoneNumbercAndClient_id();

}


