package com.ratection.gammamonitor.datasource;

import com.google.common.base.Strings;
import com.ratection.gammamonitor.datasource.dto.Metric;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
@Repository

public class InfluxDataSource {

    @Autowired
    @Qualifier(value = "influxDB")
    private InfluxDB influxDB;

    @Value("${influxdb.db}")
    private String database;

    private InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields, long time, TimeUnit timeUnit) {
        if (!isValid(measurement)) {
            return ;
        }

        Point.Builder builder = Point.measurement(measurement);
        builder.tag(tags);
        builder.fields(fields);
        if (0 != time) {
            builder.time(time, timeUnit);
        }
        influxDB.write(database, "", builder.build());
    }

    /*
    public List<String> getTagKeys(String measurement){
        if (!isValid(measurement)) {
            return Collections.emptyList();
        }

        String sql = "show tag keys from " + measurement + "";

        Query query = new Query(sql, database);  //todo review
        QueryResult rs = influxDB.query(query);
        if (!rs.hasError()) {
            if (rs.getResults() != null) {
                return rs.getResults().stream()
                        .flatMap(this::buildTagKeys).collect(toList());
            }

            return Collections.emptyList();
        }

        log.error("get tag keys failed, error message: {}", rs.getError());

        return Collections.emptyList();
    }

    private Stream<String> buildTagKeys(Result result) {
        if (result.getSeries() != null) {
            return result.getSeries().stream()
                    .flatMap(series -> series.getValues()
                            .stream()
                            .map(values -> values.get(0).toString())
                    );
        }

        return Stream.empty();
    }

    public List<String> getTagValues(String measurement, String tagKey){
        if (!isValid(measurement) || !isValid(tagKey)) {
            return Collections.emptyList();
        }

        String sql = "show tag values from " + measurement + " with key = " + tagKey + " ";

        Query query = new Query(sql, database);
        QueryResult rs = influxDB.query(query);
        if (!rs.hasError()) {
            if (rs.getResults() != null) {
                return rs.getResults().stream()
                        .flatMap(this::buildTagValues).collect(toList());
            }

            return Collections.emptyList();
        }

        log.error("get tag values failed, error message: {}", rs.getError());

        return Collections.emptyList();
    }

    private Stream<String> buildTagValues(Result result) {
        if (result.getSeries() != null) {
            return result.getSeries().stream()
                    .flatMap(series -> series.getValues()
                            .stream()
                            .map(values -> values.get(1).toString())
                    );
        }

        return Stream.empty();
    }
    */

    public List<Metric> getMetrics(String measurement, String system, List<String> deviceNumList, Long startTime, Long endTime){
        if (!isValid(measurement) || !isValid(system) ||
                (deviceNumList != null && !isValid(String.join(" ", deviceNumList)))) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM " + measurement + " WHERE time >= " + startTime + "ms AND time < " + endTime + "ms AND \"system\" = '" + system + "' ";
        if (deviceNumList != null && !deviceNumList.isEmpty()) {
            sql += " AND ("
                    + deviceNumList.stream()
                    .map(deviceNum -> " \"device-num\" = '" + deviceNum + "' ")
                    .collect(Collectors.joining(" or "))
                    + ") ";
        }
        sql += " tz('Asia/Shanghai')";

        Query query = new Query(sql, database);
        QueryResult rs = influxDB.query(query);
        if (!rs.hasError()) {
            return new ArrayList<>(resultMapper.toPOJO(rs, Metric.class));
        }

        log.error("get metrics failed, error message: {}", rs.getError());

        return Collections.emptyList();
    }

    /*
    public List<Metric> getMetricsByPaging(String measurement, String deviceNum, Long startTime, Long endTime, Integer pageNumber, Integer pageSize){

        String sql = "SELECT * FROM " + measurement + " WHERE time >= " + startTime + "ms AND time < " + endTime + "ms ";
        if (!Strings.isNullOrEmpty(deviceNum)) {
            sql += " AND \"device-num\" = '" + deviceNum + "' ";
        }
        sql += " limit " + pageSize + " offset " + (pageNumber - 1) * pageSize + " tz('Asia/Shanghai')";

        Query query = new Query(sql, database);
        QueryResult rs = influxDB.query(query);
        if (!rs.hasError()) {
            return new ArrayList<>(resultMapper.toPOJO(rs, Metric.class));
        }

        log.error("get metrics failed, error message: {}", rs.getError());

        return Collections.emptyList();
    }

    public Long getTotalCount(String measurement, String deviceNum, Long startTime, Long endTime){

        String sql = "SELECT count(*) FROM " + measurement + " WHERE time >= " + startTime + "ms AND time < " + endTime + "ms ";
        if (!Strings.isNullOrEmpty(deviceNum)) {
            sql += " AND \"device-num\" = '" + deviceNum + "' ";
        }

        Query query = new Query(sql, database);
        QueryResult rs = influxDB.query(query);
        if (!rs.hasError()) {
            if (rs.getResults() != null) {
                return rs.getResults().stream()
                        .flatMap(this::buildCount).findFirst().get();
            }

            return null;
        }

        log.error("get count failed, error message: {}", rs.getError());

        return null;
    }

    private Stream<Long> buildCount(Result result) {
        if (result.getSeries() != null) {
            return result.getSeries().stream()
                    .flatMap(series -> series.getValues()
                            .stream()
                            .map(values -> (long) Math.ceil(Double.parseDouble(values.get(1).toString())))
                    );
        }

        return Stream.empty();
    }
    */

    //防sql注入
    private boolean isValid(String str){
        String[] sqlString = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+|,".split("\\|");
        for (String s : sqlString) {
            if (str.contains(s))
                return false;
        }
        return true;
    }
}
