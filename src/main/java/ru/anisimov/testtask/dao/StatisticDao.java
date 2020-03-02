package ru.anisimov.testtask.dao;

import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author Anton Anisimov
 * @version 1.0
 */

public interface StatisticDao {

    Future<Map<String, Integer>> save(@NonNull String userId, @NonNull String pageId);

    Future<Map<String, Integer>> insertWithResult(@NonNull String userId, @NonNull String pageId);

    Map<String, Integer> select(@NonNull Date from, @NonNull Date by, int pageLimit);

    void deleteAll();

}
