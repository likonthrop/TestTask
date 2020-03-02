package ru.anisimov.testtask.dao.impl.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.anisimov.testtask.entity.EntityStatistic;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @author Anton Anisimov
 * @version 1.0
 */

public interface StatisticRepository extends CrudRepository<EntityStatistic, Long> {

    List<EntityStatistic> findAllByVisitDateBetween(@NonNull Timestamp from, @NonNull Timestamp by);

    List<EntityStatistic> findAllByVisitDateAfter(@NonNull Timestamp startDayDate);

    @Query(nativeQuery = true, value = "with in_row as (insert into table_statistic (user_id, page_id) values (?1,?2) returning user_id),\n" +
            "day_total as (select user_id from table_statistic where visit_date > date_trunc('day', current_timestamp) union all (select * from in_row))\n" +
            "select (select count(*) from day_total) as total, (select count(*) from (select distinct * from day_total) as u) as uniq")
    Map<String, Integer> insertWithResult(String userId, String PageId);

}
