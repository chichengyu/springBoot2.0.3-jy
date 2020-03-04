package com.jy.provider;


import com.jy.pojo.Video;
import org.apache.ibatis.jdbc.SQL;

/**  new SQL(){{}}  外部括号是创建一个匿名内部类，内部花括号是一个构造代码块，在实例化一个对象时，会先于构造方式执行，编译时会将构造代码块移入构造方法中
 * video 构建动态sql语句
 */
public class VideoProvider {

    /**
     * 更新 video 动态 sql语句
     * @param video
     * @return
     */
    public String updateVideo(final Video video){
        return new SQL(){{
            UPDATE("video");

            // 条件判断
            if (video.getTitle() != null){
                SET("title=#{title}");
            }
            if (video.getSummary() != null){
                SET("summary=#{summary}");
            }
            if (video.getCoverImg() != null){
                SET("cover_img=#{CoverImg}");
            }
            if(video.getViewNum()!=null){
                SET("view_num=#{viewNum}");
            }
            if(video.getPrice()!=null){
                SET("price=#{price}");
            }
            if(video.getOnline()!=null){
                SET("online=#{online}");
            }
            if(video.getPoint()!=null){
                SET("point=#{point}");
            }

            WHERE("id=#{id}");

        }}.toString();
    }
}
