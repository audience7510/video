package com.github.hero.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.hero.common.Result;
import com.github.hero.common.ResultGenerator;
import com.github.hero.pojo.*;
import com.github.hero.service.IMovieService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-03-02
 */
@RestController
@RequestMapping("/movie")
@Api(tags = "影视管理")
@CrossOrigin
public class MovieController {

    @Autowired
    private IMovieService movieService;

    //影视列表分页查询
    @PostMapping("list/{current}/{size}")
    public Result list(@PathVariable int current, @PathVariable int size,
                       @RequestBody MovieQuery movieQuery) {
        Page<Movie> page = new Page<>(current,size);
        QueryWrapper queryWrapper = new QueryWrapper();
        String title = movieQuery.getTitle();
        String status = movieQuery.getStatus();
        String createTimeStart = movieQuery.getCreateTimeStart();
        String createTimeEnd = movieQuery.getCreateTimeEnd();
        if (!StringUtils.isEmpty(title)){
            queryWrapper.like("title",title);
        }
        if (!StringUtils.isEmpty(status)){
            queryWrapper.like("status",status);
        }
        if (!StringUtils.isEmpty(createTimeStart)){
            queryWrapper.ge("create_time",createTimeStart);
        }
        if (!StringUtils.isEmpty(createTimeEnd)){
            queryWrapper.le("create_time",createTimeEnd);
        }
        queryWrapper.orderByDesc("create_time");
        IPage<Movie> ipage = movieService.page(page, queryWrapper);
        return ResultGenerator.success(ipage);
    }

    //添加课程基本信息的方法
    @PostMapping("addCourseInfo")
    public Result addCourseInfo(@RequestBody MovieInfoVo movieInfoVo) {
        //返回添加之后课程id，为了后面添加大纲使用
        String id = movieService.saveCourseInfo(movieInfoVo);
        return ResultGenerator.success(id);
    }

    //根据课程id查询影视基本信息
    @GetMapping("getMovieInfo/{movieId}")
    public Result getCourseInfo(@PathVariable String movieId) {
        MovieInfoVo movieInfo = movieService.getMovieInfo(movieId);
        return ResultGenerator.success(movieInfo);
    }

    //修改影视信息
    @PostMapping("updateMovieInfo")
    public Result updateMovieInfo(@RequestBody MovieInfoVo movieInfoVo) {
        movieService.updateMovieInfo(movieInfoVo);
        return ResultGenerator.success();
    }

    //根据影视id查询影视基本信息
    @GetMapping("getPublishVo/{id}")
    public Result getPublishVo(@PathVariable String id) {
        PublishVo publishVo = movieService.getPublishVo(id);
        return ResultGenerator.success(publishVo);
    }

    //修改影视信息
    @PostMapping("publishMovie/{id}")
    public Result publishMovie(@PathVariable String id) {
        Movie movie = new Movie();
        movie.setId(id);
        //已发布
        movie.setStatus("Normal");
        movieService.updateById(movie);
        return ResultGenerator.success();
    }

    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable String id) {
        movieService.delete(id);
        return ResultGenerator.success();
    }
}

