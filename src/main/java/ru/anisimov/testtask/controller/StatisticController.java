package ru.anisimov.testtask.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import ru.anisimov.testtask.dao.StatisticDao;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Anton Anisimov
 * @version 1.0
 */

@Api(tags = {"Statistic"}, description = "Для работы со статистикой посещаемлсти сайта")
@RestController
@RequestMapping("/statistic/*")
public class StatisticController {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private StatisticDao statisticDao;

    /**
     * Создание события посещения сайта пользователем. Параметры:
     * a.      Идентификатор пользователя
     * b.      Идентификатор страницы сайта
     * Ответ должен содержать (в формате JSON):
     * a.      Общее количество посещений за текущие сутки
     * b.      Количество уникальных пользователей за текущие сутки
     */
    @ApiOperation(value = "Создание события посещения сайта пользователем.")
    @GetMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<String>> save(
            @ApiParam(value = "userId - Идентификатор пользователя") @NonNull @RequestParam() String userId,
            @ApiParam(value = "pageId - Идентификатор страницы сайта") @NonNull @RequestParam() String pageId
    ) {
        long start = System.currentTimeMillis();
        DeferredResult<ResponseEntity<String>> response = new DeferredResult<>();

        ForkJoinPool.commonPool().submit(() -> {
            try {
                Map<String, Integer> result = statisticDao.insertWithResult(userId, pageId).get();
                response.setResult(new ResponseEntity<>(new JSONObject(result).toString(), HttpStatus.OK));
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getLocalizedMessage(), e);
                response.setErrorResult(ResponseEntity.badRequest().body(e.getLocalizedMessage()));
            }
        });
        response.onCompletion(() -> logger.info("duration = [" + (System.currentTimeMillis() - start) + "]"));
        return response;
    }

    /**
     * Получение статистики посещения за произвольный период. Параметр запроса:
     * a.      период учёта
     * Ответ должен содержать (в формате JSON):
     * a.      Общее количество посещений за указанный период
     * b.      Количество уникальных пользователей за указанный период
     * c.      Количество постоянных пользователей за указанный период
     * (пользователей, которые за период просмотрели не менее 10 различныхстраниц).
     */
    @ApiOperation(value = "Получение статистики посещения за произвольный период.")
    @GetMapping(value = "select", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> select(
            @ApiParam(value = "from - Дата начала периода учета", defaultValue = "2018-12-31") @RequestParam(required = false) String from,
            @ApiParam(value = "by - Дата окончания периода учета", defaultValue = "2019-12-31") @RequestParam(required = false) String by
    ) {
        Calendar fromDate = setCalender(from);
        if (fromDate == null) return ResponseEntity.badRequest().body("Не верно указана дата начала периода учета");
        Calendar byDate = setCalender(by);
        if (byDate == null) return ResponseEntity.badRequest().body("Не верно указана дата окончания периода учета");

        Map<String, Integer> responseMap = statisticDao.select(fromDate.getTime(), byDate.getTime(), 10);

        return new ResponseEntity<>(new JSONObject(responseMap).toString(), HttpStatus.OK);
    }

    private Calendar setCalender(String stringDate) {
        Calendar calendar = Calendar.getInstance();
        try {
            String[] byArray = stringDate.split("-");
            calendar.set(Calendar.YEAR, Integer.valueOf(byArray[0]));
            calendar.set(Calendar.MONTH, Integer.valueOf(byArray[1]));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(byArray[2]));
            return calendar;
        } catch (NumberFormatException e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}