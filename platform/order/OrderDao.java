package nike.platform.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderDao extends JpaRepository<Orders, Long> {

    @Query(value = "select * from orders where style_color=:style_color",nativeQuery = true)//
    Page<Orders> findAllByStyleColor(Pageable pageable, String style_color);

    @Modifying
    @Transactional
    @Query(value = "delete from orders where style_color=:style_color",nativeQuery = true)//
    void deleteByStyleColor(String style_color);
}
