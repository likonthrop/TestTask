package ru.anisimov.testtask.dao.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.anisimov.testtask.dao.StatisticDao;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.anisimov.testtask.Constants.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticImplTest {

    @Autowired
    private StatisticDao dao;

    private Timestamp startDayDate;
    private Timestamp byDate;

    @Before
    public void setUp() throws InterruptedException {

        dao.deleteAll();
        dao.save("userId-1", "pageId-1");
        dao.save("userId-1", "pageId-2");
        dao.save("userId-1", "pageId-3");
        dao.save("userId-2", "pageId-4");
        dao.save("userId-2", "pageId-5");

        Thread.sleep(1000);

        startDayDate = new Timestamp(0);
        byDate = new Timestamp(System.currentTimeMillis());
    }


    @After
    public void tearDown() {
        dao.deleteAll();
    }

    @Test
    public void save() throws ExecutionException, InterruptedException {

        int resultCount = 2;

        int total = 6;
        int unique = 3;

        Future<Map<String, Integer>> saveFuture = dao.save("userId-3", "pageId-5");

        assertNotNull(saveFuture);

        Map<String, Integer> resultMap = saveFuture.get();

        assertNotNull(resultMap);
        assertEquals(resultMap.size(), resultCount);
        assertEquals((int) resultMap.get(TOTAL), total);
        assertEquals((int) resultMap.get(UNIQUE), unique);
    }

    @Test
    public void select() {

        int pageLimit = 3;

        int mapElementCount = 3;

        int total = 5;
        int unique = 2;
        int regular = 1;

        Map<String, Integer> resultMap = dao.select(startDayDate, byDate, pageLimit);

        assertNotNull(resultMap);
        assertEquals(resultMap.size(), mapElementCount);
        assertEquals((int) resultMap.get(TOTAL), total);
        assertEquals((int) resultMap.get(UNIQUE), unique);
        assertEquals((int) resultMap.get(REGULAR), regular);
    }
}