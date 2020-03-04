package com.jy.dao;

import com.jy.pojo.Video;
import com.jy.provider.VideoProvider;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

public interface VideoDao {

    /*  這样写太麻烦，mybatis 表字段与实体类属性 大小写映射
    @Results({
            @Result(column = "create_time",property = "createTime")
    })*/
    @Select("select * from jy_video")
    List<Video> findAll();

    @Select("select * from jy_video where id=#{id}")
    Video findById(Integer id);

    //@Update("update video set title=#{title} where id=#{id}")
    @UpdateProvider(type = VideoProvider.class,method = "updateVideo")
    int update(Video video);

    @Delete("delete from jy_video where id=#{id}")
    int delete(Integer id);

    @Insert("INSERT INTO `jy`.`jy_video` (`id`, `title`, `summary`, `cover_img`, `view_num`, `price`, `create_time`, `online`, `point`) VALUES (null, #{title},#{summary}, #{coverImg}, #{viewNum}, #{price}, #{createTime}, #{online}, #{point});")
    @Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
    int save(Video video);
}
