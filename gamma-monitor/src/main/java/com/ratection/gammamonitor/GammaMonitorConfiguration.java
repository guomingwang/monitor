package com.ratection.gammamonitor;

import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class GammaMonitorConfiguration {

    @Value("${influxdb.url}")
    private String INFLUX_DB_HOST;

    @Value("${influxdb.user}")
    private String INFLUX_DB_USER;

    @Value("${influxdb.password}")
    private String INFLUX_DB_PASSWORD;

    @Value("${influxdb.db}")
    private String INFLUX_DB_DB;

    @Bean(name = "influxDB", destroyMethod = "close")
    public InfluxDB getDbproxyInfluxDB() {
        InfluxDB influxDB = InfluxDBFactory.connect(
                INFLUX_DB_HOST,
                INFLUX_DB_USER,
                INFLUX_DB_PASSWORD,
                new OkHttpClient.Builder()
                        .readTimeout(10, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS));

        //设置数据默认保存策略
        influxDB.query(new Query(
                String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s SHARD DURATION %s DEFAULT", "default", INFLUX_DB_DB, "365d", 1, "168h"),
                INFLUX_DB_DB
        ));
        return influxDB;
    }
}
