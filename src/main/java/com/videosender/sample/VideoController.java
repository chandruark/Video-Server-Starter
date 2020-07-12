package com.videosender.sample;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


import static java.lang.Long.min;

@RestController
public class VideoController {

    @Value("${video.location}")
    String videoLocation;

    Long ChunkSize = 1000000L;



    @SneakyThrows
    @GetMapping("/videos/{name}/full")
    public ResponseEntity<UrlResource> getFullVideo(@PathVariable String name, @RequestHeader HttpHeaders headers) {
        UrlResource video = new UrlResource("file:$videoLocation/$name");
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(video);
    }

    @SneakyThrows
    @GetMapping("/videos/{name}")
    public ResponseEntity<ResourceRegion> getPartialVideo(@PathVariable String name, @RequestHeader HttpHeaders headers) {
        UrlResource video = new UrlResource("file:$videoLocation/$name");
        ResourceRegion region = resourceRegion(video, headers);
        return (ResponseEntity<ResourceRegion>) ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }


    private ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) {
        try {
            long contentLength = video.contentLength();
            HttpRange range = (HttpRange) headers.get("range");
           if (range != null) {
                long start = range.getRangeStart(contentLength);
                long end = range.getRangeEnd(contentLength);
                long rangeLength = min(ChunkSize, end - start + 1);
               return  new ResourceRegion(video, start, rangeLength);
            } else {
                long rangeLength = min(ChunkSize, contentLength);
               return  new  ResourceRegion(video, 0, rangeLength);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
