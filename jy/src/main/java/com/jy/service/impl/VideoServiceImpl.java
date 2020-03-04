package com.jy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jy.dao.VideoDao;
import com.jy.pojo.Video;
import com.jy.service.VideoService;
import com.jy.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoDao videoDao;

    @Override
    public Response<PageInfo<Video>> findAll(Integer page,Integer size) {
        PageHelper.startPage(page,size);
        List<Video> all = videoDao.findAll();
        PageInfo<Video> pageInfo = new PageInfo<>(all);
        return Response.success(pageInfo);
    }

    @Override
    public Response<Video> findById(Integer id) {
        if (StringUtils.isEmpty(id)){
            return Response.error("参数错误");
        }
        return Response.success(videoDao.findById(id));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Response<String> update(Video video) {
        int resultCount = videoDao.update(video);
        if (resultCount > 0){
            return Response.success();
        }
        return Response.error();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Response<String> delete(Integer id) {
        if (StringUtils.isEmpty(id)){
            return Response.error("参数错误");
        }
        int resultCount = videoDao.delete(id);
        if (resultCount > 0){
            return Response.success();
        }
        return Response.error();
    }

    @Override
    public Response<String> insert(Video video) {
        int resultCount = videoDao.save(video);
        if (resultCount > 0){
            return Response.success();
        }
        return Response.error();
    }
}
