package nike.platform.monitor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitorDao extends JpaRepository<Monitor, Long> {

    @Query(value = "select * from monitor order by view_start_date desc",nativeQuery = true)//
    Page<Monitor> findAll(Pageable pageable);

    @Query(value = "select * from monitor where replenish_status=:replenish_status ",nativeQuery = true)//
    Page<Monitor> findAllByReplenishStatus(Pageable pageable, Integer replenish_status);

    @Query(value = "select * from monitor where start_entry_date is not null  order by start_entry_date desc",nativeQuery = true)//
    Page<Monitor> findAllByExactTime(Pageable pageable, Integer replenish_status);


    @Query(value = "select * from monitor where style_color in( select DISTINCT( style_color) from task   )",nativeQuery = true)//
    Page<Monitor> findAllAlreadyInvolved(Pageable pageable);


    @Query(value = "select * from monitor where full_title  like CONCAT('%',:full_title,'%')  ",nativeQuery = true)//
    Page<Monitor> findAllByFullTitle(Pageable pageable, String full_title);

    @Query(value = "select * from monitor where full_title  like CONCAT('%',:full_title,'%') or style_color  like CONCAT('%',:style_color,'%')  ",nativeQuery = true)//
    Page<Monitor> findAllByFullTitleOrStyleColor(Pageable pageable, String full_title, String style_color);

    @Query(value = "select * from monitor where style_color=:style_color ",nativeQuery = true)//
    Page<Monitor> findAllByStyleColor(Pageable pageable, String style_color);

    @Query(value = "select * from monitor where id=:id   limit 1",nativeQuery = true)//
    Monitor findOneById(Integer id);

    @Query(value = "select * from monitor where style_color=:style_color   limit 1",nativeQuery = true)//
    Monitor findOneByStyleColor(String style_color);


}
