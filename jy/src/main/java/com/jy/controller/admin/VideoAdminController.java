package com.jy.controller.admin;

import com.github.pagehelper.PageInfo;
import com.jy.pojo.Video;
import com.jy.service.VideoService;
import com.jy.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/v1/video")
public class VideoAdminController {

    private static final Logger logger = LoggerFactory.getLogger(VideoAdminController.class);

    @Autowired
    private VideoService videoService;

    @GetMapping("/findAll")
    public Response<PageInfo<Video>> pageVideo(Integer page, Integer size){
        if (StringUtils.isEmpty(page)){
            page = 1;
        }
        if (StringUtils.isEmpty(size)){
            size = 2;
        }
        return videoService.findAll(page,size);
    }

    @GetMapping("/find_by_id")
    public Response<Video> findById(Integer id){
        return videoService.findById(id);
    }

    @PostMapping("/update")
    public Response<String> update(Integer id,String title){
        Video video = new Video();
        video.setId(id);
        video.setTitle(title);
        return videoService.update(video);
    }

    @GetMapping("/delete")
    public Response<String> delete(Integer id){
        return videoService.delete(id);
    }

    @PostMapping("/save")
    public Response<String> save(Video video){
        return videoService.insert(video);
    }
}
