package nike.platform.persion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersionDao extends JpaRepository<Persion, Long> {

//    @Query(value = "select igroup,count(1) as count from persion  where login_status=1 GROUP BY igroup",nativeQuery = true)//
//    List<Map<String,Integer>> findGroup();
//
//    @Query(value = "select * from persion  where igroup=?1 and  login_status=?2",nativeQuery = true)//
//    List<Persion> findAllByGroupAndLoginStatus(String igroup,Integer login_status);


    @Query(value = "select * from persion  where login_status=?1",nativeQuery = true)//
    List<Persion> findAllByLoginStatus(Integer login_status);

}
