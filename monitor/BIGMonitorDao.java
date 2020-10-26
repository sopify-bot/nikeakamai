package nike.monitor;

import nike.platform.monitor.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BIGMonitorDao extends JpaRepository<Monitor, Long> {

    @Query(value = "select * from monitor where style_color=:style_color limit 1",nativeQuery = true)//
    Monitor findOneByStyleColor(String style_color);




}
