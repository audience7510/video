package com.github.hero.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.hero.common.ResultCode;
import com.github.hero.handler.LolException;
import com.github.hero.mapper.ChapterMapper;
import com.github.hero.pojo.ChapterVo;
import com.github.hero.pojo.MovieChapter;
import com.github.hero.pojo.MovieVideo;
import com.github.hero.pojo.VideoVo;
import com.github.hero.service.IChapterService;
import com.github.hero.service.IVideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-03-02
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, MovieChapter> implements IChapterService {

    @Autowired
    private IVideoService videoService;//注入小节service

    //课程大纲列表,根据课程id进行查询
    @Override
    public List<ChapterVo> getChapterVideoByMovieId(String movieId) {

        //1 根据课程id查询课程里面所有的章节
        QueryWrapper<MovieChapter> wrapperChapter = new QueryWrapper<>();
        wrapperChapter.eq("movie_id",movieId);
        List<MovieChapter> movieChapterList = baseMapper.selectList(wrapperChapter);

        //2 根据课程id查询课程里面所有的小节
        QueryWrapper<MovieVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("movie_id",movieId);
        List<MovieVideo> movieVideoList = videoService.list(wrapperVideo);

        //创建list集合，用于最终封装数据
        List<ChapterVo> finalList = new ArrayList<>();

        //3 遍历查询章节list集合进行封装
        //遍历查询章节list集合
        for (int i = 0; i < movieChapterList.size(); i++) {
            //每个章节
            MovieChapter movieChapter = movieChapterList.get(i);
            //eduChapter对象值复制到ChapterVo里面
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(movieChapter,chapterVo);
            //把chapterVo放到最终list集合
            finalList.add(chapterVo);

            //创建集合，用于封装章节的小节
            List<VideoVo> videoList = new ArrayList<>();

            //4 遍历查询小节list集合，进行封装
            for (int m = 0; m < movieVideoList.size(); m++) {
                //得到每个小节
                MovieVideo movieVideo = movieVideoList.get(m);
                //判断：小节里面chapterid和章节里面id是否一样
                if(movieVideo.getChapterId().equals(movieChapter.getId())) {
                    //进行封装
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(movieVideo,videoVo);
                    //放到小节封装集合
                    videoList.add(videoVo);
                }
            }
            //把封装之后小节list集合，放到章节对象里面
            chapterVo.setChildren(videoList);
        }
        return finalList;
    }

    ////删除章节的方法
    @Override
    public boolean deleteChapter(String chapterId) {
        //根据chapterid章节id 查询小节表，如果查询数据，不进行删除
        QueryWrapper<MovieVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id",chapterId);
        long count = videoService.count(wrapper);
        //判断
        if(count >0) {//查询出小节，不进行删除
            throw new LolException(ResultCode.FAIL,"不能删除");
        } else { //不能查询数据，进行删除
            //删除章节
            int result = baseMapper.deleteById(chapterId);
            //成功  1>0   0>0
            return result>0;
        }
    }
}