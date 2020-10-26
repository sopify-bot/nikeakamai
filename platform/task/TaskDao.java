package nike.platform.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface TaskDao extends JpaRepository<Task, Long> {

    @Query(value = "select * from task ",nativeQuery = true)//
    Page<Task> findAll(Pageable pageable);

    @Query(value = "select * from task where style_color=:style_color",nativeQuery = true)//
    Page<Task> findAllByStyleColor(Pageable pageable, String style_color);

    @Query(value = "select * from task where id=:id",nativeQuery = true)//
    Task findOneById(Long id);


    @Query(value = "select * from task where task_status=:task_status and style_color=:style_color",nativeQuery = true)//
    List<Task> findAllByTaskStatusAAndStyleColor(int task_status, String style_color);


    @Query(value = "select DISTINCT(style_color) from task where task_status=:task_status limit 1",nativeQuery = true)//
    String findOneStyleColorByTaskStatus(int task_status);



    @Query(value = "select DISTINCT(detail_url_id) from task ",nativeQuery = true)//
    Set<String> findDetailURLIdByTaskStatus();

    @Transactional
    @Modifying
    @Query(value = "delete from task where style_color=:style_color",nativeQuery = true)//
    void deleteByStyleColor(String style_color);

}
