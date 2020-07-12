package com.videosender.sample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Controller
public class PageController {

    @Value("${video.location}")
    String videoLocation;

    @RequestMapping(value = "/index")
    public String index(Model model){
        // getting all of the files in video folder
        try(Stream<Path> videos =  Files.walk(Paths.get(videoLocation))){
                     videos.filter(Files::isRegularFile);
            model.addAttribute("videos", videos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "index";
    }

    @RequestMapping(value = "/{videoName}")
    public String  video(@PathVariable("videoName") String videoName, Model model){
        model.addAttribute("videoName", videoName);
        return "video";
    }
}
