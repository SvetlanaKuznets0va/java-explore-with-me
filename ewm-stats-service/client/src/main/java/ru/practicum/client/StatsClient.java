package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static ru.practicum.constants.Constants.LDT_FORMAT;

@Service
public class StatsClient extends BaseClient {
    @Autowired
    public StatsClient(@Value("${stats.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(HitDto hitDto) {
        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String s = start.format(DateTimeFormatter.ofPattern(LDT_FORMAT));
        String e = end.format(DateTimeFormatter.ofPattern(LDT_FORMAT));
        Map<String, Object> parameters;
        StringBuilder path = new StringBuilder("/stats?");
        path.append("start={start}").append("&end={end}");
        if (CollectionUtils.isEmpty(uris)) {
            parameters = Map.of(
                    "start", s,
                    "end", e,
                    "unique", unique
            );
            path.append("&unique={unique}");
        } else {
            parameters = Map.of(
                    "start", s,
                    "end", e,
                    "uris", uris,
                    "unique", unique
            );
            path.append("&uris=").append(String.join(",", uris)).append("&unique={unique}");
        }

        return get(path.toString(), parameters);
    }
}
