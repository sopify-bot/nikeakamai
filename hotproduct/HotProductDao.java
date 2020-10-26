package nike.hotproduct;

import nike.platform.monitor.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HotProductDao extends JpaRepository<Monitor, Long> {

    @Query(value = "select * from monitor where id>:id and replenish_status=0  and (:startTime>start_entry_date or start_entry_date is null)   limit 1",nativeQuery = true)//
    Monitor findNextOneById(Integer id, String startTime);


}
