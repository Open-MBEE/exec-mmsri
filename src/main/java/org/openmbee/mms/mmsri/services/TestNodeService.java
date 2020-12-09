package org.openmbee.mms.mmsri.services;

import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.services.NodeService;
import org.openmbee.mms.crud.services.DefaultNodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service("testNodeService")
public class TestNodeService extends DefaultNodeService implements NodeService {

    @Value("${sdvc.stream.limit}")
    private int streamLimit;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public ResponseEntity<ResponseBodyEmitter> stream(String projectId, String refId, String req, Map<String, String> params) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        executor.execute(() -> {
            try {
                List<String> indexIds = new ArrayList<>();
                ContextHolder.setContext(projectId, refId);
                nodeRepository.findAllByDeleted(false).forEach(node -> {
                    indexIds.add(node.getDocId());
                });
                batches(indexIds, streamLimit).forEach(ids -> {
                    try {
                        emitter.send(nodeIndex.findAllById(Set.copyOf(ids)));
                    } catch (IOException ioe) {
                        // Should ignore this maybe
                        emitter.completeWithError(ioe);
                    }
                });
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            } finally {
                emitter.complete();
            }
        });
        return new ResponseEntity(emitter, HttpStatus.OK);
    }

    public static <T> Stream<List<T>> batches(List<T> source, int length) {
        return IntStream.iterate(0, i -> i < source.size(), i -> i + length).mapToObj(i -> source.subList(i, Math.min(i + length, source.size())));
    }
}
