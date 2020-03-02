package ru.anisimov.testtask.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.anisimov.testtask.dao.StatisticDao;
import ru.anisimov.testtask.dao.impl.repository.StatisticRepository;
import ru.anisimov.testtask.entity.EntityStatistic;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.anisimov.testtask.Constants.*;

/**
 * @author Anton Anisimov
 * @version 1.0
 */

@Service
@Repository
public class StatisticImpl implements StatisticDao {

    @Autowired
    private StatisticRepository repository;

    @Async
    @Override
    public Future<Map<String, Integer>> save(String userId, String pageId) {

        EntityStatistic statistic = new EntityStatistic(userId, pageId);

        repository.save(statistic);

        int DAYTIME = 86_400_000;
        long startDayMS = statistic.getVisitDate().getTime() / DAYTIME * DAYTIME;
        Timestamp startDayDate = new Timestamp(startDayMS);

        List<EntityStatistic> dayVisitList = repository.findAllByVisitDateAfter(startDayDate);

        Map<String, Integer> responseMap = new HashMap<>();

        responseMap.put(TOTAL, dayVisitList.size());
        responseMap.put(UNIQUE, getUnique(dayVisitList).size());

        return new AsyncResult<>(responseMap);
    }

    @Async
    @Override
    public Future<Map<String, Integer>> insertWithResult(String userId, String pageId) {
        return new AsyncResult<>(repository.insertWithResult(userId, pageId));
    }

    @Override
    public Map<String, Integer> select(Date from, Date by, int limit) {

        List<EntityStatistic> dayVisitList = repository.findAllByVisitDateBetween(new Timestamp(from.getTime()), new Timestamp(by.getTime()));

        Map<String, Integer> responseMap = new HashMap<>();

        responseMap.put(TOTAL, dayVisitList.size());

        Map<String, String> uniqueMap = getUnique(dayVisitList);
        responseMap.put(UNIQUE, uniqueMap.size());

        responseMap.put(REGULAR, getRegular(dayVisitList, uniqueMap, limit));

        return responseMap;
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    private Map<String, String> getUnique(List<EntityStatistic> list) {
        Map<String, String> responseMap = new HashMap<>();

        list.forEach(i -> responseMap.put(i.getUserId(), i.getPageId()));

        return responseMap;
    }

    private int getRegular(List<EntityStatistic> dayVisitList, Map<String, String> uniqueMap, int limit) {
        AtomicInteger regularVisitCount = new AtomicInteger();

        uniqueMap.forEach((k, v) -> {
            long pageCount = dayVisitList.stream().filter(o -> o.getUserId().equals(k))
                    .map(EntityStatistic::getPageId).distinct().count();
            if (pageCount >= limit) regularVisitCount.getAndIncrement();
        });

        return regularVisitCount.get();
    }

}
