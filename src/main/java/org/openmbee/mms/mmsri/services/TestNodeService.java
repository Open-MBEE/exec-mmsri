package org.openmbee.mms.mmsri.services;

import org.openmbee.mms.core.services.NodeService;
import org.openmbee.mms.crud.services.DefaultNodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestNodeService extends DefaultNodeService implements NodeService {

    @Value("sdvc.stream.limit")
    int streamLimit;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public ResponseEntity<ResponseBodyEmitter> stream(String projectId, String refId, String req, Map<String, String> params) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        executor.execute(() -> {
            try {
                List<String> indexIds = new ArrayList<>();
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
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return new ResponseEntity(emitter, HttpStatus.OK);
    }

    public static <T> Stream<List<T>> batches(List<T> source, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length = " + length);
        }
        int size = source.size();
        if (size <= 0) {
            return Stream.empty();
        }
        int fullChunks = (size - 1) / length;
        return IntStream.range(0, fullChunks + 1).mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
    }
}
