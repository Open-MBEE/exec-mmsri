package org.openmbee.mms.mmsri.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.services.NodeService;
import org.openmbee.mms.crud.services.DefaultNodeService;
import org.openmbee.mms.json.ElementJson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service("testNodeService")
public class TestNodeService extends DefaultNodeService implements NodeService {

    @Value("${sdvc.stream.limit}")
    private int streamLimit;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<StreamingResponseBody> stream(String projectId, String refId, String req, Map<String, String> params) {
        StreamingResponseBody stream = outputStream -> {
            List<String> indexIds = new ArrayList<>();
            ContextHolder.setContext(projectId, refId);
            nodeRepository.findAllByDeleted(false).forEach(node -> {
                indexIds.add(node.getDocId());
            });
            outputStream.write("{\"elements\":[".getBytes(StandardCharsets.UTF_8));
            batches(indexIds, streamLimit).forEach(ids -> {
                try {
                    outputStream.write(nodeIndex.findAllById(Set.copyOf(ids)).stream().map(this::toJson).collect(Collectors.joining(",")).getBytes(StandardCharsets.UTF_8));
                } catch (IOException ioe) {
                    // Good luck
                    logger.error("Error writing to stream", ioe);
                }
            });
            outputStream.write("]}".getBytes(StandardCharsets.UTF_8));
        };
        return new ResponseEntity(stream, HttpStatus.OK);
    }

    public static <T> Stream<List<T>> batches(List<T> source, int length) {
        return IntStream.iterate(0, i -> i < source.size(), i -> i + length).mapToObj(i -> source.subList(i, Math.min(i + length, source.size())));
    }

    public String toJson(ElementJson elementJson) {
        try {
            return objectMapper.writeValueAsString(elementJson);
        } catch (JsonProcessingException e) {
            logger.error("Error in toJson: ", e);
        }
        return null;
    }
}
