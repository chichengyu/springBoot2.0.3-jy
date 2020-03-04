package com.jy.service;

import com.github.pagehelper.PageInfo;
import com.jy.pojo.Video;
import com.jy.utils.Response;

import java.util.List;

public interface VideoService {

    Response<PageInfo<Video>> findAll(Integer page,Integer size);

    Response<Video> findById(Integer id);

    Response<String> update(Video video);

    Response<String> delete(Integer id);

    Response<String> insert(Video video);
}
